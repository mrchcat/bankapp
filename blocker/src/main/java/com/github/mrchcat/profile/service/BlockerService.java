package com.github.mrchcat.profile.service;

import com.github.mrchcat.shared.blocker.BlockerResponseDto;
import com.github.mrchcat.shared.cash.CashTransactionDto;
import com.github.mrchcat.shared.transfer.NonCashTransferDto;

public interface BlockerService {

    BlockerResponseDto checkCashTransaction(CashTransactionDto cashTransactionDto);

    BlockerResponseDto checkNonCashTransaction(NonCashTransferDto nonCashTransferDto);

}
