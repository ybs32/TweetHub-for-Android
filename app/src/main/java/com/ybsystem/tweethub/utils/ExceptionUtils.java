package com.ybsystem.tweethub.utils;

import java.util.HashMap;
import java.util.Map;

import twitter4j.TwitterException;

/**
 * Utils class for creating error message
 */
public class ExceptionUtils {

    private ExceptionUtils() {
    }

    /**
     * Return error message that matches the error code
     * @param e TwitterException instance
     * @return Error message
     */
    public static String getErrorMessage(TwitterException e) {

        if (e == null) {
            return "読み込みエラー";
        }

        if (e.isCausedByNetworkIssue()) {
            return "ネットワーク接続エラー";
        }

        if (e.exceededRateLimitation() || e.getErrorCode() == 88) {
            return "API制限に到達しました。\n"
                    + e.getRateLimitStatus().getSecondsUntilReset() / 60 + "分"
                    + e.getRateLimitStatus().getSecondsUntilReset() % 60 + "秒後に解除されます。";
        }

        String errorMessage = sErrorMessageMap.get(e.getErrorCode());
        if (errorMessage == null) {
            return "読み込みエラー";
        } else {
            return errorMessage;
        }
    }

    private static Map<Integer, String> sErrorMessageMap = new HashMap<Integer, String>() {
        {
            put(3, "無効なパラメータです。");
            put(13, "場所を検索できません。");
            put(17, "プロファイルが見つかりません。");
            put(32, "認証できませんでした。");
            put(34, "対象が存在しません。");
            put(36, "自分はスパム対象外です。");
            put(38, "パラメータが存在しません。");
            put(44, "URLパラメータが無効です。");
            put(50, "ユーザーが見つかりません。");
            put(63, "制限されているアカウントです。");
            put(64, "凍結されているアカウントです。");
            put(68, "古いAPIが使用されました。");
            put(87, "許可されない操作です。");
            put(89, "アクセストークンエラー");
            put(92, "SSL通信が必要です。");
            put(93, "許可されない操作です。");
            put(99, "アクセストークンエラー");
            put(120, "長すぎる値が入力されました。");
            put(130, "Twitterのサーバーで\nエラーが発生しています。");
            put(131, "Twitterのサーバーで\nエラーが発生しています。");
            put(135, "端末の時刻が不正です。");
            put(139, "既に実行済みの操作です。");
            put(144, "対象が存在しません。");
            put(150, "フォロー外のユーザです。");
            put(151, "メッセージ送信エラー。");
            put(160, "既にフォローを要求済みです。");
            put(161, "フォロー制限に到達しました。\nしばらく経ってから試して下さい。");
            put(179, "鍵アカウントの可能性があります。");
            put(185, "ツイート回数制限に到達しました。\nしばらく経ってから試して下さい。");
            put(186, "ツイートが長すぎます。");
            put(187, "同じ文章は連投できません。");
            put(195, "無効なURLパラメータです。");
            put(205, "これ以上スパム報告できません。");
            put(214, "DM許可の設定が必要です。");
            put(215, "認証情報に誤りがあります。");
            put(220, "トークンが制限されています。");
            put(226, "スパム行為と判断されました。");
            put(231, "アカウントログインエラー");
            put(251, "既に存在しないリソースです。");
            put(261, "アプリの権限がありません。");
            put(271, "自分はミュートできません。");
            put(272, "ミュートしていないユーザーです。");
            put(323, "複数画像にGIFを含められません。");
            put(324, "メディアIDに問題があります。");
            put(325, "メディアIDが見つかりません。");
            put(326, "アカウントがロックされています。");
            put(327, "既にリツイート済みです。");
            put(349, "メッセージを送信できません。");
            put(354, "メッセージが長すぎます。");
            put(355, "既に購読済みです。");
            put(385, "対象が存在しません。");
            put(386, "ツイートに添付し過ぎです。");
            put(407, "ツイート内のURLが無効です。");
            put(415, "コールバックURLエラー。");
            put(416, "アプリが制限されました。");
            put(417, "認証コールバックエラー。");
            put(421, "ツイートを取得できません。");
            put(422, "制限されたツイートです。");
            put(423, "返信ユーザが限定されています。");
        }
    };

}
