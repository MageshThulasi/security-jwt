package com.lexisnexis.batch.security.user;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum UserRoles {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private String code;
    private static final Map<String, UserRoles> ALL_ROLES;

    private UserRoles(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    static {
        ALL_ROLES = Arrays.asList(UserRoles.values())
                        .stream()
                        .collect(Collectors.toMap(role -> role.code(), role -> role));
    }

    public static UserRoles get(String codeString) {
        return ALL_ROLES.get(codeString);
    }
}
