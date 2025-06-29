package com.github.mrchcat.front.controller;

import com.github.mrchcat.front.domain.UserRole;
import com.github.mrchcat.front.dto.PasswordUpdateFrontDto;
import com.github.mrchcat.front.dto.UserDetailsDto;
import com.github.mrchcat.front.service.FrontService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONUtil;
import org.springframework.boot.SpringApplication;
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
import org.w3c.dom.ls.LSOutput;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FrontController {
    private final FrontService frontService;

    /**
     * кэш содержит основные данные для заполнения шаблона:
     */

    private final HashMap<String, Object> cache = new HashMap<>(20);

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


    @GetMapping("/main")
    String getMain(Model model, Principal principal) {
        model.addAttribute("login", principal.getName());
        return "/main";
    }

    @PostMapping("/user/{username}/editPassword")
    String editClientPassword(@PathVariable @NotNull @NotBlank String username,
                              @ModelAttribute @Valid PasswordUpdateFrontDto passwordDto,
                              BindingResult bindingResult,
                              Model model,
                              Principal principal) {
        List<String> passwordErrors = new ArrayList<>();
        model.addAttribute("passwordErrors", passwordErrors);
        model.addAttribute("isPasswordUpdated", false);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .forEach(passwordErrors::add);
            return getMain(model, principal);
        }
        try {
            UserDetails newUserDetails = frontService.editClientPassword(username, passwordDto.password());
            model.addAttribute("isPasswordUpdated", true);
        } catch (Exception ex) {
            passwordErrors.add(ex.getMessage());
        }
        return getMain(model, principal);
    }

    @GetMapping("/registration")
    String registerNewClient() {
        return "/signup";
    }

}
