package com.github.mrchcat.front.service;

import com.github.mrchcat.front.dto.FrontBankUserDto;
import com.github.mrchcat.front.dto.FrontCashTransactionDto;
import com.github.mrchcat.front.dto.FrontEditUserAccountDto;
import com.github.mrchcat.front.dto.FrontRate;
import com.github.mrchcat.front.dto.NewClientRegisterDto;
import com.github.mrchcat.front.dto.NonCashTransfer;
import com.github.mrchcat.shared.accounts.BankUserDto;
import com.github.mrchcat.shared.enums.CashAction;
import jakarta.security.auth.message.AuthException;
import org.springframework.security.core.userdetails.UserDetails;

import javax.naming.ServiceUnavailableException;
import java.util.List;

public interface FrontService {

    UserDetails editClientPassword(String username, String password);

    UserDetails registerNewClient(NewClientRegisterDto newClientRegisterDto) throws AuthException;

    FrontBankUserDto getClientDetailsAndAccounts(String username) throws AuthException;

    List<FrontBankUserDto> getAllClientsWithActiveAccounts() throws AuthException, ServiceUnavailableException;

    BankUserDto editUserAccount(String username, FrontEditUserAccountDto frontEditUserAccountDto) throws AuthException;

    void processCashOperation(String username, FrontCashTransactionDto cashOperationDto, CashAction action) throws AuthException;

    void processNonCashOperation(NonCashTransfer nonCashTransaction) throws AuthException;

    List<FrontRate> getAllRates() throws AuthException;

}
