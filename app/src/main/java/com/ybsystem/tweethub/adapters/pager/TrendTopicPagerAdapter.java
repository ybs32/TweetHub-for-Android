package com.ybsystem.tweethub.adapters.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ybsystem.tweethub.fragments.timeline.TopicTimeline;
import com.ybsystem.tweethub.fragments.timeline.TrendTimeline;

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
                return "トレンド";
            case 1:
                return "話題のツイート";
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
