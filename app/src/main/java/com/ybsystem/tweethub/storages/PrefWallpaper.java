package com.ybsystem.tweethub.storages;

import android.content.SharedPreferences;

import com.ybsystem.tweethub.R;

public class PrefWallpaper extends PrefBase {

    public static final String KEY_ADD_WALLPAPER = RES.getString(R.string.pref_key_add_wallpaper);
    public static final String KEY_DELETE_WALLPAPER = RES.getString(R.string.pref_key_delete_wallpaper);
    public static final String KEY_WALLPAPER_PATH = RES.getString(R.string.pref_key_wallpaper_path);
    public static final String KEY_WALLPAPER_TRANSPARENCY = RES.getString(R.string.pref_key_wallpaper_transparency);

    private PrefWallpaper() {
    }

    public static String getWallpaperPath() {
        return getDefaultSharedPreferences().getString(KEY_WALLPAPER_PATH, "未設定");
    }

    public static void saveWallpaperPath(String wallpaperPath) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences().edit();
        editor.putString(KEY_WALLPAPER_PATH, wallpaperPath);
        editor.apply();
    }

    public static int applyTransparency(int color) {
        String transparency = getDefaultSharedPreferences().getString(KEY_WALLPAPER_TRANSPARENCY, "#CC");
        switch (transparency) {
            case "#E6":
                return (color & 0x00FFFFFF) | (0xE6 << 24);
            case "#CC":
                return (color & 0x00FFFFFF) | (0xCC << 24);
            case "#B3":
                return (color & 0x00FFFFFF) | (0xB3 << 24);
            case "#99":
                return (color & 0x00FFFFFF) | (0x99 << 24);
            case "#80":
                return (color & 0x00FFFFFF) | (0x80 << 24);
            default:
                return (color & 0x00FFFFFF) | (0xCC << 24);
        }
    }

}
