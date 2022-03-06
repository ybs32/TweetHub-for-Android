package com.ybsystem.tweetmate.models.enums;

import lombok.Getter;

public enum LikeStyle {
    LIKE("LIKE"),
    FAVORITE("FAVORITE");

    @Getter
    private final String val;

    LikeStyle(final String val) {
        this.val = val;
    }

    public static LikeStyle toEnum(String val) {
        for (LikeStyle obj : LikeStyle.values()) {
            if (obj.getVal().equals(val)) {
                return obj;
            }
        }
        return null;
    }
}
