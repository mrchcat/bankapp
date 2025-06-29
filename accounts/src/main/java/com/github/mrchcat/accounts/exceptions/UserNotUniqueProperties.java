package com.github.mrchcat.accounts.exceptions;

import java.util.List;
import java.util.Map;

public class UserNotUniqueProperties extends IllegalArgumentException {
    List<String> duplicateProperties;

    public UserNotUniqueProperties(List<String> duplicateProperties) {
        super();
        this.duplicateProperties = duplicateProperties;
    }
}
