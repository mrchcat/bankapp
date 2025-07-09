package com.github.mrchcat.front.service;

import com.github.mrchcat.front.dto.BankUserDto;
import com.github.mrchcat.front.dto.CashTransactionDto;
import com.github.mrchcat.front.dto.EditUserAccountDto;
import com.github.mrchcat.front.dto.FrontBankUserDto;
import com.github.mrchcat.front.dto.FrontRate;
import com.github.mrchcat.front.dto.NewClientRegisterDto;
import com.github.mrchcat.front.dto.NonCashTransfer;
import com.github.mrchcat.front.model.CashAction;
import jakarta.security.auth.message.AuthException;
import org.springframework.security.core.userdetails.UserDetails;

import javax.naming.ServiceUnavailableException;
import java.net.URI;
import java.util.List;

public interface FrontService {

    UserDetails editClientPassword(String username, String password);

    UserDetails registerNewClient(NewClientRegisterDto newClientRegisterDto) throws AuthException;

    FrontBankUserDto getClientDetailsAndAccounts(String username);

    List<FrontBankUserDto> getAllClientsWithActiveAccounts() throws AuthException, ServiceUnavailableException;

    BankUserDto editUserAccount(String username, EditUserAccountDto editUserAccountDto) throws AuthException;

    void processCashOperation(String username, CashTransactionDto cashOperationDto, CashAction action) throws AuthException;

    void processNonCashOperation(NonCashTransfer nonCashTransaction) throws AuthException;

    List<FrontRate> getAllRates() throws AuthException;

    URI getFrontExchangeUri();
}
