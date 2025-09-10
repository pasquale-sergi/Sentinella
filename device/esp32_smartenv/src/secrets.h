// device-cpp/src/secrets.h (DO NOT COMMIT THIS FILE TO GIT)
#ifndef SECRETS_H
#define SECRETS_H

// --- Wi-Fi Credentials ---
#define WIFI_SSID       "TP-Link_Sergi"
#define WIFI_PASSWORD   "Giovi2021"

// --- MQTT Broker Details ---
#define MQTT_SERVER     "192.168.1.134" 
#define MQTT_PORT       1883
#define MQTT_CLIENT_ID  "Sentinella_device" 

// --- BME680 I2C Pins ---
// These are generally not secrets, but can be customized per device
#define BME_SDA_PIN     8
#define BME_SCL_PIN     9

// --- Sensor Calibration & AI ---
// These are configuration, not secrets, but often good to keep in secrets.h
// or a separate config.h depending on how often they change.
#define TEMPERATURE_OFFSET      2.00
#define ANOMALY_THRESHOLD       0.015
#define CALIBRATION_DURATION_MS (3 * 60 * 1000UL) // 3 minutes for debugging
#define DEEP_SLEEP_TIME_SEC     60

#endif 