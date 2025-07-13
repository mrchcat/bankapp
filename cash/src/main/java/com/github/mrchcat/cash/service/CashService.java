package com.github.mrchcat.cash.service;

import com.github.mrchcat.shared.blocker.BlockerResponseDto;
import com.github.mrchcat.shared.cash.CashTransactionDto;
import jakarta.security.auth.message.AuthException;

import javax.naming.ServiceUnavailableException;

public interface CashService {

    void processCashOperation(CashTransactionDto cashOperationDto) throws AuthException, ServiceUnavailableException;

}
