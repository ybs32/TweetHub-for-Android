package com.ybsystem.tweetmate.models.enums;

import lombok.Getter;

public enum TweetMenu {
    REPLY("REPLY"),
    RETWEET("RETWEET"),
    LIKE("LIKE"),
    TALK("TALK"),
    DELETE("DELETE"),
    URL("URL"),
    HASH("HASH"),
    USER("USER"),
    DETAIL("DETAIL"),
    COPY("COPY"),
    SHARE("SHARE"),
    TWITTER("TWITTER");

    @Getter
    private final String val;

    TweetMenu(final String val) {
        this.val = val;
    }

    public static TweetMenu toEnum(String val) {
        for (TweetMenu obj : TweetMenu.values()) {
            if (obj.getVal().equals(val)) {
                return obj;
            }
        }
        return null;
    }
}
