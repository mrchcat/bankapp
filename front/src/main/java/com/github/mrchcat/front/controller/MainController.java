package com.github.mrchcat.front.controller;

import com.github.mrchcat.front.dto.BankUserDto;
import com.github.mrchcat.front.dto.EditUserAccountDto;
import com.github.mrchcat.front.dto.FrontAccountDto;
import com.github.mrchcat.front.dto.FrontBankUserDto;
import com.github.mrchcat.front.model.UserRole;
import com.github.mrchcat.front.dto.PasswordUpdateDto;
import com.github.mrchcat.front.service.FrontService;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONUtil;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.w3c.dom.ls.LSOutput;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    String getMain(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("login", username);

        FrontBankUserDto clientDetailsAndAccounts = frontService.getClientDetailsAndAccounts(username);
        System.out.println("получили clientDetailsAndAccounts=" + clientDetailsAndAccounts);
        model.addAttribute("fullName", clientDetailsAndAccounts.fullName());
        model.addAttribute("birthDate", clientDetailsAndAccounts.birthDay());
        model.addAttribute("email", clientDetailsAndAccounts.email());
        model.addAttribute("accounts", clientDetailsAndAccounts.accounts());

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
}
