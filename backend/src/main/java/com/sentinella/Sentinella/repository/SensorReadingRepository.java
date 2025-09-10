package com.sentinella.Sentinella.repository;

import com.sentinella.Sentinella.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    List<SensorReading> findBySensor_IdOrderByTimestampDesc(Long sensorId); // <- Changed here

}