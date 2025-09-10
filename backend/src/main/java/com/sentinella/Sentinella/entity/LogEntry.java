package com.sentinella.Sentinella.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private String level; // e.g., INFO, WARNING, ERROR, ALERT
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor; // Log belongs to a sensor

    // Constructors, Getters, Setters
    public LogEntry() { this.timestamp = LocalDateTime.now(); }

    public LogEntry(String message, String level, Sensor sensor) {
        this.message = message;
        this.level = level;
        this.timestamp = LocalDateTime.now();
        this.sensor = sensor;
    }
    // ... (rest of getters/setters)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }
}