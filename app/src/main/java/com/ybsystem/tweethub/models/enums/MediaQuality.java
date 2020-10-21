package com.ybsystem.tweethub.models.enums;

import lombok.Getter;

public enum MediaQuality {
    LOW("LOW"),
    MIDDLE("MIDDLE"),
    HIGH("HIGH");

    @Getter
    private final String val;

    private MediaQuality(final String val) {
        this.val = val;
    }

    public static MediaQuality toEnum(String val) {
        for (MediaQuality obj : MediaQuality.values()) {
            if (obj.getVal().equals(val)) {
                return obj;
            }
        }
        return null;
    }
}
