package com.github.mrchcat.accounts.account.repository;

import com.github.mrchcat.accounts.account.model.Account;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    List<Account> findAllActiveAccountsByUser(UUID userId);

//    Optional<Account> findAccountById(UUID accountId);

//    void deactivateEmptyAccounts(List<UUID> accountsId);
//
//    void createOrActivateNewAccounts(String username);
}
