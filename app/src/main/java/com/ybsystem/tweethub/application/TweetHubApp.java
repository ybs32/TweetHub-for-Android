package com.ybsystem.tweethub.application;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.ybsystem.tweethub.models.entities.Account;
import com.ybsystem.tweethub.models.entities.AppData;
import com.ybsystem.tweethub.models.entities.Via;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.storages.PrefEntity;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Application class of TweetHub
 * Defines global variables used in the application
 */
public class TweetHubApp extends Application {

    @SuppressLint("StaticFieldLeak")
    private static TweetHubApp sTweetHubApp;
    private AppCompatActivity mActivity;
    private AppData mAppData;
    private Twitter mTwitter;

    @Override
    public void onCreate() {
        super.onCreate();

        sTweetHubApp = this;

        // Check if the app launch first time
        mAppData = PrefEntity.loadAppData();
        if (mAppData == null) {
            mAppData = new AppData();
            return;
        }

        this.init();
    }

    public void init() {
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

    /**
     * Get application class instance
     * @return Instance of application class
     */
    public static synchronized TweetHubApp getInstance() {
        return sTweetHubApp;
    }

    /**
     * Get current activity instance
     * @return Instance of current activity
     */
    public static AppCompatActivity getActivity() {
        return getInstance().mActivity;
    }

    /**
     * Set current activity instance
     * @param activity Activity
     */
    public static void setActivity(AppCompatActivity activity) {
        getInstance().mActivity = activity;
    }

    /**
     * Get app data instance
     * @return Instance of app data
     */
    public static AppData getAppData() {
        return getInstance().mAppData;
    }

    /**
     * Get account instance
     * @return Instance of account
     */
    public static Account getMyAccount() {
        return getAppData().getAccounts().getCurrentAccount();
    }

    /**
     * Get twitter user instance
     * @return Instance of twitter user
     */
    public static TwitterUser getMyUser() {
        return getMyAccount().getUser();
    }

    /**
     * Get twitter instance
     * @return Instance of twitter
     */
    public static Twitter getTwitter() {
        return getInstance().mTwitter;
    }

}
