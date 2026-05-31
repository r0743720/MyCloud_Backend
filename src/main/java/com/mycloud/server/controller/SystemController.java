package com.mycloud.server.controller;

import com.mycloud.server.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @GetMapping("/cpu-temp")
    public Map<String, Double> getCpuTemp() {
        return Map.of("cpuTemperature", systemService.getCpuTemperature());
    }
}