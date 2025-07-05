package com.github.mrchcat.cash.service;

import com.github.mrchcat.cash.dto.AccountDto;
import com.github.mrchcat.cash.dto.BankUserDto;
import com.github.mrchcat.cash.dto.CashTransactionDto;
import com.github.mrchcat.cash.dto.CashTransactionRequestDto;
import com.github.mrchcat.cash.dto.TransactionConfirmation;
import com.github.mrchcat.cash.dto.BlockerResponseDto;
import com.github.mrchcat.cash.exceptions.AccountServiceException;
import com.github.mrchcat.cash.exceptions.BlockerException;
import com.github.mrchcat.cash.exceptions.NotEnoughMoney;
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
import org.springframework.http.ProblemDetail;
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
        //        создаем новую транзакцию
        var newTransaction = cashRepository.createNewTransaction(transaction);
        cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.STARTED);
        //        подтверждаем получение денег от банкомата
        if (isATMConfirmMoneyTransfer(newTransaction.getTransactionId())) {
            //        если получили деньги
            cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CASH_RECEIVED);
            var confirmation = sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.CASH_RECEIVED));
            if (validateTransaction(confirmation, newTransaction.getTransactionId(), TransactionStatus.CASH_RECEIVED)) {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.SUCCESS);
            } else {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.ERROR);
                throw new RuntimeException("ошибка: операция внесения денег не подтверждена");
            }
        } else {
            //        если не получили
            cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CANCEL);
            throw new RejectedByClient("");
        }
    }

    private boolean validateTransaction(TransactionConfirmation confirmation, UUID transactionId, TransactionStatus status) {
        if (!transactionId.equals(confirmation.transactionId())) {
            return false;
        }
        return status.equals(confirmation.status());
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
                String message = "Пользователь " + client.fullName() + " не имеет аккаунта в валюте " + currency.name();
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
        cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.BLOCKING_REQUEST);
        //        блокируем деньги на счету
        try {
            TransactionConfirmation confirmation = sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.BLOCKING_REQUEST));
            if (validateTransaction(confirmation, newTransaction.getTransactionId(), TransactionStatus.BLOCKING_REQUEST)) {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.BLOCKED);
            } else {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CANCEL);
                sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.CANCEL));
                throw new RuntimeException("ошибка: операция внесения денег не подтверждена");
            }
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                var details = ex.getResponseBodyAs(ProblemDetail.class);
                if (details != null && details.getDetail() != null && details.getDetail().equals("Недостаточно средств")) {
                    throw new NotEnoughMoney("");
                }
            }
        } catch (Exception e) {
            cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CANCEL);
            sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.CANCEL));
            throw new RuntimeException("ошибка: операция внесения денег не подтверждена");
        }

//        проверяем, что деньги забрали из банкомата
//        если забрали
        if (isATMConfirmMoneyTransfer(newTransaction.getTransactionId())) {
            cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CASH_WAS_GIVEN);
            TransactionConfirmation confirmation = sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction,
                    TransactionStatus.CASH_WAS_GIVEN));
            //        списываем со счета
            if (validateTransaction(confirmation, newTransaction.getTransactionId(), newTransaction.getStatus())) {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.SUCCESS);
            }
//          если не забрали
        } else {
            cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CANCEL);
            sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.CANCEL));
            throw new RejectedByClient("");
        }
    }

    private boolean isATMConfirmMoneyTransfer(UUID transactionId) {
        System.out.println("проверяем, что клиент физически забрал деньги из банкомата");
        return true;
    }

    private void giveMoneyBackFromATM(UUID transactionId) {
        System.out.println("возвращаем деньги обратно клиенту");
    }

}
