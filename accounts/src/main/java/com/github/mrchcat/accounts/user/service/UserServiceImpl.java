package com.github.mrchcat.accounts.user.service;

import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.exceptions.UserNotUniqueProperties;
import com.github.mrchcat.accounts.security.OAuthHeaderGetter;
import com.github.mrchcat.accounts.user.dto.BankNotificationDtoRequest;
import com.github.mrchcat.accounts.user.dto.BankUserDto;
import com.github.mrchcat.accounts.user.dto.CreateNewClientDto;
import com.github.mrchcat.accounts.user.mapper.UserMapper;
import com.github.mrchcat.accounts.user.model.BankUser;
import com.github.mrchcat.accounts.user.model.UserRole;
import com.github.mrchcat.accounts.user.repository.UserRepository;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final String NOTIFICATION_SERVICE = "bankNotifications";
    private final String NOTIFICATION_SEND_NOTIFICATION = "/notification";
    private final String ACCOUNT_SERVICE = "bankAccounts";

    private final RestClient.Builder restClientBuilder;
    private final OAuthHeaderGetter oAuthHeaderGetter;
    private final UserRepository userRepository;


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

    @Override
    public UserDetails registerNewClient(CreateNewClientDto upDto) throws AuthException {
        validateIfClientPropertiesExistAlready(upDto);
        BankUser newClient = BankUser.builder()
                .fullName(upDto.fullName())
                .birthDay(upDto.birthDay())
                .email(upDto.email())
                .username(upDto.username())
                .password(upDto.password())
                .roles(UserRole.CLIENT.roleName)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(newClient);
        String message = "Зарегистрирован новый клиент ФИО: " + newClient.getFullName();
        sendNotification(newClient, message);
        return getUserDetails(upDto.username());
    }

    @Override
    public BankUser getClient(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public void editClientData(String username, EditUserAccountDto dto) throws AuthException {
        BankUser client = getClient(username);
        boolean hasNewProperties = false;
        String newEmail = dto.email();
        if (newEmail != null && !newEmail.isBlank()) {
            newEmail = newEmail.trim();
            if (!newEmail.equals(client.getEmail())) {
                validateIfEmailAlreadyExists(newEmail);
                client.setEmail(newEmail);
                hasNewProperties = true;
            }
        }
        String newFullName = dto.fullName();
        if (newFullName != null && !newFullName.isBlank()) {
            client.setFullName(newFullName.trim());
            hasNewProperties = true;
        }
        if (hasNewProperties) {
            client.setUpdatedAt(LocalDateTime.now());
            userRepository.save(client);
            String message = "Клиент с username " + client.getUsername() + " обновил свои данные";
            sendNotification(client, message);
        }
    }

    private void validateIfEmailAlreadyExists(String email) {
        if (userRepository.isEmailExists(email)) {
            throw new UserNotUniqueProperties(List.of("email"));
        }
    }

    private void validateIfClientPropertiesExistAlready(CreateNewClientDto upDto) {
        List<String> propertyUniquenessCheckResult = new ArrayList<>();
        String username = upDto.username();
        if (username != null && userRepository.findByUsername(username).isPresent()) {
            propertyUniquenessCheckResult.add("username");
        }
        String email = upDto.username();
        if (email != null && userRepository.isEmailExists(email)) {
            propertyUniquenessCheckResult.add("email");
        }
        if (!propertyUniquenessCheckResult.isEmpty()) {
            throw new UserNotUniqueProperties(propertyUniquenessCheckResult);
        }
    }

    @Override
    public List<BankUserDto> getAllActiveClients() {
        return UserMapper.toDto(userRepository.findAllActive());
    }


    private void sendNotification(BankUser client, String message) {
        try {
            var notification = BankNotificationDtoRequest.builder()
                    .service(ACCOUNT_SERVICE)
                    .username(client.getUsername())
                    .fullName(client.getFullName())
                    .email(client.getEmail())
                    .message(message)
                    .build();
            var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
            String requestUrl = "http://" + NOTIFICATION_SERVICE + NOTIFICATION_SEND_NOTIFICATION;
            restClientBuilder.build()
                    .post()
                    .uri(requestUrl)
                    .header(oAuthHeader.name(), oAuthHeader.value())
                    .body(notification)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignore) {
        }
    }

}
