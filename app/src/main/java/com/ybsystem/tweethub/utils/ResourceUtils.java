package com.ybsystem.tweethub.utils;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.appcompat.content.res.AppCompatResources;

import com.ybsystem.tweethub.R;
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

    public static int getBackgroundColor() {
        return getColor(android.R.attr.colorBackground);
    }

    public static int getTextColor() {
        return getColor(android.R.attr.textColor);
    }

    public static int getPrimaryColor() {
        return getColor(android.R.attr.colorPrimary);
    }

    public static int getAccentColor() {
        return getColor(android.R.attr.colorAccent);
    }

    public static int getHighlightControl() {
        return getColor(android.R.attr.colorControlHighlight);
    }

    public static int getBorderColor() {
        return getColor(R.attr.colorBorder);
    }

    public static int getDividerColor() {
        return getColor(R.attr.colorDivider);
    }

    public static int getDisabledTextColor() {
        return getColor(R.attr.colorDisabledText);
    }

    public static int getIconColor() {
        return getColor(R.attr.colorIcon);
    }

    public static int getLinkColor() {
        return getColor(R.attr.colorLink);
    }

}
