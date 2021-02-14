package com.ybsystem.tweethub.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wangjie.rapidfloatingactionbutton.*;
import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.preference.SettingActivity;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.dialog.*;
import com.ybsystem.tweethub.fragments.fragment.DrawerFragment;
import com.ybsystem.tweethub.fragments.fragment.EasyTweetFragment;
import com.ybsystem.tweethub.fragments.fragment.MainFragment;
import com.ybsystem.tweethub.libs.glide.GlideApp;
import com.ybsystem.tweethub.libs.rfab.CardItem;
import com.ybsystem.tweethub.libs.rfab.RapidFloatingActionListView;
import com.ybsystem.tweethub.storages.*;
import com.ybsystem.tweethub.utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;
import static com.ybsystem.tweethub.models.enums.ConfirmAction.*;

public class MainActivity extends ActivityBase
        implements RapidFloatingActionListView.OnRfaListViewListener {

    // FloatingActionButton
    private RapidFloatingActionHelper mRfaHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if account exists
        if (TweetHubApp.getData().isAccountEmpty()) {
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
        setTweetAction(savedInstanceState);
        setWallpaper();
        showUpdateInfo();
    }

    @Override
    public void onBackPressed() {
        int s = GravityCompat.START;
        DrawerLayout dl = findViewById(R.id.drawer_layout);

        // Check drawer state
        if (dl.isDrawerOpen(s)) {
            dl.closeDrawer(s);
        } else if (PrefSystem.getConfirmSettings().contains(FINISH)) {
            DialogUtils.showConfirm("アプリを終了しますか？", (d, w) -> finish());
        } else {
            finish();
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent;
        switch (position) {
            case 0: // 検索
                intent = new Intent(this, SearchActivity.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
                break;
            case 1: // アカウント
                DialogUtils.showAccountChoice();
                break;
            case 2: // プロフィール
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("USER_ID", TweetHubApp.getMyUser().getId());
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
                break;
            case 3: // 設定
                intent = new Intent(this, SettingActivity.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
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
                DialogUtils.showProgress("設定を適用中...", this);
                new Handler().postDelayed(() -> {
                    DialogUtils.dismissProgress();
                    ToastUtils.showShortToast("設定を適用しました。");
                    ActivityUtils.rebootActivity(MainActivity.this, 0, 0);
                }, 1500);
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

    private void setTweetAction(Bundle savedInstanceState) {
        if (PrefSystem.isEasyTweetEnabled()) {
            // EasyTweet
            if (savedInstanceState == null) {
                Fragment fragment = new EasyTweetFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_easy_tweet, fragment);
                transaction.commit();
            }
            // Hide buttons
            findViewById(R.id.fab).setVisibility(View.GONE);
            findViewById(R.id.rfab).setVisibility(View.GONE);
        } else {
            // TweetButton
            findViewById(R.id.fab).setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), PostActivity.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
            });
            // MoreButton
            setMoreActionButton();
        }
    }

    private void setMoreActionButton() {
        // Find
        RapidFloatingActionLayout rfaLayout = findViewById(R.id.rfal);
        RapidFloatingActionButton rfaButton = findViewById(R.id.rfab);

        // Create ListView
        RapidFloatingActionListView rfaListView = new RapidFloatingActionListView(this);
        rfaListView.setOnRfaListViewListener(this);

        List<CardItem> cardItems = new ArrayList<>();
        cardItems.add(new CardItem().setName("検索").setResId(R.drawable.ic_search));
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

    private void setWallpaper() {
        // Check setting
        String path = PrefWallpaper.getWallpaperPath();
        if (path.equals("未設定")) {
            return;
        }
        // Get color
        int color = ResourceUtils.getBackgroundColor();
        color = PrefWallpaper.applyTransparency(color);

        // Visible wallpaper
        ImageView wallpaper = findViewById(R.id.image_wallpaper);
        wallpaper.setColorFilter(color);
        wallpaper.setVisibility(View.VISIBLE);

        // Load image
        Uri uri = Uri.fromFile(new File(path));
        GlideApp.with(this).load(uri).into(wallpaper);
    }

    private void showUpdateInfo() {
        // Get version
        String version = TweetHubApp.getData().getVersion();
        String newVersion = getString(R.string.app_version);
        String updateInfo = getString(R.string.app_update_info);

        // Check version
        if (version.equals(newVersion)) {
            return;
        }
        // Show dialog
        NoticeDialog dialog = new NoticeDialog().newInstance("アップデート情報", updateInfo);
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("NoticeDialog") == null) {
            dialog.show(fm, "NoticeDialog");
        }
        // Save new version
        TweetHubApp.getData().setVersion(newVersion);
    }

}
