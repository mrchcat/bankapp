package com.github.mrchcat.profile.controller;

import com.github.mrchcat.profile.dto.CashTransactionDto;
import com.github.mrchcat.profile.dto.BlockerResponseDto;
import com.github.mrchcat.profile.service.BlockerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.ls.LSOutput;

@RestController
@RequiredArgsConstructor
public class BlockerController {
    private final BlockerService blockerService;

    @PostMapping("/blocker/cash")
    BlockerResponseDto checkCashTransaction(@RequestBody @Valid CashTransactionDto cashTransactionDto) {
        System.out.println("зашли " + cashTransactionDto);
        return blockerService.checkCashTransaction(cashTransactionDto);
    }

}
