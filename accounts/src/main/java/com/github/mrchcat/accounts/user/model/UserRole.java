package com.github.mrchcat.accounts.user.model;

public enum UserRole {
    ADMIN("ADMIN", "/adminPanel"),
    CLIENT("CLIENT", "/main"),
    MANAGER("MANAGER", "/registration");

    public final String roleName;
    public final String urlAfterSuccessLogin;


    UserRole(String roleName, String urlAfterSuccessLogin) {
        this.roleName = roleName;
        this.urlAfterSuccessLogin = urlAfterSuccessLogin;
    }
}
