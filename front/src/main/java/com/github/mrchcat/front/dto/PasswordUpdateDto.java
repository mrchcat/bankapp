package com.github.mrchcat.front.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;


public record PasswordUpdateDto(
        @NotNull(message = "ошибка: пустой пароль")
        @NotBlank(message = "ошибка: пустой пароль")
        @Length(min = 3, max = 256, message = "ошибка: пароль должен иметь длину от 3 до 256 символов")
        String password,

        String confirmPassword) {

    @AssertTrue(message = "ошибка: пароли не совпадают")
    boolean isPasswordsEqual() {
        return password.equals(confirmPassword);
    }
}