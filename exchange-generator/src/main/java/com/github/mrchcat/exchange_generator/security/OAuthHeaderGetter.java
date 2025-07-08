package com.github.mrchcat.exchange_generator.security;


import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthHeaderGetter {
    private final AuthorizedClientServiceOAuth2AuthorizedClientManager manager;

    String CLIENT_REGISTRATION_ID = "bank_exchange_generator";

    public OAuthHeader getOAuthHeader() throws AuthException {

        try{
            var token = manager.authorize(OAuth2AuthorizeRequest
                    .withClientRegistrationId(CLIENT_REGISTRATION_ID)
                    .principal("system")
                    .build());
            System.out.println("токен="+token);
            if (token == null) {
                throw new AuthException("OAuth token is absent");
            }
            return new OAuthHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken().getTokenValue());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
