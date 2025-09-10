#include "nvs_manager.h"
#include "Arduino.h"
#include "global_state.h" 
#include "config.h"      

Preferences preferences; 

void initNVS() {
  preferences.begin("sentinella", false); 
}

bool loadCalibrationParameters() {

  // Check if FINAL calibration (anomaly threshold) is found
if (preferences.isKey("anomaly_thresh") && preferences.getFloat("anomaly_thresh", 0.0) > 0.0) {
    Serial.println("DEBUG NVS: Found anomaly_thresh. Loading FINAL parameters."); Serial.flush();
    
    for (int i = 0; i < 4; ++i) {
      char key_min[10], key_max[10];
      sprintf(key_min, "min_val_%d", i);
      sprintf(key_max, "max_val_%d", i);
      savedMinVals[i] = preferences.getFloat(key_min,0.0);
      savedMaxVals[i] = preferences.getFloat(key_max, 0.0);
      savedDataRange[i] = savedMaxVals[i] - savedMinVals[i];
    }
    dynamicAnomalyThreshold = preferences.getFloat("anomaly_thresh", ANOMALY_THRESHOLD_CONF); 
    
    // Reset cumulative calibration time for MONITOR MODE
    cumulativeCalibrationTimeMs = 0; 
    calibrationErrorCount = 0;
    calibrationErrorMean = 0.0;
    calibrationErrorM2 = 0.0;

    return true; 
  }
  // If no final threshold, check for PARTIAL calibration data
  else if (preferences.isKey("cal_err_count") && preferences.getULong("cal_err_count", 0) > 0) {
    
    for (int i = 0; i < 4; ++i) {
      char key_min[10], key_max[10];
      sprintf(key_min, "min_val_%d", i);
      sprintf(key_max, "max_val_%d", i); 
      savedMinVals[i] = preferences.getFloat(key_min,9999999.0);
      savedMaxVals[i] = preferences.getFloat(key_max,-9999999.0);
      savedDataRange[i] = savedMaxVals[i] - savedMinVals[i];
    }
    
    calibrationErrorMean = preferences.getFloat("cal_err_mean", 0.0);
    calibrationErrorM2 = preferences.getFloat("cal_err_M2", 0.0);
    calibrationErrorCount = preferences.getULong("cal_err_count", 0);
    cumulativeCalibrationTimeMs = preferences.getULong(NVS_KEY_CUMULATIVE_TIME, 0);
    dynamicAnomalyThreshold = 0.0; 

    return false; 
  }
  
  // No valid calibration data (final or partial) found in NVS. Starting fresh calibration.
  // Ensure these globals are explicitly reset for a fresh start, even if they are default 0.0
  cumulativeCalibrationTimeMs = 0; 
  calibrationErrorCount = 0;     
  calibrationErrorMean = 0.0;
  calibrationErrorM2 = 0.0;
  dynamicAnomalyThreshold = 0.0; // Ensure no leftover threshold
  for (int i = 0; i < 4; ++i) {
      savedMinVals[i] = 9999999.0;
      savedMaxVals[i] = -9999999.0;
      savedDataRange[i] = 0.0; // Consistent with setup() fresh init
  }
  return false;
}

void saveCalibrationParameters() {
  for (int i = 0; i < 4; ++i) {
    char key_min[10], key_max[10];
    sprintf(key_min, "min_val_%d", i);
    sprintf(key_max, "max_val_%d", i); 
    preferences.putFloat(key_min, savedMinVals[i]);
    preferences.putFloat(key_max, savedMaxVals[i]);
  }
  preferences.putFloat("cal_err_mean", calibrationErrorMean);
  preferences.putFloat("cal_err_M2", calibrationErrorM2);
  preferences.putULong("cal_err_count", calibrationErrorCount);
  preferences.putULong(NVS_KEY_CUMULATIVE_TIME, cumulativeCalibrationTimeMs);


  
}