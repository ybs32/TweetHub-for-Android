package com.ybsystem.tweethub.models.enums;

import lombok.Getter;

public enum UserIconStyle {
    CIRCLE("CIRCLE"),
    SQUARE("SQUARE");

    @Getter
    private final String val;

    UserIconStyle(final String val) {
        this.val = val;
    }

    public static UserIconStyle toEnum(String val) {
        for (UserIconStyle obj : UserIconStyle.values()) {
            if (obj.getVal().equals(val)) {
                return obj;
            }
        }
        return null;
    }
}
