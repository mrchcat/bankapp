package com.github.mrchcat.profile.service;

import com.github.mrchcat.profile.dto.CashTransactionDto;
import com.github.mrchcat.profile.dto.BlockerResponseDto;

public interface BlockerService {

    BlockerResponseDto checkCashTransaction(CashTransactionDto cashTransactionDto);

}
