package com.github.mrchcat.front.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface FrontService {

    UserDetails editClientPassword(String username, String password);
}
