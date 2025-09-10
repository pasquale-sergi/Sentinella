package com.sentinella.Sentinella.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper; // For JSON parsing
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // For OffsetDateTime
import com.sentinella.Sentinella.entity.Sensor;
import com.sentinella.Sentinella.entity.SensorReading;
import com.sentinella.Sentinella.repository.SensorReadingRepository;
import com.sentinella.Sentinella.repository.SensorRepository;
import com.sentinella.Sentinella.repository.UserRepository;
import com.sentinella.Sentinella.service.TelegramNotificationService;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.Message; // Import Message
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler; // Add this line
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Configuration
public class MqttConfig {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.topics.live}")
    private String liveDataTopic;

    @Value("${mqtt.topics.anomaly}")
    private String anomalyTopic;

    @Value("${mqtt.topics.request}")
    private String requestTopic;

    private final SensorReadingRepository sensorReadingRepository;
    private final ObjectMapper objectMapper; // Jackson ObjectMapper for JSON
    private final SensorRepository sensorRepository;
    private final UserRepository userRepository;
    private final TelegramNotificationService telegramNotificationService;

    public MqttConfig(SensorReadingRepository sensorReadingRepository, SensorRepository sensorRepository, UserRepository userRepository, TelegramNotificationService telegramNotificationService) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.sensorRepository = sensorRepository;
        this.userRepository = userRepository;
        this.telegramNotificationService = telegramNotificationService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Register module for OffsetDateTime
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Ensure proper date format
    }

    // --- MQTT Client Factory ---
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setServerURIs(new String[]{brokerUrl});
        connectOptions.setCleanSession(true);


        factory.setConnectionOptions(connectOptions); // Set the options object on the factory
        return factory;
    }


    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer mqttInbound() {

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId + "_inbound", mqttClientFactory(),
                        liveDataTopic, anomalyTopic);
        adapter.setCompletionTimeout(5000);

        adapter.setQos(0);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMqttMessage(Message<String> message) { // Receive full message object
        String payload = message.getPayload();
        String topic = (String) message.getHeaders().get("mqtt_receivedTopic");

        System.out.println("Received message from topic: " + topic + " with payload: " + payload);

        try {
            // Parse the JSON payload from the ESP32
            JsonNode jsonNode = objectMapper.readTree(payload);

            //find sensor info
            String deviceId = jsonNode.get("device_id").asText();

            Optional<Sensor> sensorOptional = sensorRepository.findByDeviceId(deviceId);
            Sensor sensor;

            if (sensorOptional.isEmpty()) {
                //sensor not found in db, so not associated to user
                Sensor newSensor = new Sensor(deviceId);
                sensor = sensorRepository.save(newSensor);
                System.out.println("New sensor detected and saved: " + newSensor);
            }else {
                sensor = sensorOptional.get();
            }


            SensorReading reading = new SensorReading();
            reading.setSensor(sensor);

            reading.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime());

            reading.setTemperature(jsonNode.get("temp").floatValue());
            reading.setHumidity(jsonNode.get("humidity").floatValue());
            reading.setPressure(jsonNode.get("pressure").floatValue());
            reading.setGasResistance(jsonNode.get("gas").floatValue());
            reading.setReconstructionError(jsonNode.get("error").floatValue());
            reading.setIsAnomaly(jsonNode.get("anomaly").asBoolean());

            sensorReadingRepository.save(reading); // Save to database
            if (reading.getIsAnomaly() != null && reading.getIsAnomaly()) {
                String alertMessage = String.format(
                        "🚨 <b>ANOMALY DETECTED!</b> 🚨\nDevice: <b>%s</b> (%s)\nError: %.4f\nTemp: %.2f°C\nHum: %.2f%%\nPres: %.2fhPa\nGas: %.0fOhms\nTimestamp: %s",
                        sensor.getName(), sensor.getDeviceId(), reading.getReconstructionError(), reading.getTemperature(), reading.getHumidity(), (reading.getPressure() / 100), reading.getGasResistance(), reading.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                telegramNotificationService.sendAnomalyAlert(alertMessage);
            }


        } catch (Exception e) {
            System.err.println("Error processing MQTT message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId + "_outbound", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(requestTopic);
        messageHandler.setDefaultQos(1);
        return messageHandler;
    }
}