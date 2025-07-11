package com.github.mrchcat.front.controller;

import com.github.mrchcat.front.dto.NewClientRegisterDto;
import com.github.mrchcat.front.service.FrontService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final FrontService frontService;

    @GetMapping("/registration")
    String registerNewClient(Model model) {
        return "signup";
    }

    /**
     * контроллер для регистрации нового пользователя
     */
    @PostMapping("/registration")
    String registerNewClient(@ModelAttribute @Valid NewClientRegisterDto newClientRegisterDto,
                             BindingResult bindingResult,
                             Model model) {
        List<String> errors = new ArrayList<>();

        model.addAttribute("errors", errors);
        model.addAttribute("isNewClientRegistered", false);

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .forEach(errors::add);
            return "signup";
        }
        try {
            UserDetails newUserDetails = frontService.registerNewClient(newClientRegisterDto);
            model.addAttribute("isNewClientRegistered", true);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                String notUniqueProperties = ex.getResponseHeaders().get("X-not-unique").get(0);
                errors.add("Ошибка, указанные свойства не уникальны: " + notUniqueProperties);
            }
        } catch (Exception ex) {
            errors.add(ex.getMessage());
        }
        return "signup";
    }
}
