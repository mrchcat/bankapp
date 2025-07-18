package com.github.mrchcat.accounts.account.controllers;

import com.github.mrchcat.accounts.account.service.AccountService;
import com.github.mrchcat.accounts.user.service.UserService;
import com.github.mrchcat.shared.accounts.AccountCashTransactionDto;
import com.github.mrchcat.shared.accounts.AccountTransferTransactionDto;
import com.github.mrchcat.shared.accounts.BankUserDto;
import com.github.mrchcat.shared.accounts.EditUserAccountDto;
import com.github.mrchcat.shared.accounts.TransactionConfirmation;
import com.github.mrchcat.shared.enums.BankCurrency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;

    /**
     * получение информации о клиенте банка для фронта
     */
    @GetMapping("/account/{username}")
    BankUserDto getClientAccountsAndPersonalData(@PathVariable @NotNull @NotBlank String username,
                                                 @RequestParam(name = "currency", required = false) BankCurrency currency
    ) {
        if (currency == null) {
            return accountService.getClient(username);
        } else {
            return accountService.getClient(username, currency);
        }
    }

    /**
     * получить список пользователей с активными аккаунтами
     */
    @GetMapping("/account")
    List<BankUserDto> getAllActiveClientsAccountsAndPersonalData() {
        return userService.getAllActiveClients();
    }

    /**
     * корректировка информации о клиенте банка по запросу фронта
     */
    @PatchMapping("/account/{username}")
    BankUserDto editClientAccountsAndPersonalData(@PathVariable @NotNull @NotBlank String username,
                                                  @RequestBody @Valid EditUserAccountDto editUserAccountDto) {
        if (!validateIfAllPropertiesEmpty(editUserAccountDto)) {
            userService.editClientData(username, editUserAccountDto);
        }
        accountService.editClientAccounts(username, editUserAccountDto);
        return accountService.getClient(username);
    }

    private boolean validateIfAllPropertiesEmpty(EditUserAccountDto editUserAccountDto) {
        String email = editUserAccountDto.email();
        if (email != null && !email.isBlank()) {
            return false;
        }
        String fullName = editUserAccountDto.fullName();
        return fullName == null || fullName.isBlank();
    }

    /**
     * выполнение транзакции от сервиса выдачи денег
     */
    @PostMapping("/account/cash")
    TransactionConfirmation processCashTransaction(@RequestBody @Valid AccountCashTransactionDto cashTransactionDto) {
        return accountService.processCashTransaction(cashTransactionDto);
    }

    /**
     * выполнение транзакции от сервиса переводов
     */
    @PostMapping("/account/transfer")
    TransactionConfirmation processNonCashTransaction(@RequestBody @Valid AccountTransferTransactionDto accountTransferTransactionDto) {
        return accountService.processNonCashTransaction(accountTransferTransactionDto);
    }

}
