package com.ybsystem.tweethub.storages;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;

public class PrefBase {

    protected static final Resources RES = TweetHubApp.getInstance().getResources();

    protected static SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(
                TweetHubApp.getInstance()
        );
    }

    protected static SharedPreferences getEntitySharedPreferences() {
        return TweetHubApp.getInstance().getSharedPreferences(
                RES.getString(R.string.pref_key_entity), Context.MODE_PRIVATE
        );
    }

}
