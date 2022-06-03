package com.ybsystem.tweetmate.fragments.preference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.databases.PrefSystem;

public class VersionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        setVersionText(view);
        return view;
    }

    private void setVersionText(View view) {
        String newVersion = getString(R.string.app_update_info);
        String oldVersion;

        if (PrefSystem.getLanguage().equals("en")) {
            oldVersion =
                    "\n" +
                    "Ver.2.2.0 (Apr 24, 2022)\n" +
                    "・Added language setting\n" +
                    "・Added trend region setting\n" +
                    "・Released app to english-speaking countries\n" +
                    "\n" +
                    "...\n" +
                    "\n" +
                    "Ver.1.0.0 (Jun 24, 2020)\n" +
                    "・Relocated to Google Play Store\n";
        } else {
            oldVersion =
                    "\n" +
                    "Ver.2.2.0\n" +
                    "・言語設定を追加\n" +
                    "・トレンド地域の設定を追加\n" +
                    "・英語圏にアプリを公開\n" +
                    "\n" +
                    "Ver.2.1.0\n" +
                    "・動画プレーヤーを刷新\n" +
                    "・GIFを再生できない不具合を修正\n" +
                    "・YouTubeのサムネイル表示に対応\n" +
                    "・会話を表示する動作を改良\n" +
                    "\n" +
                    "Ver.2.0.3\n" +
                    "・外部からツイート先の指定に対応\n" +
                    "・デフォルトの設定値を一部修正\n" +
                    "\n" +
                    "Ver.2.0.2\n" +
                    "・ツイート画面のカメラボタンを廃止\n" +
                    "・ツイート画面に現在のアカウントを表示\n" +
                    "\n" +
                    "Ver.2.0.1\n" +
                    "・テーマ設定のプレビュー機能を修正\n" +
                    "・タイムライン設定にプレビューを追加\n" +
                    "\n" +
                    "Ver.2.0.0\n" +
                    "・アプリ名とアイコンを刷新\n" +
                    "・ライセンス表記を追加\n" +
                    "\n" +
                    "Ver.1.5.1\n" +
                    "・ダークテーマの色を微修正\n" +
                    "・メニューのユーザアイコン表示に対応\n" +
                    "\n" +
                    "Ver.1.5.0\n" +
                    "・ソースコードを公開\n" +
                    "　(オープンソース化)\n" +
                    "\n" +
                    "Ver.1.4.1\n" +
                    "・壁紙関連の不具合修正\n" +
                    "\n" +
                    "Ver.1.4.0\n" +
                    "・検索カラムを追加\n" +
                    "・保存した検索に対応\n" +
                    "・キーボード関連の不具合修正\n" +
                    "\n" +
                    "Ver.1.3.2\n" +
                    "・認証時にクラッシュする不具合を修正\n" +
                    "・タイムラインで強制終了する不具合を修正\n" +
                    "\n" +
                    "Ver.1.3.1\n" +
                    "・画像ツイートに失敗する不具合を修正\n" +
                    "\n" +
                    "Ver.1.3.0\n" +
                    "・タイムライン上でのツイート機能を実装\n" +
                    "・検索画面でツイートを可能に更新\n" +
                    "・デフォルトの背景色を一部修正\n" +
                    "・ドロワーの項目を一部修正\n" +
                    "\n" +
                    "Ver.1.2.0\n" +
                    "・リストを読み込めない不具合を修正\n" +
                    "・検索結果のインターフェースを変更\n" +
                    "・デフォルトの背景色を追加\n" +
                    "・一部のアイコンをカラーリング\n" +
                    "・非同期処理ライブラリを変更\n" +
                    "\n" +
                    "Ver.1.1.4\n" +
                    "・メンション形式を修正\n" +
                    "\n" +
                    "Ver.1.1.3\n" +
                    "・処理効率を改善\n" +
                    "・軽微な不具合修正\n" +
                    "\n" +
                    "Ver.1.1.2\n" +
                    "・レイアウトを修正\n" +
                    "・軽微な不具合修正\n" +
                    "\n" +
                    "Ver.1.1.1\n" +
                    "・レイアウトを修正\n" +
                    "\n" +
                    "Ver.1.1.0\n" +
                    "・アプリを最適化\n" +
                    "\n" +
                    "Ver.1.0.0 (2020/6/24)\n" +
                    "・GooglePlayストアへ移転\n";
        }

        TextView tv = view.findViewById(R.id.text_description);
        tv.setText(newVersion + "\n" + oldVersion);
    }

}
