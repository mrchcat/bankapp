package com.github.mrchcat.front.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class Config {

    @Bean
    RestClient getRestClient(){
        return RestClient.create();
    }

}
