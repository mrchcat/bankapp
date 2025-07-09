package com.github.mrchcat.accounts.exceptions;

import java.util.List;

public class UserNotUniqueProperties extends IllegalArgumentException {
    final List<String> duplicateProperties;

    public UserNotUniqueProperties(List<String> duplicateProperties) {
        super();
        this.duplicateProperties = duplicateProperties;
    }
}
