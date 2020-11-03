package com.ybsystem.tweethub.fragments.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.preference.AppearanceActivity;
import com.ybsystem.tweethub.activities.preference.ClickActionActivity;
import com.ybsystem.tweethub.activities.preference.SystemActivity;
import com.ybsystem.tweethub.activities.preference.ThemeActivity;
import com.ybsystem.tweethub.activities.preference.WallpaperActivity;
import com.ybsystem.tweethub.application.TweetHubApp;

public class SettingFragment extends PreferenceFragmentBase {

    private static final Resources res = TweetHubApp.getInstance().getResources();

    // 各種設定
    private static final String THEME = res.getString(R.string.pref_key_theme);
    private static final String APPEARANCE = res.getString(R.string.pref_key_appearance);
    private static final String CLICK_ACTION = res.getString(R.string.pref_key_click_action);
    private static final String SYSTEM = res.getString(R.string.pref_key_system);
    private static final String WALLPAPER = res.getString(R.string.pref_key_wallpaper);

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {

        setPreferencesFromResource(R.xml.preferences, rootKey);

        // テーマ設定
        findPreference(THEME).setOnPreferenceClickListener(
                preference -> {
                    intentTo(ThemeActivity.class);
                    return true;
                }
        );

        // タイムライン表示
        findPreference(APPEARANCE).setOnPreferenceClickListener(
                preference -> {
                    intentTo(AppearanceActivity.class);
                    return true;
                }
        );

        // タップ動作
        findPreference(CLICK_ACTION).setOnPreferenceClickListener(
                preference -> {
                    intentTo(ClickActionActivity.class);
                    return true;
                }
        );

        // システム動作
        findPreference(SYSTEM).setOnPreferenceClickListener(
                preference -> {
                    intentTo(SystemActivity.class);
                    return true;
                }
        );

        // 壁紙設定
        findPreference(WALLPAPER).setOnPreferenceClickListener(
                preference -> {
                    intentTo(WallpaperActivity.class);
                    return true;
                }
        );
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Do nothing because no changes to SharedPreferences occur on this fragment.
    }

    private void intentTo(Class activityClass) {
        Intent intent = new Intent(getActivity(), activityClass);
        getActivity().startActivityForResult(intent, 0);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

}
