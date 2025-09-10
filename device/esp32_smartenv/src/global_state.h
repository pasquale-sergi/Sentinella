#ifndef GLOBAL_STATE_H
#define GLOBAL_STATE_H

#include <Arduino.h> 

// Calibration Mode & Status
extern bool inCalibrationMode;
extern unsigned long calibrationStartTime;
extern unsigned long cumulativeCalibrationTimeMs;

// Dynamically calculated Min/Max/Range values (loaded from NVS)
// extern because they are defined in nvs_manager.cpp or main.cpp and used elsewhere
extern float savedMinVals[4];
extern float savedMaxVals[4];
extern float savedDataRange[4];

extern float dynamicAnomalyThreshold;
extern float calibrationErrorMean;
extern float calibrationErrorM2;
extern unsigned long calibrationErrorCount;
#endif 