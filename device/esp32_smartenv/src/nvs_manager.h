#ifndef NVS_MANAGER_H
#define NVS_MANAGER_H

#include <Preferences.h>
#include "global_state.h" 

extern Preferences preferences; 

void initNVS();
bool loadCalibrationParameters();
void saveCalibrationParameters();

#endif