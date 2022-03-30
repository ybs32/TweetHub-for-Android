package com.ybsystem.tweetmate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.FragmentManager;

import com.ybsystem.tweetmate.BuildConfig;
import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.fragments.dialog.NoticeDialog;
import com.ybsystem.tweetmate.utils.DialogUtils;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class OAuthActivity extends ActivityBase {

    // For authorization
    private Twitter mTwitter;
    private RequestToken mRequestToken;

    // Flag to stop pressing the auth button
    private boolean mClickEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_oauth);

        // When auth button clicked
        findViewById(R.id.button_oauth).setOnClickListener(v -> {
            if (mClickEnable) {
                // Disable flag to not tap continuously
                mClickEnable = false;

                // Enable flag after 3 seconds
                new Handler().postDelayed(() ->
                        mClickEnable = true,
                        3000
                );

                // Prepare auth
                mTwitter = new TwitterFactory().getInstance();
                mTwitter.setOAuthConsumer(BuildConfig.CK, BuildConfig.CS);

                // Start auth
                startAuthentication();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.oauth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If authorization failed
            case R.id.item_trouble:
                NoticeDialog dialog = new NoticeDialog().newInstance(STR_SENTENCE_TROUBLE_AUTH);
                FragmentManager fm = getSupportFragmentManager();
                if (fm.findFragmentByTag("NoticeDialog") == null) {
                    dialog.show(fm, "NoticeDialog");
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Start oauth authorization.
     * Get the URL for authorization and call the browser.
     */
    @SuppressLint("StaticFieldLeak")
    private void startAuthentication() {

        new AsyncTask<Void, Void, TwitterException>() {
            private String authorizationURL;

            @Override
            protected void onPreExecute() {
                DialogUtils.showProgress(STR_CONNECTING, OAuthActivity.this);
            }

            @Override
            protected TwitterException doInBackground(Void... params) {
                // Fetch authorization url
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken(BuildConfig.CB);
                    authorizationURL = mRequestToken.getAuthorizationURL();
                    return null;
                } catch (TwitterException e) {
                    return e;
                }
            }

            @Override
            protected void onPostExecute(TwitterException e) {
                DialogUtils.dismissProgress();

                // If authorization url can be acquired
                if (e == null && authorizationURL != null && authorizationURL != "") {
                    // Success
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationURL));
                    startActivity(intent);
                } else {
                    // Failed...
                    ToastUtils.showShortToast(STR_FAIL_AUTH);
                    ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
                }
            }
        }.execute();
    }

    /**
     * Process when returning from browser
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);

        // Do nothing if not authorized
        if (intent == null
                || intent.getData() == null
                || intent.getData().toString().contains("denied=")
                || !intent.getData().toString().startsWith(BuildConfig.CB)) {
            return;
        }

        new AsyncTask<Void, Void, TwitterException>() {
            private AccessToken accessToken;
            private User user;

            @Override
            protected void onPreExecute() {
                DialogUtils.showProgress(STR_LOADING, OAuthActivity.this);
            }

            @Override
            protected TwitterException doInBackground(Void... params) {
                // Get user and access token
                try {
                    accessToken = mTwitter.getOAuthAccessToken(mRequestToken, intent.getData().getQueryParameter("oauth_verifier"));
                    user = mTwitter.showUser(mTwitter.getId());
                    return null;
                } catch (TwitterException e) {
                    return e;
                }
            }

            @Override
            protected void onPostExecute(TwitterException e) {
                DialogUtils.dismissProgress();

                // If get accessToken and user
                if (e == null && accessToken != null && user != null) {
                    // Add account
                    TweetMateApp.getData().addAccount(accessToken, user);
                    TweetMateApp.getInstance().init();

                    // Intent to MainActivity
                    ToastUtils.showLongToast(STR_SUCCESS_AUTH);
                    Intent intent = new Intent(OAuthActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Failed...
                    ToastUtils.showShortToast(STR_FAIL_AUTH);
                    ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
                }
            }
        }.execute();
    }

}
