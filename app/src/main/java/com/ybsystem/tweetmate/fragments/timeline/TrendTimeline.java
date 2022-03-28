package com.ybsystem.tweetmate.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.recycler.TrendRecyclerAdapter;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class TrendTimeline extends TimelineBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        // Init
        mScrollLoad = true;

        // Set view
        setRecyclerView(view);
        setRecyclerAdapter(new TrendRecyclerAdapter());
        setUltraPullToRefresh(view);

        return view;
    }

    @Override
    protected void loadTweet(boolean isPullLoad, boolean isClickLoad) {
        // Change footer
        mFooterText.setText(STR_LOADING);
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        TrendRecyclerAdapter adapter = (TrendRecyclerAdapter) mRecyclerAdapter;
        Observable<Trends> observable = Observable.create(e -> {
            try {
                int woeIdJp = 23424856;
                e.onNext(TweetMateApp.getTwitter().getPlaceTrends(woeIdJp));
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<Trends> disposable = new DisposableObserver<Trends>() {
            @Override
            public void onNext(Trends trends) {
                // Success
                adapter.clear();
                for (Trend trend : trends.getTrends()) {
                    adapter.add(trend);
                }
                adapter.notifyDataSetChanged();
                mFooterView.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                mFooterText.setText(STR_TAP_TO_LOAD);
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
