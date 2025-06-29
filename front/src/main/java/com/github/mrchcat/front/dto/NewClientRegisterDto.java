package com.github.mrchcat.front.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record NewClientRegisterDto(
                                   @NotNull(message = "ошибка: пустой логин")
                                   @NotBlank(message = "ошибка: пустой логин")
                                   @Length(min = 4, max = 20, message = "ошибка: логин должен иметь длину от 4 до 20 символов")
                                   String login,

                                   @NotNull(message = "ошибка: пустой пароль")
                                   @NotBlank(message = "ошибка: пустой пароль")
                                   @Length(min = 3, max = 256, message = "ошибка: пароль должен иметь длину от 3 до 256 символов")
                                   String password,

                                   @NotNull(message = "ошибка: пустой пароль")
                                   @NotBlank(message = "ошибка: пустой пароль")
                                   @Length(min = 3, max = 256, message = "ошибка: пароль должен иметь длину от 3 до 256 символов")
                                   String confirmPassword,

                                   @NotNull(message = "ошибка: пустое имя")
                                   @NotBlank(message = "ошибка: пустое имя")
                                   String fullName,

                                   @Past(message = "ошибка: дата рождения не может быть в будущем")
                                   LocalDate birthDate,

                                   @NotNull(message = "ошибка: пустой e-mail")
                                   @Email(message = "ошибка: некорректный e-mail")
                                   String email
) {
    static final int MINIMUM_AGE_YEARS = 18;

    @AssertTrue(message = "ошибка: возраст не может быть ниже чем " + MINIMUM_AGE_YEARS + " лет")
    boolean isCorrectAge() {
        return birthDate.isBefore(LocalDate.now().minusYears(MINIMUM_AGE_YEARS));
    }

    @AssertTrue(message = "ошибка: пароли не совпадают")
    boolean isPasswordsEqual() {
        return password.equals(confirmPassword);
    }
}
