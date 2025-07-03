package com.github.mrchcat.cash.service;

import com.github.mrchcat.cash.dto.AccountDto;
import com.github.mrchcat.cash.dto.BankUserDto;
import com.github.mrchcat.cash.dto.CashTransactionDto;
import com.github.mrchcat.cash.dto.CashTransactionRequestDto;
import com.github.mrchcat.cash.dto.TransactionConfirmation;
import com.github.mrchcat.cash.dto.BlockerResponseDto;
import com.github.mrchcat.cash.exceptions.BlockerException;
import com.github.mrchcat.cash.exceptions.RejectedByClient;
import com.github.mrchcat.cash.mapper.CashMapper;
import com.github.mrchcat.cash.model.BankCurrency;
import com.github.mrchcat.cash.model.CashAction;
import com.github.mrchcat.cash.model.CashTransaction;
import com.github.mrchcat.cash.model.TransactionStatus;
import com.github.mrchcat.cash.repository.CashRepository;
import com.github.mrchcat.cash.security.OAuthHeaderGetter;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import javax.naming.ServiceUnavailableException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {
    private final String ACCOUNT_SERVICE = "bankAccounts";
    private final String BLOCKER_SERVICE = "bankBlocker";

    private final String ACCOUNTS_GET_CLIENT_API = "/account";
    private final String ACCOUNTS_SEND_TRANSACTION_API = "/account/cash";

    private final String BLOCKER_ASK_PERMISSION = "/blocker/cash";

    private final RestClient.Builder restClientBuilder;
    private final OAuthHeaderGetter oAuthHeaderGetter;
    private final CashRepository cashRepository;


    @Override
    @Transactional
    public void processCashOperation(CashTransactionDto cashOperationDto) throws AuthException, ServiceUnavailableException {
        BankUserDto client = getClient(cashOperationDto.username(), cashOperationDto.currency());
        var blockerResponse = checkCashTransaction(cashOperationDto);
        if (!blockerResponse.isConfirmed()) {
            throw new BlockerException(blockerResponse.reason());
        }
        switch (cashOperationDto.action()) {
            case DEPOSIT -> deposit(client, cashOperationDto);
            case WITHDRAWAL -> withdrawal(client, cashOperationDto);
            default -> throw new UnsupportedOperationException("некорректный тип акции:" + cashOperationDto.action());
        }
    }

    private BlockerResponseDto checkCashTransaction(CashTransactionDto cashTransactionDto) throws AuthException, ServiceUnavailableException {
        System.out.println("отправляем в блокер " + cashTransactionDto);
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        var blockerResponse = restClientBuilder.build()
                .post()
                .uri("http://" + BLOCKER_SERVICE + BLOCKER_ASK_PERMISSION)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(cashTransactionDto)
                .retrieve()
                .body(BlockerResponseDto.class);
        if (blockerResponse == null) {
            throw new ServiceUnavailableException("сервис подтверждения не доступен");
        }
        return blockerResponse;
    }

    private void deposit(BankUserDto client, CashTransactionDto cashOperationDto) throws AuthException, ServiceUnavailableException {
        AccountDto processedAccount = client.accounts().get(0);
        CashTransaction transaction = CashTransaction.builder()
                .transactionId(UUID.randomUUID())
                .action(CashAction.DEPOSIT)
                .userId(client.id())
                .username(client.username())
                .accountId(processedAccount.id())
                .currencyStringCodeIso4217(BankCurrency.valueOf(processedAccount.currencyStringCode()))
                .amount(cashOperationDto.value())
                .build();
        var newTransaction = cashRepository.createNewTransaction(transaction);
        var confirmation = sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.STARTED));
        if (validateTransaction(newTransaction, confirmation)) {
            cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.SUCCESS);
        } else {
            throw new RuntimeException("ошибка: операция внесения денег не подтверждена");
        }
    }

    private boolean validateTransaction(CashTransaction transaction, TransactionConfirmation confirmation) {
        if (!transaction.getTransactionId().equals(confirmation.transactionId())) {
            return false;
        }
        return transaction.getStatus().equals(confirmation.status());
    }

    private TransactionConfirmation sendTransactionToAccountService(CashTransactionRequestDto cashTransactionRequestDto) throws AuthException, ServiceUnavailableException {
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
                String message = "Пользователь " + client.fullName() + "не имеет аккаунта в валюте " + currency.name();
                throw new IllegalArgumentException(message);
            }
            return client;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                throw new UsernameNotFoundException(ex.getMessage());
            }
        }
        return null;
    }

    private void withdrawal(BankUserDto client, CashTransactionDto cashOperationDto) throws AuthException, ServiceUnavailableException {
        AccountDto processedAccount = client.accounts().get(0);
        CashTransaction transaction = CashTransaction.builder()
                .transactionId(UUID.randomUUID())
                .action(CashAction.WITHDRAWAL)
                .userId(client.id())
                .username(client.username())
                .accountId(processedAccount.id())
                .currencyStringCodeIso4217(BankCurrency.valueOf(processedAccount.currencyStringCode()))
                .amount(cashOperationDto.value())
                .build();
        //        создаем новую транзакцию
        var newTransaction = cashRepository.createNewTransaction(transaction);
        //        блокируем деньги на счету
        try {
            TransactionConfirmation confirmation = sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.STARTED));
            if (validateTransaction(newTransaction, confirmation)) {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.BLOCK);
            } else {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CANCELED);
                throw new RuntimeException("ошибка: операция внесения денег не подтверждена");
            }
        } catch (Exception e) {
            cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CANCELED);
            throw new RuntimeException("ошибка: операция внесения денег не подтверждена");
        }

//        проверяем, что деньги забрали
        if (isMoneyTakenFromATM(newTransaction.getTransactionId())) {
            TransactionConfirmation confirmation = sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.SUCCESS));
            //        списываем со счета
            if (validateTransaction(newTransaction, confirmation)) {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.SUCCESS);
            }
        } else {
            cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CANCELED);
            sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.CANCELED));
            throw new RejectedByClient("");
        }
    }

    private boolean isMoneyTakenFromATM(UUID transactionId) {
        System.out.println("проверяем, что клиент физически забрал деньги из банкомата");
        return true;
    }

}
