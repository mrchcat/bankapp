package com.github.mrchcat.accounts.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table("users")
public class BankUser {
    @Id
    UUID id;

    @Column("full_name")
    String fullName;

    @Column("birth_day")
    LocalDate birthDay;

    @Column("email")
    String email;

    @Column("username")
    String username;

    @Column("password")
    String password;

    @Column("roles")
    String roles;

    @Column("enabled")
    boolean enabled;

    @Column("created_at")
    LocalDateTime createdAt;

    @Column("updated_at")
    LocalDateTime updatedAt;
}
