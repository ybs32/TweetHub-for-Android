package com.ybsystem.tweethub.storages;

import com.ybsystem.tweethub.R;

public class PrefAppearance extends PrefBase {

    public static final String KEY_SIZE_FONT = RES.getString(R.string.pref_key_size_font);
    public static final String KEY_SIZE_USER_ICON = RES.getString(R.string.pref_key_size_user_icon);

    public static final String KEY_STYLE_LIKE_FAV = RES.getString(R.string.pref_key_style_like_fav);
    public static final String KEY_STYLE_USER_ICON = RES.getString(R.string.pref_key_style_user_icon);

    public static final String KEY_SHOW_ABSOLUTE_TIME = RES.getString(R.string.pref_key_show_absolute_time);
    public static final String KEY_SHOW_VIA = RES.getString(R.string.pref_key_show_via);
    public static final String KEY_SHOW_RT_FAV_COUNT = RES.getString(R.string.pref_key_show_rt_fav_count);
    public static final String KEY_SHOW_RETWEETED_BY = RES.getString(R.string.pref_key_show_retweeted_by);
    public static final String KEY_SHOW_THUMBNAIL = RES.getString(R.string.pref_key_show_thumbnail);

    private PrefAppearance() {
    }

    // ----- サイズ -----

    public static int getFontSize() {
        return Integer.parseInt(
                getDefaultSharedPreferences().getString(KEY_SIZE_FONT, "14")
        );
    }

    public static int getUserIconSize() {
        return Integer.parseInt(
                getDefaultSharedPreferences().getString(KEY_SIZE_USER_ICON, "48")
        );
    }

    // ----- スタイル -----

    public static String getLikeFavText() {
        String style = getDefaultSharedPreferences().getString(KEY_STYLE_LIKE_FAV, "LIKE");
        if (style.equals("LIKE")) {
            return "いいね";
        } else {
            return "お気に入り";
        }
    }

    public static int getLikeFavDrawable() {
        String style = getDefaultSharedPreferences().getString(KEY_STYLE_LIKE_FAV, "LIKE");
        if (style.equals("LIKE")) {
            return R.drawable.ic_like;
        } else {
            return R.drawable.ic_favorite;
        }
    }

    public static String getUserIconStyle() {
        return getDefaultSharedPreferences().getString(KEY_STYLE_USER_ICON, "CIRCLE");
    }

    // ----- 表示項目 -----

    public static boolean isShowAbsoluteTime() {
        return getDefaultSharedPreferences().getBoolean(KEY_SHOW_ABSOLUTE_TIME, false);
    }

    public static boolean isShowVia() {
        return getDefaultSharedPreferences().getBoolean(KEY_SHOW_VIA, false);
    }

    public static boolean isShowRtFavCount() {
        return getDefaultSharedPreferences().getBoolean(KEY_SHOW_RT_FAV_COUNT, false);
    }

    public static boolean isShowRetweetedBy() {
        return getDefaultSharedPreferences().getBoolean(KEY_SHOW_RETWEETED_BY, false);
    }

    public static boolean isShowThumbnail() {
        return getDefaultSharedPreferences().getBoolean(KEY_SHOW_THUMBNAIL, true);
    }

}