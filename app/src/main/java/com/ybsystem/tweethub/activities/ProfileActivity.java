package com.ybsystem.tweethub.activities;

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
import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.adapters.pager.ProfilePagerAdapter;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.ProfileFragment;
import com.ybsystem.tweethub.fragments.dialog.UserListDialog;
import com.ybsystem.tweethub.fragments.timeline.TimelineBase;
import com.ybsystem.tweethub.libs.eventbus.UserEvent;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.usecases.ClickUseCase;
import com.ybsystem.tweethub.usecases.UserUseCase;
import com.ybsystem.tweethub.utils.ExceptionUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class ProfileActivity extends ActivityBase {

    private TwitterUser mUser;
    private Relationship mRelation;

    private Menu mMenu;
    private ViewPager mProfileTlPager;
    private ProfilePagerAdapter mProfilePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init
        long userId = getIntent().getLongExtra("USER_ID", 0);

        // Set
        setContentView(R.layout.activity_profile);

        // Fetch user
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
            // ミュート
            case R.id.item_mute:
                UserUseCase.mute(mUser);
                return true;
            // ブロック
            case R.id.item_block:
                UserUseCase.block(mUser);
                return true;
            // 返信する
            case R.id.item_reply:
                ClickUseCase.tweetWithPrefix("@" + mUser.getScreenName());
                return true;
            // リストに追加
            case R.id.item_add_list:
                UserListDialog dialog = new UserListDialog().newInstance(mUser);
                FragmentManager fm = TweetHubApp.getActivity().getSupportFragmentManager();
                if (fm.findFragmentByTag("UserListDialog") == null) {
                    dialog.show(fm, "UserListDialog");
                }
                return true;
            // ブラウザで開く
            case R.id.item_browser:
                ClickUseCase.openUser(mUser);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchTwitterUser(long userId, Bundle savedInstanceState) {
        Observable<Object> observable = Observable.create(e -> {
            // Fetch user
            Twitter twitter = TweetHubApp.getTwitter();
            User user4j = twitter.showUser(userId);

            if (user4j != null) {
                mUser = new TwitterUser(user4j);
                mRelation = twitter.showFriendship(
                        TweetHubApp.getMyUser().getId(), mUser.getId()
                );
            }
            e.onComplete();
        });

        DisposableObserver<Object> disposable = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                TwitterException e = (TwitterException) t;
                ToastUtils.showShortToast("ユーザーの取得に失敗しました...");
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
                finish();
                overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_to_right);
            }

            @Override
            public void onComplete() {
                // Success
                mUser.setFriend(mRelation.isSourceFollowingTarget());
                mUser.setFollower(mRelation.isSourceFollowedByTarget());
                mUser.setMuting(mRelation.isSourceMutingTarget());
                mUser.setBlocking(mRelation.isSourceBlockingTarget());

                // Set contents
                updateMenu();
                setProfileFragment(savedInstanceState);
                setProfileTlPager();
                new Handler().postDelayed(() ->
                        findViewById(R.id.view_cover).setVisibility(View.GONE), 200
                );
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable);
    }

    private void updateMenu() {
        // Init
        MenuItem mute = mMenu.findItem(R.id.item_mute);
        MenuItem block = mMenu.findItem(R.id.item_block);
        MenuItem reply = mMenu.findItem(R.id.item_reply);
        MenuItem addList = mMenu.findItem(R.id.item_add_list);

        // Check myself
        if (mUser.isMyself()) {
            mute.setVisible(false);
            block.setVisible(false);
            reply.setVisible(false);
            addList.setVisible(false);
            return;
        }
        // Check muting
        if (mUser.isMuting()) {
            mute.setTitle("ミュート解除");
        } else {
            mute.setTitle("ミュート");
        }
        // Check blocking
        if (mUser.isBlocking()) {
            block.setTitle("ブロック解除");
        } else {
            block.setTitle("ブロック");
        }
    }

    private void setProfileFragment(Bundle savedInstanceState) {
        // Set actionbar text
        if (mUser.getId() == TweetHubApp.getMyUser().getId()) {
            getSupportActionBar().setTitle("マイプロフィール");
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
        for (int i = 0; i < 3; i++) {
            // When tab clicked
            final int TAB_NUM = i;
            vg.getChildAt(i).setOnClickListener(v -> {
                // If current tab clicked, move top of the timeline
                int currentPage = mProfileTlPager.getCurrentItem();
                if (currentPage == TAB_NUM) {
                    TimelineBase timeline = (TimelineBase) mProfilePagerAdapter.instantiateItem(mProfileTlPager, currentPage);
                    timeline.getRecyclerView().scrollToPosition(0);
                }
            });
        }
    }

}
