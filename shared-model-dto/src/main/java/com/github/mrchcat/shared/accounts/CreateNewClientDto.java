package com.github.mrchcat.shared.accounts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDate;

@Builder
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
