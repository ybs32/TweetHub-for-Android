package com.ybsystem.tweetmate.fragments.preference;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.ybsystem.tweetmate.BuildConfig;
import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.Account;
import com.ybsystem.tweetmate.utils.DialogUtils;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import static com.ybsystem.tweetmate.activities.preference.SettingActivity.*;

public class BrowserAuthFragment extends Fragment {
    // Web client
    private WebView mWebView;

    // For authorization
    private Twitter mTwitter;
    private RequestToken mRequestToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create
        View view = createWebView();

        // Prepare auth
        mTwitter = new TwitterFactory().getInstance();
        mTwitter.setOAuthConsumer(BuildConfig.CK, BuildConfig.CS);

        // Start auth
        new PreTask().execute();

        return view;
    }

    private View createWebView() {
        // Create web view
        mWebView = new WebView(getActivity());

        // Create web view client
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                boolean result = true;
                if (url != null && url.contains("oauth_verifier=")) {
                    String verifier = Uri.parse(url).getQueryParameter("oauth_verifier");
                    PostTask postTask = new PostTask();
                    postTask.execute(verifier);
                } else {
                    result = super.shouldOverrideUrlLoading(view, url);
                }
                return result;
            }
        };

        // Remove cookie
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        cookieManager.removeSessionCookies(null);
        cookieManager.flush();

        // Remove cache
        mWebView.clearCache(true);
        mWebView.clearHistory();

        // Set web view
        mWebView.setWebViewClient(webViewClient);
        return mWebView;
    }

    @SuppressLint("StaticFieldLeak")
    private class PreTask extends AsyncTask<Void, Void, TwitterException> {
        private String authorizationURL;

        @Override
        protected void onPreExecute() {
            // Show progress
            getActivity().setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected TwitterException doInBackground(Void... params) {
            try {
                String callbackURL =  BuildConfig.CB;
                mRequestToken = mTwitter.getOAuthRequestToken(callbackURL);
                authorizationURL = mRequestToken.getAuthorizationURL();
                return null;
            } catch (TwitterException e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(TwitterException e) {
            // Hide progress
            getActivity().setProgressBarIndeterminateVisibility(false);

            // If authorization url can be acquired
            if (e == null && authorizationURL != null && authorizationURL != "") {
                // Success
                mWebView.loadUrl(authorizationURL);
            } else {
                // Failed...
                ToastUtils.showShortToast("認証に失敗しました...");
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_to_right);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PostTask extends AsyncTask<String, Void, TwitterException> {
        private AccessToken accessToken;
        private User user;

        @Override
        protected void onPreExecute() {
            // Show progress
            getActivity().setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected TwitterException doInBackground(String... params) {
            // Get user and access token
            if (params != null) {
                try {
                    accessToken = mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                    user = mTwitter.showUser(mTwitter.getId());
                    return null;
                } catch (TwitterException e) {
                    return e;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(TwitterException e) {
            // Hide progress
            getActivity().setProgressBarIndeterminateVisibility(false);

            // If get accessToken and user
            if (e == null && user != null && accessToken != null) {
                // Success
                addAccount();
            } else {
                // Failed...
                ToastUtils.showShortToast("認証に失敗しました...");
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
                getActivity().finish();
            }
        }

        private void addAccount() {
            // Check if already user added
            for (Account account : TweetMateApp.getData().getAccounts()) {
                if (accessToken.getUserId() == account.getUser().getId()) {
                    ToastUtils.showShortToast("エラーが発生しました...");
                    ToastUtils.showShortToast("既に追加済みのユーザです。");
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_to_right);
                    return;
                }
            }
            // Add account
            TweetMateApp.getData().addAccount(accessToken, user);
            TweetMateApp.getInstance().init();

            // Reboot application
            DialogUtils.showProgress("読み込み中...", getActivity());
            new Handler().postDelayed(() -> {
                DialogUtils.dismissProgress();
                ToastUtils.showShortToast("アカウントを追加しました。");
                getActivity().setResult(REBOOT_IMMEDIATE);
                getActivity().finish();
            }, 1500);
        }
    }

}
