package com.ybsystem.tweetmate.fragments.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.activities.preference.*;
import com.ybsystem.tweetmate.application.TweetMateApp;

public class SettingFragment extends PreferenceFragmentBase {

    private static final Resources res = TweetMateApp.getInstance().getResources();

    // 設定
    private static final String THEME = res.getString(R.string.pref_key_theme);
    private static final String APPEARANCE = res.getString(R.string.pref_key_appearance);
    private static final String CLICK_ACTION = res.getString(R.string.pref_key_click_action);
    private static final String SYSTEM = res.getString(R.string.pref_key_system);
    private static final String WALLPAPER = res.getString(R.string.pref_key_wallpaper);

    // 管理
    private static final String COLUMN = res.getString(R.string.pref_key_column);
    private static final String ACCOUNT = res.getString(R.string.pref_key_account);

    // その他
    private static final String VERSION = res.getString(R.string.pref_key_version);
    private static final String FEEDBACK = res.getString(R.string.pref_key_feedback);
    private static final String ABOUT = res.getString(R.string.pref_key_about);

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

        // カラム管理
        findPreference(COLUMN).setOnPreferenceClickListener(
                preference -> {
                    intentTo(ColumnActivity.class);
                    return true;
                }
        );

        // アカウント管理
        findPreference(ACCOUNT).setOnPreferenceClickListener(
                preference -> {
                    intentTo(AccountActivity.class);
                    return true;
                }
        );

        // バージョン
        findPreference(VERSION).setOnPreferenceClickListener(
                preference -> {
                    intentTo(VersionActivity.class);
                    return true;
                }
        );

        // About
        findPreference(ABOUT).setOnPreferenceClickListener(
                preference -> {
                    intentTo(AboutActivity.class);
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
