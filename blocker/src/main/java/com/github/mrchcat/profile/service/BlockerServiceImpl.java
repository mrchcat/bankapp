package com.github.mrchcat.profile.service;

import com.github.mrchcat.shared.blocker.BlockerResponseDto;
import com.github.mrchcat.shared.cash.CashTransactionDto;
import com.github.mrchcat.shared.transfer.NonCashTransferDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockerServiceImpl implements BlockerService {
    private final double CONFIRM_PROBABILITY = 0.90;

    private final List<String> rejectReasons = List.of(
            "сомнительные источники поступлений",
            "подозрительная операция",
            "нарушение закона об отмывании средств",
            "ограничения на лимит перевода",
            "ограничения на определённые виды деятельности"
    );

    @Override
    public BlockerResponseDto checkCashTransaction(CashTransactionDto cashTransactionDto) {
        return getRandom();
    }

    @Override
    public BlockerResponseDto checkNonCashTransaction(NonCashTransferDto nonCashTransferDto) {
        return getRandom();
    }

    private BlockerResponseDto getRandom() {
        if (Math.random() > CONFIRM_PROBABILITY) {
            int answer = (int) (Math.random() * rejectReasons.size());
            return new BlockerResponseDto(false, rejectReasons.get(answer));
        }
        return new BlockerResponseDto(true, "операция подтверждена");
    }

}
