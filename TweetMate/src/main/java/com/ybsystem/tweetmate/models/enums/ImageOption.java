package com.ybsystem.tweetmate.models.enums;

import lombok.Getter;

public enum ImageOption {
    NONE("NONE"),
    SQUARE("SQUARE"),
    CIRCLE("CIRCLE");

    @Getter
    private final String val;

    private ImageOption(final String val) {
        this.val = val;
    }

    public static ImageOption toEnum(String val) {
        for (ImageOption obj : ImageOption.values()) {
            if (obj.getVal().equals(val)) {
                return obj;
            }
        }
        return null;
    }
}
