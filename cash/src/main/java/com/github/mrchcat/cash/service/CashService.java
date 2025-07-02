package com.github.mrchcat.cash.service;

import com.github.mrchcat.cash.dto.CashTransactionDto;
import jakarta.security.auth.message.AuthException;

import javax.naming.ServiceUnavailableException;

public interface CashService {

    void processCashOperation(CashTransactionDto cashOperationDto) throws AuthException, ServiceUnavailableException;

}
