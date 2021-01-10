package com.ybsystem.tweethub.utils;

import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.TypedValue;

import androidx.appcompat.content.res.AppCompatResources;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.storages.PrefWallpaper;

/**
 * Utils class for resource processing
 */
public class ResourceUtils {

    private ResourceUtils() {
    }

    /**
     * Get resource text
     *
     * @param resId Resource id
     * @return Text
     */
    public static String getText(int resId) {
        return TweetHubApp.getActivity().getString(resId);
    }

    /**
     * Get resource drawable
     *
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
     *
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

    public static int getWeakColor() {
        return getColor(R.attr.colorWeak);
    }

    public static int getMiddleColor() {
        return getColor(R.attr.colorMiddle);
    }

    public static int getStrongColor() {
        return getColor(R.attr.colorStrong);
    }

    public static int getRetweetColor() {
        return getColor(R.attr.colorRetweet);
    }

    public static int getFavoriteColor() {
        return getColor(R.attr.colorFavorite);
    }

    public static int getLikeColor() {
        return getColor(R.attr.colorLike);
    }

    public static int getTalkColor() {
        return getColor(R.attr.colorTalk);
    }

    public static int getDeleteColor() {
        return getColor(R.attr.colorDelete);
    }

    public static int getLinkColor() {
        return getColor(R.attr.colorLink);
    }

    public static int getLinkWeakColor() {
        return getColor(R.attr.colorLinkWeak);
    }

    public static int getBgRetweetColor() {
        int color = getColor(R.attr.colorBgRetweet);
        if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

    public static int getBgReplyColor() {
        int color = getColor(R.attr.colorBgReply);
        if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

    public static int getBgMyTweetColor() {
        int color = getColor(R.attr.colorBgMyTweet);
        if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

    public static String getK() {
        String s = getText(R.string.hello_android);
        byte[] b = Base64.decode(
                new StringBuilder(s).reverse().toString(), Base64.NO_WRAP);
        return new String(b);
    }

    public static String getS() {
        String s = getText(R.string.hello_java);
        byte[] b = Base64.decode(
                new StringBuilder(s).reverse().toString(), Base64.NO_WRAP);
        return new String(b);
    }

}
