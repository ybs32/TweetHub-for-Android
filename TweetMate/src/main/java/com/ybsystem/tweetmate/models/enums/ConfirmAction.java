package com.ybsystem.tweetmate.models.enums;

import lombok.Getter;

public enum ConfirmAction {
    TWEET("TWEET"),
    RETWEET("RETWEET"),
    LIKE("LIKE"),
    DELETE("DELETE"),
    FOLLOW("FOLLOW"),
    MUTE("MUTE"),
    BLOCK("BLOCK"),
    SUBSCRIBE("SUBSCRIBE"),
    FINISH("FINISH");

    @Getter
    private final String val;

    ConfirmAction(final String val) {
        this.val = val;
    }

    public static ConfirmAction toEnum(String val) {
        for (ConfirmAction obj : ConfirmAction.values()) {
            if (obj.getVal().equals(val)) {
                return obj;
            }
        }
        return null;
    }
}
