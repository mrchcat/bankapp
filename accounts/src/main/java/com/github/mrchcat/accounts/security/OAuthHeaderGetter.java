package com.github.mrchcat.accounts.security;


import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthHeaderGetter {
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private final String CLIENT_REGISTRATION_ID = "bank_accounts";

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
}
