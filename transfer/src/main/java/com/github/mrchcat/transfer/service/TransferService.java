package com.github.mrchcat.transfer.service;

import com.github.mrchcat.transfer.dto.NonCashTransferDto;
import com.github.mrchcat.transfer.model.TransferTransaction;
import jakarta.security.auth.message.AuthException;

import javax.naming.ServiceUnavailableException;
import java.sql.SQLException;

public interface TransferService {

    TransferTransaction processTransfer(NonCashTransferDto transaction) throws AuthException, ServiceUnavailableException, SQLException;
}
