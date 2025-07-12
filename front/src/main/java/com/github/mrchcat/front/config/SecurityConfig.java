package com.github.mrchcat.front.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/login", "/logout/**").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/registration", "/signup").hasAuthority("MANAGER")
                                .anyRequest().hasAnyAuthority("CLIENT", "MANAGER")
//                                .anyRequest().authenticated()
//                                .anyRequest().permitAll()
                )
                .oauth2Client(Customizer.withDefaults())
                .formLogin(cst -> cst
                        .defaultSuccessUrl("/defaultAfterLogin", true)
                        .failureUrl("/error.html")
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

    @Bean
    BCryptPasswordEncoder getEncoder() {
        int strength = 10;
        return new BCryptPasswordEncoder(strength, new SecureRandom());
    }

    @LoadBalanced
    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
