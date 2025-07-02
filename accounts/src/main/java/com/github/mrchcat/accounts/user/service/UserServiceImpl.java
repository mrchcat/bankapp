package com.github.mrchcat.accounts.user.service;

import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.exceptions.UserNotUniqueProperties;
import com.github.mrchcat.accounts.user.dto.CreateNewClientDto;
import com.github.mrchcat.accounts.user.mapper.UserMapper;
import com.github.mrchcat.accounts.user.model.BankUser;
import com.github.mrchcat.accounts.user.model.UserRole;
import com.github.mrchcat.accounts.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
