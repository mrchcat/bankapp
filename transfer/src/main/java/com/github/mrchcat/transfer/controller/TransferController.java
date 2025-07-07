package com.github.mrchcat.transfer.controller;

import com.github.mrchcat.transfer.dto.NonCashTransferDto;
import com.github.mrchcat.transfer.model.TransferTransaction;
import com.github.mrchcat.transfer.service.TransferService;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.ServiceUnavailableException;
import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void processTransfer(@RequestBody @Valid NonCashTransferDto nonCashTransferDto) throws AuthException, SQLException, ServiceUnavailableException {
        System.out.println("получили " + nonCashTransferDto);
        transferService.processTransfer(nonCashTransferDto);
    }

}
