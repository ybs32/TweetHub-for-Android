package com.ybsystem.tweetmate.fragments.timeline;

import android.os.Bundle;
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

public class TalkTimeline extends TimelineBase {
    // Status
    private TwitterStatus mStatus;

    // For reply to
    private boolean mToLoaded;
    private ArrayList<TwitterStatus> mToStatusList;

    // For reply from
    private Query mQuery;
    private boolean mFromLoaded;
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
        mFooterText.setText("読み込み中...");
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        // Load reply status
        loadReplyToStatus(mStatus);
        loadReplyFromStatus(mStatus);
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
                // Load next
                if (status.getInReplyToStatusId() != -1) {
                    loadReplyToStatus(status);
                    return;
                }
                mToLoaded = true;
                loadCompleted();
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                mToLoaded = true;
                loadCompleted();
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
                // Success
                if (result.getTweets().size() != 0) {
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
                mFromLoaded = true;
                loadCompleted();
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                mFromLoaded = true;
                loadCompleted();
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

    private void loadCompleted() {
        if (mToLoaded && mFromLoaded) {
            // Insert toStatus
            for (TwitterStatus toStatus : mToStatusList) {
                mRecyclerAdapter.insert(0, toStatus);
            }
            // Add fromStatus
            for (TwitterStatus fromStatus : mFromStatusList) {
                mRecyclerAdapter.add(fromStatus);
            }
            mRecyclerAdapter.notifyDataSetChanged();

            // Hide footer
            mFooterView.setVisibility(View.GONE);
        }
    }

}
