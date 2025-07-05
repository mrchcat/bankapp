package com.github.mrchcat.accounts.user.service;

import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.user.dto.BankUserDto;
import com.github.mrchcat.accounts.user.dto.CreateNewClientDto;
import com.github.mrchcat.accounts.user.model.BankUser;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {

    UserDetails getUserDetails(String username);

    UserDetails updateUserDetails(String username, String passwordHash);

    UserDetails registerNewClient(CreateNewClientDto newClientDto);

    BankUser getClient(String username);

    List<BankUserDto> getAllActiveClients();

    void editClientData(String username, EditUserAccountDto editUserAccountDto);
}
