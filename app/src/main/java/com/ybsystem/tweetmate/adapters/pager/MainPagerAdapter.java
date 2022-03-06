package com.ybsystem.tweetmate.adapters.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.fragments.timeline.MainListTimeline;
import com.ybsystem.tweetmate.fragments.timeline.MainSearchTimeline;
import com.ybsystem.tweetmate.fragments.timeline.MainTimeline;
import com.ybsystem.tweetmate.fragments.timeline.SearchTweetTimeline;
import com.ybsystem.tweetmate.models.entities.Column;
import com.ybsystem.tweetmate.models.entities.ColumnArray;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final ColumnArray<Column> mColumns;

    public MainPagerAdapter(FragmentManager manager) {
        super(manager);
        this.mColumns = TweetMateApp.getMyAccount().getColumns();
    }

    @Override
    public int getCount() {
        return mColumns.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mColumns.get(position).getName();
    }

    @Override
    public Fragment getItem(int position) {
        Column column = mColumns.get(position);
        switch (column.getType()) {
            case LIST:
                return new MainListTimeline();
            case LIST_SINGLE:
                return new MainTimeline().newInstance(column);
            case SEARCH:
                return new MainSearchTimeline();
            case SEARCH_SINGLE:
                return new SearchTweetTimeline().newInstance(column.getName());
            default:
                return new MainTimeline().newInstance(column);
        }
    }

}
