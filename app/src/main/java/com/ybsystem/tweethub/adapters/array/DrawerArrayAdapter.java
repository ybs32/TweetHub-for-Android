package com.ybsystem.tweethub.adapters.array;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.storages.PrefAppearance;
import com.ybsystem.tweethub.utils.ToastUtils;

public class DrawerArrayAdapter extends ArrayAdapter<Integer> {

    public DrawerArrayAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate
        View cv = convertView;
        if (cv == null) {
            cv = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_common, parent, false);
        }
        // Set contents
        setItemByPosition(cv, position);

        return cv;
    }

    private void setItemByPosition(View cv, int position) {
        // Find view
        TextView tv = cv.findViewById(R.id.text_item);
        ImageView iv = cv.findViewById(R.id.image_item);

        switch (position) {
            case 0:
                tv.setText("ホーム");
                iv.setImageResource(R.drawable.ic_home);
                cv.setOnClickListener(v -> {
                    // TODO: Implement click action
                    ToastUtils.showShortToast(tv.getText().toString());
                });
                break;
            case 1:
                tv.setText("@Mentions");
                iv.setImageResource(R.drawable.ic_mention);
                cv.setOnClickListener(v -> {
                    // TODO: Implement click action
                    ToastUtils.showShortToast(tv.getText().toString());
                });
                break;
            case 2:
                tv.setText(PrefAppearance.getLikeFavText());
                iv.setImageResource(PrefAppearance.getLikeFavDrawable());
                cv.setOnClickListener(v -> {
                    // TODO: Implement click action
                    ToastUtils.showShortToast(tv.getText().toString());
                });
                break;
            case 3:
                tv.setText("リスト");
                iv.setImageResource(R.drawable.ic_list);
                cv.setOnClickListener(v -> {
                    // TODO: Implement click action
                    ToastUtils.showShortToast(tv.getText().toString());
                });
                break;
            case 4:
                tv.setText("メッセージ");
                iv.setImageResource(R.drawable.ic_mail);
                cv.setOnClickListener(v -> {
                    // TODO: Implement click action
                    ToastUtils.showShortToast(tv.getText().toString());
                });
                break;
            case 5:
                tv.setText("RTされたツイート");
                iv.setImageResource(R.drawable.ic_retweet);
                cv.setOnClickListener(v -> {
                    // TODO: Implement click action
                    ToastUtils.showShortToast(tv.getText().toString());
                });
                break;
            case 6:
                tv.setText("ミュートユーザー");
                iv.setImageResource(R.drawable.ic_mute);
                cv.setOnClickListener(v -> {
                    // TODO: Implement click action
                    ToastUtils.showShortToast(tv.getText().toString());
                });
                break;
            case 7:
                tv.setText("ブロックユーザー");
                iv.setImageResource(R.drawable.ic_block);
                cv.setOnClickListener(v -> {
                    // TODO: Implement click action
                    ToastUtils.showShortToast(tv.getText().toString());
                });
                break;
            case 8:
                tv.setText("フォローリクエスト");
                iv.setImageResource(R.drawable.ic_user);
                cv.setOnClickListener(v -> {
                    // TODO: Implement click action
                    ToastUtils.showShortToast(tv.getText().toString());
                });
                break;
        }
    }

}
