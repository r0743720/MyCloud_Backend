package com.mycloud.server.service;

import com.mycloud.server.model.MovementAlert;
import com.mycloud.server.model.SensorReading;
import com.mycloud.server.repository.MovementAlertRepository;
import com.mycloud.server.repository.SensorReadingRepository;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class MqttSubscriberService {

    private final MovementAlertRepository movementAlertRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final ObjectMapper objectMapper =  new ObjectMapper();

    public void onSensorMessage(String topic, MqttMessage message){
        try{
            JsonNode payload = objectMapper.readTree(message.getPayload());

            SensorReading reading = new SensorReading();
            reading.setTemperature(payload.get("temperature").asDouble());
            reading.setHumidity(payload.get("humidity").asDouble());
            reading.setPressure(payload.get("pressure").asDouble());

            sensorReadingRepository.save(reading);
            System.out.println("Sensor reading saved: " + payload);
        } catch(Exception e){
            System.out.println("Sensor reading save failed: " + e.getMessage());
        }
    }

    public void onMovementAlert(String topic, MqttMessage message){
        try{
            JsonNode payload = objectMapper.readTree(message.getPayload());
            JsonNode accel = payload.get("acceleration");

            MovementAlert alert = new MovementAlert();
            alert.setAccelerationX(accel.get("x").asDouble());
            alert.setAccelerationY(accel.get("y").asDouble());
            alert.setAccelerationZ(accel.get("z").asDouble());

            movementAlertRepository.save(alert);
            System.out.println("Movement alert saved: " + payload);
        } catch(Exception e){
            System.out.println("Movement alert save failed: " + e.getMessage());
        }
    }
}

