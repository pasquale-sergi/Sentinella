package com.sentinella.Sentinella.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.security.core.userdetails.memory.UserAttribute;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String deviceId;
    private String name;
    private String location;
    @Column(unique = true, nullable = true)
    private String apiKey; // Unique API key for sensor authentication (e.g., from ESP32)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonBackReference("userSensors") // This is the "back" side for User-Sensor
    private UserApplication user; // Sensor belongs to a user

    // Sensor has many readings
    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("sensorReadings") // This is the "owning" side for Sensor-Reading
    private Set<SensorReading> readings = new HashSet<>();

    // Sensor has many log entries
    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("sensorLogEntries") // This is the "owning" side for Sensor-LogEntry
    private Set<LogEntry> logEntries = new HashSet<>();

    // Constructors
    public Sensor() {}
    public Sensor(String deviceId) {
        this.deviceId = deviceId;
        this.name = "Unnamed Sensor " + deviceId; // Default name
        this.location = "Unknown"; // Default location
    }

    public Sensor(String name, String location, String apiKey, UserApplication user) {
        this.name = name;
        this.location = location;
        this.apiKey = apiKey;
        this.user = user;
    }

    // Getters and Setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public UserApplication getUser() { return user; }
    public void setUser(UserApplication user) { this.user = user; }
    public Set<SensorReading> getReadings() { return readings; }
    public void setReadings(Set<SensorReading> readings) { this.readings = readings; }
    public Set<LogEntry> getLogEntries() { return logEntries; }
    public void setLogEntries(Set<LogEntry> logEntries) { this.logEntries = logEntries; }
}