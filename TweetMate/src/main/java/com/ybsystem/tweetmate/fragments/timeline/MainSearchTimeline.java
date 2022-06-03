package com.ybsystem.tweetmate.fragments.timeline;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.widget.PopupMenu;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.recycler.TrendRecyclerAdapter;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.databases.PrefSystem;
import com.ybsystem.tweetmate.usecases.ClickUseCase;
import com.ybsystem.tweetmate.usecases.SearchUseCase;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.KeyboardUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class MainSearchTimeline extends TimelineBase {

    private EditText mSearchEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_timeline_search, container, false);

        // Init
        mScrollLoad = true;

        // Set trend
        setRecyclerView(view);
        setRecyclerAdapter(new TrendRecyclerAdapter());
        setUltraPullToRefresh(view);

        // Set others
        setSearchEdit(view);
        setMoreButton(view);

        return view;
    }

    private void setSearchEdit(View view) {
        // Find
        mSearchEdit = view.findViewById(R.id.edit_search);
        mSearchEdit.setOnKeyListener((v, keyCode, event) -> {
            // When pressed enter
            if (mSearchEdit.length() != 0 && keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                // Close keyboard
                KeyboardUtils.closeKeyboard(v);

                // Intent to SearchActivity
                ClickUseCase.searchWord(mSearchEdit.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void setMoreButton(View view) {
        // Find
        ImageButton moreButton = view.findViewById(R.id.button_more);
        PopupMenu popup = new PopupMenu(view.getContext(), moreButton);
        popup.getMenuInflater().inflate(R.menu.search, popup.getMenu());

        // Hide menu
        Menu menu = popup.getMenu();
        menu.findItem(R.id.item_save_search).setVisible(false);
        menu.findItem(R.id.item_add_column).setVisible(false);

        // When more button clicked
        moreButton.setOnClickListener(v -> popup.show());

        // When popup item clicked
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                // Saved searches
                case R.id.item_saved_search:
                    SearchUseCase.showSavedSearch();
                    break;
            }
            return true;
        });
    }

    protected void loadTweet(final boolean isPullLoad, final boolean isClickLoad) {
        // Change footer
        mFooterText.setText(STR_LOADING);
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        TrendRecyclerAdapter adapter = (TrendRecyclerAdapter) mRecyclerAdapter;
        Observable<Trends> observable = Observable.create(e -> {
            try {
                int woeIdJp = PrefSystem.getTrendWoeId(getContext());
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
