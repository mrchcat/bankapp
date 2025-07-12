package com.github.mrchcat.transfer.service;

import com.github.mrchcat.shared.transfer.NonCashTransferDto;
import jakarta.security.auth.message.AuthException;

import javax.naming.ServiceUnavailableException;
import java.sql.SQLException;

public interface TransferService {

    void processTransfer(NonCashTransferDto transaction) throws AuthException, ServiceUnavailableException, SQLException;
}
