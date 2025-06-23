package com.github.mrchcat.accounts.user.controllers;

import com.github.mrchcat.accounts.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/credentials/{username}")
    UserDetails getUserDenails(@PathVariable String username) {
        return userService.getUserDetails(username);
    }
}
