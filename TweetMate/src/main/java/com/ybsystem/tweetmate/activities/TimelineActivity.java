package com.ybsystem.tweetmate.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweetmate.fragments.timeline.DetailTimeline;
import com.ybsystem.tweetmate.fragments.timeline.MainTimeline;
import com.ybsystem.tweetmate.fragments.timeline.PrevNextTimeline;
import com.ybsystem.tweetmate.fragments.timeline.TalkTimeline;
import com.ybsystem.tweetmate.fragments.timeline.UserTimeline;
import com.ybsystem.tweetmate.models.entities.Column;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.models.enums.ColumnType;
import com.ybsystem.tweetmate.databases.PrefAppearance;

import static com.ybsystem.tweetmate.models.enums.ColumnType.*;
import static com.ybsystem.tweetmate.resources.ResString.*;

public class TimelineActivity extends ActivityBase {

    private ColumnType mColumnType;
    private TwitterStatus mStatus;
    private TwitterUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get intent
        if (savedInstanceState == null) {
            mColumnType = (ColumnType) getIntent().getSerializableExtra("COLUMN_TYPE");
            mStatus = (TwitterStatus) getIntent().getSerializableExtra("STATUS");
            mUser = (TwitterUser) getIntent().getSerializableExtra("USER");
        } else {
            mColumnType = (ColumnType) savedInstanceState.getSerializable("COLUMN_TYPE");
            mStatus = (TwitterStatus) savedInstanceState.getSerializable("STATUS");
            mUser = (TwitterUser) savedInstanceState.getSerializable("USER");
        }

        // Set timeline
        setTimelineFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("COLUMN_TYPE", mColumnType);
        outState.putSerializable("STATUS", mStatus);
        outState.putSerializable("USER", mUser);
    }

    private void setTimelineFragment() {
        Fragment fragment = null;

        switch (mColumnType) {
            case HOME:
                getSupportActionBar().setTitle(STR_HOME);
                fragment = new MainTimeline().newInstance(
                        new Column(-1, "", HOME, false)
                );
                break;
            case MENTIONS:
                getSupportActionBar().setTitle(STR_MENTIONS);
                fragment = new MainTimeline().newInstance(
                        new Column(-1, "", MENTIONS, false)
                );
                break;
            case FAVORITE:
                getSupportActionBar().setTitle(PrefAppearance.getLikeFavText());
                fragment = new MainTimeline().newInstance(
                        new Column(-1, "", FAVORITE, false)
                );
                break;
            case TALK:
                getSupportActionBar().setTitle(STR_TALK);
                fragment = new TalkTimeline().newInstance(mStatus);
                break;
            case PREV_NEXT:
                getSupportActionBar().setTitle(STR_PREV_NEXT);
                fragment = new PrevNextTimeline().newInstance(mStatus);
                break;
            case DETAIL:
                getSupportActionBar().setTitle(STR_DETAIL);
                fragment = new DetailTimeline().newInstance(mStatus);
                break;
            case FOLLOW:
                getSupportActionBar().setTitle(STR_FOLLOW + " (@" + mUser.getScreenName() + ")");
                fragment = new UserTimeline().newInstance(
                        new Column(mUser.getId(), "", FOLLOW, false)
                );
                break;
            case FOLLOWER:
                getSupportActionBar().setTitle(STR_FOLLOWER + " (@" + mUser.getScreenName() + ")");
                fragment = new UserTimeline().newInstance(
                        new Column(mUser.getId(), "", FOLLOWER, false)
                );
                break;
            case MUTE:
                getSupportActionBar().setTitle(STR_MUTE);
                fragment = new UserTimeline().newInstance(
                        new Column(-1, "", MUTE, false)
                );
                break;
            case BLOCK:
                getSupportActionBar().setTitle(STR_BLOCK);
                fragment = new UserTimeline().newInstance(
                        new Column(-1, "", BLOCK, false)
                );
                break;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(android.R.id.content, fragment);
        ft.commit();
    }

}
