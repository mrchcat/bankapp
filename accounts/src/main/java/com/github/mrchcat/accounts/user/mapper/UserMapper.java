package com.github.mrchcat.accounts.user.mapper;

import com.github.mrchcat.accounts.account.mapper.AccountMapper;
import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.accounts.user.model.BankUser;
import com.github.mrchcat.shared.accounts.BankUserDto;
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
                .id(bankUser.getId())
                .username(bankUser.getUsername())
                .fullName(bankUser.getFullName())
                .birthDay(bankUser.getBirthDay())
                .email(bankUser.getEmail())
                .accounts(AccountMapper.toDto(accounts))
                .build();
    }

    public static BankUserDto toDto(BankUser bankUser) {
        return BankUserDto.builder()
                .id(bankUser.getId())
                .username(bankUser.getUsername())
                .fullName(bankUser.getFullName())
                .birthDay(bankUser.getBirthDay())
                .email(bankUser.getEmail())
                .accounts(null)
                .build();
    }

    public static List<BankUserDto> toDto(List<BankUser> bankUsers) {
        return bankUsers.stream()
                .map(UserMapper::toDto)
                .toList();
    }

}
