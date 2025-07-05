package com.github.mrchcat.accounts.blocks.service;

import com.github.mrchcat.accounts.blocks.model.AccountBlock;
import com.github.mrchcat.accounts.blocks.repository.AccountBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountBlockServiceImpl implements AccountBlockService {
    private final AccountBlockRepository accountBlockRepository;

    @Override
    public void block(AccountBlock block) {
        accountBlockRepository.save(block);
    }

    @Override
    public void free(UUID blockingTransactionId) {
        accountBlockRepository.free(blockingTransactionId);
    }

    @Override
    public BigDecimal getBlockedAmount(UUID accountId) {
        return accountBlockRepository.blockedAmountByAccount(accountId).orElse(BigDecimal.ZERO);
    }
}
