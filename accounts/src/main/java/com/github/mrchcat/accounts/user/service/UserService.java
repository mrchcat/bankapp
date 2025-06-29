package com.github.mrchcat.accounts.user.service;

import com.github.mrchcat.accounts.user.dto.CreateNewClientDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface UserService {

    UserDetails getUserDetails(String username);

    UserDetails updateUserDetails(String username, String passwordHash);

    UserDetails registerNewClient(CreateNewClientDto newClientDto);
}
