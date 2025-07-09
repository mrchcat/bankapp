package com.github.mrchcat.cash.controller;

import com.github.mrchcat.cash.dto.CashTransactionDto;
import com.github.mrchcat.cash.service.CashService;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.ServiceUnavailableException;

@RestController
@RequiredArgsConstructor
public class CashController {
    private final CashService cashService;

    @PostMapping("/cash")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void processOperation(@RequestBody @Valid CashTransactionDto cashTransactionDto) throws AuthException, ServiceUnavailableException {
        cashService.processCashOperation(cashTransactionDto);
    }
}
