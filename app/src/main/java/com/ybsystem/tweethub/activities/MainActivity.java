package com.ybsystem.tweethub.activities;

import android.content.Intent;
import android.os.Bundle;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;

public class MainActivity extends ActivityBase {

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

        setContentView(R.layout.activity_main);
    }

}
