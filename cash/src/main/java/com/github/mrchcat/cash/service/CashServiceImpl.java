package com.github.mrchcat.cash.service;

import com.github.mrchcat.cash.dto.CashOperationDto;
import jakarta.security.auth.message.AuthException;
import org.springframework.stereotype.Service;

@Service
public class CashServiceImpl implements CashService {

    @Override
    public void processCashOperation(CashOperationDto cashOperationDto) {
        System.out.println("получили " + cashOperationDto);
    }
}
