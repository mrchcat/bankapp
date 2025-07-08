package com.github.mrchcat.profile.service;

import com.github.mrchcat.profile.dto.CashTransactionDto;
import com.github.mrchcat.profile.dto.BlockerResponseDto;
import com.github.mrchcat.profile.dto.NonCashTransferDto;

public interface BlockerService {

    BlockerResponseDto checkCashTransaction(CashTransactionDto cashTransactionDto);

    BlockerResponseDto checkNonCashTransaction(NonCashTransferDto nonCashTransferDto);

}
