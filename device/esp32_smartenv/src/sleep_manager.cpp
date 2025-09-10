#include "sleep_manager.h"
#include "Arduino.h" 
#include <WiFi.h> 

void go_to_deep_sleep() {
  Serial.printf("Going to deep sleep for %d seconds...\n", DEEP_SLEEP_TIME_SEC_CONF);
  Serial.flush(); 
  esp_sleep_enable_timer_wakeup(DEEP_SLEEP_TIME_SEC_CONF * 1000000ULL); 



  WiFi.disconnect(true); 
  WiFi.mode(WIFI_OFF);  
  esp_deep_sleep_start();
}