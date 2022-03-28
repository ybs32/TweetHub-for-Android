package com.ybsystem.tweetmate.adapters.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ybsystem.tweetmate.fragments.timeline.ProfileTimeline;
import com.ybsystem.tweetmate.models.entities.Column;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.models.enums.ColumnType;
import com.ybsystem.tweetmate.databases.PrefAppearance;

import static com.ybsystem.tweetmate.models.enums.ColumnType.*;
import static com.ybsystem.tweetmate.resources.ResString.*;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private TwitterUser mUser;

    public ProfilePagerAdapter(FragmentManager manager, TwitterUser user) {
        super(manager);
        this.mUser = user;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return STR_TWEET + "\n" + mUser.getStatusesCount();
            case 1:
                return STR_MEDIA;
            case 2:
                return PrefAppearance.getLikeFavText() + "\n" + mUser.getFavouritesCount();
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {

        ColumnType type = null;
        switch (position) {
            case 0: // ツイート
                type = USER_TWEET;
                break;
            case 1: // メディア
                type = USER_MEDIA;
                break;
            case 2: // いいね
                type = USER_FAVORITE;
                break;
        }

        return new ProfileTimeline().newInstance(
                new Column(mUser.getId(), "", type, false),
                mUser.isPrivate()
        );
    }

}
