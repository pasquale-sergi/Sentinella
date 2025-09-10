#ifndef CONFIG_H
#define CONFIG_H
#define NVS_KEY_CUMULATIVE_TIME "cal_cum_time_ms"
#define DEEP_SLEEP_DURATION_S 60
#define DEEP_SLEEP_DURATION_MS (DEEP_SLEEP_DURATION_S * 1000)

#define MQTT_MAX_PACKET_SIZE 512
// --- Wi-Fi Credentials ---
extern const char* WIFI_SSID_CONF;       
extern const char* WIFI_PASSWORD_CONF;   

// --- MQTT Broker Details ---
extern const char* MQTT_SERVER_CONF;
extern const int MQTT_PORT_CONF;       
extern const char* MQTT_CLIENT_ID_CONF;
extern const char* MQTT_PUBLISH_TOPIC_ANOMALY;
extern const char* MQTT_PUBLISH_TOPIC_LIVE;
extern const char* MQTT_SUBSCRIBE_TOPIC_REQUEST;

// --- BME680 I2C Pins ---
extern const int BME_SDA_PIN_CONF; 
extern const int BME_SCL_PIN_CONF; 

// --- Sensor Calibration & AI ---
extern const float TEMPERATURE_OFFSET_CONF;
extern const float ANOMALY_THRESHOLD_CONF;

// --- Power Management ---
extern const unsigned long CALIBRATION_DURATION_MS_CONF;
extern const int DEEP_SLEEP_TIME_SEC_CONF; 

#endif 