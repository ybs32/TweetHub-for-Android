package com.ybsystem.tweethub.adapters.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.timeline.MainListTimeline;
import com.ybsystem.tweethub.fragments.timeline.MainTimeline;
import com.ybsystem.tweethub.fragments.timeline.SearchTweetTimeline;
import com.ybsystem.tweethub.models.entities.Column;
import com.ybsystem.tweethub.models.entities.ColumnArray;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final ColumnArray<Column> mColumns;

    public MainPagerAdapter(FragmentManager manager) {
        super(manager);
        this.mColumns = TweetHubApp.getMyAccount().getColumns();
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
            case SEARCH:
                return new SearchTweetTimeline().newInstance(column.getName());
            default:
                return new MainTimeline().newInstance(column);
        }
    }

}
