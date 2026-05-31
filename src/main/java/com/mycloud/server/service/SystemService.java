package com.mycloud.server.service;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SystemService {

    public double getCpuTemperature() {
        try {
            String raw = Files.readString(Path.of("/sys/class/thermal/thermal_zone0/temp"));
            return Double.parseDouble(raw.trim()) / 1000.0;
        } catch (Exception e) {
            return -1;
        }
    }
}
