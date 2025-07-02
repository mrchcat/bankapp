package com.github.mrchcat.front.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.security.SecureRandom;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/login", "/logout/**").permitAll()
                        .requestMatchers("/registration").hasAuthority("MANAGER")
                        .anyRequest().hasAnyAuthority("CLIENT", "MANAGER")
//                                .anyRequest().permitAll()
                )
                .oauth2Client(Customizer.withDefaults())
                .formLogin(cst -> cst
                        .defaultSuccessUrl("/defaultAfterLogin", true)
                )
                .logout(cst -> cst
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                );
        return http.build();
    }

//    @Bean
//    PasswordEncoder getEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }

    @Bean
    BCryptPasswordEncoder getEncoder() {
        int strength = 10;
        return new BCryptPasswordEncoder(strength, new SecureRandom());
    }

//    @Bean
//    public RestClient restClient(RestClient.Builder builder, OAuth2AuthorizedClientManager authorizedClientManager) {
//        OAuth2ClientHttpRequestInterceptor requestInterceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
//        return builder.requestInterceptor(requestInterceptor).build();
//    }

//    @Bean
//    @Primary
//    public RestClient.Builder restClientBuilder(RestClientBuilderConfigurer configurer,
//                                                OAuth2AuthorizedClientManager authorizedClientManager) {
//        var requestInterceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
//        return configurer.configure(RestClient.builder()).requestInterceptor(requestInterceptor);
//    }

    @LoadBalanced
    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    String CLIENT_REGISTRATION_ID = "bank_front";

    HttpHeaders authHeaders(OAuth2AuthorizedClientManager authorizedClientManager) {
        String token = authorizedClientManager.authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId(CLIENT_REGISTRATION_ID)
                        .principal("system")
                        .build())
                .getAccessToken()
                .getTokenValue();
        return new HttpHeaders(MultiValueMap
                .fromSingleValue(Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + token)));

    }

}
