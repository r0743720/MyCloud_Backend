package com.mycloud.server.repository;

import com.mycloud.server.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    Optional<SensorReading> findTopByOrderByTimestampDesc();
    List<SensorReading> findByTimestampAfterOrderByTimestampAsc(LocalDateTime after);
}
