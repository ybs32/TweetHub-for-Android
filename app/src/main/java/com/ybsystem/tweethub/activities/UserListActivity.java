package com.ybsystem.tweethub.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.viewpager.widget.ViewPager;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.adapters.pager.UserListPagerAdapter;
import com.ybsystem.tweethub.libs.eventbus.UserListEvent;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUserList;
import com.ybsystem.tweethub.usecases.UserUseCase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
            getSupportActionBar().setTitle("リスト" + " (@" + mUser.getScreenName()  + ")");
        } else {
            getSupportActionBar().setTitle(mUserList.getName());
        }

        // Set contents
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.item_subscribe:
                UserUseCase.subscribeList(mUserList);
                return true;
            default:
                return false;
        }
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
            // 自分のリスト画面
            if (mUser.isMyself()) {
                subscribe.setVisible(false);
                return;
            }
            // 他人のリスト画面
            if (!mUser.isMyself()) {
                mMenu.clear();
                return;
            }
        }

        if (mUserList != null) {
            // 自分のリスト
            if (mUserList.isOwner()) {
                mMenu.clear();
                return;
            }
            // 購読中のリスト
            if (mUserList.isFollowing()) {
                subscribe.setTitle("購読を解除");
                return;
            }
            // 購読していないリスト
            if (!mUserList.isFollowing()) {
                subscribe.setTitle("購読する");
                return;
            }
        }
    }

}
