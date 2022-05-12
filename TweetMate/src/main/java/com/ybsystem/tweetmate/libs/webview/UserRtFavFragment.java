package com.ybsystem.tweetmate.libs.webview;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.ybsystem.tweetmate.libs.eventbus.UserRtFavFetchEvent;
import com.ybsystem.tweetmate.libs.eventbus.UserRtFavAuthEvent;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweetmate.models.enums.ColumnType;
import com.ybsystem.tweetmate.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ybsystem.tweetmate.models.enums.ColumnType.RT_USER;
import static com.ybsystem.tweetmate.resources.ResString.*;

public class UserRtFavFragment extends Fragment {
    // Web client
    private WebView mWebView;
    private boolean mLoaded;

    public UserRtFavFragment newInstance(TwitterStatus status, ColumnType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("STATUS", status);
        bundle.putSerializable("COLUMN_TYPE", type);
        setArguments(bundle);
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create
        View view = createWebView();

        TwitterStatus status = (TwitterStatus) getArguments().getSerializable("STATUS");
        ColumnType type = (ColumnType) getArguments().getSerializable("COLUMN_TYPE");

        // Start auth
        String path = (type == RT_USER) ? "/retweets" : "/likes";
        mWebView.loadUrl("https://mobile.twitter.com/"
                + status.getUser().getScreenName() + "/status/"
                + status.getId() + path);

        return view;
    }

    private View createWebView() {
        // Create web view
        mWebView = new WebView(getActivity());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "WebViewFragment");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                if (url.endsWith("/")) {
                    mWebView.loadUrl("https://mobile.twitter.com/i/flow/login");
                    new Handler().postDelayed(() -> {
                        ToastUtils.showLongToast(STR_SENTENCE_AUTHENTICATE_TO_VIEW);
                        EventBus.getDefault().post(new UserRtFavAuthEvent());
                    }, 4000);
                }
                else if (url.endsWith("/home")) {
                    ToastUtils.showLongToast(STR_SENTENCE_AUTHENTICATE_SUCCESS);
                    getActivity().finish();
                }
                else if (url.endsWith("/likes") || url.endsWith("/retweets")) {
                    if (mLoaded) {
                        return;
                    }
                    mLoaded = true;
                    new Handler().postDelayed(() ->
                        view.loadUrl("javascript:window.WebViewFragment.viewSource(document.documentElement.outerHTML);")
                    , 4000);
                }
            }
        });

        // Remove cache
        mWebView.clearCache(true);
        mWebView.clearHistory();

        return mWebView;
    }

    @JavascriptInterface
    public void viewSource(final String src) {
        // Init
        ArrayList<Long> userIds = new ArrayList<>();

        // Extract user ids
        Pattern pattern = Pattern.compile("data-testid=\"(.*?)\"");
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            String matchStr = matcher.group(1);
            if (matchStr.endsWith("-follow")) {
                String idStr = matchStr.substring(0, matchStr.length() - 7);
                userIds.add(Long.parseLong(idStr));
            }
        }
        // Notify
        EventBus.getDefault().post(new UserRtFavFetchEvent(userIds));
    }

}
