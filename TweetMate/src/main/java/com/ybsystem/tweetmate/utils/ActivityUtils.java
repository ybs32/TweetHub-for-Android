package com.ybsystem.tweetmate.utils;

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
     */
    public static void rebootActivity(Activity activity) {
        Intent intent = activity.getIntent();
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
    }

}
