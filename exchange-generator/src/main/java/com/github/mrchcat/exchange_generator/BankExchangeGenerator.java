package com.github.mrchcat.exchange_generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BankExchangeGenerator {
    public static void main(String[] args) {
        SpringApplication.run(BankExchangeGenerator.class, args);
    }
}
