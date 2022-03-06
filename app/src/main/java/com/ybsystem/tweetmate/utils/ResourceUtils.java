package com.ybsystem.tweetmate.utils;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.appcompat.content.res.AppCompatResources;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.storages.PrefWallpaper;

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
        return TweetMateApp.getActivity().getString(resId);
    }

    /**
     * Get resource drawable
     *
     * @param resId Resource id
     * @return Drawable
     */
    public static Drawable getDrawable(int resId) {
        return AppCompatResources.getDrawable(
                TweetMateApp.getActivity(), resId
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
        TweetMateApp.getActivity().getTheme()
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

    public static int getUserNameColor() {
        return getColor(R.attr.colorUserName);
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

}