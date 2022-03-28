package com.ybsystem.tweetmate.utils;

import android.widget.Toast;

import com.ybsystem.tweetmate.application.TweetMateApp;

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
        Toast.makeText(TweetMateApp.getInstance().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show toast about 4 seconds
     * @param text Display text
     */
    public static void showLongToast(final String text) {
        Toast.makeText(TweetMateApp.getInstance().getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

}
