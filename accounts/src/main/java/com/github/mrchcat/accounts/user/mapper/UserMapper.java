package com.github.mrchcat.accounts.user.mapper;

import com.github.mrchcat.accounts.user.model.BankUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class UserMapper {

    public static UserDetails toUserDetails(BankUser bankUser) {
        return User.builder()
                .username(bankUser.getUsername())
                .password(bankUser.getPassword())
                .authorities(bankUser.getRoles().split(";"))
                .disabled(!bankUser.isEnabled())
                .build();
    }
}
