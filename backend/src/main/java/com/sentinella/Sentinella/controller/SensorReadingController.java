package com.sentinella.Sentinella.controller;

import com.sentinella.Sentinella.entity.Sensor;
import com.sentinella.Sentinella.entity.SensorReading;
import com.sentinella.Sentinella.entity.UserApplication;
import com.sentinella.Sentinella.repository.SensorReadingRepository;
import com.sentinella.Sentinella.repository.SensorRepository;
import com.sentinella.Sentinella.service.UserService;
import com.sentinella.Sentinella.utility.SensorReadingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sensor-data")
@CrossOrigin(origins ="http://localhost:8080" )
public class SensorReadingController {
    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/receive-info")
    public ResponseEntity<SensorReading> requestReadingToDevice(@RequestBody SensorReadingRequest request) {
        Optional<Sensor> sensorOpt = sensorRepository.findById(request.getSensorId());
        if (sensorOpt.isPresent()) {
            Sensor sensor = sensorOpt.get();
            SensorReading reading = new SensorReading(
                    request.getTemperature(),
                    request.getHumidity(),
                    request.getPressure(),
                    request.getGasResistance(),
                    sensor);

            return ResponseEntity.status(HttpStatus.CREATED).body(sensorReadingRepository.save(reading));
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //latest reading from sensor
    @GetMapping("/latest")
    public ResponseEntity<List<SensorReading>> getLatestReadingsFromDevice(@RequestParam Long sensorId){
        UserApplication currentUser = userService.getCurrentAuthenticatedUser();
        if(currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean userOwnsSensor = sensorRepository.findByIdAndUserId(sensorId, currentUser.getId()).isPresent();
        if(!userOwnsSensor) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<SensorReading> readings = sensorReadingRepository
                .findBySensor_IdOrderByTimestampDesc(sensorId);
        System.out.println("Received readings request from sensor " + sensorId + " sending data: " + readings);
        return ResponseEntity.status(HttpStatus.OK).body(readings);
    }
}
