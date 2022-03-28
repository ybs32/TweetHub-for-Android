package com.ybsystem.tweetmate.adapters.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ybsystem.tweetmate.fragments.timeline.TopicTimeline;
import com.ybsystem.tweetmate.fragments.timeline.TrendTimeline;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class TrendTopicPagerAdapter extends FragmentPagerAdapter {

    public TrendTopicPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return STR_TREND;
            case 1:
                return STR_TOPIC;
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TrendTimeline();
            case 1:
                return new TopicTimeline();
        }
        return null;
    }

}
