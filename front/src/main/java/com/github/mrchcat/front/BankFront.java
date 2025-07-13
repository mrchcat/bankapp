package com.github.mrchcat.front;

import net.minidev.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BankFront {

    public static void main(String[] args) {
        SpringApplication.run(BankFront.class, args);
    }
}
