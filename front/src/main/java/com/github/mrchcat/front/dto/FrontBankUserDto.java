package com.github.mrchcat.front.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Builder
public record FrontBankUserDto(String fullName,
                               LocalDate birthDay,
                               String email,
                               List<FrontAccountDto> accounts) {
}
