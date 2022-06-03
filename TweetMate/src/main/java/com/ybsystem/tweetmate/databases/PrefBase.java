package com.ybsystem.tweetmate.databases;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.application.TweetMateApp;

public class PrefBase {

    protected static final Resources RES = TweetMateApp.getInstance().getResources();

    protected static SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(
                TweetMateApp.getInstance()
        );
    }

    protected static SharedPreferences getEntitySharedPreferences() {
        return TweetMateApp.getInstance().getSharedPreferences(
                RES.getString(R.string.pref_key_entity), Context.MODE_PRIVATE
        );
    }

}
