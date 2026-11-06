package com.example.health_service.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy;

@Component
public class LifecycleLogger {
    private static final Logger log = LoggerFactory.getLogger(LifecycleLogger.class);

    @EventListener(ApplicationReadyEvent.class)
    public void onStart() {
        log.info("Application started");
    }

    @PreDestroy
    public void onStop() {
        log.info("Application stopping");
    }
}