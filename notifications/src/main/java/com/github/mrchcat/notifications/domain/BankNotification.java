package com.github.mrchcat.notifications.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Getter
@Setter
@Table("notifications")
@ToString
public class BankNotification {
    @Id
    long id;
    @Column("service")
    String service;
    @Column("username")
    String username;
    @Column("full_name")
    String fullName;
    @Column("email")
    String email;
    @Column("message")
    String message;
    @Column("is_processed")
    boolean isProcessed;
}