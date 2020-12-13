package com.ybsystem.tweethub.adapters.array;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.TimelineActivity;
import com.ybsystem.tweethub.activities.UserListActivity;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.models.enums.ColumnType;
import com.ybsystem.tweethub.storages.PrefAppearance;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import static com.ybsystem.tweethub.models.enums.ColumnType.*;

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
                cv.setOnClickListener(v -> intentToTimeline(HOME));
                break;
            case 1:
                tv.setText("@Mentions");
                iv.setImageResource(R.drawable.ic_mention);
                cv.setOnClickListener(v -> intentToTimeline(MENTIONS));
                break;
            case 2:
                tv.setText(PrefAppearance.getLikeFavText());
                iv.setImageResource(PrefAppearance.getLikeFavDrawable());
                cv.setOnClickListener(v -> intentToTimeline(FAVORITE));
                break;
            case 3:
                tv.setText("リスト");
                iv.setImageResource(R.drawable.ic_list);
                cv.setOnClickListener(v -> intentToUserList());
                break;
            case 4:
                tv.setText("メッセージ");
                iv.setImageResource(R.drawable.ic_mail);
                cv.setOnClickListener(v -> openOfficialPlayStore());
                break;
            case 5:
                tv.setText("RTされたツイート");
                iv.setImageResource(R.drawable.ic_retweet);
                cv.setOnClickListener(v -> intentToTimeline(RETWEETED));
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
                cv.setOnClickListener(v -> openOfficialPlayStore());
                break;
        }
    }

    private void intentToTimeline(ColumnType columnType) {
        Activity act = TweetHubApp.getActivity();
        Intent intent = new Intent(act, TimelineActivity.class);
        intent.putExtra("COLUMN_TYPE", columnType);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    private void intentToUserList() {
        Activity act = TweetHubApp.getActivity();
        Intent intent = new Intent(act, UserListActivity.class);
        intent.putExtra("USER", TweetHubApp.getMyUser());
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    private void openOfficialPlayStore() {
        DialogUtils.showConfirmDialog(
                "公式アプリで確認する",
                (dialog, which) -> {
                    Uri uri = Uri.parse("market://details?id=com.twitter.android");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    TweetHubApp.getActivity().startActivity(intent);
                }
        );
    }

}
