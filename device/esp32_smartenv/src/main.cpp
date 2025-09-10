#include <Arduino.h>
#include "config.h"
#include "global_state.h" 
#include "sensor_manager.h"
#include "nvs_manager.h"
#include "tflm_inference.h"
#include "network_manager.h"
#include "sleep_manager.h"


bool inCalibrationMode = true;
unsigned long calibrationStartTime = 0; // For tracking duration of current boot cycle
unsigned long cumulativeCalibrationTimeMs = 0; // Tracks total calibration time across boots
float savedMinVals[4];
float savedMaxVals[4];
float savedDataRange[4];
float dynamicAnomalyThreshold = 0.0;
float calibrationErrorMean = 0.0;
float calibrationErrorM2 = 0.0;
unsigned long calibrationErrorCount = 0;


void setup() {
  Serial.begin(115200);
  delay(1000); 
  Serial.flush(); 

  Serial.println("\n--- Sentinella Device Startup ---");
  Serial.flush();
  delay(2000); 
  
  initNVS(); 
  delay(2000);
  Serial.flush();

  initTFLM(); 
  delay(2000);
  Serial.flush();
  
  if (loadCalibrationParameters()) { 
    // loadCalibrationParameters() returned true: means FINAL calibration data was loaded
    inCalibrationMode = false; // Confirm Monitor Mode
    Serial.println("\nFinal calibration loaded. Entering MONITOR MODE.");
    Serial.flush();

    // In Monitor Mode, initialize network components
    initWiFi(); 
    Serial.println("WiFi initialized.");
    Serial.flush();
  
    initMQTT(); 
    Serial.println("MQTT initialized.");
    Serial.flush();

  } else {
    inCalibrationMode = true; 
    calibrationStartTime = millis(); // reset current boot's timer for calibration progress

    if (cumulativeCalibrationTimeMs == 0 && calibrationErrorCount == 0) {
        // This is a truly fresh start
        Serial.println("\n******************************************************************\n");
        Serial.println("No calibration data found. Starting FRESH CALIBRATION.");
        // Initialize min/max for fresh calibration
        for (int i = 0; i < 4; ++i) {
            savedMinVals[i] = 9999999.0; 
            savedMaxVals[i] = -9999999.0;
            savedDataRange[i] = 0.0;
        }
        // Error stats are already 0.0 by definition (global init)
    } else {
        Serial.printf("\n*******************\n Resuming calibration. Total elapsed: %lu ms. Samples: %lu.\n", cumulativeCalibrationTimeMs, calibrationErrorCount);
        cumulativeCalibrationTimeMs += DEEP_SLEEP_DURATION_MS;
        // Min/max/error stats are already loaded from NVS by loadCalibrationParameters()
        Serial.printf("\nUPDATED total after sleep: %lu ms.\n", cumulativeCalibrationTimeMs);
    }
    Serial.flush();
  }
  
  // Initialize Sensor always
  initSensor(); 
  Serial.flush();

  Serial.println("--- Setup Complete ---");
  Serial.println("\n***********************************************\n");
  Serial.flush(); // Final flush for setup.
}


void loop() {
  float temp, hum, pres, gas;
  if (!readSensorData(temp, hum, pres, gas)) {
    Serial.println("Failed to read BME680 sensor data. Going to deep sleep.");
    Serial.flush();
    go_to_deep_sleep();
    return;
  }

  float raw_sensor_values[4] = {temp, hum, pres, gas};
  float reconstruction_error = 0.0; 


  if (inCalibrationMode) {
    // Update min/max values on the fly
    savedMinVals[0] = min(savedMinVals[0], temp);
    savedMaxVals[0] = max(savedMaxVals[0], temp);
    savedMinVals[1] = min(savedMinVals[1], hum);
    savedMaxVals[1] = max(savedMaxVals[1], hum);
    savedMinVals[2] = min(savedMinVals[2], pres);
    savedMaxVals[2] = max(savedMaxVals[2], pres);
    savedMinVals[3] = min(savedMinVals[3], gas);
    savedMaxVals[3] = max(savedMaxVals[3], gas);

    for (int i = 0; i < 4; ++i) {
      savedDataRange[i] = savedMaxVals[i] - savedMinVals[i];


    }

    runTFLMInference(raw_sensor_values, reconstruction_error);




    // Update error statistics
    updateErrorStatistics(reconstruction_error);
    


    Serial.printf("CALIBRATING: T:%.2fC, H:%.2f%%, P:%.2fhPa, G:%.0fOhms, Error:%.6f\n", temp, hum, pres, gas, reconstruction_error);
    Serial.printf("Current Min/Max: T:[%.2f,%.2f], H:[%.2f,%.2f], P:[%.2f,%.2f], G:[%.0f,%.0f]\n",
                  savedMinVals[0], savedMaxVals[0], savedMinVals[1], savedMaxVals[1],
                  savedMinVals[2], savedMaxVals[2], savedMinVals[3], savedMaxVals[3]);
    Serial.printf("Current Data Ranges: T:%.4f, H:%.4f, P:%.4f, G:%.4f\n",savedDataRange[0], savedDataRange[1], savedDataRange[2], savedDataRange[3]);
    Serial.printf("Current Error Stats (Avg/Count): %.6f / %lu\n", calibrationErrorMean, calibrationErrorCount);
    Serial.flush();

    // Check if calibration duration is complete
    if ((cumulativeCalibrationTimeMs + (millis() - calibrationStartTime)) >= CALIBRATION_DURATION_MS_CONF) { // Use predicted final cumulative time
      Serial.println("Calibration complete! Saving FINAL parameters to NVS.");
      Serial.flush();

      // Final update to cumulative time before saving
      cumulativeCalibrationTimeMs += (millis() - calibrationStartTime);
      
      for (int i = 0; i < 4; ++i) {
        char key_min[10], key_max[10];
        sprintf(key_min, "min_val_%d", i);
        sprintf(key_max, "max_val_%d", i); 
        preferences.putFloat(key_min, savedMinVals[i]);
        preferences.putFloat(key_max, savedMaxVals[i]);
      }
      //saveCalibrationParameters(); // Save final min/max/error stats

      // Calculate final dynamic anomaly threshold
      float current_mean_error = calibrationErrorMean;
      float current_std_dev_error = 0.0;
      if (calibrationErrorCount > 1) {
        float variance_arg = calibrationErrorM2 / (calibrationErrorCount - 1);
        // Safeguard: Ensure the argument to sqrt is not negative
        current_std_dev_error = sqrt(max(0.0f, variance_arg));
      }
      float THRESHOLD_FACTOR = 2.5; // Tunable factor
      dynamicAnomalyThreshold = current_mean_error + (THRESHOLD_FACTOR * current_std_dev_error);
      preferences.putFloat("anomaly_thresh", dynamicAnomalyThreshold); // Save the threshold!
            Serial.printf("Calculated Dynamic Anomaly Threshold: %.6f\n", dynamicAnomalyThreshold); Serial.flush();


      preferences.end(); // Close NVS handle


      Serial.println("\nCalibration done, rebooting into MONITOR MODE..."); Serial.flush();
      delay(4000);
      ESP.restart();
    } else {
      cumulativeCalibrationTimeMs += (millis() - calibrationStartTime);
      saveCalibrationParameters(); // Save current progress to NVS before deep sleep
      
      //trying to solve time reset issue
      preferences.end();
      delay(1000);
      Serial.flush();
      go_to_deep_sleep();
    }
  } else {
    // Monitor Mode Logic
    if (!client.connected()) {
      reconnectMQTT();
    }
    client.loop(); // Handle incoming MQTT messagesù
    runTFLMInference(raw_sensor_values, reconstruction_error);

    Serial.printf("Current Data: T:%.2fC, H:%.2f%%, P:%.2fhPa, G:%.0fOhms, Error:%.6f\n", temp, hum, pres, gas, reconstruction_error);
    Serial.flush();

    if (reconstruction_error > dynamicAnomalyThreshold) { // Use dynamicAnomalyThreshold
      Serial.println("!!! ANOMALY DETECTED !!!");
      Serial.flush();
      sendLiveData(temp, hum, pres, gas, reconstruction_error, true);
    }else{
      sendLiveData(temp, hum, pres, gas, reconstruction_error, false);
    }
    go_to_deep_sleep(); 
  }
}