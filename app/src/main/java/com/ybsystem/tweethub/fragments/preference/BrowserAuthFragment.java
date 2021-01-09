package com.ybsystem.tweethub.fragments.preference;

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

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.models.entities.Account;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ExceptionUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;

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
        String consumerKey = getString(R.string.hello_java);
        String consumerSecret = getString(R.string.hello_android);
        mTwitter.setOAuthConsumer(consumerKey, consumerSecret);

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
        cookieManager.removeAllCookie();
        cookieManager.removeSessionCookie();

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
                String callbackURL =  getString(R.string.callback_url);
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
            for (Account account : TweetHubApp.getAppData().getAccounts()) {
                if (accessToken.getUserId() == account.getUser().getId()) {
                    ToastUtils.showShortToast("エラーが発生しました...");
                    ToastUtils.showShortToast("既に追加済みのユーザです。");
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_to_right);
                    return;
                }
            }
            // Add account
            TweetHubApp.getAppData().addAccount(accessToken, user);
            TweetHubApp.getInstance().init();

            // Reboot application
            DialogUtils.showProgressDialog("読み込み中...", getActivity());
            new Handler().postDelayed(() -> {
                DialogUtils.dismissProgressDialog();
                ToastUtils.showShortToast("アカウントを追加しました。");
                getActivity().setResult(REBOOT_IMMEDIATE);
                getActivity().finish();
            }, 1500);
        }
    }

}
