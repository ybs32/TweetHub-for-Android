package com.ybsystem.tweetmate.databases;

import android.content.SharedPreferences;

import com.ybsystem.tweetmate.R;

import static com.ybsystem.tweetmate.resources.ResColor.*;
import static com.ybsystem.tweetmate.resources.ResString.*;

public class PrefTheme extends PrefBase {

    public static final String KEY_THEME_SETTING = RES.getString(R.string.pref_key_theme_setting);
    public static final String KEY_THEME_CUSTOM = RES.getString(R.string.pref_key_theme_custom);

    public static final String KEY_RETWEET_COLOR = RES.getString(R.string.pref_key_retweet_color);
    public static final String KEY_REPLY_COLOR = RES.getString(R.string.pref_key_reply_color);
    public static final String KEY_MYTWEET_COLOR = RES.getString(R.string.pref_key_mytweet_color);

    public static final String KEY_USERNAME_COLOR = RES.getString(R.string.pref_key_username_color);
    public static final String KEY_RELATIVETIME_COLOR = RES.getString(R.string.pref_key_relativetime_color);
    public static final String KEY_TWEETTEXT_COLOR = RES.getString(R.string.pref_key_tweettext_color);
    public static final String KEY_LINK_COLOR = RES.getString(R.string.pref_key_link_color);

    public static final String KEY_ABSOLUTETIME_COLOR = RES.getString(R.string.pref_key_absolutetime_color);
    public static final String KEY_VIA_COLOR = RES.getString(R.string.pref_key_via_color);
    public static final String KEY_RTFAV_COLOR = RES.getString(R.string.pref_key_rtfav_color);
    public static final String KEY_RETWEETEDBY_COLOR = RES.getString(R.string.pref_key_retweetedby_color);

    public static final String KEY_THEME_PREVIEW = RES.getString(R.string.pref_key_theme_preview);

    private PrefTheme() {
    }

    // ----- Theme -----

    public static String getTheme() {
        return getDefaultSharedPreferences().getString(KEY_THEME_SETTING, "LIGHT");
    }

    public static boolean isCustomThemeEnabled() {
        return getDefaultSharedPreferences().getBoolean(KEY_THEME_CUSTOM, false);
    }

    // ----- Timeline background color -----

    public static int getBgRetweetColor() {
        int color = getDefaultSharedPreferences().getInt(KEY_RETWEET_COLOR, -1);
        if (PrefWallpaper.getWallpaperPath().equals(STR_NOT_SET)) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

    public static int getBgReplyColor() {
        int color = getDefaultSharedPreferences().getInt(KEY_REPLY_COLOR, -1);
        if (PrefWallpaper.getWallpaperPath().equals(STR_NOT_SET)) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

    public static int getBgMyTweetColor() {
        int color = getDefaultSharedPreferences().getInt(KEY_MYTWEET_COLOR, -1);
        if (PrefWallpaper.getWallpaperPath().equals(STR_NOT_SET)) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

    // ----- Timeline text color 1 -----

    public static int getUserNameColor() {
        return getDefaultSharedPreferences().getInt(KEY_USERNAME_COLOR, -1);
    }

    public static int getRelativeTimeColor() {
        return getDefaultSharedPreferences().getInt(KEY_RELATIVETIME_COLOR, -1);
    }

    public static int getTweetTextColor() {
        return getDefaultSharedPreferences().getInt(KEY_TWEETTEXT_COLOR, -1);
    }

    public static int getLinkColor() {
        return getDefaultSharedPreferences().getInt(KEY_LINK_COLOR, -1);
    }

    // ----- Timeline text color 2 -----

    public static int getAbsoluteTimeColor() {
        return getDefaultSharedPreferences().getInt(KEY_ABSOLUTETIME_COLOR, -1);
    }

    public static int getViaColor() {
        return getDefaultSharedPreferences().getInt(KEY_VIA_COLOR, -1);
    }

    public static int getRtFavColor() {
        return getDefaultSharedPreferences().getInt(KEY_RTFAV_COLOR, -1);
    }

    public static int getRetweetedByColor() {
        return getDefaultSharedPreferences().getInt(KEY_RETWEETEDBY_COLOR, -1);
    }

    // For initialize
    public static void initCustomColors() {
        SharedPreferences.Editor editor = getDefaultSharedPreferences().edit();
        editor.putInt(KEY_RETWEET_COLOR, COLOR_BG_RETWEET);
        editor.putInt(KEY_REPLY_COLOR, COLOR_BG_REPLY);
        editor.putInt(KEY_MYTWEET_COLOR, COLOR_BG_MYTWEET);
        editor.putInt(KEY_USERNAME_COLOR, COLOR_TEXT);
        editor.putInt(KEY_RELATIVETIME_COLOR, COLOR_TEXT);
        editor.putInt(KEY_TWEETTEXT_COLOR, COLOR_TEXT);
        editor.putInt(KEY_LINK_COLOR, COLOR_LINK);
        editor.putInt(KEY_ABSOLUTETIME_COLOR, COLOR_STRONG);
        editor.putInt(KEY_VIA_COLOR, COLOR_STRONG);
        editor.putInt(KEY_RTFAV_COLOR, COLOR_STRONG);
        editor.putInt(KEY_RETWEETEDBY_COLOR, COLOR_STRONG);
        editor.apply();
    }

}
