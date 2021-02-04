package com.ybsystem.tweethub.fragments.preference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ybsystem.tweethub.R;

public class VersionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        setVersionText(view);
        return view;
    }

    private void setVersionText(View view) {
        String newVersion = getString(R.string.app_update_info);
        String oldVersion =
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
                        "Ver.1.0.0\n" +
                        "・GooglePlayストアへ移転\n";

        TextView tv = view.findViewById(R.id.text_description);
        tv.setText(newVersion + "\n" + oldVersion);
    }

}
