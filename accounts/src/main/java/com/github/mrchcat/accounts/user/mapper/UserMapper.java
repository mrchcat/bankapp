package com.github.mrchcat.accounts.user.mapper;

import com.github.mrchcat.accounts.account.mapper.AccountMapper;
import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.accounts.user.dto.BankUserDto;
import com.github.mrchcat.accounts.user.model.BankUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class UserMapper {

    public static UserDetails toUserDetails(BankUser bankUser) {
        return User.builder()
                .username(bankUser.getUsername())
                .password(bankUser.getPassword())
                .authorities(bankUser.getRoles().split(";"))
                .disabled(!bankUser.isEnabled())
                .build();
    }

    public static BankUserDto toDto(BankUser bankUser, List<Account> accounts) {
        return BankUserDto.builder()
                .fullName(bankUser.getFullName())
                .birthDay(bankUser.getBirthDay())
                .email(bankUser.getEmail())
                .accounts(AccountMapper.toDto(accounts))
                .build();
    }
}
