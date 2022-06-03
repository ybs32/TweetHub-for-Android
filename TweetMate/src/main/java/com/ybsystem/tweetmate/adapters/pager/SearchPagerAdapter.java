package com.ybsystem.tweetmate.adapters.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ybsystem.tweetmate.fragments.timeline.SearchTweetTimeline;
import com.ybsystem.tweetmate.fragments.timeline.SearchUserTimeline;

import static com.ybsystem.tweetmate.resources.ResString.*;

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
                return STR_REALTIME;
            case 1:
                return STR_POPULAR;
            case 2:
                return STR_USER;
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
