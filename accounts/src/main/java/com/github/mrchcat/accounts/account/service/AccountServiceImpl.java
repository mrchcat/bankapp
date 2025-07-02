package com.github.mrchcat.accounts.account.service;

import com.github.mrchcat.accounts.account.dto.CashTransactionDto;
import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.account.dto.TransactionConfirmation;
import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.accounts.account.model.BankCurrency;
import com.github.mrchcat.accounts.account.repository.AccountRepository;
import com.github.mrchcat.accounts.user.dto.BankUserDto;
import com.github.mrchcat.accounts.user.mapper.UserMapper;
import com.github.mrchcat.accounts.user.model.BankUser;
import com.github.mrchcat.accounts.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;

    @Override
    public BankUserDto getClient(String username, BankCurrency currency) {
        BankUser client = userService.getClient(username);
        List<Account> accounts = accountRepository.findAllActiveAccountsByUser(client.getId(), currency);
        return UserMapper.toDto(client, accounts);
    }

    @Override
    public BankUserDto getClient(String username) {
        BankUser client = userService.getClient(username);
        List<Account> accounts = accountRepository.findAllActiveAccountsByUser(client.getId());
        return UserMapper.toDto(client, accounts);
    }

    @Override
    public void editClientAccounts(String username, EditUserAccountDto editUserAccountDto) {
        System.out.println();
        Map<String, Boolean> mapOfCurrenciesToUpdateAccounts = editUserAccountDto.accounts();
        if (mapOfCurrenciesToUpdateAccounts == null || mapOfCurrenciesToUpdateAccounts.isEmpty()) {
            return;
        }
        HashSet<String> processed = new HashSet<>(mapOfCurrenciesToUpdateAccounts.size());

        UUID clientId = userService.getClient(username).getId();

        List<Account> allCurrentAccounts = accountRepository.findAllAccountsByUser(clientId);
        for (Account currentAccount : allCurrentAccounts) {
            String currencyStringCode = currentAccount.getCurrency().name();
            if (mapOfCurrenciesToUpdateAccounts.containsKey(currencyStringCode)) {
                boolean isActivated = currentAccount.isActive();
                boolean updateIsActivated = mapOfCurrenciesToUpdateAccounts.get(currencyStringCode);
                if (isActivated != updateIsActivated) {
                    boolean isBalanceEmpty = currentAccount.getBalance().compareTo(BigDecimal.ZERO) == 0;
                    if (!isActivated || isBalanceEmpty) {
                        accountRepository.setAccountActivation(currentAccount.getId(), updateIsActivated);
                    }
                }
                processed.add(currencyStringCode);
            }
        }
        for (String processedCurrencyStringCode : processed.stream().toList()) {
            mapOfCurrenciesToUpdateAccounts.remove(processedCurrencyStringCode);
        }
        for (var entry : mapOfCurrenciesToUpdateAccounts.entrySet()) {
            if (entry.getValue()) {
                BankCurrency currencyForNewAccount = BankCurrency.valueOf(entry.getKey());
                createNewAccount(clientId, currencyForNewAccount);
            }
        }
    }

    private void createNewAccount(UUID clientId, BankCurrency currencyForNewAccount) {
        Account newAccount = Account.builder()
                .number("some bank number")
                .currency(currencyForNewAccount)
                .userId(clientId)
                .build();
        accountRepository.createNewAccount(newAccount);
    }

    @Override
    public TransactionConfirmation processCashTransaction(CashTransactionDto cashTransactionDto) {
        return new TransactionConfirmation(cashTransactionDto.transactionId(), cashTransactionDto.status());
    }
}
