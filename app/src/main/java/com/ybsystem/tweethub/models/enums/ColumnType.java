package com.ybsystem.tweethub.models.enums;

import lombok.Getter;

public enum ColumnType {
    HOME("HOME"),
    MENTIONS("MENTIONS"),
    FAVORITE("FAVORITE"),
    LIST("LIST"),
    LIST_SINGLE("LIST_SINGLE"),
    RETWEETED("RETWEETED"),

    TALK("TALK"),
    DETAIL("DETAIL"),
    SEARCH("SEARCH"),

    FOLLOW("FOLLOW"),
    FOLLOWER("FOLLOWER"),
    MUTE("MUTE"),
    BLOCK("BLOCK"),

    USER_TWEET("USER_TWEET"),
    USER_MEDIA("USER_MEDIA"),
    USER_FAVORITE("USER_FAVORITE"),

    LIST_CREATED("LIST_CREATED"),
    LIST_SUBSCRIBING("LIST_SUBSCRIBING"),
    LIST_BELONGING("LIST_BELONGING"),
    LIST_SUBSCRIBER("LIST_SUBSCRIBER"),
    LIST_MEMBER("LIST_MEMBER");

    @Getter
    private final String val;

    private ColumnType(final String val) {
        this.val = val;
    }

    public static ColumnType toEnum(String val) {
        for (ColumnType obj : ColumnType.values()) {
            if (obj.getVal().equals(val)) {
                return obj;
            }
        }
        return null;
    }
}
