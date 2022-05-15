package com.ybsystem.tweetmate.fragments.timeline;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.recycler.UserRecyclerAdapter;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.libs.eventbus.UserRtFavFetchEvent;
import com.ybsystem.tweetmate.libs.eventbus.UserRtFavAuthEvent;
import com.ybsystem.tweetmate.libs.webview.UserRtFavFragment;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.models.enums.ColumnType;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class UserRtFavTimeline extends TimelineBase {

    private Handler mHandler;
    private View mUserRtFavView;
    private ArrayList<Long> mUserIds;

    public UserRtFavTimeline newInstance(TwitterStatus status, ColumnType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("STATUS", status);
        bundle.putSerializable("COLUMN_TYPE", type);
        setArguments(bundle);
        return this;
    }

    @Subscribe
    public void onEvent(UserRtFavAuthEvent event) {
        mHandler.post(() -> {
            mPtrFrame.setVisibility(View.GONE);
            mUserRtFavView.setVisibility(View.VISIBLE);
        });
    }

    @Subscribe
    public void onEvent(UserRtFavFetchEvent event) {
        mUserIds = event.getUserIds();
        loadTweet(false, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_timeline_rt_fav, container, false);

        // Set view
        setRecyclerView(view);
        setRecyclerAdapter(new UserRecyclerAdapter());
        setUltraPullToRefresh(view);
        mPtrFrame.setEnabled(false);

        // Init
        mHandler = new Handler();
        mUserRtFavView = view.findViewById(R.id.frame_container);

        // Set fragment
        Fragment fragment = new UserRtFavFragment().newInstance(
                (TwitterStatus) getArguments().getSerializable("STATUS"),
                (ColumnType) getArguments().getSerializable("COLUMN_TYPE")
        );
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();

        return view;
    }

    @Override
    protected void loadTweet(boolean isPullLoad, boolean isClickLoad) {
        // Check if user ids fetched
        if (mUserIds == null) {
            mFooterText.setText(STR_LOADING);
            mFooterProgress.setVisibility(View.VISIBLE);
            mFooterView.setVisibility(View.VISIBLE);
            return;
        }

        // Create request
        Observable<ResponseList<User>> observable = Observable.create(e -> {
            try {
                long[] userIds = mUserIds.stream().mapToLong(i -> i).toArray();
                if (userIds != null && userIds.length > 0) {
                    e.onNext(
                        TweetMateApp.getTwitter().lookupUsers(userIds)
                    );
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<ResponseList<User>> disposable = new DisposableObserver<ResponseList<User>>() {
            @Override
            public void onNext(ResponseList<User> userList) {
                // Success
                if (!userList.isEmpty()) {
                    for (User user4j : userList) {
                        mRecyclerAdapter.add(new TwitterUser(user4j));
                    }
                    mRecyclerAdapter.notifyDataSetChanged();
                }
                mFooterView.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                mFooterView.setVisibility(View.GONE);
            }

            @Override
            public void onComplete() {
                // No user ids...
                mFooterView.setVisibility(View.GONE);
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable);
    }

}
