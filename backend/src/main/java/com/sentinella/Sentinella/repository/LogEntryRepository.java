package com.sentinella.Sentinella.repository;

import com.sentinella.Sentinella.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findTop20BySensorIdOrderByTimestampDesc(Long sensorId);
}
