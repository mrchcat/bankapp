package com.github.mrchcat.accounts.user.service;

import com.github.mrchcat.accounts.user.mapper.UserMapper;
import com.github.mrchcat.accounts.user.repository.UserRepository;
import com.github.mrchcat.notifications.domain.BankNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RestClient restClient;
    @Value("${application.bank_notifications_url}")
    private  String ACCOUNT_SERVICE_URL;
    private final String NOTIFICATION_POST_MESSAGE = "/notification";
    private final String CLIENT_REGISTRATION_ID = "bank_accounts";

    @Override
    public UserDetails getUserDetails(String username) {
        BankNotification notification = BankNotification.builder()
                .id(UUID.randomUUID())
                .email("anna@mail.ru")
                .fullName("Anna")
                .message("какое то сообщение")
                .build();
        sendBankNotification(notification);
        return userRepository.findByUsername(username)
                .map(UserMapper::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private void sendBankNotification(BankNotification notification) {
        try {
            System.out.println("пытаемся отправить");
            var responce = restClient.post()
                    .uri(ACCOUNT_SERVICE_URL + NOTIFICATION_POST_MESSAGE)
                    .attributes(clientRegistrationId(CLIENT_REGISTRATION_ID))
                    .body(notification)
                    .retrieve()
                    .body(UUID.class);
            System.out.println("вернулся ответ=" + responce);
        } catch (HttpClientErrorException ex) {
            System.out.println("ошибка=" + ex.getMessage());
        }
    }
}
