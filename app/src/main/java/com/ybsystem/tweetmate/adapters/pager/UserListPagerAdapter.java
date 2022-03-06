package com.ybsystem.tweetmate.adapters.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ybsystem.tweetmate.fragments.timeline.MainTimeline;
import com.ybsystem.tweetmate.fragments.timeline.UserListTimeline;
import com.ybsystem.tweetmate.fragments.timeline.UserTimeline;
import com.ybsystem.tweetmate.models.entities.Column;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUserList;

import static com.ybsystem.tweetmate.models.enums.ColumnType.*;

public class UserListPagerAdapter extends FragmentPagerAdapter {

    private TwitterUser mUser;
    private TwitterUserList mUserList;

    public UserListPagerAdapter(FragmentManager manager,
                                TwitterUser user, TwitterUserList userList) {
        super(manager);
        this.mUser = user;
        this.mUserList = userList;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mUser != null ? "作成済み" : "ツイート";
            case 1:
                return mUser != null ? "購読中" : "メンバー\n" + mUserList.getMemberCount();
            case 2:
                return mUser != null ? "追加された" : "購読中\n" + mUserList.getSubscriberCount();
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mUser != null
                        ? new UserListTimeline().newInstance(new Column(mUser.getId(), "", LIST_CREATED, false))
                        : new MainTimeline().newInstance(new Column(mUserList.getId(), "", LIST_SINGLE, false));
            case 1:
                return mUser != null
                        ? new UserListTimeline().newInstance(new Column(mUser.getId(), "", LIST_SUBSCRIBING, false))
                        : new UserTimeline().newInstance(new Column(mUserList.getId(), "", LIST_MEMBER, false));
            case 2:
                return mUser != null
                        ? new UserListTimeline().newInstance(new Column(mUser.getId(), "", LIST_BELONGING, false))
                        : new UserTimeline().newInstance(new Column(mUserList.getId(), "", LIST_SUBSCRIBER, false));
        }
        return null;
    }

}
