package com.github.mrchcat.transfer.service;

import com.github.mrchcat.notifications.dto.BankNotificationDto;
import com.github.mrchcat.transfer.dto.AccountDto;
import com.github.mrchcat.transfer.dto.BankUserDto;
import com.github.mrchcat.transfer.dto.BlockerResponseDto;
import com.github.mrchcat.transfer.dto.CurrencyExchangeRateDto;
import com.github.mrchcat.transfer.dto.NonCashTransferDto;
import com.github.mrchcat.transfer.dto.TransactionConfirmation;
import com.github.mrchcat.transfer.exception.AccountServiceException;
import com.github.mrchcat.transfer.exception.BlockerException;
import com.github.mrchcat.transfer.exception.ExchangeServiceException;
import com.github.mrchcat.transfer.exception.NotEnoughMoney;
import com.github.mrchcat.transfer.mapper.TransferMapper;
import com.github.mrchcat.transfer.model.BankCurrency;
import com.github.mrchcat.transfer.model.TransactionStatus;
import com.github.mrchcat.transfer.model.TransferDirection;
import com.github.mrchcat.transfer.model.TransferTransaction;
import com.github.mrchcat.transfer.repository.TransferRepository;
import com.github.mrchcat.transfer.security.OAuthHeaderGetter;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final String ACCOUNT_SERVICE = "bankAccounts";
    private final String BLOCKER_SERVICE = "bankBlocker";
    private final String EXCHANGE_SERVICE = "bankExchange";


    private final String ACCOUNTS_GET_CLIENT_API = "/account";
    private final String ACCOUNTS_SEND_TRANSFER_TRANSACTION_API = "/account/transfer";
    private final String BLOCKER_ASK_PERMISSION = "/blocker/noncash";
    private final String EXCHANGE_GET_EXCHANGE_RATE = "/exchange";

    private final String NOTIFICATION_SERVICE = "bankNotifications";
    private final String NOTIFICATION_SEND_NOTIFICATION = "/notification";

    private final String TRANSFER_SERVICE = "bankTransfer";

    private final RestClient.Builder restClientBuilder;
    private final OAuthHeaderGetter oAuthHeaderGetter;
    private final TransferRepository transferRepository;

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
        var blockerResponse = checkCashTransaction(transaction);
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
            exchangeRate = getExchangeRate(transaction.fromCurrency(), transaction.toCurrency());
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
        var confirmation = sendTransaction(newTransaction);
        try {
            if (validateTransaction(confirmation, newTransaction.getTransactionId(), newTransaction.getStatus())) {
                transferRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.SUCCESS);
                String messageToSender = String.format("Со счета %s списаны средства в размере %s %s",
                        fromAccountId, newTransaction.getFromAmount(), senderAccount.currencyStringCode());
                sendNotification(senderClient, messageToSender);
                String message2ToSender = String.format("На счет %s начислены средства в размере %s %s",
                        toAccountId, newTransaction.getToAmount(), senderAccount.currencyStringCode());
                sendNotification(senderClient, message2ToSender);

                if (transaction.direction().equals(TransferDirection.OTHER)) {
                    String messageToReceiver = String.format("На счет %s зачислены средства в размере %s %s",
                            toAccountId, newTransaction.getToAmount(), receiverAccount.currencyStringCode());
                    sendNotification(receiverClient, messageToReceiver);
                }
            } else {
                transferRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.ERROR);
                String messageToSender = String.format("Ошибка при попытке списания средств со счета %s", fromAccountId);
                sendNotification(senderClient, messageToSender);
                if (transaction.direction().equals(TransferDirection.OTHER)) {
                    String messageToReceiver = String.format("Ошибка при попытке зачисления средств на счет %s", toAccountId);
                    sendNotification(receiverClient, messageToReceiver);
                }
                throw new AccountServiceException("ошибка: операция внесения денег не подтверждена");
            }
        } catch (Exception e) {
            transferRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.ERROR);
            throw e;
        }
    }

    private BigDecimal getExchangeRate(BankCurrency fromCurrency, BankCurrency toCurrency) throws AuthException {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        String requestUrl = "http://"
                + EXCHANGE_SERVICE + EXCHANGE_GET_EXCHANGE_RATE + "/" + fromCurrency.name() + "?toCurrency=" + toCurrency.name();
        try {
            var exchangeRate = restClientBuilder.build()
                    .get()
                    .uri(requestUrl)
                    .header(oAuthHeader.name(), oAuthHeader.value())
                    .retrieve()
                    .body(CurrencyExchangeRateDto.class);
            if (exchangeRate == null) {
                throw new ExchangeServiceException("");
            }
            return exchangeRate.rate();
        } catch (Exception ex) {
            throw new ExchangeServiceException("");
        }
    }

    private boolean validateTransaction(TransactionConfirmation confirmation, UUID transactionId, TransactionStatus status) {
        if (!transactionId.equals(confirmation.transactionId())) {
            return false;
        }
        return status.equals(confirmation.status());
    }

    private TransactionConfirmation sendTransaction(TransferTransaction transferTransaction) throws AuthException, ServiceUnavailableException {
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        String requestUrl = "http://" + ACCOUNT_SERVICE + ACCOUNTS_SEND_TRANSFER_TRANSACTION_API;
        var confirmation = restClientBuilder.build()
                .post()
                .uri(requestUrl)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(TransferMapper.toRequestDto(transferTransaction))
                .retrieve()
                .body(TransactionConfirmation.class);
        if (confirmation == null) {
            throw new ServiceUnavailableException("Сервис аккаунтов не доступен");
        }
        return confirmation;
    }

    private UUID getToAccountAndValidate(String username, BankCurrency currency) throws AuthException {
        BankUserDto receiver = getClient(username, currency);
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
        BankUserDto sender = getClient(username, currency);
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


    private BankUserDto getClient(String username, BankCurrency currency) throws AuthException {
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        String requestUrl = "http://" + ACCOUNT_SERVICE + ACCOUNTS_GET_CLIENT_API + "/" + username + "?currency=" + currency.name();
        try {
            var client = restClientBuilder.build()
                    .get()
                    .uri(requestUrl)
                    .header(oAuthHeader.name(), oAuthHeader.value())
                    .retrieve()
                    .body(BankUserDto.class);
            if (client == null) {
                throw new UsernameNotFoundException("Клиент не найден:" + username);
            }
            if (client.accounts().isEmpty()) {
                String message = "Пользователь " + client.fullName() + " не имеет аккаунта в валюте " + currency.name();
                throw new IllegalArgumentException(message);
            }
            return client;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                var details = ex.getResponseBodyAs(ProblemDetail.class);
                if (details != null && details.getDetail() != null) {
                    throw new UsernameNotFoundException(details.getDetail());
                }
            }
        }
        return null;
    }

    private BlockerResponseDto checkCashTransaction(NonCashTransferDto transaction) throws AuthException, ServiceUnavailableException {
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        var blockerResponse = restClientBuilder.build()
                .post()
                .uri("http://" + BLOCKER_SERVICE + BLOCKER_ASK_PERMISSION)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(transaction)
                .retrieve()
                .body(BlockerResponseDto.class);
        if (blockerResponse == null) {
            throw new ServiceUnavailableException("сервис подтверждения не доступен");
        }
        return blockerResponse;
    }

    private void sendNotification(BankUserDto client, String message) {
        try {
            var notification = BankNotificationDto.builder()
                    .service(TRANSFER_SERVICE)
                    .username(client.username())
                    .fullName(client.fullName())
                    .email(client.email())
                    .message(message)
                    .build();
            var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
            String requestUrl = "http://" + NOTIFICATION_SERVICE + NOTIFICATION_SEND_NOTIFICATION;
            restClientBuilder.build()
                    .post()
                    .uri(requestUrl)
                    .header(oAuthHeader.name(), oAuthHeader.value())
                    .body(notification)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception ignore) {
        }
    }

}
