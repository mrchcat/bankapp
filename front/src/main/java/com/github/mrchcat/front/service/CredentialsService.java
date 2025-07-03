package com.github.mrchcat.front.service;


import com.github.mrchcat.front.dto.PasswordUpdateRequestDto;
import com.github.mrchcat.front.dto.UserDetailsDto;
import com.github.mrchcat.front.mapper.FrontMapper;
import com.github.mrchcat.front.security.OAuthHeaderGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class CredentialsService implements UserDetailsService, UserDetailsPasswordService {
    String ACCOUNT_SERVICE = "bankAccounts";
    String ACCOUNTS_GET_USER_DETAILS_API = "/credentials";
    String ACCOUNTS_UPDATE_USER_PASSWORD_API = "/credentials";

    private final RestClient.Builder restClientBuilder;
    private final OAuthHeaderGetter oAuthHeaderGetter;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
            var response = restClientBuilder.build()
                    .get()
                    .uri("http://" + ACCOUNT_SERVICE + ACCOUNTS_GET_USER_DETAILS_API + "/" + username)
                    .header(oAuthHeader.name(), oAuthHeader.value())
                    .retrieve()
                    .body(UserDetailsDto.class);
            if (response == null) {
                throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
            }
//            System.out.println("получили response" + response);
//            System.out.println("получили UserDetails=" + FrontMapper.toUserDetails(response));
//            System.out.println("получили size=" + FrontMapper.toUserDetails(response).getAuthorities().size());
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

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        String passwordHash = encoder.encode(newPassword);
        try {
            var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
            var response = restClientBuilder.build()
                    .post()
                    .uri("http://" + ACCOUNT_SERVICE + ACCOUNTS_UPDATE_USER_PASSWORD_API + "/" + user.getUsername())
                    .header(oAuthHeader.name(), oAuthHeader.value())
                    .body(new PasswordUpdateRequestDto(passwordHash))
                    .retrieve()
                    .body(UserDetailsDto.class);
            if (response == null) {
                throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
            }
            System.out.println("Получили обновленный= " + FrontMapper.toUserDetails(response));
            return FrontMapper.toUserDetails(response);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new UsernameNotFoundException(user + "not found");
            }
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
