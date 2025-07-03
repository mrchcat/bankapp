package com.github.mrchcat.accounts.account.service;

import com.github.mrchcat.accounts.account.dto.CashTransactionDto;
import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.account.dto.TransactionConfirmation;
import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.accounts.account.model.BankCurrency;
import com.github.mrchcat.accounts.account.repository.AccountRepository;
import com.github.mrchcat.accounts.exceptions.TransactionWasCompletedAlready;
import com.github.mrchcat.accounts.log.model.TransactionLog;
import com.github.mrchcat.accounts.log.repository.LogRepository;
import com.github.mrchcat.accounts.user.dto.BankUserDto;
import com.github.mrchcat.accounts.user.mapper.UserMapper;
import com.github.mrchcat.accounts.user.model.BankUser;
import com.github.mrchcat.accounts.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final LogRepository logRepository;

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
    @Transactional
    public TransactionConfirmation processCashTransaction(CashTransactionDto cashTransactionDto) {
        System.out.println("зашли processCashTransaction" + cashTransactionDto);
        validateCashTransaction(cashTransactionDto);
        return switch (cashTransactionDto.action()) {
            case DEPOSIT -> processCashDeposit(cashTransactionDto);
            case WITHDRAWAL -> null;
            default ->
                    throw new UnsupportedOperationException("Ошибка: операция не поддерживается" + cashTransactionDto.action().name());
        };
    }

    private void validateCashTransaction(CashTransactionDto cashTransactionDto) {
        UUID transactionId = cashTransactionDto.transactionId();
        if (logRepository.existsByTransactionId(transactionId)) {
            throw new TransactionWasCompletedAlready(transactionId.toString());
        }
        UUID accountId = cashTransactionDto.accountId();
        if (!accountRepository.isExistActive(accountId)) {
            throw new NoSuchElementException(accountId.toString());
        }
    }

    private TransactionConfirmation processCashDeposit(CashTransactionDto cashTransactionDto) {
        System.out.println("зашли processCashTransaction" + cashTransactionDto);
        accountRepository.changeBalance(cashTransactionDto.accountId(), cashTransactionDto.amount());
        System.out.println("обновили баланс");
        logCashWithdrawal(cashTransactionDto);
        System.out.println("записали лог");
        return new TransactionConfirmation(cashTransactionDto.transactionId(), cashTransactionDto.status());
    }

    private void logCashWithdrawal(CashTransactionDto cashTransactionDto) {
        System.out.println("зашли в логирование");
        TransactionLog logRecord = TransactionLog.builder()
                .transactionId(cashTransactionDto.transactionId())
                .transactionType(cashTransactionDto.action().name())
                .fromAccountId(null)
                .toAccountId(cashTransactionDto.accountId())
                .amountFrom(null)
                .amountTo(cashTransactionDto.amount())
                .build();
        logRepository.save(logRecord);
    }


}
