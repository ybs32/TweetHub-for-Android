package com.ybsystem.tweethub.usecases;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.FragmentManager;

import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.dialog.TweetDialog;
import com.ybsystem.tweethub.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweethub.utils.ToastUtils;

public class ClickUseCase {

    private ClickUseCase() {
    }

    public static void showTweetMenu(TwitterStatus status) {
        // Show tweet dialog
        TweetDialog dialog = new TweetDialog(status);
        FragmentManager fmanager = TweetHubApp.getActivity().getSupportFragmentManager();
        if (fmanager.findFragmentByTag("TweetDialog") == null) {
            dialog.show(fmanager, "TweetDialog");
        }
    }

    public static void reply(TwitterStatus status) {
        // Intent to PostActivity
        ToastUtils.showShortToast("reply");
    }

    public static void quote(TwitterStatus status) {
        // Intent to PostActivity
        ToastUtils.showShortToast("quote");
    }

    public static void showTalk(TwitterStatus status) {
        // Intent to TimelineActivity
        ToastUtils.showShortToast("showTalk");
    }

    public static void openURL(String url) {
        // Intent to Browser
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        TweetHubApp.getActivity().startActivity(intent);
    }

    public static void searchWord(String searchWord) {
        // Intent to TimelineActivity
        ToastUtils.showShortToast("searchWord");
    }

    public static void tweetWithWord(String word) {
        // Intent to PostActivity
        ToastUtils.showShortToast("tweetWithWord");
    }

    public static void showUser(long userId) {
        // Intent to ProfileActivity
        ToastUtils.showShortToast("showUser");
    }

    public static void showDetail(TwitterStatus status) {
        // Intent to TimelineActivity
        ToastUtils.showShortToast("showDetail");
    }

    public static void copyText(TwitterStatus status) {
        ClipboardManager clipboardManager = (ClipboardManager)
                TweetHubApp.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", status.getText()));
            ToastUtils.showShortToast("テキストをコピーしました。");
        }
    }

    public static void share(TwitterStatus status) {
        // Create uri
        String screenName = status.getUser().getScreenName();
        String statusId = String.valueOf(status.getId());
        Uri uri = Uri.parse("https://twitter.com/" + screenName + "/status/" + statusId);

        // Intent to external
        Activity activity = TweetHubApp.getActivity();
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_TEXT, uri);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        activity.startActivity(intent);
    }

    public static void openStatus(TwitterStatus status) {
        // Create uri
        String screenName = status.getUser().getScreenName();
        String statusId = String.valueOf(status.getId());
        Uri uri = Uri.parse("https://twitter.com/" + screenName + "/status/" + statusId);

        // Intent to external
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        TweetHubApp.getActivity().startActivity(intent);
    }

}
