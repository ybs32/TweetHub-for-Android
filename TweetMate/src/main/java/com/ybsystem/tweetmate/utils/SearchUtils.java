package com.ybsystem.tweetmate.utils;

import android.widget.EditText;

import androidx.fragment.app.FragmentManager;

import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.fragments.dialog.ListDialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.ybsystem.tweetmate.resources.ResString.*;

/**
 * Utils class for searching
 */
public class SearchUtils {

    public static final List<String> CONDITION_KEYS;
    public static final List<String> CONDITION_VALUES;

    private SearchUtils() {
    }

    static {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(STR_ONLY_IMAGES, "filter:images");
        map.put(STR_ONLY_VIDEOS, "filter:videos");
        map.put(STR_ONLY_GIF_ANIMES, "card_name:animated_gif");
        map.put(STR_CONTAINS_MEDIA, "filter:media");
        map.put(STR_CONTAINS_HASHTAG, "filter:hashtags");
        map.put(STR_MORE_THAN_X_LIKES, "min_faves:100");
        map.put(STR_LESS_THAN_X_LIKES, "-min_faves:100");
        map.put(STR_MORE_THAN_X_RETWEETS, "min_retweets:100");
        map.put(STR_LESS_THAN_X_RETWEETS, "-min_retweets:100");
        map.put(STR_ONLY_ENGLISH_TWEETS, "lang:en");
        map.put(STR_ONLY_JAPANESE_TWEETS, "lang:ja");
        map.put(STR_NOT_CONTAIN_SPECIFIC_WORD, "-:" + STR_EXCLUDE_WORD);
        map.put(STR_RELATES_TO_SPECIFIC_USER, "@" + TweetMateApp.getMyUser().getScreenName());
        map.put(STR_TWEETS_FROM_SPECIFIC_USER, "from:@" + TweetMateApp.getMyUser().getScreenName());
        map.put(STR_TWEETS_TO_SPECIFIC_USER, "to:@" + TweetMateApp.getMyUser().getScreenName());
        map.put(STR_TWEETS_FROM_VERIFIED_USER, "filter:verified");
        map.put(STR_POSITIVE_TWEETS, ":)");
        map.put(STR_NEGATIVE_TWEETS, ":(");
        CONDITION_KEYS = new ArrayList<>(map.keySet());
        CONDITION_VALUES = new ArrayList<>(map.values());
    }

    public static void showConditionalSearch(EditText edit) {
        // Prepare dialog
        String[] items = new String[CONDITION_KEYS.size()];
        for (int i = 0; i < CONDITION_KEYS.size(); i++) {
            items[i] = CONDITION_KEYS.get(i);
        }
        ListDialog dialog = new ListDialog().newInstance(items);
        dialog.setOnItemClickListener((parent, v, position, id) -> {
            // Require input search word first
            if (edit.getText().length() == 0) {
                ToastUtils.showLongToast(STR_FAIL_CONDITIONAL_SEARCH);
                dialog.dismiss();
                return;
            }
            ToastUtils.showLongToast(STR_SUCCESS_CONDITIONAL_SEARCH);
            edit.setText(edit.getText() + " " + CONDITION_VALUES.get(position));
            edit.setSelection(edit.getText().length());
            dialog.dismiss();
        });
        // Show dialog
        FragmentManager fm = TweetMateApp.getActivity().getSupportFragmentManager();
        if (fm.findFragmentByTag("ListDialog") == null) {
            dialog.show(fm, "ListDialog");
        }
    }

}
