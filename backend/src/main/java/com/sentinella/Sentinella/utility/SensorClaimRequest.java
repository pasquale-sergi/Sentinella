package com.sentinella.Sentinella.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SensorClaimRequest {

    private String deviceId;
    private String name;
    private String location;

    public void setLocation(String location) { this.location = location; }
    public String getLocation() { return location; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getDeviceId() { return deviceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
