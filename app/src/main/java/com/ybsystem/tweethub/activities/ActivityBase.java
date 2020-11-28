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
    protected void onResume(){
        super.onResume();
        TweetHubApp.setActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TweetHubApp.setActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (getLocalClassName()) {
            case "activities.MainActivity":
                break;
            case "activities.PostActivity":
                overridePendingTransition(R.anim.none, R.anim.fade_out_to_bottom);
                break;
            case "activities.TimelineActivity":
            case "activities.preference.SettingActivity":
            case "activities.preference.ThemeActivity":
            case "activities.preference.AppearanceActivity":
            case "activities.preference.ClickActionActivity":
            case "activities.preference.SystemActivity":
            case "activities.preference.WallpaperActivity":
            case "activities.preference.ColumnActivity":
            case "activities.preference.AccountActivity":
            case "activities.preference.BrowserAuthActivity":
                overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_to_right);
                break;
            default:
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
            default:
                break;
        }
    }

    private void setBackButton() {
        switch (getLocalClassName()) {
            case "activities.MainActivity":
            case "activities.PostActivity":
                break;
            case "activities.TimelineActivity":
            case "activities.preference.SettingActivity":
            case "activities.preference.ThemeActivity":
            case "activities.preference.AppearanceActivity":
            case "activities.preference.ClickActionActivity":
            case "activities.preference.SystemActivity":
            case "activities.preference.WallpaperActivity":
            case "activities.preference.ColumnActivity":
            case "activities.preference.AccountActivity":
            case "activities.preference.BrowserAuthActivity":
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
            default:
                break;
        }
    }

}
