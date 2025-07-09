package com.github.mrchcat.front.controller;

import com.github.mrchcat.front.dto.CashTransactionDto;
import com.github.mrchcat.front.dto.EditUserAccountDto;
import com.github.mrchcat.front.dto.FrontAccountDto;
import com.github.mrchcat.front.dto.FrontBankUserDto;
import com.github.mrchcat.front.dto.NonCashTransfer;
import com.github.mrchcat.front.dto.PasswordUpdateDto;
import com.github.mrchcat.front.model.BankCurrency;
import com.github.mrchcat.front.model.CashAction;
import com.github.mrchcat.front.model.UserRole;
import com.github.mrchcat.front.service.FrontService;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//TODO добавить проверку, что username действительно соответствует авторизированнному пользователю

@Controller
@RequiredArgsConstructor
public class MainController {
    private final FrontService frontService;

    /**
     * после авторизации загружаются разные страницы в зависимости от роли
     */
    @GetMapping("/defaultAfterLogin")
    String getDefaultUrlAfter(Authentication authentication) {
        var authorities = authentication.getAuthorities();
        for (UserRole role : UserRole.values()) {
            if (authorities.contains(new SimpleGrantedAuthority(role.roleName))) {
                return "redirect:" + role.urlAfterSuccessLogin;
            }
        }
        return "redirect:/main";
    }

    /**
     * основная страница
     */
    @GetMapping(path = {"/main", "/"})
    String getMain(Model model, Principal principal) throws AuthException, ServiceUnavailableException {
        String username = principal.getName();
        model.addAttribute("login", username);

        FrontBankUserDto clientDetailsAndAccounts = frontService.getClientDetailsAndAccounts(username);
        model.addAttribute("fullName", clientDetailsAndAccounts.fullName());
        model.addAttribute("birthDate", clientDetailsAndAccounts.birthDay());
        model.addAttribute("email", clientDetailsAndAccounts.email());
        model.addAttribute("accounts", clientDetailsAndAccounts.accounts());

        var clientsWithAccounts = frontService.getAllClientsWithActiveAccounts();
        System.out.println("отправляем " + clientsWithAccounts);
        model.addAttribute("clientsWithAccounts", clientsWithAccounts);

        model.addAttribute("ratesLink", "http://localhost:8080/front/rates");
//        model.addAttribute("ratesLink", frontService.getFrontExchangeUri());

        return "/main";
    }

    /**
     * контроллер обновления пароля
     */
    @PostMapping("/user/{username}/editPassword")
    RedirectView editClientPassword(@PathVariable @NotNull @NotBlank String username,
                                    @ModelAttribute @Valid PasswordUpdateDto passwordDto,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        RedirectView redirectView = new RedirectView();
        redirectView.setContextRelative(true);
        redirectView.setUrl("/main");
        List<String> passwordErrors = new ArrayList<>();
        redirectAttributes.addFlashAttribute("passwordErrors", passwordErrors);
        redirectAttributes.addFlashAttribute("isPasswordUpdated", false);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .forEach(passwordErrors::add);
            return redirectView;
        }
        try {
            UserDetails newUserDetails = frontService.editClientPassword(username, passwordDto.password());
            redirectAttributes.addFlashAttribute("isPasswordUpdated", true);
        } catch (Exception ex) {
            passwordErrors.add(ex.getMessage());
        }
        return redirectView;
    }

    /**
     * контроллер обновления личных данных и данных об аккаунтах
     */
    @PostMapping("/user/{username}/editUserAccounts")
    RedirectView editUserAccounts(@PathVariable @NotNull @NotBlank String username,
                                  @ModelAttribute @Valid EditUserAccountDto editUserAccountDto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes
    ) {
        RedirectView redirectView = new RedirectView();
        redirectView.setContextRelative(true);
        redirectView.setUrl("/main");
        List<String> userAccountsErrors = new ArrayList<>();
        redirectAttributes.addFlashAttribute("userAccountsErrors", userAccountsErrors);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .forEach(userAccountsErrors::add);
        }
        validateCheckBoxes(username, editUserAccountDto, userAccountsErrors);
        if (!userAccountsErrors.isEmpty()) {
            return redirectView;
        }
        try {
            frontService.editUserAccount(username, editUserAccountDto);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                String notUniqueProperties = ex.getResponseHeaders().get("X-not-unique").get(0);
                userAccountsErrors.add("Ошибка, указанные свойства не уникальны: " + notUniqueProperties);
            }
        } catch (Exception ex) {
            userAccountsErrors.add(ex.getMessage());
        }
        return redirectView;
    }

    private void validateCheckBoxes(String username, EditUserAccountDto editUserAccountDto, List<String> userAccountsErrors) {
        List<String> activeAccountsFromFront = (editUserAccountDto.account() == null) ? Collections.emptyList() : editUserAccountDto.account();
        List<FrontAccountDto> existingAccounts = frontService.getClientDetailsAndAccounts(username).accounts();
        List<String> notEmptyAccounts = frontService.getClientDetailsAndAccounts(username)
                .accounts().stream()
                .filter(account -> account.balance() != null)
                .filter(account -> account.balance().compareTo(BigDecimal.ZERO) > 0)
                .map(FrontAccountDto::currencyStringCode)
                .filter(currencyStringCode -> !activeAccountsFromFront.contains(currencyStringCode))
                .toList();
        if (notEmptyAccounts.isEmpty()) {
            return;
        }
        String message = "Ошибка: невозможно удалить следующие аккаунты, т.к. они баланс не пуст: "
                + String.join(",", notEmptyAccounts);
        userAccountsErrors.add(message);
    }


    /**
     * контроллер для работы с наличными
     */
    @PostMapping(path = "/user/{username}/сash")
    RedirectView depositCash(@PathVariable @NotNull @NotBlank String username,
                             @ModelAttribute @Valid CashTransactionDto cashOperationDto,
                             @RequestParam("action") @NotNull CashAction action,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        System.out.println("получили " + cashOperationDto);
        RedirectView redirectView = new RedirectView();
        redirectView.setContextRelative(true);
        redirectView.setUrl("/main");
        List<String> cashErrors = new ArrayList<>();
        redirectAttributes.addFlashAttribute("cashErrors", cashErrors);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .forEach(cashErrors::add);
            return redirectView;
        }
        try {
            frontService.processCashOperation(username, cashOperationDto, action);
            redirectAttributes.addFlashAttribute("isCashOperationSucceed", true);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
                var details = ex.getResponseBodyAs(ProblemDetail.class);
                if (details != null && details.getDetail() != null) {
                    cashErrors.add(details.getDetail());
                }
            }
        } catch (Exception ex) {
            cashErrors.add(ex.getMessage());
        }
        return redirectView;
    }


    /**
     * контроллер для перевода денег
     */
    @PostMapping(path = "/user/{username}/transfer")
    RedirectView depositCash(@PathVariable @NotNull @NotBlank String username,
                             @ModelAttribute @Valid NonCashTransfer nonCashTransaction,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws AuthException, ServiceUnavailableException {
        System.out.println("проверка " + nonCashTransaction);
        RedirectView redirectView = new RedirectView();
        redirectView.setContextRelative(true);
        redirectView.setUrl("/main");
        List<String> transferErrors = new ArrayList<>();
        switch (nonCashTransaction.direction()) {
            case YOURSELF -> redirectAttributes.addFlashAttribute("transferYourselfErrors", transferErrors);
            case OTHER -> redirectAttributes.addFlashAttribute("transferOtherErrors", transferErrors);
        }
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .forEach(transferErrors::add);
            return redirectView;
        }
        try {
            frontService.processNonCashOperation(nonCashTransaction);
            switch (nonCashTransaction.direction()) {
                case YOURSELF -> redirectAttributes.addFlashAttribute("isTransferYourselfSucceed", true);
                case OTHER -> redirectAttributes.addFlashAttribute("isTransferOtherSucceed", true);
            }
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
                var details = ex.getResponseBodyAs(ProblemDetail.class);
                if (details != null && details.getDetail() != null) {
                    transferErrors.add(details.getDetail());
                }
            }
        } catch (Exception ex) {
            transferErrors.add(ex.getMessage());
        }
        return redirectView;
    }
}
