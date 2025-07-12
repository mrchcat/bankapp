package com.github.mrchcat.accounts.account.service;

import com.github.mrchcat.accounts.account.dto.CashTransactionDto;
import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.account.dto.TransactionConfirmation;
import com.github.mrchcat.accounts.account.dto.TransferTransactionDto;
import com.github.mrchcat.accounts.user.dto.BankUserDto;
import com.github.mrchcat.shared.enums.BankCurrency;

public interface AccountService {

    BankUserDto getClient(String username);

    BankUserDto getClient(String username, BankCurrency currency);

    void editClientAccounts(String username, EditUserAccountDto editUserAccountDto);

    TransactionConfirmation processCashTransaction(CashTransactionDto cashTransactionDto);

    TransactionConfirmation processNonCashTransaction(TransferTransactionDto transferTransactionDto);

}
