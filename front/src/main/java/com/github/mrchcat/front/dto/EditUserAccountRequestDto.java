package com.github.mrchcat.front.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record EditUserAccountRequestDto(String fullName,
                                        String email,
                                        Map<String, Boolean> accounts) {
}
