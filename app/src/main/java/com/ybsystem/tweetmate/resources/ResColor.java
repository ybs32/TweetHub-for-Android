package com.ybsystem.tweetmate.resources;

import android.util.TypedValue;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.storages.PrefWallpaper;

public class ResColor {

    public static int COLOR_BACKGROUND;
    public static int COLOR_TEXT;
    public static int COLOR_PRIMARY;
    public static int COLOR_ACCENT;
    public static int COLOR_HIGHLIGHT;

    public static int COLOR_WEAK;
    public static int COLOR_MIDDLE;
    public static int COLOR_STRONG;

    public static int COLOR_RETWEET;
    public static int COLOR_FAVORITE;
    public static int COLOR_LIKE;
    public static int COLOR_TALK;
    public static int COLOR_DELETE;
    public static int COLOR_LINK;
    public static int COLOR_LINK_WEAK;

    public static int COLOR_BG_RETWEET;
    public static  int COLOR_BG_REPLY;
    public static int COLOR_BG_MYTWEET;

    public static void init() {
        COLOR_BACKGROUND = getColor(android.R.attr.colorBackground);
        COLOR_TEXT = getColor(android.R.attr.textColor);

        COLOR_PRIMARY = getColor(android.R.attr.colorPrimary);
        COLOR_ACCENT = getColor(android.R.attr.colorAccent);
        COLOR_HIGHLIGHT = getColor(android.R.attr.colorControlHighlight);

        COLOR_WEAK = getColor(R.attr.colorWeak);
        COLOR_MIDDLE = getColor(R.attr.colorMiddle);
        COLOR_STRONG = getColor(R.attr.colorStrong);

        COLOR_RETWEET = getColor(R.attr.colorRetweet);
        COLOR_FAVORITE = getColor(R.attr.colorFavorite);
        COLOR_LIKE = getColor(R.attr.colorLike);
        COLOR_TALK = getColor(R.attr.colorTalk);
        COLOR_DELETE = getColor(R.attr.colorDelete);
        COLOR_LINK = getColor(R.attr.colorLink);
        COLOR_LINK_WEAK = getColor(R.attr.colorLinkWeak);

        COLOR_BG_RETWEET = getBackgroundColor(R.attr.colorBgRetweet);
        COLOR_BG_REPLY = getBackgroundColor(R.attr.colorBgReply);
        COLOR_BG_MYTWEET = getBackgroundColor(R.attr.colorBgMyTweet);
    }

    /**
     * Get resource color
     *
     * @param resId Resource id
     * @return Color int
     */
    private static int getColor(int resId) {
        TypedValue typedValue = new TypedValue();
        TweetMateApp.getActivity().getTheme()
                .resolveAttribute(resId, typedValue, true);
        return typedValue.data;
    }

    /**
     * Get background color
     *
     * @param resId Resource id
     * @return Color int
     */
    private static int getBackgroundColor(int resId) {
        int color = getColor(resId);
        if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

}
