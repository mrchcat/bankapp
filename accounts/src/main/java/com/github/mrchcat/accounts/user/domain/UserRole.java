package com.github.mrchcat.accounts.user.domain;

public enum UserRole {
    ADMIN("ADMIN"), USER("USER");

    public final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }
}
