package com.ybsystem.tweethub.usecases;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.FragmentManager;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.PostActivity;
import com.ybsystem.tweethub.activities.ProfileActivity;
import com.ybsystem.tweethub.activities.SearchActivity;
import com.ybsystem.tweethub.activities.TimelineActivity;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.dialog.TweetDialog;
import com.ybsystem.tweethub.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.utils.ToastUtils;

import static com.ybsystem.tweethub.models.enums.ColumnType.*;

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
        Activity activity = TweetHubApp.getActivity();
        Intent intent = new Intent(activity, PostActivity.class);
        intent.putExtra("REPLY_STATUS", status);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
    }

    public static void quote(TwitterStatus status) {
        // Intent to PostActivity
        Activity activity = TweetHubApp.getActivity();
        Intent intent = new Intent(activity, PostActivity.class);
        intent.putExtra("QUOTE_STATUS", status);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
    }

    public static void showTalk(TwitterStatus status) {
        // Intent to TimelineActivity
        Activity activity = TweetHubApp.getActivity();
        Intent intent = new Intent(TweetHubApp.getActivity(), TimelineActivity.class);
        intent.putExtra("STATUS", status);
        intent.putExtra("COLUMN_TYPE", TALK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    public static void openURL(String url) {
        // Intent to Browser
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        TweetHubApp.getActivity().startActivity(intent);
    }

    public static void searchWord(String searchWord) {
        // Intent to SearchActivity
        Activity activity = TweetHubApp.getActivity();
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra("SEARCH_WORD", searchWord);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    public static void tweetWithWord(String word) {
        // Intent to PostActivity
        Activity activity = TweetHubApp.getActivity();
        Intent intent = new Intent(activity, PostActivity.class);
        intent.putExtra("TWEET_WORD", word);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
    }

    public static void showUser(long userId) {
        // Intent to ProfileActivity
        Activity activity = TweetHubApp.getActivity();
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra("USER_ID", userId);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    public static void showDetail(TwitterStatus status) {
        // Intent to TimelineActivity
        Activity activity = TweetHubApp.getActivity();
        Intent intent = new Intent(activity, TimelineActivity.class);
        intent.putExtra("STATUS", status);
        intent.putExtra("COLUMN_TYPE", DETAIL);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    public static void copyText(TwitterStatus status) {
        // Get clipboard
        ClipboardManager clipboardManager = (ClipboardManager)
                TweetHubApp.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        // Check null
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

    public static void openUser(TwitterUser user) {
        // Create uri
        String screenName = user.getScreenName();
        Uri uri = Uri.parse("https://twitter.com/" + screenName);

        // Intent to external
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        TweetHubApp.getActivity().startActivity(intent);
    }

}
