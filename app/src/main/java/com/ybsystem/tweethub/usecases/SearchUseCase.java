package com.ybsystem.tweethub.usecases;

import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ExceptionUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.SavedSearch;
import twitter4j.TwitterException;

public class SearchUseCase {

    private SearchUseCase() {
    }

    public static void saveSearch(String query) {
        // Create
        Observable<Object> observable = Observable.create(e -> {
            try {
                TweetHubApp.getTwitter().createSavedSearch(query);
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<Object> disposable = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object obj) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                TwitterException e = (TwitterException) t;
                ToastUtils.showShortToast("検索の保存に失敗しました...");
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
            }

            @Override
            public void onComplete() {
                // Success
                ToastUtils.showShortToast("「" + query + "」を保存しました。");
            }
        };

        // Show confirm dialog
        DialogUtils.showConfirmDialog(
                "検索を保存しますか？",
                (dialog, which) -> observable
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(disposable)
        );
    }

    public static void destroySavedSearch(SavedSearch ss) {
        // Create
        Observable<Object> observable = Observable.create(e -> {
            try {
                TweetHubApp.getTwitter().destroySavedSearch(ss.getId());
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<Object> disposable = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object obj) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                TwitterException e = (TwitterException) t;
                ToastUtils.showShortToast("検索の削除に失敗しました...");
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
            }

            @Override
            public void onComplete() {
                // Success
                ToastUtils.showShortToast("「" + ss.getName() + "」を削除しました。");
            }
        };

        // Show confirm dialog
        DialogUtils.showConfirmDialog(
                "検索を削除しますか？",
                (dialog, which) -> observable
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(disposable)
        );
    }

}
