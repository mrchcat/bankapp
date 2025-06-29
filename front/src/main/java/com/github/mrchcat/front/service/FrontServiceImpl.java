package com.github.mrchcat.front.service;

import com.github.mrchcat.front.dto.CreateNewClientRequestDto;
import com.github.mrchcat.front.dto.NewClientRegisterDto;
import com.github.mrchcat.front.dto.UserDetailsDto;
import com.github.mrchcat.front.mapper.FrontMapper;
import com.github.mrchcat.front.security.OAuthHeaderGetter;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.w3c.dom.ls.LSOutput;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FrontServiceImpl implements FrontService {
    String ACCOUNT_SERVICE = "bankAccounts";
    String ACCOUNTS_REGISTER_NEW_CLIENT_API = "/registration";

    private final UserDetailsPasswordService userDetailsPasswordService;
    private final UserDetailsService userDetailsService;
    private final RestClient.Builder restClientBuilder;
    private final OAuthHeaderGetter oAuthHeaderGetter;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails editClientPassword(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return userDetailsPasswordService.updatePassword(userDetails, password);
    }

    @Override
    public UserDetails registerNewClient(NewClientRegisterDto ncrdto) throws AuthException {
        System.out.println("приступили к регистрации");
        String passwordHash = encoder.encode(ncrdto.password());
        var newClientRequestDto = FrontMapper.toCreateNewClientRequestDto(ncrdto, passwordHash);
        System.out.println("отправляем " + newClientRequestDto);
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        var response = restClientBuilder.build()
                .post()
                .uri("http://" + ACCOUNT_SERVICE + ACCOUNTS_REGISTER_NEW_CLIENT_API)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(newClientRequestDto)
                .retrieve()
                .body(UserDetailsDto.class);
        System.out.println("вернулся " + response);
        if (response == null) {
            throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
        }
        return FrontMapper.toUserDetails(response);
    }
}
