package com.ybsystem.tweetmate.fragments.preference;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;

import com.ybsystem.tweetmate.R;

import static com.ybsystem.tweetmate.activities.preference.SettingActivity.*;
import static com.ybsystem.tweetmate.databases.PrefSystem.*;

public class SystemFragment extends PreferenceFragmentBase {

    // Preferences
    private ListPreference mImageQuality, mVideoQuality;
    private ListPreference mBootLoadCount, mScrollLoadCount;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {

        setPreferencesFromResource(R.xml.preference_system, rootKey);

        // Image quality
        mImageQuality = findPreference(KEY_QUALITY_IMAGE);
        mImageQuality.setSummary(mImageQuality.getEntry());

        // Video quality
        mVideoQuality = findPreference(KEY_QUALITY_VIDEO);
        mVideoQuality.setSummary(mVideoQuality.getEntry());

        // Boot load count
        mBootLoadCount = findPreference(KEY_COUNT_BOOT_LOAD);
        mBootLoadCount.setSummary(mBootLoadCount.getEntry());

        // Scroll load count
        mScrollLoadCount = findPreference(KEY_COUNT_SCROLL_LOAD);
        mScrollLoadCount.setSummary(mScrollLoadCount.getEntry());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Prepare reboot
        getActivity().setResult(REBOOT_PREPARATION);

        // When setting changed
        if (key.equals(KEY_QUALITY_IMAGE)) {
            mImageQuality.setSummary(mImageQuality.getEntry());
        }
        else if (key.equals(KEY_QUALITY_VIDEO)) {
            mVideoQuality.setSummary(mVideoQuality.getEntry());
        }
        else if (key.equals(KEY_COUNT_BOOT_LOAD)) {
            mBootLoadCount.setSummary(mBootLoadCount.getEntry());
        }
        else if (key.equals(KEY_COUNT_SCROLL_LOAD)) {
            mScrollLoadCount.setSummary(mScrollLoadCount.getEntry());
        }
    }

}
