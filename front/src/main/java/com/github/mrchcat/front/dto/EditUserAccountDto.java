package com.github.mrchcat.front.dto;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record EditUserAccountDto(
        String fullName,
        String email) {

    private static final String regexPattern =
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";


    @AssertFalse(message = "ошибка: нет данных для сохранения; все поля пустые")
    boolean isAllEmpty() {
        return (fullName == null || fullName.isBlank()) && (email == null || email.isBlank());
    }

    @AssertTrue(message = "ошибка: некорректный формат e-mail")
    boolean isEmailCorrectIfExist() {
        if (email != null && !email.isBlank()) {
            return email.matches(regexPattern);
        }
        return true;
    }
}
