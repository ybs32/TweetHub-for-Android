package com.ybsystem.tweetmate.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.pager.ProfilePagerAdapter;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.fragments.fragment.ProfileFragment;
import com.ybsystem.tweetmate.fragments.dialog.UserListDialog;
import com.ybsystem.tweetmate.fragments.timeline.TimelineBase;
import com.ybsystem.tweetmate.libs.eventbus.UserEvent;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.usecases.ClickUseCase;
import com.ybsystem.tweetmate.usecases.UserUseCase;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.User;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class ProfileActivity extends ActivityBase {

    private TwitterUser mUser;
    private Relationship mRelation;

    private Menu mMenu;
    private ViewPager mProfileTlPager;
    private ProfilePagerAdapter mProfilePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        // Fetch user
        long userId = getIntent().getLongExtra("USER_ID", 0);
        fetchTwitterUser(userId, savedInstanceState);
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
    public void onEvent(UserEvent event) {
        updateMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Mute
            case R.id.item_mute:
                UserUseCase.mute(mUser);
                return true;
            // Block
            case R.id.item_block:
                UserUseCase.block(mUser);
                return true;
            // Replay
            case R.id.item_reply:
                ClickUseCase.tweetWithPrefix("@" + mUser.getScreenName());
                return true;
            // Add list
            case R.id.item_add_list:
                UserListDialog dialog = new UserListDialog().newInstance(mUser);
                FragmentManager fm = getSupportFragmentManager();
                if (fm.findFragmentByTag("UserListDialog") == null) {
                    dialog.show(fm, "UserListDialog");
                }
                return true;
            // Open browser
            case R.id.item_browser:
                ClickUseCase.openUser(mUser);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchTwitterUser(long userId, Bundle savedInstanceState) {
        // Async
        Observable<User> observable = Observable.create(e -> {
            // Fetch user
            Twitter twitter = TweetMateApp.getTwitter();
            User user4j = twitter.showUser(userId);

            if (user4j != null) {
                mUser = new TwitterUser(user4j);
                mRelation = twitter.showFriendship(
                        TweetMateApp.getMyUser().getId(), mUser.getId()
                );
                e.onNext(user4j);
            }
            e.onComplete();
        });

        DisposableObserver<User> disposable = new DisposableObserver<User>() {
            @Override
            public void onNext(User ignored) {
                // Success
                mUser.setFriend(mRelation.isSourceFollowingTarget());
                mUser.setFollower(mRelation.isSourceFollowedByTarget());
                mUser.setMuting(mRelation.isSourceMutingTarget());
                mUser.setBlocking(mRelation.isSourceBlockingTarget());

                // Main process
                setProfileFragment(savedInstanceState);
                setProfileTlPager();
                updateMenu();
                showScreen();
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                ToastUtils.showShortToast(STR_FAIL_GET_USER);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
                finish();
                overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_to_right);
            }

            @Override
            public void onComplete() {
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable);
    }

    private void setProfileFragment(Bundle savedInstanceState) {
        // Set actionbar text
        if (mUser.getId() == TweetMateApp.getMyUser().getId()) {
            getSupportActionBar().setTitle(STR_PROFILE_MYSELF);
        } else {
            getSupportActionBar().setTitle(mUser.getName() + " (@" + mUser.getScreenName() + ")");
        }
        // Set profile fragment
        if (savedInstanceState == null) {
            Fragment fragment = new ProfileFragment().newInstance(mUser);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    private void setProfileTlPager() {
        // Set profile pager
        mProfileTlPager = findViewById(R.id.pager_profile);
        mProfileTlPager.setOffscreenPageLimit(3);
        mProfilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), mUser);
        mProfileTlPager.setAdapter(mProfilePagerAdapter);

        // Set tab
        TabLayout tabLayout = findViewById(R.id.tab_profile);
        tabLayout.setupWithViewPager(mProfileTlPager);
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);

        // When tab clicked
        for (int i = 0; i < 3; i++) {
            final int TAB_NUM = i;
            vg.getChildAt(i).setOnClickListener(v -> {
                // If current tab clicked, move top of the timeline
                int currentPage = mProfileTlPager.getCurrentItem();
                if (currentPage == TAB_NUM) {
                    TimelineBase timeline = (TimelineBase)
                            mProfilePagerAdapter.instantiateItem(mProfileTlPager, currentPage);
                    timeline.getRecyclerView().scrollToPosition(0);
                }
            });
        }
    }

    private void updateMenu() {
        // Find
        MenuItem mute = mMenu.findItem(R.id.item_mute);
        MenuItem block = mMenu.findItem(R.id.item_block);
        MenuItem reply = mMenu.findItem(R.id.item_reply);
        MenuItem addList = mMenu.findItem(R.id.item_add_list);

        // Check if myself
        if (mUser.isMyself()) {
            mute.setVisible(false);
            block.setVisible(false);
            reply.setVisible(false);
            addList.setVisible(false);
            return;
        }
        // Check muting
        if (mUser.isMuting()) {
            mute.setTitle(STR_UNMUTE);
        } else {
            mute.setTitle(STR_MUTE);
        }
        // Check blocking
        if (mUser.isBlocking()) {
            block.setTitle(STR_UNBLOCK);
        } else {
            block.setTitle(STR_BLOCK);
        }
    }

    private void showScreen() {
        new Handler().postDelayed(() ->
            findViewById(R.id.view_cover).setVisibility(View.GONE),
            200
        );
    }

}
