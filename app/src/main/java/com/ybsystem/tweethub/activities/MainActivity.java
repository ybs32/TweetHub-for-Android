package com.ybsystem.tweethub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.preference.SettingActivity;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.DrawerFragment;
import com.ybsystem.tweethub.fragments.MainFragment;
import com.ybsystem.tweethub.fragments.dialog.ChoiceDialog;
import com.ybsystem.tweethub.libs.rfab.CardItem;
import com.ybsystem.tweethub.libs.rfab.RapidFloatingActionListView;
import com.ybsystem.tweethub.models.entities.Account;
import com.ybsystem.tweethub.models.entities.AccountArray;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.utils.ActivityUtils;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;

public class MainActivity extends ActivityBase
        implements RapidFloatingActionListView.OnRfaListViewListener {

    // FloatingActionButton
    private RapidFloatingActionHelper mRfaHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if account exists
        if (!TweetHubApp.getAppData().existAccount()) {
            // Intent to OAuthActivity
            Intent intent = new Intent(this, OAuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Main process
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        setMainFragment(savedInstanceState);
        setDrawerFragment(savedInstanceState);
        setTweetActionButton();
        setMoreActionButton();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent;
        switch (position) {
            case 0: // アカウント
                showAccountDialog();
                break;
            case 1: // プロフィール
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("USER_ID", TweetHubApp.getMyUser().getId());
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
                break;
            case 2: // 設定
                intent = new Intent(this, SettingActivity.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
                break;
            default:
                break;
        }
        mRfaHelper.toggleContent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case REBOOT_IMMEDIATE:
                ActivityUtils.rebootActivity(this, 0, 0);
                break;
            case REBOOT_PREPARATION:
                // Reboot activity
                DialogUtils.showProgressDialog("設定を適用中...", this);
                new Handler().postDelayed(() -> {
                    DialogUtils.dismissProgressDialog();
                    ToastUtils.showShortToast("設定を適用しました。");
                    ActivityUtils.rebootActivity(MainActivity.this, 0, 0);
                }, 1500);
                break;
            default:
                break;
        }
    }

    private void setMainFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Fragment fragment = new MainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_main, fragment);
            transaction.commit();
        }
    }

    private void setDrawerFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Fragment fragment = new DrawerFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_drawer, fragment);
            transaction.commit();
        }
    }

    private void setTweetActionButton() {
        findViewById(R.id.fab).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostActivity.class);
            startActivityForResult(intent, 0);
            overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
        });
    }

    private void setMoreActionButton() {
        // Init
        RapidFloatingActionLayout rfaLayout = findViewById(R.id.rfal);
        RapidFloatingActionButton rfaButton = findViewById(R.id.rfab);

        // Create ListView
        RapidFloatingActionListView rfaListView = new RapidFloatingActionListView(this);
        rfaListView.setOnRfaListViewListener(this);

        List<CardItem> cardItems = new ArrayList<>();
        cardItems.add(new CardItem().setName("アカウント").setResId(R.drawable.ic_user));
        cardItems.add(new CardItem().setName("プロフィール").setResId(R.drawable.ic_doc));
        cardItems.add(new CardItem().setName("設定").setResId(R.drawable.ic_setting));
        rfaListView.setList(cardItems);

        // Build action button
        mRfaHelper = new RapidFloatingActionHelper(
                this,
                rfaLayout,
                rfaButton,
                rfaListView
        ).build();
    }

    private void showAccountDialog() {
        // Create dialog
        AccountArray<Account> accounts = TweetHubApp.getAppData().getAccounts();
        String[] items = new String[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            TwitterUser user = accounts.get(i).getUser();
            items[i] = user.getName() + " (@" + user.getScreenName() + ")";
        }
        ChoiceDialog dialog = new ChoiceDialog()
                .newInstance(items, accounts.getCurrentAccountNum());

        dialog.setOnItemClickListener((dialog1, position) -> {
            // Change account and reboot
            accounts.setCurrentAccount(position);
            TweetHubApp.getInstance().init();
            ToastUtils.showShortToast("アカウントを切り替えました。");
            ActivityUtils.rebootActivity(MainActivity.this, 0, 0);
        });

        // Show dialog
        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentByTag("AccountDialog") == null) {
            dialog.show(manager, "AccountDialog");
        }
    }

}
