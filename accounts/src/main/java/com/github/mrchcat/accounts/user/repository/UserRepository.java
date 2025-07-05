package com.github.mrchcat.accounts.user.repository;

import com.github.mrchcat.accounts.user.model.BankUser;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
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

    @Query(value = """
            SELECT EXISTS(
            	SELECT *
            	FROM users
            	WHERE lower(email)=lower(:email)
            	LIMIT 1
            )
            """)
    boolean isEmailExists(String email);

    @Query(value = """
                SELECT DISTINCT u.id,u.full_name,u.birth_day,u.email,u.username,u.password,u.roles,u.enabled,u.created_at,u.updated_at
                FROM users AS u JOIN accounts AS a ON a.user_id=u.id
                WHERE a.is_active=true AND u.enabled=true
            """)
    List<BankUser> findAllActive();

}
