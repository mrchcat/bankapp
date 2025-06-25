package com.github.mrchcat.front.controller;

import com.github.mrchcat.front.dto.UserDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@RestController
public class TetsController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestClient.Builder builder;


    @Autowired
    private DiscoveryClient discoveryClient;

    String SERVICE_NAME="bankAccounts";

    @GetMapping("/get-data")
    public UserDetailsDto callServiceB() {
        System.out.println("внутри");
        return restTemplate.getForObject("http://bankAccounts/credentials/anna", UserDetailsDto.class);
    }

    @GetMapping("/get-data2")
    public UserDetailsDto callServiceB4() {
        Optional<URI> uri = discoveryClient.getInstances("bankAccounts")
                .stream()
                .findFirst()
                .map(ServiceInstance::getUri);
        System.out.println("вызываем=" + uri.get() + "/credentials/anna");
        return restTemplate.getForObject(uri.get() + "/credentials/anna", UserDetailsDto.class);
    }

    @GetMapping("/get-data3")
    public String callServiceB3() {
        Optional<URI> uri = discoveryClient.getInstances("bankAccounts")
                .stream()
                .findFirst()
                .map(ServiceInstance::getUri);
        if(uri.isEmpty()){
            return "пустой";
        }
        return "вызываем=" + uri.get() + "/credentials/anna";
    }

    @GetMapping("/get-data4")
    public String callServiceB434() {
        return "test";
    }

    @GetMapping("/get-data5")
    public UserDetailsDto callServiceBwew() {
        System.out.println("внутри");
        return builder.build()
                .get()
                .uri("http://bankAccounts/credentials/anna").retrieve()
                .body(UserDetailsDto.class);
    }

    @Autowired
    private OAuth2AuthorizedClientManager manager;

    private String getToken(String OAuthClientId) {
        return manager.authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId(OAuthClientId)
                        .principal("system")
                        .build())
                .getAccessToken()
                .getTokenValue();
    }



}
