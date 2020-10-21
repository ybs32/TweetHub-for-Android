package com.ybsystem.tweethub.utils;

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

        switch (e.getErrorCode()) {
            case 3:
                return "無効なパラメータです。";
            case 13:
                return "場所を検索できません。";
            case 17:
                return "プロファイルが見つかりません。";
            case 32:
                return "認証できませんでした。";
            case 34:
                return "対象が存在しません。";
            case 36:
                return "自分はスパム対象外です。";
            case 38:
                return "パラメータが存在しません。";
            case 44:
                return "URLパラメータが無効です。";
            case 50:
                return "ユーザーが見つかりません。";
            case 63:
                return "制限されているアカウントです。";
            case 64:
                return "凍結されているアカウントです。";
            case 68:
                return "古いAPIが使用されました。";
            case 87:
                return "許可されない操作です。";
            case 89:
                return "アクセストークンエラー";
            case 92:
                return "SSL通信が必要です。";
            case 93:
                return "許可されない操作です。";
            case 99:
                return "アクセストークンエラー";
            case 120:
                return "長すぎる値が入力されました。";
            case 130:
            case 131:
                return "Twitterのサーバーで\n"
                        + "エラーが発生しています。";
            case 135:
                return "端末の時刻が不正です。";
            case 139:
                return "既に実行済みの操作です。";
            case 144:
                return "対象が存在しません。";
            case 150:
                return "フォロー外のユーザです。";
            case 151:
                return "メッセージ送信エラー。";
            case 160:
                return "既にフォローを要求済みです。";
            case 161:
                return "フォロー制限に到達しました。\n"
                        + "しばらく経ってから試して下さい。";
            case 179:
                return "鍵アカウントの可能性があります。";
            case 185:
                return "ツイート回数制限に到達しました。\n"
                        + "しばらく経ってから試して下さい。";
            case 186:
                return "ツイートが長すぎます。";
            case 187:
                return "同じ文章は連投できません。";
            case 195:
                return "無効なURLパラメータです。";
            case 205:
                return "これ以上スパム報告できません。";
            case 214:
                return "DM許可の設定が必要です。";
            case 215:
                return "認証情報に誤りがあります。";
            case 220:
                return "トークンが制限されています。";
            case 226:
                return "スパム行為と判断されました。";
            case 231:
                return "アカウントログインエラー";
            case 251:
                return "既に存在しないリソースです。";
            case 261:
                return "アプリの権限がありません。";
            case 271:
                return "自分はミュートできません。";
            case 272:
                return "ミュートしていないユーザーです。";
            case 323:
                return "複数画像にGIFを含められません。";
            case 324:
                return "メディアIDに問題があります。";
            case 325:
                return "メディアIDが見つかりません。";
            case 326:
                return "アカウントがロックされています。";
            case 327:
                return "既にリツイート済みです。";
            case 349:
                return "メッセージを送信できません。";
            case 354:
                return "メッセージが長すぎます。";
            case 355:
                return "既に購読済みです。";
            case 385:
                return "対象が存在しません。";
            case 386:
                return "ツイートに添付し過ぎです。";
            case 407:
                return "ツイート内のURLが無効です。";
            case 415:
                return "コールバックURLエラー。";
            case 416:
                return "アプリが制限されました。";
            case 417:
                return "認証コールバックエラー。";
            case 421:
                return "ツイートを取得できません。";
            case 422:
                return "制限されたツイートです。";
            case 423:
                return "返信ユーザが限定されています。";
            default:
                return "読み込みエラー";
        }
    }

}
