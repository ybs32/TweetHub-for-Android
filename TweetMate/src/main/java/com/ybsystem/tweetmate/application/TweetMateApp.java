package com.ybsystem.tweetmate.application;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.ybsystem.tweetmate.models.entities.Account;
import com.ybsystem.tweetmate.models.entities.AppData;
import com.ybsystem.tweetmate.models.entities.Via;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.databases.PrefEntity;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Application class of TweetMate
 * Defines global variables used in the application
 */
public class TweetMateApp extends Application {

    @SuppressLint("StaticFieldLeak")
    private static TweetMateApp sTweetMateApp;

    private AppCompatActivity mActivity;
    private AppData mAppData;
    private Twitter mTwitter;

    @Override
    public void onCreate() {
        super.onCreate();

        sTweetMateApp = this;

        // Check if launch first time
        mAppData = PrefEntity.loadAppData();
        if (mAppData == null) {
            mAppData = new AppData();
            return;
        }

        this.initTwitter();
    }

    public void initTwitter() {
        // Get current via
        Via via = getMyAccount().getVias().getCurrentVia();
        String ck = via.getConsumerKey();
        String cs = via.getConsumerSecret();
        String t = via.getToken();
        String ts = via.getTokenSecret();

        // Create twitter instance
        mTwitter = new TwitterFactory().getInstance();
        mTwitter.setOAuthConsumer(ck, cs);
        mTwitter.setOAuthAccessToken(new AccessToken(t, ts));
    }

    public static synchronized TweetMateApp getInstance() {
        return sTweetMateApp;
    }

    public static String getActivityName(String fullName) {
        String[] arr = fullName.split("\\.");
        return arr[arr.length - 1];
    }

    public static AppCompatActivity getActivity() {
        return getInstance().mActivity;
    }

    public static void setActivity(AppCompatActivity activity) {
        getInstance().mActivity = activity;
    }

    public static AppData getData() {
        return getInstance().mAppData;
    }

    public static Account getMyAccount() {
        return getData().getAccounts().getCurrentAccount();
    }

    public static TwitterUser getMyUser() {
        return getMyAccount().getUser();
    }

    public static Twitter getTwitter() {
        return getInstance().mTwitter;
    }

}
