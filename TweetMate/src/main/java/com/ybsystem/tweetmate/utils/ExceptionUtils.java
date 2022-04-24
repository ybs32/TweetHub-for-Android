package com.ybsystem.tweetmate.utils;

import com.ybsystem.tweetmate.databases.PrefSystem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import twitter4j.TwitterException;

/**
 * Utils class for creating error message
 */
public class ExceptionUtils {

    private static final Map<Integer, String> ERROR_MESSAGE_MAP;

    private ExceptionUtils() {
    }

    /**
     * Return error message that matches the error code
     *
     * @param t Throwable instance
     * @return Error message
     */
    public static String getErrorMessage(Throwable t) {

        if (!(t instanceof TwitterException)) {
            if (PrefSystem.getLanguage().equals("en")) {
                return "Failed to load…";
            } else {
                return "読み込みエラー";
            }
        }

        TwitterException e = (TwitterException) t;

        if (e.isCausedByNetworkIssue()) {
            if (PrefSystem.getLanguage().equals("en")) {
                return "Network error…";
            } else {
                return "ネットワーク接続エラー";
            }
        }

        if (e.exceededRateLimitation() || e.getErrorCode() == 88) {
            if (PrefSystem.getLanguage().equals("en")) {
                return "API limit reached.\n"
                        + "It will be lifted after "
                        + e.getRateLimitStatus().getSecondsUntilReset() / 60 + "m"
                        + e.getRateLimitStatus().getSecondsUntilReset() % 60 + "s.";
            } else {
                return "API制限に到達しました。\n"
                        + e.getRateLimitStatus().getSecondsUntilReset() / 60 + "分"
                        + e.getRateLimitStatus().getSecondsUntilReset() % 60 + "秒後に解除されます。";
            }
        }

        if (PrefSystem.getLanguage().equals("en")) {
            return "Failed to load…";
        }

        String errorMessage = ERROR_MESSAGE_MAP.get(e.getErrorCode());
        if (errorMessage == null) {
            return "読み込みエラー";
        } else {
            return errorMessage;
        }
    }

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(3, "無効なパラメータです。");
        map.put(13, "場所を検索できません。");
        map.put(17, "プロファイルが見つかりません。");
        map.put(32, "認証できませんでした。");
        map.put(34, "対象が存在しません。");
        map.put(36, "自分はスパム対象外です。");
        map.put(38, "パラメータが存在しません。");
        map.put(44, "URLパラメータが無効です。");
        map.put(50, "ユーザーが見つかりません。");
        map.put(63, "制限されているアカウントです。");
        map.put(64, "凍結されているアカウントです。");
        map.put(68, "古いAPIが使用されました。");
        map.put(87, "許可されない操作です。");
        map.put(89, "アクセストークンエラー");
        map.put(92, "SSL通信が必要です。");
        map.put(93, "許可されない操作です。");
        map.put(99, "アクセストークンエラー");
        map.put(120, "長すぎる値が入力されました。");
        map.put(130, "Twitterのサーバーで\nエラーが発生しています。");
        map.put(131, "Twitterのサーバーで\nエラーが発生しています。");
        map.put(135, "端末の時刻が不正です。");
        map.put(139, "既に実行済みの操作です。");
        map.put(144, "対象が存在しません。");
        map.put(150, "フォロー外のユーザです。");
        map.put(151, "メッセージ送信エラー。");
        map.put(160, "既にフォローを要求済みです。");
        map.put(161, "フォロー制限に到達しました。\nしばらく経ってから試して下さい。");
        map.put(179, "鍵アカウントの可能性があります。");
        map.put(185, "ツイート回数制限に到達しました。\nしばらく経ってから試して下さい。");
        map.put(186, "ツイートが長すぎます。");
        map.put(187, "同じ文章は連投できません。");
        map.put(195, "無効なURLパラメータです。");
        map.put(205, "これ以上スパム報告できません。");
        map.put(214, "DM許可の設定が必要です。");
        map.put(215, "認証情報に誤りがあります。");
        map.put(220, "トークンが制限されています。");
        map.put(226, "スパム行為と判断されました。");
        map.put(231, "アカウントログインエラー");
        map.put(251, "既に存在しないリソースです。");
        map.put(261, "アプリの権限がありません。");
        map.put(271, "自分はミュートできません。");
        map.put(272, "ミュートしていないユーザーです。");
        map.put(323, "複数画像にGIFを含められません。");
        map.put(324, "メディアIDに問題があります。");
        map.put(325, "メディアIDが見つかりません。");
        map.put(326, "アカウントがロックされています。");
        map.put(327, "既にリツイート済みです。");
        map.put(349, "メッセージを送信できません。");
        map.put(354, "メッセージが長すぎます。");
        map.put(355, "既に購読済みです。");
        map.put(385, "対象が存在しません。");
        map.put(386, "ツイートに添付し過ぎです。");
        map.put(407, "ツイート内のURLが無効です。");
        map.put(415, "コールバックURLエラー。");
        map.put(416, "アプリが制限されました。");
        map.put(417, "認証コールバックエラー。");
        map.put(421, "ツイートを取得できません。");
        map.put(422, "制限されたツイートです。");
        map.put(423, "返信ユーザが限定されています。");
        ERROR_MESSAGE_MAP = Collections.unmodifiableMap(map);
    }

}
