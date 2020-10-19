package com.ybsystem.tweethub.application;

import android.app.Activity;
import android.app.Application;

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

    private static TweetHubApp sTweetHubApp;
    private Activity mActivity;
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
        // Get credential
        Via via = getMyAccount().getVias().getCurrentVia();
        String consumerKey = via.getConsumerKey();
        String consumerSecret = via.getConsumerSecret();
        String token = via.getToken();
        String tokenSecret = via.getTokenSecret();

        // Create twitter instance
        mTwitter = new TwitterFactory().getInstance();
        mTwitter.setOAuthConsumer(consumerKey, consumerSecret);
        mTwitter.setOAuthAccessToken(new AccessToken(token, tokenSecret));
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
    public static Activity getActivity() {
        return getInstance().mActivity;
    }

    /**
     * Set current activity instance
     * @param activity Activity
     */
    public static void setActivity(Activity activity) {
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
