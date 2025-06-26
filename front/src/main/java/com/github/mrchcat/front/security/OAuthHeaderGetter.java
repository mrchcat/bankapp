package com.github.mrchcat.front.security;

import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthHeaderGetter {
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    //    @Value("${spring.security.oauth2.registration}")
    String CLIENT_REGISTRATION_ID = "bank_front";


    public OAuthHeader getOAuthHeader() throws AuthException {

        var token = authorizedClientManager.authorize(OAuth2AuthorizeRequest
                .withClientRegistrationId(CLIENT_REGISTRATION_ID)
                .principal("system")
                .build());
        if (token == null) {
            throw new AuthException("OAuth token is absent");
        }
        return new OAuthHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken().getTokenValue());
    }

//    public OAuthHeader getOAuthHeader() throws AuthException {
//        var token = authorizedClient.getAccessToken();
//        service.
//
//        if (token == null) {
//            throw new AuthException("OAuth token is absent");
//        }
//        return new OAuthHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue());
//    }
}
