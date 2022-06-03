package com.ybsystem.tweetmate.fragments.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.preference.ListPreference;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.utils.DialogUtils;

import static com.ybsystem.tweetmate.activities.preference.SettingActivity.*;
import static com.ybsystem.tweetmate.databases.PrefSystem.*;
import static com.ybsystem.tweetmate.resources.ResString.STR_APPLYING;

public class SystemFragment extends PreferenceFragmentBase {

    // Preferences
    private ListPreference mLocale, mTrend;
    private ListPreference mImageQuality, mVideoQuality;
    private ListPreference mBootLoadCount, mScrollLoadCount;
    private ListPreference mTweetStyle;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {

        setPreferencesFromResource(R.xml.preference_system, rootKey);

        // Locale
        mLocale = findPreference(KEY_LOCALE);
        mLocale.setSummary(mLocale.getEntry());

        // Trend
        mTrend = findPreference(KEY_TREND);
        mTrend.setSummary(mTrend.getEntry());

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

        // Tweet style
        mTweetStyle = findPreference(KEY_TWEET_STYLE);
        mTweetStyle.setSummary(mTweetStyle.getEntry());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Prepare reboot
        getActivity().setResult(REBOOT_PREPARATION);

        // When setting changed
        if (key.equals(KEY_LOCALE)) {
            mLocale.setSummary(mLocale.getEntry());

            // Finish activity
            DialogUtils.showPersistentProgress(STR_APPLYING);
            new Handler().postDelayed(() -> {
                DialogUtils.dismissProgress();
                getActivity().setResult(1);
                getActivity().finish();
            }, 1500);
        }
        else if (key.equals(KEY_TREND)) {
            mTrend.setSummary(mTrend.getEntry());
        }
        else if (key.equals(KEY_QUALITY_IMAGE)) {
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
        else if (key.equals(KEY_TWEET_STYLE)) {
            mTweetStyle.setSummary(mTweetStyle.getEntry());
        }
    }

}
