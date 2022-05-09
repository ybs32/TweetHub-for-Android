package com.ybsystem.tweetmate.fragments.timeline;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.recycler.TweetRecyclerAdapter;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

import static com.ybsystem.tweetmate.resources.ResString.STR_LOADING;

public class PrevNextTimeline extends TimelineBase {
    // Status
    private TwitterStatus mStatus;

    // For prev
    private ArrayList<TwitterStatus> mPrevStatusList;

    // For next
    private ArrayList<TwitterStatus> mNextStatusList;

    public PrevNextTimeline newInstance(TwitterStatus status) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("STATUS", status);
        setArguments(bundle);
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        // Init
        mStatus = (TwitterStatus) getArguments().getSerializable("STATUS");
        mPrevStatusList = new ArrayList<>();
        mNextStatusList = new ArrayList<>();
        mScrollLoad = true;

        // Set view
        setRecyclerView(view);
        setRecyclerAdapter(new TweetRecyclerAdapter());
        setUltraPullToRefresh(view);
        mPtrFrame.setEnabled(false);

        // Add source tweet
        mRecyclerAdapter.add(mStatus);

        return view;
    }

    @Override
    protected void loadTweet(boolean isPullLoad, boolean isClickLoad) {
        // Change footer
        mFooterText.setText(STR_LOADING);
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        // Load prev
        loadPrevStatus();
    }

    private void loadPrevStatus() {
        // Create request
        Observable<List<Status>> observable = Observable.create(e -> {
            try {
                Paging paging = new Paging();
                paging.setCount(5);
                paging.setMaxId(mStatus.getId() - 1);

                List<Status> statusList = TweetMateApp.getTwitter()
                        .getUserTimeline(mStatus.getUser().getScreenName(), paging);
                e.onNext(statusList);
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<List<Status>> disposable = new DisposableObserver<List<Status>>() {
            @Override
            public void onNext(List<Status> statusList) {
                // Success
                for (Status status4j : statusList) {
                    mPrevStatusList.add(new TwitterStatus(status4j));
                }
                renderPrev();
                loadNextStatus(1);
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                renderPrev();
                loadNextStatus(1);
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

    private void loadNextStatus(int page) {
        // Create request
        Observable<List<Status>> observable = Observable.create(e -> {
            try {
                Paging paging = new Paging();
                paging.setPage(page);
                paging.setCount(200);

                List<Status> statusList = TweetMateApp.getTwitter()
                        .getUserTimeline(mStatus.getUser().getScreenName(), paging);
                e.onNext(statusList);
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<List<Status>> disposable = new DisposableObserver<List<Status>>() {
            @Override
            public void onNext(List<Status> statusList) {
                if (statusList.isEmpty()) {
                    // No tweet...
                } else {
                    // Success
                    for (int i = 0; i < statusList.size(); i++) {
                        if (statusList.get(i).getId() == mStatus.getId()) {
                            for (Status status4j : statusList.subList(Math.max(0, i - 5), i)) {
                                mNextStatusList.add(new TwitterStatus(status4j));
                            }
                            renderNext();
                            return;
                        }
                    }
                    if (page >= 8) {
                        renderNext();
                    } else {
                        loadNextStatus(page + 1);
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                renderNext();
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

    private void renderPrev() {
        // Render
        for (TwitterStatus prevStatus : mPrevStatusList) {
            mRecyclerAdapter.add(prevStatus);
            mRecyclerAdapter.notifyItemInserted(mRecyclerAdapter.getObjCount());
        }
        mPrevStatusList.clear();
    }

    private void renderNext() {
        // Render
        Collections.reverse(mNextStatusList);
        for (TwitterStatus nextStatus : mNextStatusList) {
            mRecyclerAdapter.insert(0, nextStatus);
            mRecyclerAdapter.notifyItemInserted(0);
        }
        mNextStatusList.clear();

        // Hide footer
        new Handler().postDelayed(
            () -> mFooterView.setVisibility(View.GONE)
        , 200);
    }

}
