package com.ybsystem.tweethub.storages;

import android.content.SharedPreferences;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.utils.ResourceUtils;

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

    private PrefTheme() {
    }

    // ----- テーマ -----

    public static String getTheme() {
        return getDefaultSharedPreferences().getString(KEY_THEME_SETTING, "LIGHT");
    }

    public static boolean isCustomThemeEnabled() {
        return getDefaultSharedPreferences().getBoolean(KEY_THEME_CUSTOM, false);
    }

    // ----- タイムライン背景色 -----

    public static int getRetweetColor() {
        int color = getDefaultSharedPreferences().getInt(KEY_RETWEET_COLOR, -1);
        if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

    public static int getReplyColor() {
        int color = getDefaultSharedPreferences().getInt(KEY_REPLY_COLOR, -1);
        if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

    public static int getMyTweetColor() {
        int color = getDefaultSharedPreferences().getInt(KEY_MYTWEET_COLOR, -1);
        if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
            return color;
        } else {
            return PrefWallpaper.applyTransparency(color);
        }
    }

    // ----- タイムライン文字色１ -----

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

    // ----- タイムライン文字色２ -----

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

    // 初期化用
    public static void initCustomColors() {
        SharedPreferences.Editor editor = getDefaultSharedPreferences().edit();
        editor.putInt(KEY_RETWEET_COLOR, ResourceUtils.getBackgroundColor());
        editor.putInt(KEY_REPLY_COLOR, ResourceUtils.getBackgroundColor());
        editor.putInt(KEY_MYTWEET_COLOR, ResourceUtils.getBackgroundColor());
        editor.putInt(KEY_USERNAME_COLOR, ResourceUtils.getTextColor());
        editor.putInt(KEY_RELATIVETIME_COLOR, ResourceUtils.getTextColor());
        editor.putInt(KEY_TWEETTEXT_COLOR, ResourceUtils.getTextColor());
        editor.putInt(KEY_LINK_COLOR, ResourceUtils.getLinkColor());
        editor.putInt(KEY_ABSOLUTETIME_COLOR, ResourceUtils.getTextColor());
        editor.putInt(KEY_VIA_COLOR, ResourceUtils.getTextColor());
        editor.putInt(KEY_RTFAV_COLOR, ResourceUtils.getTextColor());
        editor.putInt(KEY_RETWEETEDBY_COLOR, ResourceUtils.getTextColor());
        editor.apply();
    }

}
