package com.mycloud.server.config;

import com.mycloud.server.service.MqttSubscriberService;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// https://www.baeldung.com/java-mqtt-client
@Configuration
@RequiredArgsConstructor
public class MqttConfig {
    @Value("${mqtt.broker:tcp://mosquitto:1883}")
    private String brokerurl;

    private final MqttSubscriberService subscriberService;

    @Bean
    @ConditionalOnProperty(name = "mqtt.enabled", havingValue = "true", matchIfMissing = true) // this property was added souly for the development and runtimt testing on windows, where mqtt is not present
    MqttClient mqttClient() throws MqttException {
        MqttClient client = new MqttClient(brokerurl, "spring-boot-subscriber", new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);

        client.connect(options);

        client.subscribe("mycloud/sensors/bme280", 1, subscriberService::onSensorMessage);
        client.subscribe("mycloud/alerts/movement",1, subscriberService::onMovementAlert);

        System.out.println("MQTT client connected and subscribed.");
        return client;
    }
}
