package com.github.mrchcat.profile.service;

import com.github.mrchcat.profile.dto.BlockerResponseDto;
import com.github.mrchcat.profile.dto.NonCashTransferBlockerRequestDto;
import com.github.mrchcat.shared.cash.CashTransactionDto;

public interface BlockerService {

    BlockerResponseDto checkCashTransaction(CashTransactionDto cashTransactionDto);

    BlockerResponseDto checkNonCashTransaction(NonCashTransferBlockerRequestDto nonCashTransferBlockerRequestDto);

}
