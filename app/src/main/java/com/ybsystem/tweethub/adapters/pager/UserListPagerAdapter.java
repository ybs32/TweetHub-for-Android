package com.ybsystem.tweethub.adapters.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ybsystem.tweethub.fragments.timeline.MainTimeline;
import com.ybsystem.tweethub.fragments.timeline.UserListTimeline;
import com.ybsystem.tweethub.fragments.timeline.UserTimeline;
import com.ybsystem.tweethub.models.entities.Column;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUserList;

import static com.ybsystem.tweethub.models.enums.ColumnType.*;

public class UserListPagerAdapter extends FragmentPagerAdapter {

    private TwitterUser mUser;
    private TwitterUserList mUserList;

    public UserListPagerAdapter(FragmentManager manager,
                                TwitterUser user, TwitterUserList userList) {
        super(manager);
        this.mUser = user;
        this.mUserList = userList;
    }

    // ページ数
    @Override
    public int getCount() {
        return 3;
    }

    // ページタイトル
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

    // 各ページ
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
