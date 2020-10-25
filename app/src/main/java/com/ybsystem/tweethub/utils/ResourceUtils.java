package com.ybsystem.tweethub.utils;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.appcompat.content.res.AppCompatResources;

import com.ybsystem.tweethub.application.TweetHubApp;

/**
 * Utils class for resource processing
 */
public class ResourceUtils {

    private ResourceUtils() {
    }

    /**
     * Get resource text
     * @param resId Resource id
     * @return Text
     */
    public static String getText(int resId) {
        return TweetHubApp.getActivity().getString(resId);
    }

    /**
     * Get resource drawable
     * @param resId Resource id
     * @return Drawable
     */
    public static Drawable getDrawable(int resId) {
        return AppCompatResources.getDrawable(
                TweetHubApp.getActivity(), resId
        );
    }

    /**
     * Get resource color
     * @param resId Resource id
     * @return Color
     */
    public static int getColor(int resId) {
        TypedValue typedValue = new TypedValue();
        TweetHubApp.getActivity().getTheme()
                .resolveAttribute(resId, typedValue, true);
        return typedValue.data;
    }

}
