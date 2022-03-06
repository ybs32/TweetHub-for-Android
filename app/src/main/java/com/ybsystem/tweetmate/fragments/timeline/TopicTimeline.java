package com.ybsystem.tweetmate.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.recycler.TweetRecyclerAdapter;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;

public class TopicTimeline extends TimelineBase {

    private Query mQuery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        // Init
        mScrollLoad = true;

        // Set view
        setRecyclerView(view);
        setRecyclerAdapter(new TweetRecyclerAdapter());
        setUltraPullToRefresh(view);

        return view;
    }

    @Override
    protected void loadTweet(boolean isPullLoad, boolean isClickLoad) {
        // Change footer
        mFooterText.setText("読み込み中...");
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        TweetRecyclerAdapter adapter = (TweetRecyclerAdapter) mRecyclerAdapter;
        Observable<QueryResult> observable = Observable.create(e -> {
            try {
                if (isPullLoad || adapter.isEmpty()) {
                    mQuery = new Query("lang:ja min_retweets:1000");
                }
                mQuery.setCount(200);
                e.onNext(TweetMateApp.getTwitter().search(mQuery));
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
                    if (adapter.isEmpty()) {
                        mFooterText.setText("ツイートなし");
                        mFooterProgress.setVisibility(View.GONE);
                    } else
                        mFooterView.setVisibility(View.GONE);
                } else {
                    // Success
                    if (isPullLoad) {
                        adapter.clear();
                    }
                    for (twitter4j.Status status : result.getTweets()) {
                        adapter.add(new TwitterStatus(status));
                    }
                    adapter.notifyDataSetChanged();

                    // Check next
                    if (result.hasNext()) {
                        mQuery = result.nextQuery();
                        mScrollLoad = true;
                    } else
                        mFooterView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                mFooterText.setText("タップして読み込む");
                mFooterProgress.setVisibility(View.GONE);

                // Show error message if loaded by user action
                if (isPullLoad || isClickLoad) {
                    ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
                }
                mFooterClick = true;
            }

            @Override
            public void onComplete() {
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (isPullLoad) {
                        mPtrFrame.refreshComplete();
                    }
                })
                .subscribe(disposable);
    }

}
