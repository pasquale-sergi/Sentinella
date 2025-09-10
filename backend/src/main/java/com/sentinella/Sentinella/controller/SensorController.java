package com.sentinella.Sentinella.controller;

import com.sentinella.Sentinella.entity.Sensor;
import com.sentinella.Sentinella.entity.SensorReading;
import com.sentinella.Sentinella.entity.UserApplication;
import com.sentinella.Sentinella.repository.SensorRepository;
import com.sentinella.Sentinella.service.UserService;
import com.sentinella.Sentinella.utility.SensorClaimRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/sensors")
@CrossOrigin(origins = "http://localhost:8080")
public class SensorController {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerSensor(@RequestBody Sensor sensor) {
        UserApplication currentUser =userService.getCurrentAuthenticatedUser();
        if(currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //generate a uniquekey for the sensor
        sensor.setApiKey(UUID.randomUUID().toString());
        sensor.setUser(currentUser);

        Sensor savedSensor = sensorRepository.save(sensor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSensor);

    }

    @PostMapping("/claim")
    public ResponseEntity<?> claimSensor(@RequestBody SensorClaimRequest claimRequest) {
        //ensure user is authenticated
        UserApplication currentUser =userService.getCurrentAuthenticatedUser();
        if(currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String deviceId = claimRequest.getDeviceId();
        if(deviceId == null || deviceId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Device ID cannot be empty");
        }

        Optional<Sensor> existingSensor = sensorRepository.findByDeviceId(deviceId);
        Sensor sensorToClaim;

        if(existingSensor.isPresent()) {
            sensorToClaim = existingSensor.get();
            //case 1 - sensor found in db
            if (sensorToClaim.getUser() != null){
                //sensor already claimed by user
                if (!sensorToClaim.getUser().getId().equals(currentUser.getId())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Sensor: " + deviceId + " already claimed by another user");
                }else {
                    //sensor already claimed by current user
                    return ResponseEntity.ok("Sensor: " + deviceId + " already claimed by you");
                }
            }
            //sensor exist but is unclaimed -- proceed to claim it

        }else {
            //sensor not found in db that means no exisiting messaging through mqtt channel
            sensorToClaim = new Sensor(deviceId);
            sensorToClaim.setApiKey(UUID.randomUUID().toString());
            System.out.println("Creating new sensor entity for deviceId: " + deviceId);
        }
        sensorToClaim.setUser(currentUser);
        if(claimRequest.getName() != null && !claimRequest.getName().trim().isEmpty()) {
            sensorToClaim.setName(claimRequest.getName());
        }
        if(claimRequest.getLocation() != null && !claimRequest.getLocation().trim().isEmpty()) {
            sensorToClaim.setLocation(claimRequest.getLocation());
        }

        Sensor savedSensor = sensorRepository.save(sensorToClaim);
        return ResponseEntity.status(HttpStatus.OK).body(savedSensor);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Sensor>> getUserSensors() {
        UserApplication currentUser = userService.getCurrentAuthenticatedUser();
        if(currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Sensor> sensors = sensorRepository.findByUserId(currentUser.getId());
        return ResponseEntity.ok(sensors);
    }

    //get a specific sensor
    @GetMapping("/{id}")
    public ResponseEntity<Sensor> getSensorById(@PathVariable Long id) {
        UserApplication currentUser = userService.getCurrentAuthenticatedUser();
        if(currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return sensorRepository.findByIdAndUser_Id(id, currentUser.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/unclaimed")
    public ResponseEntity<List<Sensor>> getUnclaimedSensors() {
        List<Sensor> unclaimedSensors = sensorRepository.findByUserIsNull();
        return ResponseEntity.ok(unclaimedSensors);
    }

}
