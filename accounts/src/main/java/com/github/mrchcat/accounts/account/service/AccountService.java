package com.github.mrchcat.accounts.account.service;

import com.github.mrchcat.accounts.account.dto.CashTransactionDto;
import com.github.mrchcat.accounts.account.dto.EditUserAccountDto;
import com.github.mrchcat.accounts.account.dto.TransactionConfirmation;
import com.github.mrchcat.accounts.account.model.BankCurrency;
import com.github.mrchcat.accounts.user.dto.BankUserDto;

import java.util.List;

public interface AccountService {

    BankUserDto getClient(String username);

    BankUserDto getClient(String username, BankCurrency currency);

    void editClientAccounts(String username, EditUserAccountDto editUserAccountDto);

    TransactionConfirmation processCashTransaction(CashTransactionDto cashTransactionDto);
}
