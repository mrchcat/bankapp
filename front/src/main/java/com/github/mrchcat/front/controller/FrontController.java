package com.github.mrchcat.front.controller;

import com.github.mrchcat.front.dto.PasswordUpdateFrontDto;
import com.github.mrchcat.front.dto.UserDetailsDto;
import com.github.mrchcat.front.service.FrontService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FrontController {
    private final FrontService frontService;

    @GetMapping("/main")
    String getMain(Principal principal,
                   Model model) {
        model.addAttribute("login", principal.getName());
        return "/main";
    }

    @PostMapping("/user/{username}/editPassword")
    String editClientPassword(@PathVariable @NotNull @NotBlank String username,
                              @ModelAttribute @Valid PasswordUpdateFrontDto passwordDto,
                              Principal principal,
                              BindingResult bindingResult,
                              Model model) {
        List<String> passwordErrors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                passwordErrors.add(objectError.getDefaultMessage());
            }
        } else {
            try {
                UserDetails newUserDetails = frontService.editClientPassword(username, passwordDto.password());
                if (newUserDetails != null) {
                    model.addAttribute("isPasswordUpdated", true);
                }
            } catch (Exception ex) {
                passwordErrors.add(ex.getMessage());
            }
        }
        if (!passwordErrors.isEmpty()) {
            model.addAttribute("passwordErrors", passwordErrors);
        }
        model.addAttribute("login", principal.getName());
        return "/main";
    }


}
