package com.ybsystem.tweethub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.storages.PrefTheme;

public abstract class ActivityBase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TweetHubApp.setActivity(this);
        setTheme();
        setBackButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TweetHubApp.setActivity(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        switch (TweetHubApp.getActivityName(getLocalClassName())) {
            case "MainActivity":
            case "SearchActivity":
                findViewById(R.id.root).requestFocus();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TweetHubApp.setActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (TweetHubApp.getActivityName(getLocalClassName())) {
            case "MainActivity":
                break;
            case "PostActivity":
            case "PhotoActivity":
            case "VideoActivity":
                overridePendingTransition(R.anim.none, R.anim.fade_out_to_bottom);
                break;
            case "ProfileActivity":
            case "TimelineActivity":
            case "UserListActivity":
            case "SearchActivity":
            case "SettingActivity":
            case "ThemeActivity":
            case "AppearanceActivity":
            case "ClickActionActivity":
            case "SystemActivity":
            case "WallpaperActivity":
            case "ColumnActivity":
            case "AccountActivity":
            case "BrowserAuthActivity":
            case "AboutActivity":
            case "VersionActivity":
                overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_to_right);
                break;
        }
    }

    private void setTheme() {
        switch (PrefTheme.getTheme()) {
            case "LIGHT":
                setTheme(R.style.LightTheme);
                break;
            case "DARK":
                setTheme(R.style.DarkTheme);
                break;
        }
    }

    private void setBackButton() {
        switch (TweetHubApp.getActivityName(getLocalClassName())) {
            case "MainActivity":
            case "PostActivity":
            case "PhotoActivity":
            case "VideoActivity":
                break;
            case "ProfileActivity":
            case "TimelineActivity":
            case "UserListActivity":
            case "SearchActivity":
            case "SettingActivity":
            case "ThemeActivity":
            case "AppearanceActivity":
            case "ClickActionActivity":
            case "SystemActivity":
            case "WallpaperActivity":
            case "ColumnActivity":
            case "AccountActivity":
            case "BrowserAuthActivity":
            case "AboutActivity":
            case "VersionActivity":
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
        }
    }

}
