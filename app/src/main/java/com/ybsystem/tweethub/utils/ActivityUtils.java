package com.ybsystem.tweethub.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Utils class for activities
 */
public class ActivityUtils {

    private ActivityUtils() {
    }

    /**
     * Reboot activity with animation
     *
     * @param activity Target activity
     * @param enterAnim Enter animation
     * @param exitAnim Exit animation
     */
    public static void rebootActivity(Activity activity, int enterAnim, int exitAnim) {
        Intent intent = activity.getIntent();
        activity.finish();
        activity.overridePendingTransition(enterAnim, exitAnim);
        activity.startActivity(intent);
    }
}
