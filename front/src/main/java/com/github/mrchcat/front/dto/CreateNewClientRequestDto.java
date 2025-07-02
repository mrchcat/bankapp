package com.github.mrchcat.front.dto;

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
