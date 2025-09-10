#include "sensor_manager.h"
#include "Arduino.h" 

TwoWire I2C_BME = TwoWire(0); 
Adafruit_BME680 bme(&I2C_BME);
void initSensor() {
  I2C_BME.begin(BME_SDA_PIN_CONF, BME_SCL_PIN_CONF, 100000); 
  if (!bme.begin(0x77)) { 
    Serial.println("Could not find BME680 sensor! Check wiring.");
    while (1); 
  }
  bme.setTemperatureOversampling(BME680_OS_8X);
  bme.setHumidityOversampling(BME680_OS_2X);
  bme.setPressureOversampling(BME680_OS_4X);
  bme.setIIRFilterSize(BME680_FILTER_SIZE_3);
  bme.setGasHeater(320, 150);
  Serial.println("BME680 Sensor initialized.");
}

bool readSensorData(float& temp, float& hum, float& pres, float& gas) {
  if (bme.performReading()) {
    temp = bme.temperature - TEMPERATURE_OFFSET_CONF; 
    hum = bme.humidity;
    pres = bme.pressure;
    gas = bme.gas_resistance;
    return true;
  }
  return false;
}