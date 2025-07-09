package com.github.mrchcat.accounts.account.repository;

import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.accounts.account.model.BankCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final JdbcTemplate jdbc;
    private final AccountRowMapper accountRowMapper;

    @Override
    public List<Account> findAllActiveAccountsByUser(UUID userId) {
        String query = """
                SELECT id, number,balance, currency_string_code_iso4217, user_id, created_at, updated_at,is_active
                FROM accounts
                WHERE user_id=? AND is_active=true;
                """;
        return jdbc.query(query, accountRowMapper, userId);
    }

    @Override
    public List<Account> findAllActiveAccountsByUser(UUID userId, BankCurrency currency) {
        String query = """
                SELECT id, number,balance, currency_string_code_iso4217, user_id, created_at, updated_at,is_active
                FROM accounts
                WHERE user_id=? AND is_active=true AND currency_string_code_iso4217=CAST(? AS currency);
                """;
        return jdbc.query(query, accountRowMapper, userId, currency.name());
    }

    @Override
    public List<Account> findAllAccountsByUser(UUID userId) {
        String query = """
                SELECT id, number,balance, currency_string_code_iso4217, user_id, created_at, updated_at,is_active
                FROM accounts
                WHERE user_id=?;
                """;
        return jdbc.query(query, accountRowMapper, userId);
    }

    @Override
    public void setAccountActivation(UUID accountId, boolean isActive) {
        String query = """
                UPDATE accounts
                SET is_Active=?
                WHERE id=?
                """;
        jdbc.update(query, isActive, accountId);
    }

    @Override
    public void createNewAccount(Account account) {
        String query = """
                INSERT INTO accounts(number,currency_string_code_iso4217,user_id,updated_at)
                VALUES (?,CAST(? AS currency),?,NOW())
                """;
        jdbc.update(query, ps -> {
            ps.setString(1, account.getNumber());
            ps.setString(2, account.getCurrency().name());
            ps.setObject(3, account.getUserId());
        });
    }

    @Override
    public void changeBalance(UUID accountId, BigDecimal amount) {
        String query = """
                UPDATE accounts
                SET balance=balance+?
                WHERE id=?
                """;
        jdbc.update(query, ps -> {
            ps.setBigDecimal(1, amount);
            ps.setObject(2, accountId);
        });
    }

    @Override
    public Boolean isExistActive(UUID accountId) {
        String query = """
                SELECT EXISTS(
                SELECT id FROM accounts WHERE id=? AND is_active=true)
                """;
        return jdbc.queryForObject(query, Boolean.class, accountId);
    }

    @Override
    public Optional<BigDecimal> getBalance(UUID accountId) {
        String query = """
                SELECT balance
                FROM accounts
                WHERE id=? AND is_active=true
                """;
        BigDecimal balance = jdbc.queryForObject(query, BigDecimal.class, accountId);
        return Optional.ofNullable(balance);
    }

    @Override
    public Optional<Account> findActiveAccountById(UUID accountId) {
        String query = """
                SELECT *
                FROM accounts
                WHERE id=? AND is_active=true
                """;
        return Optional.ofNullable(jdbc.queryForObject(query, accountRowMapper, accountId));
    }
}
