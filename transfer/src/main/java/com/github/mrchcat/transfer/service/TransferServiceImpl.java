package com.github.mrchcat.transfer.service;

import com.github.mrchcat.shared.accounts.AccountDto;
import com.github.mrchcat.shared.accounts.BankUserDto;
import com.github.mrchcat.shared.accounts.TransactionConfirmation;
import com.github.mrchcat.shared.enums.BankCurrency;
import com.github.mrchcat.shared.enums.TransactionStatus;
import com.github.mrchcat.shared.enums.TransferDirection;
import com.github.mrchcat.shared.transfer.NonCashTransferDto;
import com.github.mrchcat.transfer.exception.AccountServiceException;
import com.github.mrchcat.transfer.exception.BlockerException;
import com.github.mrchcat.transfer.exception.NotEnoughMoney;
import com.github.mrchcat.transfer.model.TransferTransaction;
import com.github.mrchcat.transfer.repository.TransferRepository;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final TransferRepository transferRepository;
    private final Notifications notifications;
    private final Exchange exchange;
    private final Blocker blocker;
    private final Account account;

    BankUserDto senderClient;
    BankUserDto receiverClient;
    AccountDto senderAccount;
    AccountDto receiverAccount;

    @Override
    public void processTransfer(NonCashTransferDto transaction) throws AuthException, ServiceUnavailableException, SQLException {
        UUID fromAccountId = getFromAccountAndValidate(transaction.fromUsername(), transaction.amount(), transaction.fromCurrency());
        UUID toAccountId = switch (transaction.direction()) {
            case YOURSELF -> getToAccountAndValidate(transaction.fromUsername(), transaction.toCurrency());
            case OTHER -> getToAccountAndValidate(transaction.toUsername(), transaction.toCurrency());
        };
        var blockerResponse = blocker.checkCashTransaction(transaction);
        if (!blockerResponse.isConfirmed()) {
            throw new BlockerException(blockerResponse.reason());
        }
        BigDecimal fromAmount = transaction.amount();
        BigDecimal toAmount;
        BigDecimal exchangeRate;
        if (transaction.fromCurrency().equals(transaction.toCurrency())) {
            toAmount = fromAmount;
            exchangeRate = BigDecimal.ONE;
        } else {
            exchangeRate = exchange.getExchangeRate(transaction.fromCurrency(), transaction.toCurrency());
            toAmount = fromAmount.multiply(exchangeRate);
        }
        TransferTransaction transferTransaction = TransferTransaction.builder()
                .fromAccount(fromAccountId)
                .toAccount(toAccountId)
                .fromAmount(fromAmount)
                .toAmount(toAmount)
                .exchangeRate(exchangeRate)
                .status(TransactionStatus.STARTED)
                .build();
        TransferTransaction newTransaction = transferRepository.createNewTransaction(transferTransaction);
        var confirmation = account.sendTransaction(newTransaction);
        try {
            if (validateTransaction(confirmation, newTransaction.getTransactionId(), newTransaction.getStatus())) {
                transferRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.SUCCESS);
                String messageToSender = String.format("Со счета %s списаны средства в размере %s %s",
                        fromAccountId, newTransaction.getFromAmount(), senderAccount.currencyStringCode());
                try {
                    notifications.sendNotification(senderClient, messageToSender);
                } catch (Exception ignore) {
                }
                String message2ToSender = String.format("На счет %s начислены средства в размере %s %s",
                        toAccountId, newTransaction.getToAmount(), senderAccount.currencyStringCode());
                try {
                    notifications.sendNotification(senderClient, message2ToSender);
                } catch (Exception ignore) {
                }

                if (transaction.direction().equals(TransferDirection.OTHER)) {
                    String messageToReceiver = String.format("На счет %s зачислены средства в размере %s %s",
                            toAccountId, newTransaction.getToAmount(), receiverAccount.currencyStringCode());
                    try {
                        notifications.sendNotification(receiverClient, messageToReceiver);
                    } catch (Exception ignore) {
                    }
                }
            } else {
                transferRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.ERROR);
                String messageToSender = String.format("Ошибка при попытке списания средств со счета %s", fromAccountId);
                try {
                    notifications.sendNotification(senderClient, messageToSender);
                } catch (Exception ignore) {
                }
                if (transaction.direction().equals(TransferDirection.OTHER)) {
                    String messageToReceiver = String.format("Ошибка при попытке зачисления средств на счет %s", toAccountId);
                    try {
                        notifications.sendNotification(receiverClient, messageToReceiver);
                    } catch (Exception ignore) {
                    }
                }
                throw new AccountServiceException("ошибка: операция внесения денег не подтверждена");
            }
        } catch (Exception e) {
            transferRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.ERROR);
            throw e;
        }
    }

    private boolean validateTransaction(TransactionConfirmation confirmation, UUID transactionId, TransactionStatus status) {
        if (!transactionId.equals(confirmation.transactionId())) {
            return false;
        }
        return status.equals(confirmation.status());
    }

    private UUID getToAccountAndValidate(String username, BankCurrency currency) throws AuthException {
        BankUserDto receiver = account.getClient(username, currency);
        receiverClient = receiver;
        List<AccountDto> accounts = receiver.accounts();
        if (accounts == null || accounts.isEmpty()) {
            throw new AccountServiceException("сервис не вернул список аккаунтов");
        }
        AccountDto toAccount = accounts.get(0);
        receiverAccount = toAccount;
        return toAccount.id();
    }

    private UUID getFromAccountAndValidate(String username, BigDecimal fromAmount, BankCurrency currency) throws AuthException {
        BankUserDto sender = account.getClient(username, currency);
        senderClient = sender;
        List<AccountDto> accounts = sender.accounts();
        if (accounts == null) {
            throw new AccountServiceException("сервис не вернул список аккаунтов");
        }
        if (accounts.isEmpty()) {
            throw new NotEnoughMoney("");
        }
        AccountDto fromAccountDto = accounts.stream()
                .filter(accountDto -> accountDto.balance().compareTo(fromAmount) >= 0)
                .findFirst()
                .orElseThrow(() -> new NotEnoughMoney(""));
        senderAccount = fromAccountDto;
        return fromAccountDto.id();
    }
}
