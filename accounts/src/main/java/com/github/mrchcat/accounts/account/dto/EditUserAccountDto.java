package com.github.mrchcat.accounts.account.dto;

import jakarta.validation.constraints.AssertTrue;

import java.util.Map;

public record EditUserAccountDto(
        String fullName,
        String email,
        Map<String,Boolean> accounts) {

    private static final String regexPattern =
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";


//    @AssertFalse(message = "ошибка: нет данных для сохранения; все поля пустые")
//    boolean isAllEmpty() {
//        return (fullName == null || fullName.isBlank()) && (email == null || email.isBlank());
//    }

    @AssertTrue(message = "ошибка: некорректный формат e-mail")
    boolean isEmailCorrectIfExist() {
        if (email != null && !email.isBlank()) {
            System.out.println("проверка =" + (email != null && !email.isBlank()));
            return email.matches(regexPattern);
        }
        return true;
    }
}
