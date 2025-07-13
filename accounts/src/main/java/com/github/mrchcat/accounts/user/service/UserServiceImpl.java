package com.github.mrchcat.accounts.user.service;

import com.github.mrchcat.accounts.exceptions.UserNotUniqueProperties;
import com.github.mrchcat.accounts.security.OAuthHeaderGetter;
import com.github.mrchcat.accounts.user.mapper.UserMapper;
import com.github.mrchcat.accounts.user.model.BankUser;
import com.github.mrchcat.accounts.user.repository.UserRepository;
import com.github.mrchcat.shared.accounts.BankUserDto;
import com.github.mrchcat.shared.accounts.CreateNewClientDto;
import com.github.mrchcat.shared.accounts.EditUserAccountDto;
import com.github.mrchcat.shared.enums.UserRole;
import com.github.mrchcat.shared.notification.BankNotificationDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
    private final UserRepository userRepository;
    private final Notifications notifications;


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
    public UserDetails registerNewClient(CreateNewClientDto upDto) {
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
        try {
            notifications.sendNotification(newClient, message);
        } catch (Exception ignore) {
        }
        return getUserDetails(upDto.username());
    }

    @Override
    public BankUser getClient(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public void editClientData(String username, EditUserAccountDto dto) {
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
            try {
                notifications.sendNotification(client, message);
            } catch (Exception ignore) {
            }
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
}
