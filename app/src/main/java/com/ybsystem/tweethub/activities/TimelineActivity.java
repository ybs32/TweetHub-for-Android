package com.ybsystem.tweethub.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweethub.fragments.timeline.DetailTimeline;
import com.ybsystem.tweethub.fragments.timeline.MainTimeline;
import com.ybsystem.tweethub.fragments.timeline.TalkTimeline;
import com.ybsystem.tweethub.fragments.timeline.UserTimeline;
import com.ybsystem.tweethub.models.entities.Column;
import com.ybsystem.tweethub.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.models.enums.ColumnType;
import com.ybsystem.tweethub.storages.PrefAppearance;

import static com.ybsystem.tweethub.models.enums.ColumnType.*;

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
                getSupportActionBar().setTitle("ホーム");
                fragment = new MainTimeline().newInstance(
                        new Column(-1, "", HOME, false)
                );
                break;
            case MENTIONS:
                getSupportActionBar().setTitle("@Mentions");
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
                getSupportActionBar().setTitle("会話");
                fragment = new TalkTimeline().newInstance(mStatus);
                break;
            case DETAIL:
                getSupportActionBar().setTitle("詳細");
                fragment = new DetailTimeline().newInstance(mStatus);
                break;
            case FOLLOW:
                getSupportActionBar().setTitle("フォロー" + " (@" + mUser.getScreenName() + ")");
                fragment = new UserTimeline().newInstance(
                        new Column(mUser.getId(), "", FOLLOW, false)
                );
                break;
            case FOLLOWER:
                getSupportActionBar().setTitle("フォロワー" + " (@" + mUser.getScreenName() + ")");
                fragment = new UserTimeline().newInstance(
                        new Column(mUser.getId(), "", FOLLOWER, false)
                );
                break;
            case MUTE:
                getSupportActionBar().setTitle("ミュートユーザー");
                fragment = new UserTimeline().newInstance(
                        new Column(-1, "", MUTE, false)
                );
                break;
            case BLOCK:
                getSupportActionBar().setTitle("ブロックユーザー");
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
