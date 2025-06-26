package com.github.mrchcat.accounts.user.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

public interface UserService {

    UserDetails getUserDetails(String username);

    UserDetails updateUserDetails(String username, String passwordHash);
}
