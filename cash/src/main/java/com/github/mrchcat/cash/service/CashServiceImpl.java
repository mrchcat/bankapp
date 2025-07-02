package com.github.mrchcat.cash.service;

import com.github.mrchcat.cash.dto.AccountDto;
import com.github.mrchcat.cash.dto.BankUserDto;
import com.github.mrchcat.cash.dto.CashTransactionDto;
import com.github.mrchcat.cash.dto.CashTransactionRequestDto;
import com.github.mrchcat.cash.dto.TransactionConfirmation;
import com.github.mrchcat.cash.mapper.CashMapper;
import com.github.mrchcat.cash.model.BankCurrency;
import com.github.mrchcat.cash.model.CashAction;
import com.github.mrchcat.cash.model.CashTransaction;
import com.github.mrchcat.cash.model.TransactionStatus;
import com.github.mrchcat.cash.repository.CashRepository;
import com.github.mrchcat.cash.security.OAuthHeaderGetter;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.naming.ServiceUnavailableException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {
    private final String ACCOUNT_SERVICE = "bankAccounts";
    private final String ACCOUNTS_GET_CLIENT_API = "/account";
    private final String ACCOUNTS_SEND_TRANSACTION_API = "/account/cash";

    private final RestClient.Builder restClientBuilder;
    private final OAuthHeaderGetter oAuthHeaderGetter;
    private final CashRepository cashRepository;


    @Override
    public void processCashOperation(CashTransactionDto cashOperationDto) throws AuthException, ServiceUnavailableException {
        System.out.println("внутри processCashOperation " + cashOperationDto);
        BankUserDto client = getClient(cashOperationDto.username(), cashOperationDto.currency());
        System.out.println("получили клиента " + client);
        switch (cashOperationDto.action()) {
            case DEPOSIT -> deposit(client, cashOperationDto);
            case WITHDRAWAL -> withdrawal(client, cashOperationDto);
            default -> throw new UnsupportedOperationException("некорректный тип акции:" + cashOperationDto.action());
        }
    }

    private void deposit(BankUserDto client, CashTransactionDto cashOperationDto) throws AuthException, ServiceUnavailableException {
        AccountDto processedAccount = client.accounts().get(0);
        CashTransaction transaction = CashTransaction.builder()
                .transactionId(UUID.randomUUID())
                .action(CashAction.DEPOSIT)
                .userId(client.id())
                .username(client.username())
                .accountId(processedAccount.id())
                .currency_string_code_iso4217(BankCurrency.valueOf(processedAccount.currencyStringCode()))
                .amount(cashOperationDto.value())
                .build();
        var newTransaction = cashRepository.createNewTransaction(transaction);
        System.out.println("сохраненная транзакция" + newTransaction);
        var confirmation = sendTransaction(CashMapper.toRequestDto(newTransaction, TransactionStatus.STARTED));
        System.out.println("подтверждение" + confirmation);
        if (isConfirmed(newTransaction, confirmation)) {
            cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.APPROVED);
        } else {
            throw new RuntimeException("ошибка: операция внесения денег не подтверждена");
        }
    }

    private boolean isConfirmed(CashTransaction transaction, TransactionConfirmation confirmation) {
        if (!transaction.getTransactionId().equals(confirmation.transactionId())) {
            return false;
        }
        if (!transaction.getStatus().equals(confirmation.status())) {
            return false;
        }
        return true;
    }

    private TransactionConfirmation sendTransaction(CashTransactionRequestDto cashTransactionRequestDto) throws AuthException, ServiceUnavailableException {
        System.out.println("на отправку " + cashTransactionRequestDto);
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        System.out.println("запросили=" + "http://" + ACCOUNT_SERVICE + ACCOUNTS_SEND_TRANSACTION_API);
        var confirmation = restClientBuilder.build()
                .post()
                .uri("http://" + ACCOUNT_SERVICE + ACCOUNTS_SEND_TRANSACTION_API)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(cashTransactionRequestDto)
                .retrieve()
                .body(TransactionConfirmation.class);
        if (confirmation == null) {
            throw new ServiceUnavailableException("Сервис аккаунтов не доступен");
        }
        return confirmation;
    }


    private void withdrawal(BankUserDto client, CashTransactionDto cashOperationDto) {

    }


    private BankUserDto getClient(String username, BankCurrency currency) throws AuthException {
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        String requestUrl = "http://" + ACCOUNT_SERVICE + ACCOUNTS_GET_CLIENT_API + "/" + username + "?currency=" + currency.name();
        System.out.println("запросили=" + requestUrl);
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
            String message = "Пользователь " + client.fullName() + "не имеет аккаунта в валюте " + currency.name();
            throw new IllegalArgumentException("message");
        }
        return client;
    }

}
