package com.github.mrchcat.cash.controller;

import com.github.mrchcat.cash.dto.CashOperationDto;
import com.github.mrchcat.cash.service.CashService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RestController
@RequiredArgsConstructor
public class CashController {
    private final CashService cashService;

    @PostMapping("/cash")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
    String processOperation(@RequestBody @Valid CashOperationDto cashOperationDto) {
        cashService.processCashOperation(cashOperationDto);
        return "ответ от Cash: "+ cashOperationDto;
    }
}
