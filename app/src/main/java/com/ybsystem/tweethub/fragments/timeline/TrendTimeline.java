package com.ybsystem.tweethub.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.adapters.recycler.TrendRecyclerAdapter;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.utils.ExceptionUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;

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
        mFooterText.setText("読み込み中...");
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        TrendRecyclerAdapter adapter = (TrendRecyclerAdapter) mRecyclerAdapter;
        Observable<Trends> observable = Observable.create(e -> {
            try {
                int woeIdJp = 23424856;
                e.onNext(TweetHubApp.getTwitter().getPlaceTrends(woeIdJp));
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
                mFooterText.setText("タップして読み込む");
                mFooterProgress.setVisibility(View.GONE);

                // Show error message if loaded by user action
                if (isPullLoad || isClickLoad) {
                    TwitterException e = (TwitterException) t;
                    ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
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
