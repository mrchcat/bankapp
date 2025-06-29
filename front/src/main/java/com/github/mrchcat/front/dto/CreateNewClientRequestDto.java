package com.github.mrchcat.front.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateNewClientRequestDto(
        String fullName,
        LocalDate birthDay,
        String email,
        String username,
        String password) {
}
