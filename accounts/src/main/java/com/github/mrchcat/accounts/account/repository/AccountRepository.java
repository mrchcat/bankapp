package com.github.mrchcat.accounts.account.repository;

import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.shared.enums.BankCurrency;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    List<Account> findAllActiveAccountsByUser(UUID userId);

    List<Account> findAllActiveAccountsByUser(UUID userId, BankCurrency currency);

    List<Account> findAllAccountsByUser(UUID userId);

    Optional<Account> findActiveAccountById(UUID accountId);

    void setAccountActivation(UUID accountId, boolean isActive);

    void createNewAccount(Account account);

    void changeBalance(UUID accountId, BigDecimal amount);

    Boolean isExistActive(UUID accountId);

    Optional<BigDecimal> getBalance(UUID accountId);

}
