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

    // Settings
    private static final String THEME = res.getString(R.string.pref_key_theme);
    private static final String APPEARANCE = res.getString(R.string.pref_key_appearance);
    private static final String CLICK_ACTION = res.getString(R.string.pref_key_click_action);
    private static final String SYSTEM = res.getString(R.string.pref_key_system);
    private static final String WALLPAPER = res.getString(R.string.pref_key_wallpaper);

    // Managements
    private static final String COLUMN = res.getString(R.string.pref_key_column);
    private static final String ACCOUNT = res.getString(R.string.pref_key_account);

    // Others
    private static final String VERSION = res.getString(R.string.pref_key_version);
    private static final String FEEDBACK = res.getString(R.string.pref_key_feedback);
    private static final String ABOUT = res.getString(R.string.pref_key_about);

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {

        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Theme settings
        findPreference(THEME).setOnPreferenceClickListener(
                preference -> {
                    intentTo(ThemeActivity.class);
                    return true;
                }
        );

        // Appearance settings
        findPreference(APPEARANCE).setOnPreferenceClickListener(
                preference -> {
                    intentTo(AppearanceActivity.class);
                    return true;
                }
        );

        // Tap action settings
        findPreference(CLICK_ACTION).setOnPreferenceClickListener(
                preference -> {
                    intentTo(ClickActionActivity.class);
                    return true;
                }
        );

        // System settings
        findPreference(SYSTEM).setOnPreferenceClickListener(
                preference -> {
                    intentTo(SystemActivity.class);
                    return true;
                }
        );

        // Wallpaper settings
        findPreference(WALLPAPER).setOnPreferenceClickListener(
                preference -> {
                    intentTo(WallpaperActivity.class);
                    return true;
                }
        );

        // Column management
        findPreference(COLUMN).setOnPreferenceClickListener(
                preference -> {
                    intentTo(ColumnActivity.class);
                    return true;
                }
        );

        // Account management
        findPreference(ACCOUNT).setOnPreferenceClickListener(
                preference -> {
                    intentTo(AccountActivity.class);
                    return true;
                }
        );

        // Version
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
