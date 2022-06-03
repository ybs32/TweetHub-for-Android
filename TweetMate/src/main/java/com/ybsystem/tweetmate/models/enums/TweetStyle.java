package com.ybsystem.tweetmate.models.enums;

import lombok.Getter;

public enum TweetStyle {
    ON_THE_TIMELINE("ON_THE_TIMELINE"),
    FLOATING_BUTTON("FLOATING_BUTTON");

    @Getter
    private final String val;

    TweetStyle(final String val) {
        this.val = val;
    }

    public static TweetStyle toEnum(String val) {
        for (TweetStyle obj : TweetStyle.values()) {
            if (obj.getVal().equals(val)) {
                return obj;
            }
        }
        return null;
    }
}
