package com.github.mrchcat.accounts.account.service;

import com.github.mrchcat.accounts.account.dto.AccountListDto;
import com.github.mrchcat.accounts.account.dto.BankCurrencyListDto;
import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.accounts.account.repository.AccountRepository;
import com.github.mrchcat.accounts.user.dto.BankUserDto;
import com.github.mrchcat.accounts.user.mapper.UserMapper;
import com.github.mrchcat.accounts.user.model.BankUser;
import com.github.mrchcat.accounts.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;


    @Override
    public List<Account> findAllActiveAccountsByUser(UUID userId) {
        return accountRepository.findAllActiveAccountsByUser(userId);
    }

    @Override
    public BankUserDto getClient(String username) {
        BankUser client = userService.getClient(username);
        List<Account> accounts = findAllActiveAccountsByUser(client.getId());
        return UserMapper.toDto(client, accounts);
    }

    @Override
    public BankUserDto editClientAccounts(String username, EditUserAccountDto editUserAccountDto) {
        return null;
    }

//    @Override
//    public BankUserDto createAccounts(String username, BankCurrencyListDto bankCurrencyListDto) {
//        return ;
//    }
//
//    @Override
//    public void deleteAccounts(AccountListDto accountListDto) {
//        accountRepository.deactivateEmptyAccounts(accountListDto.accountsId());
//    }
}
