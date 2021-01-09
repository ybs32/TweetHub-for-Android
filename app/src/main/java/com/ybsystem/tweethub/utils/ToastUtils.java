package com.ybsystem.tweethub.utils;

import android.widget.Toast;

import com.ybsystem.tweethub.application.TweetHubApp;

/**
 * Utils class for toast creation
 */
public class ToastUtils {

    private ToastUtils() {
    }

    /**
     * Show toast about 2 seconds
     * @param text Display text
     */
    public static void showShortToast(final String text) {
        Toast.makeText(TweetHubApp.getInstance().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show toast about 4 seconds
     * @param text Display text
     */
    public static void showLongToast(final String text) {
        Toast.makeText(TweetHubApp.getInstance().getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

}
