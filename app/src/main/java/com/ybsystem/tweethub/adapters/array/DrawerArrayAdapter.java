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
import com.ybsystem.tweethub.activities.SearchActivity;
import com.ybsystem.tweethub.activities.TimelineActivity;
import com.ybsystem.tweethub.activities.UserListActivity;
import com.ybsystem.tweethub.activities.preference.SettingActivity;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.models.enums.ColumnType;
import com.ybsystem.tweethub.storages.PrefAppearance;
import com.ybsystem.tweethub.utils.CalcUtils;
import com.ybsystem.tweethub.utils.DialogUtils;

import static com.ybsystem.tweethub.models.enums.ColumnType.*;

public class DrawerArrayAdapter extends ArrayAdapter<Integer> {

    public DrawerArrayAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate
        View cv = convertView;
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (position == 0 || position == 13) {
            cv = inflater.inflate(R.layout.list_item_divider, parent, false);
            cv.findViewById(R.id.view_divider).setVisibility(View.GONE);
            cv.setEnabled(false);
            cv.setOnClickListener(null);

        } else if (position == 6 || position == 10) {
            cv = inflater.inflate(R.layout.list_item_divider, parent, false);
            cv.setEnabled(false);
            cv.setOnClickListener(null);

        } else {
            cv = inflater.inflate(R.layout.list_item_common, parent, false);

            // Get layout params
            ImageView iv = cv.findViewById(R.id.image_item);
            TextView tv = cv.findViewById(R.id.text_item);
            ViewGroup.MarginLayoutParams logoMargin = (ViewGroup.MarginLayoutParams) iv.getLayoutParams();
            ViewGroup.MarginLayoutParams titleMargin = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();

            // Adjust layout margin
            int dp25 = CalcUtils.convertDp2Px(25);
            logoMargin.setMargins(dp25, 0, 0, 0);
            titleMargin.setMargins(dp25, 0, dp25, 0);
            iv.setLayoutParams(logoMargin);
            tv.setLayoutParams(titleMargin);
        }

        // Set item
        setItemByPosition(cv, position);

        return cv;
    }

    private void setItemByPosition(View cv, int position) {
        // Find
        ImageView iv = cv.findViewById(R.id.image_item);
        TextView tv = cv.findViewById(R.id.text_item);

        switch (position) {
            case 1:
                tv.setText("ホーム");
                iv.setImageResource(R.drawable.ic_home);
                cv.setOnClickListener(v -> intentToTimeline(HOME));
                break;
            case 2:
                tv.setText("@Mentions");
                iv.setImageResource(R.drawable.ic_mention);
                cv.setOnClickListener(v -> intentToTimeline(MENTIONS));
                break;
            case 3:
                tv.setText(PrefAppearance.getLikeFavText());
                iv.setImageResource(PrefAppearance.getLikeFavDrawable());
                cv.setOnClickListener(v -> intentToTimeline(FAVORITE));
                break;
            case 4:
                tv.setText("リスト");
                iv.setImageResource(R.drawable.ic_list);
                cv.setOnClickListener(v -> intentToUserList());
                break;
            case 5:
                tv.setText("メッセージ");
                iv.setImageResource(R.drawable.ic_mail);
                cv.setOnClickListener(v -> openOfficialPlayStore());
                break;
            case 7:
                tv.setText("検索");
                iv.setImageResource(R.drawable.ic_search);
                cv.setOnClickListener(v -> intentToSearch());
                break;
            case 8:
                tv.setText("ミュート");
                iv.setImageResource(R.drawable.ic_mute);
                cv.setOnClickListener(v -> intentToTimeline(MUTE));
                break;
            case 9:
                tv.setText("ブロック");
                iv.setImageResource(R.drawable.ic_block);
                cv.setOnClickListener(v -> intentToTimeline(BLOCK));
                break;
            case 11:
                tv.setText("アカウント");
                iv.setImageResource(R.drawable.ic_user);
                cv.setOnClickListener(v -> DialogUtils.showAccountDialog());
                break;
            case 12:
                tv.setText("設定");
                iv.setImageResource(R.drawable.ic_setting);
                cv.setOnClickListener(v -> intentToSetting());
                break;
        }
    }

    private void intentToTimeline(ColumnType columnType) {
        Activity act = (Activity) getContext();
        Intent intent = new Intent(act, TimelineActivity.class);
        intent.putExtra("COLUMN_TYPE", columnType);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    private void intentToUserList() {
        Activity act = (Activity) getContext();
        Intent intent = new Intent(act, UserListActivity.class);
        intent.putExtra("USER", TweetHubApp.getMyUser());
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    private void intentToSearch() {
        Activity act = (Activity) getContext();
        Intent intent = new Intent(act, SearchActivity.class);
        act.startActivityForResult(intent, 0);
        act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
    }

    private void intentToSetting() {
        Activity act = (Activity) getContext();
        Intent intent = new Intent(act, SettingActivity.class);
        act.startActivityForResult(intent, 0);
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
