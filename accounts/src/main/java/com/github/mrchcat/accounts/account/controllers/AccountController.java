package com.github.mrchcat.accounts.account.controllers;

import com.github.mrchcat.accounts.account.dto.CashTransactionDto;
import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.account.dto.TransactionConfirmation;
import com.github.mrchcat.accounts.account.dto.TransferTransactionDto;
import com.github.mrchcat.accounts.account.model.BankCurrency;
import com.github.mrchcat.accounts.account.service.AccountService;
import com.github.mrchcat.accounts.user.dto.BankUserDto;
import com.github.mrchcat.accounts.user.service.UserService;
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
import org.w3c.dom.ls.LSOutput;

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
        System.out.println("зашли в getAllActiveClientsAccountsAndPersonalData");
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
        var client = accountService.getClient(username);
        return client;
    }

    private final boolean validateIfAllPropertiesEmpty(EditUserAccountDto editUserAccountDto) {
        String email = editUserAccountDto.email();
        if (email != null && !email.isBlank()) {
            return false;
        }
        String fullName = editUserAccountDto.fullName();
        if (fullName != null && !fullName.isBlank()) {
            return false;
        }
        return true;
    }

    /**
     * выполнение транзакции от сервиса выдачи денег
     */
    @PostMapping("/account/cash")
    TransactionConfirmation processCashTransaction(@RequestBody @Valid CashTransactionDto cashTransactionDto) {
        System.out.println("контроллер /account/cash" + cashTransactionDto);
        return accountService.processCashTransaction(cashTransactionDto);
    }

    /**
     * выполнение транзакции от сервиса переводов
     */
    @PostMapping("/account/transfer")
    TransactionConfirmation processNonCashTransaction(@RequestBody @Valid TransferTransactionDto transferTransactionDto) {
//        System.out.println("контроллер /account/transfer" + transferTransactionDto);
//        TransactionConfirmation confirmation=accountService.processNonCashTransaction(transferTransactionDto);
//        System.out.println("возвращаем "+confirmation);
        return accountService.processNonCashTransaction(transferTransactionDto);
    }

}
