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
        TweetDialog dialog = new TweetDialog().newInstance(status);
        FragmentManager fm = TweetHubApp.getActivity().getSupportFragmentManager();
        if (fm.findFragmentByTag("TweetDialog") == null) {
            dialog.show(fm, "TweetDialog");
        }
    }

    public static void reply(TwitterStatus status) {
        // Intent to PostActivity
        Activity act = TweetHubApp.getActivity();
        Intent intent = new Intent(act, PostActivity.class);
        intent.putExtra("REPLY_STATUS", status);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
    }

    public static void quote(TwitterStatus status) {
        // Intent to PostActivity
        Activity act = TweetHubApp.getActivity();
        Intent intent = new Intent(act, PostActivity.class);
        intent.putExtra("QUOTE_STATUS", status);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
    }

    public static void showTalk(TwitterStatus status) {
        // Intent to TimelineActivity
        Activity act = TweetHubApp.getActivity();
        Intent intent = new Intent(act, TimelineActivity.class);
        intent.putExtra("STATUS", status);
        intent.putExtra("COLUMN_TYPE", TALK);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    public static void openURL(String url) {
        // Intent to Browser
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        TweetHubApp.getActivity().startActivity(intent);
    }

    public static void searchWord(String searchWord) {
        // Intent to SearchActivity
        Activity act = TweetHubApp.getActivity();
        Intent intent = new Intent(act, SearchActivity.class);
        intent.putExtra("SEARCH_WORD", searchWord);
        act.startActivityForResult(intent, 0);
        act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    public static void tweetWithPrefix(String word) {
        // Intent to PostActivity
        Activity act = TweetHubApp.getActivity();
        Intent intent = new Intent(act, PostActivity.class);
        intent.putExtra("TWEET_PREFIX", word);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
    }

    public static void tweetWithSuffix(String word) {
        // Intent to PostActivity
        Activity act = TweetHubApp.getActivity();
        Intent intent = new Intent(act, PostActivity.class);
        intent.putExtra("TWEET_SUFFIX", word);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
    }

    public static void showUser(long userId) {
        // Intent to ProfileActivity
        Activity act = TweetHubApp.getActivity();
        Intent intent = new Intent(act, ProfileActivity.class);
        intent.putExtra("USER_ID", userId);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    public static void copyText(TwitterStatus status) {
        // Get clipboard
        ClipboardManager clipboardManager = (ClipboardManager)
                TweetHubApp.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        // Check null
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", status.getText()));
            ToastUtils.showShortToast("ツイートをコピーしました。");
        }
    }

    public static void showDetail(TwitterStatus status) {
        // Intent to TimelineActivity
        Activity act = TweetHubApp.getActivity();
        Intent intent = new Intent(act, TimelineActivity.class);
        intent.putExtra("STATUS", status);
        intent.putExtra("COLUMN_TYPE", DETAIL);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    public static void share(TwitterStatus status) {
        // Create uri
        String screenName = status.getUser().getScreenName();
        String statusId = String.valueOf(status.getId());
        Uri uri = Uri.parse("https://twitter.com/" + screenName + "/status/" + statusId);

        // Intent to external
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_TEXT, uri);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        TweetHubApp.getActivity().startActivity(intent);
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
