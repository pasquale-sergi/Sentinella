package com.sentinella.Sentinella.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "sensor_readings")
@Getter
@Setter
public class SensorReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private float temperature;
    private float humidity;
    private float pressure;
    private float gasResistance;
    private Boolean isAnomaly;
    private float reconstructionError;
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    @JsonBackReference("sensorReadings") // This is the "back" side for Sensor-Reading
    private Sensor sensor;

    public SensorReading() { this.timestamp = LocalDateTime.now(); }

    public SensorReading(float temperature, float humidity, float pressure, float gasResistance, Sensor sensor) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.gasResistance = gasResistance;
        this.timestamp = LocalDateTime.now();
        this.sensor = sensor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public float getTemperature() { return temperature; }
    public void setTemperature(float temperature) { this.temperature = temperature; }

    public float getHumidity() { return humidity; }
    public void setHumidity(float humidity) { this.humidity = humidity; }

    public float getPressure() { return pressure; }
    public void setPressure(float pressure) { this.pressure = pressure; }

    public float getGasResistance() { return gasResistance; }
    public void setGasResistance(float gasResistance) { this.gasResistance = gasResistance; }

    public Boolean getIsAnomaly() { return isAnomaly; }
    public void setIsAnomaly(Boolean isAnomaly) { this.isAnomaly = isAnomaly; }

    public float getReconstructionError() { return reconstructionError; }
    public void setReconstructionError(float reconstructionError) { this.reconstructionError = reconstructionError; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }
    // --- END MANUAL GETTERS AND SETTERS ---


    @Override
    public String toString() {
        return "SensorReading{" +
                "id=" + id +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", gasResistance=" + gasResistance +
                ", isAnomaly=" + isAnomaly +
                ", reconstructionError=" + reconstructionError +
                ", timestamp=" + timestamp +
                ", sensorId=" + (sensor != null ? sensor.getId() : "null") +
                '}';
    }
}
