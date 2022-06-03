package com.ybsystem.tweetmate.databases;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.models.enums.ClickAction;
import com.ybsystem.tweetmate.models.enums.TweetMenu;

import java.util.HashSet;
import java.util.Set;

import static com.ybsystem.tweetmate.models.enums.TweetMenu.*;

public class PrefClickAction extends PrefBase {

    public static final String KEY_LEFT_CLICK = RES.getString(R.string.pref_key_left_click);
    public static final String KEY_MIDDLE_CLICK = RES.getString(R.string.pref_key_middle_click);
    public static final String KEY_RIGHT_CLICK = RES.getString(R.string.pref_key_right_click);

    public static final String KEY_LEFT_LONG_CLICK = RES.getString(R.string.pref_key_left_long_click);
    public static final String KEY_MIDDLE_LONG_CLICK = RES.getString(R.string.pref_key_middle_long_click);
    public static final String KEY_RIGHT_LONG_CLICK = RES.getString(R.string.pref_key_right_long_click);

    public static final String KEY_TWEET_MENU = RES.getString(R.string.pref_key_tweet_menu);
    public static final String KEY_CLICK_RETWEET = RES.getString(R.string.pref_key_click_retweet);
    public static final String KEY_CLICK_HASHTAG = RES.getString(R.string.pref_key_click_hashtag);

    private PrefClickAction() {
    }

    // ----- Tap -----

    public static ClickAction getLeftClick() {
        return ClickAction.toEnum(
                getDefaultSharedPreferences().getString(KEY_LEFT_CLICK, ClickAction.MENU.getVal())
        );
    }

    public static ClickAction getMiddleClick() {
        return ClickAction.toEnum(
                getDefaultSharedPreferences().getString(KEY_MIDDLE_CLICK, ClickAction.MENU.getVal())
        );
    }

    public static ClickAction getRightClick() {
        return ClickAction.toEnum(
                getDefaultSharedPreferences().getString(KEY_RIGHT_CLICK, ClickAction.MENU.getVal())
        );
    }

    // ----- Long tap -----

    public static ClickAction getLeftLongClick() {
        return ClickAction.toEnum(
                getDefaultSharedPreferences().getString(KEY_LEFT_LONG_CLICK, ClickAction.URL.getVal())
        );
    }

    public static ClickAction getMiddleLongClick() {
        return ClickAction.toEnum(
                getDefaultSharedPreferences().getString(KEY_MIDDLE_LONG_CLICK, ClickAction.URL.getVal())
        );
    }

    public static ClickAction getRightLongClick() {
        return ClickAction.toEnum(
                getDefaultSharedPreferences().getString(KEY_RIGHT_LONG_CLICK, ClickAction.URL.getVal())
        );
    }

    // ----- Tweet menu -----

    public static Set<TweetMenu> getTweetMenu() {
        Set<String> set = getDefaultSharedPreferences().getStringSet(KEY_TWEET_MENU, null);
        if (set == null) {
            Set<TweetMenu> defaultSet = new HashSet<>();
            defaultSet.add(REPLY);
            defaultSet.add(RETWEET);
            defaultSet.add(LIKE);
            defaultSet.add(TALK);
            defaultSet.add(DELETE);
            defaultSet.add(URL);
            defaultSet.add(HASH);
            defaultSet.add(USER);
            defaultSet.add(PREV_NEXT);
            defaultSet.add(DETAIL);
            return defaultSet;
        } else {
            Set<TweetMenu> enumSet = new HashSet<>();
            for (String item : set) {
                enumSet.add(TweetMenu.toEnum(item));
            }
            return enumSet;
        }
    }

    public static String getClickRetweet() {
        return getDefaultSharedPreferences().getString(KEY_CLICK_RETWEET, "NORMAL");
    }

    public static String getClickHashtag() {
        return getDefaultSharedPreferences().getString(KEY_CLICK_HASHTAG, "NORMAL");
    }

}
