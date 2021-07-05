package com.ybsystem.tweethub.adapters.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ybsystem.tweethub.fragments.timeline.SearchTweetTimeline;
import com.ybsystem.tweethub.fragments.timeline.SearchUserTimeline;

public class SearchPagerAdapter extends FragmentPagerAdapter {

    private String mSearchWord;

    public SearchPagerAdapter(FragmentManager manager, String searchWord) {
        super(manager);
        this.mSearchWord = searchWord;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "リアルタイム";
            case 1:
                return "人気";
            case 2:
                return "ユーザー";
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SearchTweetTimeline()
                        .newInstance(mSearchWord);
            case 1:
                return new SearchTweetTimeline()
                        .newInstance(mSearchWord + " min_retweets:30");
            case 2:
                return new SearchUserTimeline()
                        .newInstance(mSearchWord);
        }
        return null;
    }

}
