package com.github.mrchcat.accounts.user.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    UserDetails getUserDetails(String username);
}
