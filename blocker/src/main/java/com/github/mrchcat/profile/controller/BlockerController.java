package com.github.mrchcat.profile.controller;

import com.github.mrchcat.profile.service.BlockerService;
import com.github.mrchcat.shared.blocker.BlockerResponseDto;
import com.github.mrchcat.shared.cash.CashTransactionDto;
import com.github.mrchcat.shared.transfer.NonCashTransferDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BlockerController {
    private final BlockerService blockerService;

    @PostMapping("/blocker/cash")
    BlockerResponseDto checkCashTransaction(@RequestBody @Valid CashTransactionDto cashTransactionDto) {
        return blockerService.checkCashTransaction(cashTransactionDto);
    }

    @PostMapping("/blocker/noncash")
    BlockerResponseDto checkCashTransaction(@RequestBody @Valid NonCashTransferDto nonCashTransferDto) {
        return blockerService.checkNonCashTransaction(nonCashTransferDto);
    }

}
