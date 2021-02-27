package com.ybsystem.tweethub.resources;

import android.util.TypedValue;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.storages.PrefWallpaper;

public class ResColor {

    public static int BACKGROUND;
    public static int TEXT;
    public static int PRIMARY;
    public static int ACCENT;
    public static int HIGHLIGHT;

    public static int WEAK;
    public static int MIDDLE;
    public static int STRONG;

    public static int RETWEET;
    public static int FAVORITE;
    public static int LIKE;
    public static int TALK;
    public static int DELETE;
    public static int LINK;
    public static int LINK_WEAK;

    public static int BG_RETWEET;
    public static  int BG_REPLY;
    public static int BG_MYTWEET;

    public static void init() {
        BACKGROUND = getColor(android.R.attr.colorBackground);
        TEXT = getColor(android.R.attr.textColor);
        PRIMARY = getColor(android.R.attr.colorPrimary);
        ACCENT = getColor(android.R.attr.colorAccent);
        HIGHLIGHT = getColor(android.R.attr.colorControlHighlight);

        WEAK = getColor(R.attr.colorWeak);
        MIDDLE = getColor(R.attr.colorMiddle);
        STRONG = getColor(R.attr.colorStrong);

        RETWEET = getColor(R.attr.colorRetweet);
        FAVORITE = getColor(R.attr.colorFavorite);
        LIKE = getColor(R.attr.colorLike);
        TALK = getColor(R.attr.colorTalk);
        DELETE = getColor(R.attr.colorDelete);
        LINK = getColor(R.attr.colorLink);
        LINK_WEAK = getColor(R.attr.colorLinkWeak);

        BG_RETWEET = getBgRetweetColor();
        BG_REPLY = getBgReplyColor();
        BG_MYTWEET = getBgMyTweetColor();
    }

    private static int getColor(int resId) {
        TypedValue typedValue = new TypedValue();
        TweetHubApp.getActivity().getTheme()
                .resolveAttribute(resId, typedValue, true);
        return typedValue.data;
    }

    private static int getBgRetweetColor() {
        int color = getColor(R.attr.colorBgRetweet);
        if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

    private static int getBgReplyColor() {
        int color = getColor(R.attr.colorBgReply);
        if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

    private static int getBgMyTweetColor() {
        int color = getColor(R.attr.colorBgMyTweet);
        if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

}
