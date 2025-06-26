package com.github.mrchcat.accounts.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequestDto(@NotNull @NotBlank String password) {
}
