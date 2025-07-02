package com.github.mrchcat.accounts.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CreateNewClientDto(
        @NotNull @NotBlank
        String fullName,

        @Past()
        LocalDate birthDay,

        @NotNull() @Email()
        String email,

        @NotNull() @NotBlank
        String username,

        @NotNull() @NotBlank
        String password) {
}
