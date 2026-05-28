package com.mycloud.server.controller;

import com.mycloud.server.service.MqttSubscriberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/fan")
public class FanController {

    private final MqttSubscriberService mqttSubscriberService;

    public FanController(MqttSubscriberService mqttSubscriberService) {
        this.mqttSubscriberService = mqttSubscriberService;
    }

    @PostMapping("/control")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> control(@RequestBody Map<String, Boolean> body) {
        boolean on = body.getOrDefault("on", false);
        mqttSubscriberService.publishFanCommand(on);
        return ResponseEntity.ok(Map.of("on", on, "status", "command sent"));
    }
}
