package com.github.mrchcat.accounts.account.service;

import com.github.mrchcat.shared.accounts.AccountCashTransactionDto;
import com.github.mrchcat.shared.accounts.AccountTransferTransactionDto;
import com.github.mrchcat.shared.accounts.BankUserDto;
import com.github.mrchcat.shared.accounts.EditUserAccountDto;
import com.github.mrchcat.shared.accounts.TransactionConfirmation;
import com.github.mrchcat.shared.enums.BankCurrency;

public interface AccountService {

    BankUserDto getClient(String username);

    BankUserDto getClient(String username, BankCurrency currency);

    void editClientAccounts(String username, EditUserAccountDto editUserAccountDto);

    TransactionConfirmation processCashTransaction(AccountCashTransactionDto cashTransactionDto);

    TransactionConfirmation processNonCashTransaction(AccountTransferTransactionDto accountTransferTransactionDto);

}
