package com.mycloud.server.config;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheEvictionScheduler {

    @CacheEvict(value = "sensorHistory", allEntries = true)
    @Scheduled(fixedDelay = 60000) // evict every 60 seconds
    public void evictSensorHistoryCache() {
    }
}