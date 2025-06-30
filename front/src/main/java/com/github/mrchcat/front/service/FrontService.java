package com.github.mrchcat.front.service;

import com.github.mrchcat.front.dto.BankUserDto;
import com.github.mrchcat.front.dto.EditUserAccountDto;
import com.github.mrchcat.front.dto.NewClientRegisterDto;
import jakarta.security.auth.message.AuthException;
import org.springframework.security.core.userdetails.UserDetails;

public interface FrontService {

    UserDetails editClientPassword(String username, String password);

    UserDetails registerNewClient(NewClientRegisterDto newClientRegisterDto) throws AuthException;

    BankUserDto getClientDetailsAndAccounts(String username);
    BankUserDto editUserAccount(String username, EditUserAccountDto editUserAccountDto) throws AuthException;

}
