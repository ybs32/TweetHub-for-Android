package com.ybsystem.tweethub.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweethub.fragments.timeline.DetailTimeline;
import com.ybsystem.tweethub.fragments.timeline.TalkTimeline;
import com.ybsystem.tweethub.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweethub.models.enums.ColumnType;

public class TimelineActivity extends ActivityBase {

    private ColumnType mColumnType;
    private TwitterStatus mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get intent
        if (savedInstanceState == null) {
            mColumnType = (ColumnType) getIntent().getSerializableExtra("COLUMN_TYPE");
            mStatus = (TwitterStatus) getIntent().getSerializableExtra("STATUS");
        } else {
            mColumnType = (ColumnType) savedInstanceState.getSerializable("COLUMN_TYPE");
            mStatus = (TwitterStatus) savedInstanceState.getSerializable("STATUS");
        }

        // Set contents
        setTimelineFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("COLUMN_TYPE", mColumnType);
        outState.putSerializable("STATUS", mStatus);
    }

    private void setTimelineFragment() {
        Fragment fragment = null;

        switch (mColumnType) {
            case TALK:
                getSupportActionBar().setTitle("会話");
                fragment = new TalkTimeline().newInstance(mStatus);
                break;
            case DETAIL:
                getSupportActionBar().setTitle("詳細");
                fragment = new DetailTimeline().newInstance(mStatus);
                break;
            default:
                break;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, fragment);
        transaction.commit();
    }

}
