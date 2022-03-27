package com.ybsystem.tweetmate.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.recycler.TweetRecyclerAdapter;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class DetailTimeline extends TimelineBase {

    // Status
    private TwitterStatus mStatus;

    // For search
    private Query mQuery;
    private int mLoadCount;

    public DetailTimeline newInstance(TwitterStatus status) {
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
        mStatus.setDetail(true);
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

        // Create request
        Observable<QueryResult> observable = Observable.create(e -> {
            try {
                if (mLoadCount == 0) {
                    mQuery = new Query("to:@" + mStatus.getUser().getScreenName() + " since_id:" + mStatus.getId());
                    mQuery.setCount(200);
                }
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
                    for (twitter4j.Status status : result.getTweets()) {
                        if (status.getInReplyToStatusId() == mStatus.getId()) {
                            mRecyclerAdapter.add(new TwitterStatus(status));
                        }
                    }
                    mRecyclerAdapter.notifyDataSetChanged();

                    // Check next query
                    if (result.hasNext()) {
                        mLoadCount++;
                        mQuery = result.nextQuery();

                        // 読み込み過ぎを防止
                        if (mLoadCount > 10) {
                            mFooterView.setVisibility(View.GONE);
                        }
                        // 10件より少なければ再び読み込む
                        else if (mRecyclerAdapter.getObjCount() < 10) {
                            loadTweet(false, false);
                        }
                        // 10件を超えた場合はScrollで読み込み可能
                        else {
                            mScrollLoad = true;
                        }
                        return;
                    }
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
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable);
    }

}
