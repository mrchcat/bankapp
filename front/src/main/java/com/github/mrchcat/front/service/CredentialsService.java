package com.github.mrchcat.front.service;


import com.github.mrchcat.front.dto.UserDetailsDto;
import com.github.mrchcat.front.mapper.FrontMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Service
@RequiredArgsConstructor
public class CredentialsService implements UserDetailsService {
    String ACCOUNT_SERVICE = "bankAccounts";
    //    @Value("${application.bank_accounts_url}")
    String CLIENT_REGISTRATION_ID = "bank_front";
    String ACCOUNTS_GET_USER_DETAILS_API = "/credentials";

    private final RestClient.Builder restClientBuilder;
    private final OAuth2AuthorizedClientManager manager;


    //    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        System.out.println("поиск");
//        try {
////            var response = restClientBuilder.build()
//            var response = restClient
//                    .get()
//                    .uri("http://" + "localhost:8081" + ACCOUNTS_GET_USER_DETAILS_API + "/" + username)
////                    .uri("http://bankAccounts/credentials/" + username)
//                    .attributes(clientRegistrationId(CLIENT_REGISTRATION_ID))

    /// /                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken(CLIENT_REGISTRATION_ID))
//                    .retrieve()
//                    .body(UserDetailsDto.class);
//            if (response == null) {
//                throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
//            }
//            return FrontMapper.toUserDetails(response);
//        } catch (HttpClientErrorException ex) {
//            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
//                throw new UsernameNotFoundException(username + "not found");
//            }
//            throw ex;
//        }
//    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            var response = restClientBuilder.build()
                    .get()
                    .uri("http://" + "bankAccounts" + ACCOUNTS_GET_USER_DETAILS_API + "/" + username)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken(CLIENT_REGISTRATION_ID))
                    .retrieve()
                    .body(UserDetailsDto.class);
            if (response == null) {
                throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
            }
            return FrontMapper.toUserDetails(response);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new UsernameNotFoundException(username + "not found");
            }
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//@Override
//public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//    try {
//        var response = restClientBuilder.build()
//                .get()
//                .uri("http://" + "bankAccounts" + ACCOUNTS_GET_USER_DETAILS_API + "/" + username)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken(CLIENT_REGISTRATION_ID))
//                .retrieve()
//                .body(UserDetailsDto.class);
//        if (response == null) {
//            throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
//        }
//        return FrontMapper.toUserDetails(response);
//    } catch (HttpClientErrorException ex) {
//        if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
//            throw new UsernameNotFoundException(username + "not found");
//        }
//        throw ex;
//    }
//}
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        String uri = "http://bankAccounts/credentials/" + username;
//        try {
//            var response = restTemplate.getForObject(uri, UserDetailsDto.class);
//            if (response == null) {
//                throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
//            }
//            return FrontMapper.toUserDetails(response);
//        } catch (HttpClientErrorException ex) {
//            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
//                throw new UsernameNotFoundException(username + "not found");
//            }
//            throw ex;
//        }
//    }


    private String getToken(String OAuthClientId) {
        return manager.authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId(OAuthClientId)
                        .principal("system")
                        .build())
                .getAccessToken()
                .getTokenValue();
    }

}
