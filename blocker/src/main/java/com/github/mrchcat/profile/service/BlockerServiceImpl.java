package com.github.mrchcat.profile.service;

import com.github.mrchcat.profile.dto.CashTransactionDto;
import com.github.mrchcat.profile.dto.BlockerResponseDto;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BlockerServiceImpl implements BlockerService {
    private final double CONFIRM_PROBABILITY = 1;

    List<String> rejectReasons = List.of("такова воля богов",
            "подозрительная операция",
            "нарушение закона об отмывании средств");

    @Override
    public BlockerResponseDto checkCashTransaction(CashTransactionDto cashTransactionDto) {
        if (Math.random() > CONFIRM_PROBABILITY) {
            int answer = (int) (Math.random() * rejectReasons.size());
            return new BlockerResponseDto(false, rejectReasons.get(answer));
        }
        return new BlockerResponseDto(true, "операция подтверждена");
    }
}
