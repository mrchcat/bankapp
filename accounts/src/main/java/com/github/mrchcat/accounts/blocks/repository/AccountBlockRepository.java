package com.github.mrchcat.accounts.blocks.repository;

import com.github.mrchcat.accounts.blocks.model.AccountBlock;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface AccountBlockRepository extends CrudRepository<AccountBlock, Long> {

    @Query("""
            UPDATE amount_blocks
            SET is_active=false
            WHERE blocking_transaction_id=:blockingTransactionId
            RETURNING id
            """)
    long free(UUID blockingTransactionId);

    @Query("""
            SELECT SUM(amount)
            FROM amount_blocks
            WHERE account_id=:accountId AND is_active=true
            GROUP BY account_id
            """)
    Optional<BigDecimal> blockedAmountByAccount(UUID accountId);


}
