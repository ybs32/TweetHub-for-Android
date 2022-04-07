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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class TalkTimeline extends TimelineBase {
    // Status
    private TwitterStatus mStatus;

    // For reply to
    private int mLoadCount;
    private ArrayList<TwitterStatus> mToStatusList;

    // For reply from
    private Query mQuery;
    private ArrayList<TwitterStatus> mFromStatusList;

    public TalkTimeline newInstance(TwitterStatus status) {
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
        mToStatusList = new ArrayList<>();
        mFromStatusList = new ArrayList<>();
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

        // Load reply to
        loadReplyToStatus(mStatus);
    }

    private void loadReplyToStatus(TwitterStatus target) {
        // Create request
        Observable<Status> observable = Observable.create(e -> {
            try {
                Status status = TweetMateApp.getTwitter()
                        .showStatus(target.getInReplyToStatusId());
                // Check
                if (status != null) {
                    e.onNext(status);
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<Status> disposable = new DisposableObserver<Status>() {
            @Override
            public void onNext(Status status4j) {
                // Success
                TwitterStatus status = new TwitterStatus(status4j);
                mToStatusList.add(status);
                mLoadCount++;

                // Render tweets
                if (status.getInReplyToStatusId() == -1 || mLoadCount % 10 == 0) {
                    renderReplyTo();
                }

                // Load next
                if (status.getInReplyToStatusId() != -1) {
                    loadReplyToStatus(status);
                    return;
                }

                // Load reply from
                loadReplyFromStatus(mStatus);
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                renderReplyTo();
                loadReplyFromStatus(mStatus);
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

    private void loadReplyFromStatus(TwitterStatus target) {
        // Create request
        Observable<QueryResult> observable = Observable.create(e -> {
            try {
                mQuery = new Query("to:@" + target.getUser().getScreenName() + " since_id:" + target.getId());
                mQuery.setCount(200);
                QueryResult result = TweetMateApp.getTwitter().search(mQuery);
                // Check
                if (result != null) {
                    e.onNext(result);
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<QueryResult> disposable = new DisposableObserver<QueryResult>() {
            @Override
            public void onNext(QueryResult result) {
                if (result.getTweets().isEmpty()) {
                    // No tweet...
                } else {
                    // Success
                    for (Status status4j : result.getTweets()) {
                        if (status4j.getInReplyToStatusId() == target.getId()) {
                            // Store status
                            TwitterStatus status = new TwitterStatus(status4j);
                            mFromStatusList.add(status);
                            // Load next
                            loadReplyFromStatus(status);
                            return;
                        }
                    }
                }
                renderReplyFrom();
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                renderReplyFrom();
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

    private void renderReplyTo() {
        // Render
        for (TwitterStatus toStatus : mToStatusList) {
            mRecyclerAdapter.insert(0, toStatus);
            mRecyclerAdapter.notifyItemInserted(0);
        }
        mToStatusList.clear();
    }

    private void renderReplyFrom() {
        // Render
        for (TwitterStatus fromStatus : mFromStatusList) {
            mRecyclerAdapter.add(fromStatus);
            mRecyclerAdapter.notifyItemInserted(mRecyclerAdapter.getObjCount());
        }
        mFromStatusList.clear();

        // Hide footer
        new Handler().postDelayed(
            () -> mFooterView.setVisibility(View.GONE)
        , 200);
    }

}
