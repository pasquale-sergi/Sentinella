#ifndef SENSOR_MANAGER_H
#define SENSOR_MANAGER_H

#include <Adafruit_BME680.h>
#include <Wire.h> 
#include "config.h" 

extern TwoWire I2C_BME; 
extern Adafruit_BME680 bme; 

void initSensor();
bool readSensorData(float& temp, float& hum, float& pres, float& gas);

#endif 