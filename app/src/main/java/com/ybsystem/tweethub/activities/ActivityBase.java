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
        switch (getLocalClassName()) {
            case "activities.MainActivity":
            case "activities.SearchActivity":
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
        switch (getLocalClassName()) {
            case "activities.MainActivity":
                break;
            case "activities.PostActivity":
            case "activities.PhotoActivity":
            case "activities.VideoActivity":
                overridePendingTransition(R.anim.none, R.anim.fade_out_to_bottom);
                break;
            case "activities.ProfileActivity":
            case "activities.TimelineActivity":
            case "activities.UserListActivity":
            case "activities.SearchActivity":
            case "activities.preference.SettingActivity":
            case "activities.preference.ThemeActivity":
            case "activities.preference.AppearanceActivity":
            case "activities.preference.ClickActionActivity":
            case "activities.preference.SystemActivity":
            case "activities.preference.WallpaperActivity":
            case "activities.preference.ColumnActivity":
            case "activities.preference.AccountActivity":
            case "activities.preference.BrowserAuthActivity":
            case "activities.preference.AboutActivity":
            case "activities.preference.VersionActivity":
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
        switch (getLocalClassName()) {
            case "activities.MainActivity":
            case "activities.PostActivity":
            case "activities.PhotoActivity":
            case "activities.VideoActivity":
                break;
            case "activities.ProfileActivity":
            case "activities.TimelineActivity":
            case "activities.UserListActivity":
            case "activities.SearchActivity":
            case "activities.preference.SettingActivity":
            case "activities.preference.ThemeActivity":
            case "activities.preference.AppearanceActivity":
            case "activities.preference.ClickActionActivity":
            case "activities.preference.SystemActivity":
            case "activities.preference.WallpaperActivity":
            case "activities.preference.ColumnActivity":
            case "activities.preference.AccountActivity":
            case "activities.preference.BrowserAuthActivity":
            case "activities.preference.AboutActivity":
            case "activities.preference.VersionActivity":
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
        }
    }

}
