package com.github.mrchcat.accounts.blocks.service;

import com.github.mrchcat.accounts.blocks.model.AccountBlock;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountBlockService {

    void block(AccountBlock block);

    void free(UUID blockingTransactionId);

    BigDecimal getBlockedAmount(UUID accountId);

}
