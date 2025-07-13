package com.github.mrchcat.cash.service;

import com.github.mrchcat.cash.exceptions.BlockerException;
import com.github.mrchcat.cash.exceptions.NotEnoughMoney;
import com.github.mrchcat.cash.exceptions.RejectedByClient;
import com.github.mrchcat.cash.mapper.CashMapper;
import com.github.mrchcat.cash.model.CashTransaction;
import com.github.mrchcat.cash.repository.CashRepository;
import com.github.mrchcat.cash.security.OAuthHeaderGetter;
import com.github.mrchcat.shared.accounts.AccountCashTransactionDto;
import com.github.mrchcat.shared.accounts.AccountDto;
import com.github.mrchcat.shared.accounts.BankUserDto;
import com.github.mrchcat.shared.accounts.TransactionConfirmation;
import com.github.mrchcat.shared.blocker.BlockerResponseDto;
import com.github.mrchcat.shared.cash.CashTransactionDto;
import com.github.mrchcat.shared.enums.BankCurrency;
import com.github.mrchcat.shared.enums.TransactionStatus;
import com.github.mrchcat.shared.notification.BankNotificationDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import javax.naming.ServiceUnavailableException;
import java.util.UUID;

import static com.github.mrchcat.shared.enums.CashAction.DEPOSIT;
import static com.github.mrchcat.shared.enums.CashAction.WITHDRAWAL;


@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {
    private final CashRepository cashRepository;
    private final Blocker blocker;
    private final Notifications notifications;
    private final Account account;


    @Override
    public void processCashOperation(CashTransactionDto cashTransactionDto) throws AuthException, ServiceUnavailableException {
        BankUserDto client = account.getClient(cashTransactionDto.username(), cashTransactionDto.currency());
        var blockerResponse = blocker.checkCashTransaction(cashTransactionDto);
        if (!blockerResponse.isConfirmed()) {
            throw new BlockerException(blockerResponse.reason());
        }
        switch (cashTransactionDto.action()) {
            case DEPOSIT -> deposit(client, cashTransactionDto);
            case WITHDRAWAL -> withdrawal(client, cashTransactionDto);
            default -> throw new UnsupportedOperationException("некорректный тип акции:" + cashTransactionDto.action());
        }
    }

    private void deposit(BankUserDto client, CashTransactionDto cashOperationDto) throws AuthException, ServiceUnavailableException {
        AccountDto processedAccount = client.accounts().get(0);
        CashTransaction transaction = CashTransaction.builder()
                .transactionId(UUID.randomUUID())
                .action(DEPOSIT)
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
            var confirmation = account.sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.CASH_RECEIVED));
            if (validateTransaction(confirmation, newTransaction.getTransactionId(), TransactionStatus.CASH_RECEIVED)) {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.SUCCESS);
                String message = "приняты наличные в сумме " + newTransaction.getAmount() + " "
                        + newTransaction.getCurrencyStringCodeIso4217();
                try {
                    notifications.sendNotification(client, message);
                } catch (Exception ignore) {
                }
            } else {
                String message = "ошибка в процессе внесения наличных денег в сумме" + newTransaction.getAmount() + " "
                        + newTransaction.getCurrencyStringCodeIso4217();
                try {
                    notifications.sendNotification(client, message);
                } catch (Exception ignore) {
                }
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.ERROR);
                throw new RuntimeException("ошибка: операция внесения денег не подтверждена");
            }
        } else {
            String message = "деньги не были востребованы в банкомате в сумме" + newTransaction.getAmount() + " "
                    + newTransaction.getCurrencyStringCodeIso4217();
            try {
                notifications.sendNotification(client, message);
            } catch (Exception ignore) {
            }
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


    private void withdrawal(BankUserDto client, CashTransactionDto cashOperationDto) throws AuthException, ServiceUnavailableException {
        AccountDto processedAccount = client.accounts().get(0);
        CashTransaction transaction = CashTransaction.builder()
                .transactionId(UUID.randomUUID())
                .action(WITHDRAWAL)
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
            TransactionConfirmation confirmation = account.sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.BLOCKING_REQUEST));
            if (validateTransaction(confirmation, newTransaction.getTransactionId(), TransactionStatus.BLOCKING_REQUEST)) {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.BLOCKED);
            } else {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CANCEL);
                account.sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.CANCEL));
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
            account.sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.CANCEL));
            throw new RuntimeException("ошибка: операция внесения денег не подтверждена");
        }

//        проверяем, что деньги забрали из банкомата
//        если забрали
        if (isATMConfirmMoneyTransfer(newTransaction.getTransactionId())) {
            cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CASH_WAS_GIVEN);
            TransactionConfirmation confirmation = account.sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction,
                    TransactionStatus.CASH_WAS_GIVEN));
            //        списываем со счета
            if (validateTransaction(confirmation, newTransaction.getTransactionId(), newTransaction.getStatus())) {
                cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.SUCCESS);
            }
            String message = "выданы наличные в сумме" + newTransaction.getAmount() + " "
                    + newTransaction.getCurrencyStringCodeIso4217();
            try {
                notifications.sendNotification(client, message);
            } catch (Exception ignore) {
            }

//          если не забрали
        } else {
            cashRepository.changeTransactionStatus(newTransaction.getId(), TransactionStatus.CANCEL);
            account.sendTransactionToAccountService(CashMapper.toRequestDto(newTransaction, TransactionStatus.CANCEL));
            String message = "деньги не были востребованы в банкомате в сумме" + newTransaction.getAmount() + " "
                    + newTransaction.getCurrencyStringCodeIso4217();
            try {
                notifications.sendNotification(client, message);
            } catch (Exception ignore) {
            }
            throw new RejectedByClient("");
        }
    }

    private boolean isATMConfirmMoneyTransfer(UUID transactionId) {
        return true;
    }

    private void giveMoneyBackFromATM(UUID transactionId) {
//        возвращаем деньги обратно клиенту
    }
}