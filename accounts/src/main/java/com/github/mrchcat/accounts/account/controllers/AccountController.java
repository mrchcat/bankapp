package com.github.mrchcat.accounts.account.controllers;

import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;

//    @PostMapping("/account/{username}")
//    BankUserDto createAccounts(@PathVariable @NotNull @NotBlank String username,
//                               @RequestBody @Valid BankCurrencyListDto bankCurrencyListDto) {
//        return accountService.createAccounts(username, bankCurrencyListDto);
//    }
//
//    @DeleteMapping("/account/{username}")
//    BankUserDto deleteAccounts(@PathVariable @NotNull @NotBlank String username,
//                               @RequestBody @Valid AccountListDto accountListDto) {
//        accountService.deleteAccounts(accountListDto);
//        return userService.getClient(username);
//    }

    /**
     * получение информации о клиенте банка
     */
    @GetMapping("/account/{username}")
    BankUserDto getClientAccountsAndPersonalData(@PathVariable @NotNull @NotBlank String username) {
        return accountService.getClient(username);
    }

    @PatchMapping("/account/{username}")
    BankUserDto editClientAccountsAndPersonalData(@PathVariable @NotNull @NotBlank String username,
                                                  @RequestBody @Valid EditUserAccountDto editUserAccountDto) {
        System.out.println("получили username=" + username + " ;" + "editUserAccountDto=" + editUserAccountDto);
        if (!validateIfAllPropertiesEmpty(editUserAccountDto)) {
            userService.editClientData(username, editUserAccountDto);
        }
        accountService.editClientAccounts(username, editUserAccountDto);
        var client=accountService.getClient(username);
        System.out.println("на отправку "+client);
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

}
