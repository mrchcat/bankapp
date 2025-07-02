package com.github.mrchcat.accounts.account.repository;

import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.accounts.account.model.BankCurrency;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    List<Account> findAllActiveAccountsByUser(UUID userId);

    List<Account> findAllActiveAccountsByUser(UUID userId, BankCurrency currency);

    List<Account> findAllAccountsByUser(UUID userId);

    void setAccountActivation(UUID accountId, boolean isActive);

    void createNewAccount(Account account);

//    Optional<Account> findAccountById(UUID accountId);

//    void deactivateEmptyAccounts(List<UUID> accountsId);
//
//    void createOrActivateNewAccounts(String username);
}
