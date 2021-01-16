package com.ybsystem.tweethub.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.ybsystem.tweethub.application.TweetHubApp;

/**
 * Utils class for calculation
 */
public class CalcUtils {

    private CalcUtils() {
    }

    /**
     * Convert dp to pixel
     * @param dp DP size
     * @return Pixel size
     */
    public static int convertDp2Px(int dp){
        Resources res = TweetHubApp.getInstance().getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        return (int) (dp * metrics.density);
    }

    /**
     * Convert dp to sp
     * @param dp DP size
     * @return SP size
     */
    public static int convertDp2Sp(int dp) {
        Resources res = TweetHubApp.getInstance().getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        return (int) (convertDp2Px(dp) / metrics.scaledDensity);
    }

}
