package com.github.mrchcat.accounts.user.service;

import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.user.dto.CreateNewClientDto;
import com.github.mrchcat.accounts.user.model.BankUser;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    UserDetails getUserDetails(String username);

    UserDetails updateUserDetails(String username, String passwordHash);

    UserDetails registerNewClient(CreateNewClientDto newClientDto);

    BankUser getClient(String username);

    void editClientData(String username, EditUserAccountDto editUserAccountDto);
}
