package com.ybsystem.tweethub.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ybsystem.tweethub.application.TweetHubApp;

public abstract class ActivityBase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TweetHubApp.setActivity(this);
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

}
