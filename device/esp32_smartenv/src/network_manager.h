#ifndef NETWORK_MANAGER_H
#define NETWORK_MANAGER_H

#include <WiFi.h>
#include <PubSubClient.h>
#include "config.h"

extern WiFiClient espClient; 
extern PubSubClient client; 

void initWiFi();
void initMQTT();
void mqttCallback(char* topic, byte* payload, unsigned int length);
void reconnectMQTT();
void sendAnomalyAlert(float temp, float hum, float pres, float gas, float error);
void sendLiveData(float temp, float hum, float pres, float gas, float error, bool is_anomaly);
void handleOnDemandDataRequest(); 

#endif 