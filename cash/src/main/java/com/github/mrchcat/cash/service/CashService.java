package com.github.mrchcat.cash.service;

import com.github.mrchcat.cash.dto.CashOperationDto;
import jakarta.security.auth.message.AuthException;

public interface CashService {

    void processCashOperation(CashOperationDto cashOperationDto);

}
