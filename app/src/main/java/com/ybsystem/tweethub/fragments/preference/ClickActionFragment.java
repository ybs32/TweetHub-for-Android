package com.ybsystem.tweethub.fragments.preference;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;

import com.ybsystem.tweethub.R;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;
import static com.ybsystem.tweethub.storages.PrefClickAction.*;

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
        if (mRetweetClick.getEntry().equals("通常")) {
            mRetweetClick.setSummary("タップ：リツイート\n" + "ロングタップ：引用ツイート");
        } else {
            mRetweetClick.setSummary("タップ：引用ツイート\n" + "ロングタップ：リツイート");
        }
        // Click hashtag
        mHashtagClick = findPreference(KEY_CLICK_HASHTAG);
        if (mHashtagClick.getEntry().equals("通常")) {
            mHashtagClick.setSummary("タップ：ハッシュタグで検索\n" + "ロングタップ：ハッシュタグでツイート");
        } else {
            mHashtagClick.setSummary("タップ：ハッシュタグでツイート\n" + "ロングタップ：ハッシュタグで検索");
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
            if (mRetweetClick.getEntry().equals("通常")) {
                mRetweetClick.setSummary("タップ：リツイート\n" + "ロングタップ：引用ツイート");
            } else {
                mRetweetClick.setSummary("タップ：引用ツイート\n" + "ロングタップ：リツイート");
            }
        }
        else if (key.equals(KEY_CLICK_HASHTAG)) {
            if (mHashtagClick.getEntry().equals("通常")) {
                mHashtagClick.setSummary("タップ：ハッシュタグで検索\n" + "ロングタップ：ハッシュタグでツイート");
            } else {
                mHashtagClick.setSummary("タップ：ハッシュタグでツイート\n" + "ロングタップ：ハッシュタグで検索");
            }
        }
    }

}
