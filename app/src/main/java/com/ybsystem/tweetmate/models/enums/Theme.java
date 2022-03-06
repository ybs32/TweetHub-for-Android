package com.ybsystem.tweetmate.models.enums;

import lombok.Getter;

public enum Theme {
    LIGHT("LIGHT"),
    DARK("DARK");

    @Getter
    private final String val;

    Theme(final String val) {
        this.val = val;
    }

    public static Theme toEnum(String val) {
        for (Theme obj : Theme.values()) {
            if (obj.getVal().equals(val)) {
                return obj;
            }
        }
        return null;
    }
}
