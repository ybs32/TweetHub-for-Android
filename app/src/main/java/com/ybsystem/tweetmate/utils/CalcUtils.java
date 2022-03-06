package com.ybsystem.tweetmate.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.ybsystem.tweetmate.application.TweetMateApp;

/**
 * Utils class for calculation
 */
public class CalcUtils {

    private CalcUtils() {
    }

    /**
     * Convert dp to pixel size
     *
     * @param dp DP size
     * @return Pixel size
     */
    public static int convertDp2Px(int dp){
        Resources res = TweetMateApp.getInstance().getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        return (int) (dp * metrics.density);
    }

    /**
     * Convert dp to sp size
     *
     * @param dp DP size
     * @return SP size
     */
    public static int convertDp2Sp(int dp) {
        Resources res = TweetMateApp.getInstance().getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        return (int) (convertDp2Px(dp) / metrics.scaledDensity);
    }

}
