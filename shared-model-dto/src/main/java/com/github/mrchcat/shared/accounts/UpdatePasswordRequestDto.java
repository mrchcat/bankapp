package com.github.mrchcat.shared.accounts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequestDto(@NotNull @NotBlank String password) {
}
