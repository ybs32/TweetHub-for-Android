package com.ybsystem.tweethub.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ExceptionUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

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

        // When user clicked auth button
        findViewById(R.id.button_oauth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickEnable) {
                    // Disable flag to not tap continuously
                    mClickEnable = false;

                    // Enable flag after 3 sec with timer
                    final Handler handler = new Handler();
                    handler.postDelayed(() ->
                            mClickEnable = true,
                            3000
                    );

                    // Prepare auth
                    mTwitter = new TwitterFactory().getInstance();
                    mTwitter.setOAuthConsumer(getString(R.string.hello_java), getString(R.string.hello_android));

                    // Start auth
                    startAuthentication();
                }
            }
        });
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
                DialogUtils.showProgressDialog("接続中...", OAuthActivity.this);
            }

            @Override
            protected TwitterException doInBackground(Void... params) {
                // Fetch authorization url
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken(getString(R.string.callback_url));
                    authorizationURL = mRequestToken.getAuthorizationURL();
                    return null;
                } catch (TwitterException e) {
                    return e;
                }
            }

            @Override
            protected void onPostExecute(TwitterException e) {
                DialogUtils.dismissProgressDialog();

                // If authorization url can be acquired
                if (e == null && authorizationURL != null && authorizationURL != "") {
                    // Success
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationURL));
                    startActivity(intent);
                } else {
                    // Failed...
                    ToastUtils.showShortToast("認証に失敗しました...");
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
                || !intent.getData().toString().startsWith(getString(R.string.callback_url))) {
            return;
        }

        new AsyncTask<Void, Void, TwitterException>() {
            private AccessToken accessToken;
            private User user;

            @Override
            protected void onPreExecute() {
                DialogUtils.showProgressDialog("読み込み中...", OAuthActivity.this);
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
                DialogUtils.dismissProgressDialog();

                // If get accessToken and user
                if (e == null && accessToken != null && user != null) {
                    // Add account
                    TweetHubApp.getAppData().addAccount(accessToken, user);
                    TweetHubApp.getInstance().init();

                    // Intent to MainActivity
                    ToastUtils.showLongToast("認証に成功しました！");
                    Intent intent = new Intent(OAuthActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Failed...
                    ToastUtils.showShortToast("認証に失敗しました...");
                    ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
                }
            }
        }.execute();
    }

}
