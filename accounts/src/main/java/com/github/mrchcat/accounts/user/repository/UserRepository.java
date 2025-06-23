package com.github.mrchcat.accounts.user.repository;

import com.github.mrchcat.accounts.user.domain.BankUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<BankUser, UUID> {

    Optional<BankUser> findByUsername(String username);

}
