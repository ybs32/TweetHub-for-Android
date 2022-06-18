package com.ybsystem.tweetmate.utils;

import android.widget.EditText;

import androidx.fragment.app.FragmentManager;

import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.fragments.dialog.ListDialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
        map.put("画像のみ", "filter:images");
        map.put("動画のみ", "filter:videos");
        map.put("GIFアニメのみ", "card_name:animated_gif");
        map.put("メディアを含む", "filter:media");
        map.put("ハッシュタグを含む", "filter:hashtags");
        map.put("いいね数が～以上", "min_faves:100");
        map.put("いいね数が～以下", "-min_faves:100");
        map.put("リツイート数が～以上", "min_retweets:100");
        map.put("リツイート数が～以下", "-min_retweets:100");
        map.put("特定の文字を含まない", "-:除外ワード");
        map.put("特定のユーザーに関連する", "@" + TweetMateApp.getMyUser().getScreenName());
        map.put("特定のユーザーがしたツイート", "from:@" + TweetMateApp.getMyUser().getScreenName());
        map.put("特定のユーザー宛てのツイート", "to:@" + TweetMateApp.getMyUser().getScreenName());
        map.put("認証済みのユーザーのツイート", "filter:verified");
        map.put("ポジティブなツイート", ":)");
        map.put("ネガティブなツイート", ":(");
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
                ToastUtils.showLongToast("先に検索ワードを入力して下さい。");
                dialog.dismiss();
                return;
            }
            ToastUtils.showLongToast("条件を入力しました。");
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
