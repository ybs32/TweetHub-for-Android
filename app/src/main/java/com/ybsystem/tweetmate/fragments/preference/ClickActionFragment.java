package com.ybsystem.tweetmate.fragments.preference;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;

import com.ybsystem.tweetmate.R;

import static com.ybsystem.tweetmate.activities.preference.SettingActivity.*;
import static com.ybsystem.tweetmate.resources.ResString.*;
import static com.ybsystem.tweetmate.storages.PrefClickAction.*;

public class ClickActionFragment extends PreferenceFragmentBase {

    // Preferences
    private ListPreference mLeftClick, mMiddleClick, mRightClick;
    private ListPreference mLeftLongClick, mMiddleLongClick, mRightLongClick;
    private ListPreference mRetweetClick, mHashtagClick;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {

        setPreferencesFromResource(R.xml.preferences_click_action, rootKey);

        // Click
        mLeftClick = findPreference(KEY_LEFT_CLICK);
        mLeftClick.setSummary(mLeftClick.getEntry());

        mMiddleClick = findPreference(KEY_MIDDLE_CLICK);
        mMiddleClick.setSummary(mMiddleClick.getEntry());

        mRightClick = findPreference(KEY_RIGHT_CLICK);
        mRightClick.setSummary(mRightClick.getEntry());

        // Long click
        mLeftLongClick = findPreference(KEY_LEFT_LONG_CLICK);
        mLeftLongClick.setSummary(mLeftLongClick.getEntry());

        mMiddleLongClick = findPreference(KEY_MIDDLE_LONG_CLICK);
        mMiddleLongClick.setSummary(mMiddleLongClick.getEntry());

        mRightLongClick = findPreference(KEY_RIGHT_LONG_CLICK);
        mRightLongClick.setSummary(mRightLongClick.getEntry());

        // Click retweet
        mRetweetClick = findPreference(KEY_CLICK_RETWEET);
        if (mRetweetClick.getEntry().equals(STR_NORMAL)) {
            mRetweetClick.setSummary(STR_TAP + "：" + STR_RETWEET + "\n" + STR_LONG_TAP + "：" + STR_QUOTE);
        } else {
            mRetweetClick.setSummary(STR_TAP + "：" + STR_QUOTE + "\n" + STR_LONG_TAP + "：" + STR_RETWEET);
        }
        // Click hashtag
        mHashtagClick = findPreference(KEY_CLICK_HASHTAG);
        if (mHashtagClick.getEntry().equals(STR_NORMAL)) {
            mHashtagClick.setSummary(STR_TAP + "：" + STR_SEARCH_ON_HASHTAG + "\n" + STR_LONG_TAP + "：" + STR_TWEET_ON_HASHTAG);
        } else {
            mHashtagClick.setSummary(STR_TAP + "：" + STR_TWEET_ON_HASHTAG + "\n" + STR_LONG_TAP + "：" + STR_SEARCH_ON_HASHTAG);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Prepare reboot
        getActivity().setResult(REBOOT_PREPARATION);

        // When setting changed
        if (key.equals(KEY_LEFT_CLICK)) {
            mLeftClick.setSummary(mLeftClick.getEntry());
        }
        else if (key.equals(KEY_MIDDLE_CLICK)) {
            mMiddleClick.setSummary(mMiddleClick.getEntry());
        }
        else if (key.equals(KEY_RIGHT_CLICK)) {
            mRightClick.setSummary(mRightClick.getEntry());
        }
        else if (key.equals(KEY_LEFT_LONG_CLICK)) {
            mLeftLongClick.setSummary(mLeftLongClick.getEntry());
        }
        else if (key.equals(KEY_MIDDLE_LONG_CLICK)) {
            mMiddleLongClick.setSummary(mMiddleLongClick.getEntry());
        }
        else if (key.equals(KEY_RIGHT_LONG_CLICK)) {
            mRightLongClick.setSummary(mRightLongClick.getEntry());
        }
        else if (key.equals(KEY_CLICK_RETWEET)) {
            if (mRetweetClick.getEntry().equals(STR_NORMAL)) {
                mRetweetClick.setSummary(STR_TAP + "：" + STR_RETWEET + "\n" + STR_LONG_TAP + "：" + STR_QUOTE);
            } else {
                mRetweetClick.setSummary(STR_TAP + "：" + STR_QUOTE + "\n" + STR_LONG_TAP + "：" + STR_RETWEET);
            }
        }
        else if (key.equals(KEY_CLICK_HASHTAG)) {
            if (mHashtagClick.getEntry().equals(STR_NORMAL)) {
                mHashtagClick.setSummary(STR_TAP + "：" + STR_SEARCH_ON_HASHTAG + "\n" + STR_LONG_TAP + "：" + STR_TWEET_ON_HASHTAG);
            } else {
                mHashtagClick.setSummary(STR_TAP + "：" + STR_TWEET_ON_HASHTAG + "\n" + STR_LONG_TAP + "：" + STR_SEARCH_ON_HASHTAG);
            }
        }
    }

}
