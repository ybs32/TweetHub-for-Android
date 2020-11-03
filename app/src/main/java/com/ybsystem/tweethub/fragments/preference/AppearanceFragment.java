package com.ybsystem.tweethub.fragments.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.ListPreference;

import com.ybsystem.tweethub.R;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;
import static com.ybsystem.tweethub.storages.PrefAppearance.*;

public class AppearanceFragment extends PreferenceFragmentBase {

    // Preferences
    private ListPreference mFontSize, mUserIconSize;
    private ListPreference mLikeFavStyle, mUserIconStyle;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {

        setPreferencesFromResource(R.xml.preferences_appearance, rootKey);

        // Font size
        mFontSize = findPreference(KEY_SIZE_FONT);
        mFontSize.setSummary(mFontSize.getEntry());

        // User icon size
        mUserIconSize = findPreference(KEY_SIZE_USER_ICON);
        mUserIconSize.setSummary(mUserIconSize.getEntry());

        // Like fav style
        mLikeFavStyle = findPreference(KEY_STYLE_LIKE_FAV);
        mLikeFavStyle.setSummary(mLikeFavStyle.getEntry());

        // User icon style
        mUserIconStyle = findPreference(KEY_STYLE_USER_ICON);
        mUserIconStyle.setSummary(mUserIconStyle.getEntry());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Prepare reboot
        getActivity().setResult(REBOOT_PREPARATION);

        // When setting changed
        if (key.equals(KEY_SIZE_FONT)) {
            mFontSize.setSummary(mFontSize.getEntry());
        }
        else if (key.equals(KEY_SIZE_USER_ICON)) {
            mUserIconSize.setSummary(mUserIconSize.getEntry());
        }
        else if (key.equals(KEY_STYLE_LIKE_FAV)) {
            mLikeFavStyle.setSummary(mLikeFavStyle.getEntry());
        }
        else if (key.equals(KEY_STYLE_USER_ICON)) {
            mUserIconStyle.setSummary(mUserIconStyle.getEntry());
        }
    }

}
