package com.github.mrchcat.front.config;

import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RetryRegistryEventListener {
    @Autowired
    private RetryRegistry registry;


    @PostConstruct
    public void postConstruct() {
        //registry.retry(<resilience retry instance name>)
        registry.retry("accounts").getEventPublisher()
                .onRetry(ev -> log.info("#### RetryRegistryEventListener message: {}", ev));
    }
}
