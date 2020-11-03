package com.ybsystem.tweethub.fragments.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.storages.PrefTheme;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ResourceUtils;

import java.util.ArrayList;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;
import static com.ybsystem.tweethub.storages.PrefTheme.*;

public class ThemeFragment extends PreferenceFragmentBase {

    // Preferences
    private ListPreference mTheme;
    private SwitchPreference mCustom;

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
        // For initializing custom colors
        switch (PrefTheme.getTheme()) {
            case "LIGHT":
                getActivity().setTheme(R.style.LightTheme);
                break;
            case "DARK":
                getActivity().setTheme(R.style.DarkTheme);
        }
        // Init custom colors
        mCustom.setChecked(false);
        PrefTheme.initCustomColors();

        // Finish activity
        DialogUtils.showProgressDialog("設定を適用中...", getActivity());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissProgressDialog();
                getActivity().setResult(1);
                getActivity().finish();
            }
        }, 1500);
    }

    private void changeDependencyTextColor() {
        // Get dependency color
        String color = mCustom.isChecked()
                ? String.valueOf(ResourceUtils.getTextColor())
                : String.valueOf(ResourceUtils.getDisabledTextColor());

        // Change dependency item's text color
        for (Preference pref : mDependencyList) {
            pref.setTitle(Html.fromHtml("<font color = " + color + ">" + pref.getTitle() + "</font>"));
        }
    }

}

