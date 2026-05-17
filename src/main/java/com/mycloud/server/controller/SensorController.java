package com.mycloud.server.controller;

import com.mycloud.server.model.MovementAlert;
import com.mycloud.server.model.SensorReading;
import com.mycloud.server.repository.MovementAlertRepository;
import com.mycloud.server.repository.SensorReadingRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorReadingRepository  sensorReadingRepository;
    private final MovementAlertRepository  movementAlertRepository;

    public SensorController(SensorReadingRepository sensorReadingRepository, MovementAlertRepository movementAlertRepository) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.movementAlertRepository = movementAlertRepository;
    }

    @GetMapping("/latest")
    public SensorReading latest() {
        return sensorReadingRepository.findTopByOrderByTimestampDesc()
                .orElseThrow(() -> new RuntimeException("No sensor readings found"));
    }

    @GetMapping("/history")
    public List<SensorReading> history(@RequestParam(defaultValue = "24") int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return sensorReadingRepository.findByTimestampAfterOrderByTimestampAsc(since);
    }

    @GetMapping("/alerts")
    public List<MovementAlert> alerts(@RequestParam(defaultValue = "24") int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return movementAlertRepository.findByTimestampAfterOrderByTimestampDesc(since);
    }
}
