package com.github.mrchcat.transfer.service;

import com.github.mrchcat.transfer.dto.AccountDto;
import com.github.mrchcat.transfer.dto.BankUserDto;
import com.github.mrchcat.transfer.dto.NonCashTransferDto;
import com.github.mrchcat.transfer.dto.TransactionConfirmation;
import com.github.mrchcat.transfer.exception.AccountServiceException;
import com.github.mrchcat.transfer.exception.NotEnoughMoney;
import com.github.mrchcat.transfer.mapper.TransferMapper;
import com.github.mrchcat.transfer.model.BankCurrency;
import com.github.mrchcat.transfer.model.TransactionStatus;
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
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final String ACCOUNT_SERVICE = "bankAccounts";
    private final String BLOCKER_SERVICE = "bankBlocker";

    private final String ACCOUNTS_GET_CLIENT_API = "/account";
    private final String ACCOUNTS_SEND_TRANSFER_TRANSACTION_API = "/account/transfer";
    private final String BLOCKER_ASK_PERMISSION = "/blocker/noncash";


    private final RestClient.Builder restClientBuilder;
    private final OAuthHeaderGetter oAuthHeaderGetter;
    private final TransferRepository transferRepository;

    @Override
    public void processTransfer(NonCashTransferDto transaction) throws AuthException, ServiceUnavailableException, SQLException {
        UUID fromAccount = getFromAccountAndValidate(transaction.fromUsername(), transaction.amount(), transaction.fromCurrency());
        UUID toAccount = switch (transaction.direction()) {
            case YOURSELF -> getToAccountAndValidate(transaction.fromUsername(), transaction.toCurrency());
            case OTHER -> getToAccountAndValidate(transaction.toUsername(), transaction.toCurrency());
        };
        BigDecimal fromAmount = transaction.amount();
        BigDecimal toAmount;
        BigDecimal exchangeRate;
        if (transaction.fromCurrency().equals(transaction.toCurrency())) {
            toAmount = fromAmount;
            exchangeRate = BigDecimal.ONE;
        } else {
            exchangeRate = getExchangeRate(transaction.fromCurrency(), transaction.toCurrency());
            toAmount = fromAmount.divide(exchangeRate, 2, RoundingMode.CEILING);
        }
        TransferTransaction transferTransaction = TransferTransaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .fromAmount(fromAmount)
                .toAmount(toAmount)
                .exchangeRate(exchangeRate)
                .status(TransactionStatus.STARTED)
                .build();
        TransferTransaction newTransaction = transferRepository.createNewTransaction(transferTransaction);
        var confirmation = sendTransaction(newTransaction);
//        if (validateTransaction(confirmation, newTransaction.getTransactionId(), newTransaction.getStatus())) {
//            transferRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.SUCCESS);
//        } else {
//            transferRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.ERROR);
//            throw new AccountServiceException("ошибка: операция внесения денег не подтверждена");
//        }
    }

    private BigDecimal getExchangeRate(BankCurrency from, BankCurrency to) {
        if (from.equals(to)) {
            return BigDecimal.ONE;
        }
        return BigDecimal.TEN;
    }

    private boolean validateTransaction(TransactionConfirmation confirmation, UUID transactionId, TransactionStatus status) {
        if (!transactionId.equals(confirmation.transactionId())) {
            return false;
        }
        return status.equals(confirmation.status());
    }

    private TransactionConfirmation sendTransaction(TransferTransaction transferTransaction) throws AuthException, ServiceUnavailableException {
        System.out.println("отправляем " + TransferMapper.toRequestDto(transferTransaction));
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        String requestUrl = "http://" + ACCOUNT_SERVICE + ACCOUNTS_SEND_TRANSFER_TRANSACTION_API;
        System.out.println("запросили=" + requestUrl);
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
        List<AccountDto> accounts = receiver.accounts();
        if (accounts == null || accounts.isEmpty()) {
            throw new AccountServiceException("сервис не вернул список аккаунтов");
        }
        return accounts.get(0).id();
    }


    private UUID getFromAccountAndValidate(String username, BigDecimal fromAmount, BankCurrency currency) throws AuthException {
        BankUserDto sender = getClient(username, currency);
        List<AccountDto> accounts = sender.accounts();
        if (accounts == null) {
            throw new AccountServiceException("сервис не вернул список аккаунтов");
        }
        if (accounts.isEmpty()) {
            throw new NotEnoughMoney("");
        }
        return accounts.stream()
                .filter(accountDto -> accountDto.balance().compareTo(fromAmount) >= 0)
                .findFirst()
                .map(AccountDto::id)
                .orElseThrow(() -> new NotEnoughMoney(""));
    }


    private BankUserDto getClient(String username, BankCurrency currency) throws AuthException {
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        String requestUrl = "http://" + ACCOUNT_SERVICE + ACCOUNTS_GET_CLIENT_API + "/" + username + "?currency=" + currency.name();
        System.out.println("запросили=" + requestUrl);
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

}
