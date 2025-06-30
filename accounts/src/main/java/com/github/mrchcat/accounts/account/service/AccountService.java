package com.github.mrchcat.accounts.account.service;

import com.github.mrchcat.accounts.account.dto.AccountListDto;
import com.github.mrchcat.accounts.account.dto.BankCurrencyListDto;
import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.accounts.user.dto.BankUserDto;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    List<Account> findAllActiveAccountsByUser(UUID userId);

    BankUserDto getClient(String username);

    BankUserDto editClientAccounts(String username, EditUserAccountDto editUserAccountDto);
}
