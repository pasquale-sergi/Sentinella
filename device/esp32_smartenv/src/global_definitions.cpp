#include "config.h" // Include the header with declarations
#include "Arduino.h" // For basic Arduino types if needed
#include "secrets.h"
// --- Wi-Fi Credentials ---
const char* WIFI_SSID_CONF = WIFI_SSID;       // Now a const char* assigned from the macro
const char* WIFI_PASSWORD_CONF = WIFI_PASSWORD;

// --- MQTT Broker Details ---
const char* MQTT_SERVER_CONF = MQTT_SERVER;
const int MQTT_PORT_CONF = MQTT_PORT;
const char* MQTT_CLIENT_ID_CONF = "Sentinella_ESP32";
const char* MQTT_PUBLISH_TOPIC_ANOMALY = "sentinella/anomaly";
const char* MQTT_PUBLISH_TOPIC_LIVE = "sentinella/live_data";
const char* MQTT_SUBSCRIBE_TOPIC_REQUEST = "sentinella/get_data";

// --- BME680 I2C Pins ---
const int BME_SDA_PIN_CONF = BME_SDA_PIN;
const int BME_SCL_PIN_CONF = BME_SCL_PIN;

// --- Sensor Calibration & AI ---
const float TEMPERATURE_OFFSET_CONF = TEMPERATURE_OFFSET;
const float ANOMALY_THRESHOLD_CONF = ANOMALY_THRESHOLD;

// --- Power Management ---
const unsigned long CALIBRATION_DURATION_MS_CONF = CALIBRATION_DURATION_MS; // For debugging: 5 minutes
const int DEEP_SLEEP_TIME_SEC_CONF = DEEP_SLEEP_DURATION_MS; 