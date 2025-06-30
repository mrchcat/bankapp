package com.github.mrchcat.front.service;

import com.github.mrchcat.front.dto.BankUserDto;
import com.github.mrchcat.front.dto.EditUserAccountDto;
import com.github.mrchcat.front.dto.NewClientRegisterDto;
import com.github.mrchcat.front.dto.UserDetailsDto;
import com.github.mrchcat.front.mapper.FrontMapper;
import com.github.mrchcat.front.security.OAuthHeaderGetter;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class FrontServiceImpl implements FrontService {
    String ACCOUNT_SERVICE = "bankAccounts";
    String ACCOUNTS_REGISTER_NEW_CLIENT_API = "/registration";
    String ACCOUNTS_GET_CLIENT_API = "/account";
    String ACCOUNTS_PATCH_CLIENT_API = "/account";


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
        String passwordHash = encoder.encode(ncrdto.password());
        var newClientRequestDto = FrontMapper.toCreateNewClientRequestDto(ncrdto, passwordHash);
        System.out.println("отправляем на сохранение " + newClientRequestDto);
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        var response = restClientBuilder.build()
                .post()
                .uri("http://" + ACCOUNT_SERVICE + ACCOUNTS_REGISTER_NEW_CLIENT_API)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(newClientRequestDto)
                .retrieve()
                .body(UserDetailsDto.class);
        if (response == null) {
            throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
        }
        return FrontMapper.toUserDetails(response);
    }

    @Override
    @SneakyThrows
    public BankUserDto getClientDetailsAndAccounts(String username) {
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        var response = restClientBuilder.build()
                .get()
                .uri("http://" + ACCOUNT_SERVICE + ACCOUNTS_GET_CLIENT_API + "/" + username)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .retrieve()
                .body(BankUserDto.class);
        if (response == null) {
            throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
        }
        return response;
    }

    @Override
    public BankUserDto editUserAccount(String username, EditUserAccountDto editUserAccountDto) throws AuthException {
        System.out.println("Зашли в editUserAccount");
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        var response = restClientBuilder.build()
                .patch()
                .uri("http://" + ACCOUNT_SERVICE + ACCOUNTS_PATCH_CLIENT_API + "/" + username)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(editUserAccountDto)
                .retrieve()
                .body(BankUserDto.class);
        System.out.println("получили response response");
        if (response == null) {
            throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
        }
        return response;
    }
}
