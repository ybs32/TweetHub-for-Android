package com.ybsystem.tweetmate.databases;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterMediaEntity;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.models.enums.ConfirmAction;
import com.ybsystem.tweetmate.models.enums.MediaQuality;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import twitter4j.MediaEntity.Variant;

import static com.ybsystem.tweetmate.models.enums.ConfirmAction.*;

public class PrefSystem extends PrefBase {

    public static final String KEY_QUALITY_IMAGE = RES.getString(R.string.pref_key_quality_image);
    public static final String KEY_QUALITY_VIDEO = RES.getString(R.string.pref_key_quality_video);

    public static final String KEY_CONFIRM_SETTING = RES.getString(R.string.pref_key_confirm_setting);

    public static final String KEY_COUNT_BOOT_LOAD = RES.getString(R.string.pref_key_count_boot_load);
    public static final String KEY_COUNT_SCROLL_LOAD = RES.getString(R.string.pref_key_count_scroll_load);

    public static final String KEY_EASY_TWEET = RES.getString(R.string.pref_key_easy_tweet);

    private PrefSystem() {
    }

    // ----- メディア -----
    // https://developer.twitter.com/en/docs/accounts-and-users/user-profile-images-and-banners
    // https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/entities-object#media

    public static MediaQuality getImageQuality() {
        return MediaQuality.toEnum(
                getDefaultSharedPreferences().getString(KEY_QUALITY_IMAGE, MediaQuality.MIDDLE.getVal())
        );
    }

    public static MediaQuality getVideoQuality() {
        return MediaQuality.toEnum(
                getDefaultSharedPreferences().getString(KEY_QUALITY_VIDEO, MediaQuality.MIDDLE.getVal())
        );
    }

    public static String getBannerByQuality(TwitterUser user) {
        switch (getImageQuality()) {
            case LOW:
                return user.getProfileBannerMobileURL(); // mobile (320x160)
            case MIDDLE:
                return user.getProfileBannerMobileRetinaURL(); // mobile_retina (640x320)
            case HIGH:
                return user.getProfileBannerRetinaURL(); // web_retina (1040x520)
            default:
                return null;
        }
    }

    public static String getProfileByQuality(TwitterUser user) {
        switch (getImageQuality()) {
            case LOW:
                return user.getBiggerProfileImageURLHttps(); // bigger (73x73)
            case MIDDLE:
                return user.getProfileImageURL400x400Https(); // 400x400 (400x400)
            case HIGH:
                return user.getOriginalProfileImageURLHttps(); // original (512x512)
            default:
                return null;
        }
    }

    public static String getProfileThumbByQuality(TwitterUser user) {
        switch (getImageQuality()) {
            case LOW:
                return user.getProfileImageUrlHttps(); // normal (48x48)
            case MIDDLE:
            case HIGH:
                return user.getBiggerProfileImageURLHttps(); // bigger (73x73)
            default:
                return null;
        }
    }

    public static String getMediaByQuality(String imageURL, boolean isThirdMedia) {
        if (isThirdMedia) {
            return imageURL;
        }
        switch (getImageQuality()) {
            case LOW:
                return imageURL + ":small"; // small (680x454)
            case MIDDLE:
                return imageURL + ":medium"; // medium (1200x800)
            case HIGH:
                return imageURL + ":large"; // large (2048x1366)
            default:
                return null;
        }
    }

    public static String getMediaThumbByQuality(String imageURL, boolean isThirdMedia) {
        if (isThirdMedia) {
            return imageURL;
        }
        switch (getImageQuality()) {
            case LOW:
                return imageURL + ":thumb"; // thumb (150x150)
            case MIDDLE:
            case HIGH:
                return imageURL + ":small"; // small (680x454)
            default:
                return null;
        }
    }

    public static String getVideoByQuality(TwitterMediaEntity mediaEntity) {
        // Filter mp4
        ArrayList<Variant> mp4List = new ArrayList<>();
        for (Variant v : mediaEntity.getVideoVariants()) {
            if (v.getUrl().lastIndexOf("mp4") != -1) {
                mp4List.add(v);
            }
        }
        // Sort by bitrate
        mp4List.sort((v1, v2) ->
                v1.getBitrate() - v2.getBitrate()
        );
        switch (getVideoQuality()) {
            case LOW:
                return mp4List.get(0).getUrl();
            case MIDDLE:
                if (mp4List.size() == 1) {
                    return mp4List.get(0).getUrl();
                } else {
                    return mp4List.get(1).getUrl();
                }
            case HIGH:
                return mp4List.get(mp4List.size() - 1).getUrl();
            default:
                return null;
        }
    }

    // ----- システム -----

    public static Set<ConfirmAction> getConfirmSettings() {
        Set<String> set = getDefaultSharedPreferences().getStringSet(KEY_CONFIRM_SETTING, null);
        if (set == null) {
            Set<ConfirmAction> defaultSet = new HashSet<>();
            defaultSet.add(TWEET);
            defaultSet.add(RETWEET);
            defaultSet.add(LIKE);
            defaultSet.add(DELETE);
            defaultSet.add(FOLLOW);
            defaultSet.add(MUTE);
            defaultSet.add(BLOCK);
            defaultSet.add(SUBSCRIBE);
            defaultSet.add(FINISH);
            return defaultSet;
        } else {
            Set<ConfirmAction> enumSet = new HashSet<>();
            for (String item : set) {
                enumSet.add(ConfirmAction.toEnum(item));
            }
            return enumSet;
        }
    }

    // ----- タイムライン -----

    public static int getBootLoadCount() {
        return Integer.parseInt(
                getDefaultSharedPreferences().getString(KEY_COUNT_BOOT_LOAD, "20")
        );
    }

    public static int getScrollLoadCount() {
        return Integer.parseInt(
                getDefaultSharedPreferences().getString(KEY_COUNT_SCROLL_LOAD, "100")
        );
    }

    // ----- ツイート -----

    public static boolean isEasyTweetEnabled() {
        return getDefaultSharedPreferences().getBoolean(KEY_EASY_TWEET, true);
    }

}
