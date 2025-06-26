package com.github.mrchcat.accounts.user.service;

import com.github.mrchcat.accounts.user.domain.BankUser;
import com.github.mrchcat.accounts.user.dto.BankNotificationDto;
import com.github.mrchcat.accounts.user.mapper.UserMapper;
import com.github.mrchcat.accounts.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
//    //    private final RestClient.Builder restClientBuilder;
//    private final RestClient restClient;
//    //    private String ACCOUNT_SERVICE = "bank_notifications";
//    private String ACCOUNT_SERVICE = "localhost:8082";
//    private final String NOTIFICATION_POST_MESSAGE = "/notification";
//    private final String CLIENT_REGISTRATION_ID = "bank_accounts";

    @Override
    public UserDetails getUserDetails(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public UserDetails updateUserDetails(String username, String password) {
        UUID userId = userRepository.findByUsername(username)
                .map(BankUser::getId)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        userRepository.updateUserPassword(userId, password)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return getUserDetails(username);
    }

    //    private void sendBankNotification(BankNotificationDto notification) {
//        try {
//            System.out.println("пытаемся отправить");
//            var responce = restClient
//                    .post()
//                    .uri("http://localhost:8082" + NOTIFICATION_POST_MESSAGE)
//                    .attributes(clientRegistrationId(CLIENT_REGISTRATION_ID))
//                    .body(notification)
//                    .retrieve()
//                    .body(UUID.class);
//            System.out.println("вернулся ответ=" + responce);
//        } catch (HttpClientErrorException ex) {
//            System.out.println("ошибка=" + ex.getMessage());
//        }
//    }
}
