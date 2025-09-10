package com.sentinella.Sentinella.repository;

import com.sentinella.Sentinella.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByUserId(Long userId);
    Optional<Sensor> findByIdAndUserId(Long id, Long userId);
    Optional<Sensor> findByApiKey(String apiKey);
    Optional<Sensor> findByDeviceId(String deviceId);

    List<Sensor> findByUserIsNull();
    Optional<Sensor> findByDeviceIdAndUserIsNull(String deviceId);

    Optional<Sensor> findByIdAndUser_Id(Long id, Long userId);
}
