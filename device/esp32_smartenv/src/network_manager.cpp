#include "network_manager.h"
#include "Arduino.h" 
#include "sensor_manager.h" // To read sensor for live data request
#include "sleep_manager.h" // For deep sleep if WiFi fails
#include "tflm_inference.h"
WiFiClient espClient;
PubSubClient client(espClient);

//handle wifi connection
void initWiFi() {
  Serial.printf("Connecting to WiFi '%s'...", WIFI_SSID_CONF);
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID_CONF, WIFI_PASSWORD_CONF);
  unsigned long startAttemptTime = millis();
  while (WiFi.status() != WL_CONNECTED && millis() - startAttemptTime < 10000) { 
    delay(500);
    Serial.print(".");
  }
  if (WiFi.status() == WL_CONNECTED) {
    Serial.printf("\nConnected to WiFi. IP Address: %s\n", WiFi.localIP().toString().c_str());
  } else {
    Serial.println("\nFailed to connect to WiFi! Going to deep sleep.");
    go_to_deep_sleep(); 
  }
}

void initMQTT() {
  client.setServer(MQTT_SERVER_CONF, MQTT_PORT_CONF);
  client.setCallback(mqttCallback);
}

void reconnectMQTT() {
  Serial.print("Attempting MQTT connection...");
  if (client.connect(MQTT_CLIENT_ID_CONF)) {
    Serial.println("connected");
    client.subscribe(MQTT_SUBSCRIBE_TOPIC_REQUEST, 1);
  } else {
    Serial.printf("failed, rc=%d. Retrying in 5 seconds...\n", client.state());
    delay(5000); 
  }
}

void mqttCallback(char* topic, byte* payload, unsigned int length) {
  Serial.printf("Message arrived on topic: '%s'\n", topic);
  if (String(topic) == MQTT_SUBSCRIBE_TOPIC_REQUEST) {
    Serial.println("Received on-demand data request!");
    handleOnDemandDataRequest();
  }
}


void sendLiveData(float temp, float hum, float pres, float gas, float error, bool is_anomaly) {
    if (client.connected()) {
        char payload_buf[MQTT_MAX_PACKET_SIZE];
        int len = snprintf(payload_buf, sizeof(payload_buf), 
                 "{\"device_id\":\"%s\",\"anomaly\":%s,\"error\":%.6f,\"temp\":%.2f,\"humidity\":%.2f,\"pressure\":%.2f,\"gas\":%.0f}",
                 MQTT_CLIENT_ID_CONF,is_anomaly ? "true" : "false", is_anomaly ? error : 0.0, temp, hum, pres, gas);
      const char* topic_to_publish = is_anomaly ? MQTT_PUBLISH_TOPIC_ANOMALY : MQTT_PUBLISH_TOPIC_LIVE;
      client.publish(topic_to_publish, (const uint8_t*)payload_buf, len, false);        
      client.loop();     
      delay(1000);        
      Serial.printf("%s data published to MQTT!\n", is_anomaly ? "Anomaly" : "Live");
    } else{
        Serial.printf("MQTT not connected, could not send %s data.\n", is_anomaly ? "anomaly alert" : "live data");
    }
}

void handleOnDemandDataRequest() {
  float temp, hum, pres, gas;
  if (readSensorData(temp, hum, pres, gas)) {
      float reconstruction_error = 0.0; 

      float raw_sensor_values[4] = {temp, hum, pres, gas};
      runTFLMInference(raw_sensor_values, reconstruction_error); 

      sendLiveData(temp, hum, pres, gas, 0.0, false); 
  } else {
      Serial.println("Failed to get reading for on-demand request.");
  }
}