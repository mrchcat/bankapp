package com.github.mrchcat.shared.accounts;

import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;

import java.util.Map;

@Builder
public record EditUserAccountDto(
        String fullName,
        String email,
        Map<String,Boolean> accounts) {

    private static final String regexPattern =
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";


    @AssertTrue(message = "ошибка: некорректный формат e-mail")
    boolean isEmailCorrectIfExist() {
        if (email != null && !email.isBlank()) {
            return email.matches(regexPattern);
        }
        return true;
    }
}
