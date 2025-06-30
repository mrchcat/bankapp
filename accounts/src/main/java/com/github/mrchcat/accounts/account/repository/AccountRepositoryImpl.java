package com.github.mrchcat.accounts.account.repository;

import com.github.mrchcat.accounts.account.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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
                SELECT a.id,a.number,a.balance, cr.string_code_iso4217, cr.digital_code_iso4217, cr.ru_name,
                a.user_id,a.created_at,a.updated_at, a.is_active
                FROM accounts AS a JOIN currencies AS cr ON a.currency_string_code_iso4217=cr.string_code_iso4217
                WHERE a.user_id=? AND is_active=true;
                """;
        return jdbc.query(query, accountRowMapper, userId);
    }

//    @Override
//    public void deactivateEmptyAccounts(List<UUID> accountsId) {
//        String query = """
//                UPDATE accounts
//                SET is_active=false, updated_at=NOW()
//                WHERE id=? AND balance=0 AND is_active=true;
//                """;
//        jdbc.batchUpdate(query, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                ps.setString(1, accountsId.get(i).toString());
//            }
//
//            @Override
//            public int getBatchSize() {
//                return accountsId.size();
//            }
//        });
//    }

//    @Override
//    public void deactivateEmptyAccounts(List<UUID> accountsId) {
//        String query = """
//                UPDATE accounts
//                SET is_active=false, updated_at=NOW()
//                WHERE id=? AND balance=0 AND is_active=true;
//                """;
//        int changedRows = jdbc.update(query, accountId);
//        if (changedRows == 0) {
//            throw new IllegalArgumentException("аккаунт id=" + accountId + "не существует или не пуст");
//        }
//    }


    //    @Override
//    public Optional<Account> findAccountById(UUID accountId) {
//        String query = """
//                SELECT a.id,a.number,a.balance, cr.string_code_iso4217, cr.digital_code_iso4217, cr.ru_name,
//                a.user_id,a.created_at,a.updated_at, a.is_active
//                FROM accounts AS a JOIN currencies AS cr ON a.currency_string_code_iso4217=cr.string_code_iso4217
//                WHERE a.id=:?
//                """;
//        return Optional.ofNullable(jdbc.queryForObject(query, accountRowMapper, accountId));
//    }


}
