package com.github.mrchcat.exchange_generator;

import com.github.mrchcat.exchange_generator.service.GeneratorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BankExchangeGenerator {
    public static void main(String[] args) {
        var context=SpringApplication.run(BankExchangeGenerator.class, args);
//        context.getBean(GeneratorService.class).sendNewRates();
    }
}
