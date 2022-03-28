package com.ybsystem.tweetmate.fragments.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.fragments.dialog.PreviewThemeDialog;
import com.ybsystem.tweetmate.resources.ResColor;
import com.ybsystem.tweetmate.databases.PrefTheme;
import com.ybsystem.tweetmate.utils.DialogUtils;

import java.util.ArrayList;

import static androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT;
import static com.ybsystem.tweetmate.activities.preference.SettingActivity.*;
import static com.ybsystem.tweetmate.resources.ResColor.*;
import static com.ybsystem.tweetmate.resources.ResString.*;
import static com.ybsystem.tweetmate.databases.PrefTheme.*;

public class ThemeFragment extends PreferenceFragmentBase {

    // Preferences
    private ListPreference mTheme;
    private SwitchPreference mCustom;
    private PreferenceScreen mPreview;

    // Custom setting dependencies
    private ArrayList<Preference> mDependencyList;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {

        setPreferencesFromResource(R.xml.preferences_theme, rootKey);

        // Theme
        mTheme = findPreference(KEY_THEME_SETTING);
        mTheme.setSummary(mTheme.getEntry());

        // Custom
        mCustom = findPreference(KEY_THEME_CUSTOM);

        // Preview
        mPreview = findPreference(KEY_THEME_PREVIEW);
        mPreview.setOnPreferenceClickListener(
                preference -> {
                    FragmentManager fm = getFragmentManager();
                    if (fm.findFragmentByTag("PreviewThemeDialog") == null) {
                        new PreviewThemeDialog().show(fm, "PreviewThemeDialog");
                    }
                    return true;
                }
        );

        // Change dependency item's text color programmatically
        mDependencyList = new ArrayList<>();
        mDependencyList.add(findPreference(KEY_RETWEET_COLOR));
        mDependencyList.add(findPreference(KEY_REPLY_COLOR));
        mDependencyList.add(findPreference(KEY_MYTWEET_COLOR));
        mDependencyList.add(findPreference(KEY_USERNAME_COLOR));
        mDependencyList.add(findPreference(KEY_RELATIVETIME_COLOR));
        mDependencyList.add(findPreference(KEY_TWEETTEXT_COLOR));
        mDependencyList.add(findPreference(KEY_LINK_COLOR));
        mDependencyList.add(findPreference(KEY_ABSOLUTETIME_COLOR));
        mDependencyList.add(findPreference(KEY_VIA_COLOR));
        mDependencyList.add(findPreference(KEY_RTFAV_COLOR));
        mDependencyList.add(findPreference(KEY_RETWEETEDBY_COLOR));
        mDependencyList.add(findPreference(KEY_THEME_PREVIEW));
        changeDependencyTextColor();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Prepare reboot
        getActivity().setResult(REBOOT_PREPARATION);

        // When setting changed
        if (key.equals(KEY_THEME_SETTING)) {
            mTheme.setSummary(mTheme.getEntry());
            changeTheme();
        } else if (key.equals(KEY_THEME_CUSTOM)) {
            changeDependencyTextColor();
        }
    }

    private void changeTheme() {
        // Init custom colors
        switch (PrefTheme.getTheme()) {
            case "LIGHT":
                getActivity().setTheme(R.style.LightTheme);
                break;
            case "DARK":
                getActivity().setTheme(R.style.DarkTheme);
                break;
        }
        ResColor.init();
        PrefTheme.initCustomColors();

        // Disable custom
        mCustom.setChecked(false);

        // Finish activity
        DialogUtils.showProgress(STR_APPLYING, getActivity());
        new Handler().postDelayed(() -> {
            DialogUtils.dismissProgress();
            getActivity().setResult(1);
            getActivity().finish();
        }, 1500);
    }

    private void changeDependencyTextColor() {
        // Get dependency color
        String color = mCustom.isChecked()
                ? String.valueOf(COLOR_TEXT)
                : String.valueOf(COLOR_WEAK);

        // Change dependency item's text color
        for (Preference pref : mDependencyList) {
            pref.setTitle(HtmlCompat.fromHtml(
                    "<font color = " + color + ">"+ pref.getTitle() + "</font>", FROM_HTML_MODE_COMPACT)
            );
        }
    }

}

