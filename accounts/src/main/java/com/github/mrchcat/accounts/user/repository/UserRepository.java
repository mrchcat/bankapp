package com.github.mrchcat.accounts.user.repository;

import com.github.mrchcat.accounts.user.domain.BankUser;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<BankUser, UUID> {

    Optional<BankUser> findByUsername(String username);

    @Query(value = """
            UPDATE users
            SET password=:password, updated_at=now()
            WHERE id=:userId
            RETURNING id
            """)
    Optional<UUID> updateUserPassword(UUID userId, String password);

}
