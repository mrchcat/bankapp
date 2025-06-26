package com.github.mrchcat.front.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FrontServiceImpl implements FrontService{
    private final UserDetailsPasswordService userDetailsPasswordService;
    private final UserDetailsService userDetailsService;

    @Override
    public UserDetails editClientPassword(String username, String password) {
            UserDetails userDetails=userDetailsService.loadUserByUsername(username);
            return userDetailsPasswordService.updatePassword(userDetails,password);
    }
}
