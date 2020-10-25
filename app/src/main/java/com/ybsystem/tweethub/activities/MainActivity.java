package com.ybsystem.tweethub.activities;

import android.content.Intent;
import android.os.Bundle;

import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.libs.rfab.CardItem;
import com.ybsystem.tweethub.libs.rfab.RapidFloatingActionListView;

import java.util.ArrayList;
import java.util.List;

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

        setContentView(R.layout.activity_main);
        setMoreActionButton();
    }

    @Override
    public void onItemClick(int position) {
        mRfaHelper.toggleContent();
    }

    private void setMoreActionButton() {
        // Find view
        RapidFloatingActionLayout rfaLayout = findViewById(R.id.rfal);
        RapidFloatingActionButton rfaButton = findViewById(R.id.rfab);

        // Create ListView
        RapidFloatingActionListView rfaListView = new RapidFloatingActionListView(this);
        rfaListView.setOnRfaListViewListener(this);

        List<CardItem> cardItems = new ArrayList<>();
        rfaListView.setList(cardItems);

        // Build action button
        mRfaHelper = new RapidFloatingActionHelper(
                this,
                rfaLayout,
                rfaButton,
                rfaListView
        ).build();
    }

}
