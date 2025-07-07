package com.github.mrchcat.accounts.account.service;

import com.github.mrchcat.accounts.account.dto.CashTransactionDto;
import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.account.dto.TransactionConfirmation;
import com.github.mrchcat.accounts.account.dto.TransferTransactionDto;
import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.accounts.account.model.BankCurrency;
import com.github.mrchcat.accounts.account.model.TransactionStatus;
import com.github.mrchcat.accounts.account.repository.AccountRepository;
import com.github.mrchcat.accounts.blocks.model.AccountBlock;
import com.github.mrchcat.accounts.blocks.service.AccountBlockService;
import com.github.mrchcat.accounts.exceptions.NotEnoughMoney;
import com.github.mrchcat.accounts.exceptions.TransactionWasCompletedAlready;
import com.github.mrchcat.accounts.log.mapper.LogMapper;
import com.github.mrchcat.accounts.log.service.LogService;
import com.github.mrchcat.accounts.user.dto.BankUserDto;
import com.github.mrchcat.accounts.user.mapper.UserMapper;
import com.github.mrchcat.accounts.user.model.BankUser;
import com.github.mrchcat.accounts.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
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
    private final LogService logService;
    private final AccountBlockService accountBlockService;

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
        System.out.println("зашли processCashTransaction" + cashTransactionDto);
        validateCashTransaction(cashTransactionDto);
        return switch (cashTransactionDto.action()) {
            case DEPOSIT -> processCashDeposit(cashTransactionDto);
            case WITHDRAWAL -> processCashWithdrawal(cashTransactionDto);
            case TRANSFER -> throw new UnsupportedOperationException();
        };
    }

    private final static Map<TransactionStatus, List<TransactionStatus>> cashDepositStatusChain = new HashMap<>();
    private final static Map<TransactionStatus, List<TransactionStatus>> cashWithdrawStatusChain = new HashMap<>();

    static {
        cashDepositStatusChain.put(TransactionStatus.CASH_RECEIVED, Collections.emptyList());
        cashDepositStatusChain.put(TransactionStatus.ERROR, List.of(TransactionStatus.CASH_RECEIVED));
        cashDepositStatusChain.put(TransactionStatus.SUCCESS, List.of(TransactionStatus.CASH_RECEIVED));

        cashWithdrawStatusChain.put(TransactionStatus.BLOCKING_REQUEST, Collections.emptyList());
        cashDepositStatusChain.put(TransactionStatus.CANCEL, List.of(TransactionStatus.BLOCKED));
        cashWithdrawStatusChain.put(TransactionStatus.CASH_WAS_GIVEN, List.of(TransactionStatus.BLOCKING_REQUEST));
        cashWithdrawStatusChain.put(TransactionStatus.SUCCESS,
                List.of(TransactionStatus.DEPOSIT_PROCESSED, TransactionStatus.CASH_WAS_GIVEN));
    }

    private void validateCashTransaction(CashTransactionDto cashTransactionDto) {
        System.out.println("зашли в validateCashTransaction " + cashTransactionDto);
        // отклоняем операцию, если аккаунт не активен или не существует
        UUID accountId = cashTransactionDto.accountId();
        if (!accountRepository.isExistActive(accountId)) {
            System.out.println("validateCashTransaction нет аккаунта");
            throw new NoSuchElementException(accountId.toString());
        }
        // отклоняем операции, которые уже ранее были обработаны
        UUID transactionId = cashTransactionDto.transactionId();
        TransactionStatus status = cashTransactionDto.status();
        if (logService.existByTransaction(transactionId, status)) {
            System.out.println("validateCashTransaction транзакция есть в логах");
            throw new TransactionWasCompletedAlready(transactionId.toString());
        }
        //проверяем корректность последовательности операций
        boolean isCorrectOrder = switch (cashTransactionDto.action()) {
            case DEPOSIT ->
                    logService.isCorrectStep(transactionId, cashDepositStatusChain.get(cashTransactionDto.status()));
            case WITHDRAWAL ->
                    logService.isCorrectStep(transactionId, cashWithdrawStatusChain.get(cashTransactionDto.status()));
            case TRANSFER -> throw new UnsupportedOperationException();
        };
        if (!isCorrectOrder) {
            System.out.println("validateCashTransaction некорректная последовательность");
            throw new IllegalArgumentException("некорректная последовательность операций");
        }
    }

    private TransactionConfirmation processCashDeposit(CashTransactionDto cashTransactionDto) {
        System.out.println("зашли в processCashDeposit " + cashTransactionDto);
        logService.saveTransactionLogRecord(LogMapper.toCashLogRecord(cashTransactionDto));
        switch (cashTransactionDto.status()) {
            case CASH_RECEIVED -> {
                System.out.println("зашли в DEPOSIT ");
                accountRepository.changeBalance(cashTransactionDto.accountId(), cashTransactionDto.amount());
                logService.saveTransactionLogRecord(LogMapper.toCashLogRecord(cashTransactionDto,
                        TransactionStatus.DEPOSIT_PROCESSED));
            }
            case ERROR -> throw new UnsupportedOperationException(cashTransactionDto.status().name());
        }
        return new TransactionConfirmation(cashTransactionDto.transactionId(), cashTransactionDto.status());
    }

    private TransactionConfirmation processCashWithdrawal(CashTransactionDto cashTransactionDto) {
        System.out.println("зашли в processCashWithdrawal " + cashTransactionDto);
        logService.saveTransactionLogRecord(LogMapper.toCashLogRecord(cashTransactionDto));
        switch (cashTransactionDto.status()) {
            case BLOCKING_REQUEST -> {
                System.out.println("зашли в BLOCKING_REQUEST");
                UUID accountId = cashTransactionDto.accountId();
                BigDecimal balance = accountRepository.getBalance(accountId)
                        .orElseThrow(() -> new NoSuchElementException(accountId.toString()));
                System.out.println("баланс = " + balance.toString());
                BigDecimal blockedAmount = accountBlockService.getBlockedAmount(accountId);
                System.out.println("блокировано = " + blockedAmount.toString());
                if (balance.subtract(blockedAmount).compareTo(cashTransactionDto.amount()) < 0) {
                    throw new NotEnoughMoney(accountId.toString());
                }
                AccountBlock blockingRecord = AccountBlock.builder()
                        .blockingTransactionId(cashTransactionDto.transactionId())
                        .accountId(cashTransactionDto.accountId())
                        .amount(cashTransactionDto.amount())
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                accountBlockService.block(blockingRecord);
                logService.saveTransactionLogRecord(LogMapper.toCashLogRecord(cashTransactionDto,
                        TransactionStatus.BLOCKED));
            }
            case CASH_WAS_GIVEN -> {
                System.out.println("CASH_WAS_GIVEN");
                accountRepository.changeBalance(cashTransactionDto.accountId(), cashTransactionDto.amount().negate());
                accountBlockService.free(cashTransactionDto.transactionId());
                logService.saveTransactionLogRecord(LogMapper.toCashLogRecord(cashTransactionDto,
                        TransactionStatus.DEPOSIT_PROCESSED));
                logService.saveTransactionLogRecord(LogMapper.toCashLogRecord(cashTransactionDto,
                        TransactionStatus.SUCCESS));
            }
            case CANCEL -> {
                System.out.println("зашли в CANCELED");
                accountBlockService.free(cashTransactionDto.transactionId());
            }
            case ERROR, SUCCESS -> {
            }
        }
        return new TransactionConfirmation(cashTransactionDto.transactionId(), cashTransactionDto.status());
    }

    @Override
    @Transactional
    public TransactionConfirmation processNonCashTransaction(TransferTransactionDto transactionDto) {
        System.out.println("получили transferTransactionDto=" + transactionDto);
        // отклоняем операции, которые уже ранее были обработаны
        UUID transactionId = transactionDto.transactionId();
        TransactionStatus status = transactionDto.status();
        if (logService.existByTransaction(transactionId, status)) {
            System.out.println("validateCashTransaction транзакция есть в логах");
            throw new TransactionWasCompletedAlready(transactionId.toString());
        }
        logService.saveTransactionLogRecord(LogMapper.toNonCashLogRecord(transactionDto, TransactionStatus.STARTED));
        // отклоняем операцию, если аккаунт не активен или не существует
        UUID fromAccount = transactionDto.fromAccount();
        if (!accountRepository.isExistActive(fromAccount)) {
            throw new NoSuchElementException(fromAccount.toString());
        }
        UUID toAccountId = transactionDto.toAccount();
        if (toAccountId != fromAccount && !accountRepository.isExistActive(toAccountId)) {
            throw new NoSuchElementException(toAccountId.toString());
        }
        // отклоняем операции, если баланс недостаточен
        Account account = accountRepository.findActiveAccountById(fromAccount)
                .orElseThrow(() -> new NoSuchElementException(fromAccount.toString()));
        if (account.getBalance().compareTo(transactionDto.fromAmount()) < 0) {
            throw new NotEnoughMoney(fromAccount.toString());
        }

        // проводим операцию
        accountRepository.changeBalance(fromAccount, transactionDto.fromAmount().negate());
        accountRepository.changeBalance(toAccountId, transactionDto.toAmount());
        // логируем
        logService.saveTransactionLogRecord(LogMapper.toNonCashLogRecord(transactionDto, TransactionStatus.SUCCESS));
        return new TransactionConfirmation(transactionDto.transactionId(), transactionDto.status());
    }


}
