package com.sentinella.Sentinella.utility;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SensorReadingRequest {
    // Getters and Setters
    @Getter
    private Long sensorId; // Sensor needs to know its own ID
    private float temperature;
    private float humidity;
    private float pressure;
    private float gasResistance;

    public void setSensorId(Long sensorId) { this.sensorId = sensorId; }
    public void setTemperature(float temperature) { this.temperature = temperature; }
    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }
    public void setPressure(float pressure) { this.pressure = pressure; }
    public void setGasResistance(float gasResistance) { this.gasResistance = gasResistance; }
    public float getTemperature() { return temperature; }
    public float getHumidity() { return humidity; }
    public float getPressure() { return pressure; }
    public float getGasResistance() { return gasResistance; }
    public Long getSensorId() { return this.sensorId; }

}