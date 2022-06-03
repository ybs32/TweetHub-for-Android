package com.ybsystem.tweetmate.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.viewpager.widget.ViewPager;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.pager.UserListPagerAdapter;
import com.ybsystem.tweetmate.libs.eventbus.UserListEvent;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUserList;
import com.ybsystem.tweetmate.usecases.UserListUseCase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class UserListActivity extends ActivityBase {

    private TwitterUser mUser;
    private TwitterUserList mUserList;

    private Menu mMenu;
    private ViewPager mListPager;
    private UserListPagerAdapter mListPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init
        mUser = (TwitterUser) getIntent().getSerializableExtra("USER");
        mUserList = (TwitterUserList) getIntent().getSerializableExtra("USER_LIST");

        // Set actionbar title
        if (mUser != null) {
            getSupportActionBar().setTitle(STR_LIST + " (@" + mUser.getScreenName()  + ")");
        } else {
            getSupportActionBar().setTitle(mUserList.getName());
        }

        // Set
        setContentView(R.layout.activity_pager);
        setUserListPager();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(UserListEvent event) {
        updateMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.userlist, menu);
        updateMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Subscribe
            case R.id.item_subscribe:
                UserListUseCase.subscribeList(mUserList);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUserListPager() {
        mListPager = findViewById(R.id.pager_common);
        mListPager.setOffscreenPageLimit(3);
        mListPagerAdapter = new UserListPagerAdapter(getSupportFragmentManager(), mUser, mUserList);
        mListPager.setAdapter(mListPagerAdapter);
    }

    private void updateMenu() {
        MenuItem subscribe = mMenu.findItem(R.id.item_subscribe);

        if (mUser != null) {
            // My list screen
            if (mUser.isMyself()) {
                subscribe.setVisible(false);
                return;
            }
            // Not my list screen
            if (!mUser.isMyself()) {
                mMenu.clear();
                return;
            }
        }

        if (mUserList != null) {
            // My List
            if (mUserList.isOwner()) {
                mMenu.clear();
                return;
            }
            // Subscribed list
            if (mUserList.isFollowing()) {
                subscribe.setTitle(STR_UNSUBSCRIBE);
                return;
            }
            // Not subscribed list
            if (!mUserList.isFollowing()) {
                subscribe.setTitle(STR_SUBSCRIBE);
                return;
            }
        }
    }

}
