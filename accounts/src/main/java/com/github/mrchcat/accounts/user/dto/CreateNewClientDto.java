package com.github.mrchcat.accounts.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDate;
import java.util.UUID;

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
