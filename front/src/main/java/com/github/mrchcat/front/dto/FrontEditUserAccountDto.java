package com.github.mrchcat.front.dto;

import jakarta.validation.constraints.AssertTrue;

import java.util.List;

public record FrontEditUserAccountDto(
        String fullName,
        String email,
        List<String> account
) {

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
