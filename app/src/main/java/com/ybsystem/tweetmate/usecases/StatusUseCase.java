package com.ybsystem.tweetmate.usecases;

import android.app.Activity;
import android.net.Uri;

import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.libs.eventbus.PostEvent;
import com.ybsystem.tweetmate.libs.eventbus.StatusEvent;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweetmate.storages.PrefAppearance;
import com.ybsystem.tweetmate.storages.PrefSystem;
import com.ybsystem.tweetmate.utils.DialogUtils;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.StorageUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import static com.ybsystem.tweetmate.models.enums.ConfirmAction.*;

public class StatusUseCase {

    private StatusUseCase() {
    }

    public static void retweet(TwitterStatus status) {
        // Check
        if (!status.isRetweeted() && !status.isPublic()) {
            return;
        }

        // Create text
        boolean isRetweeted = status.isRetweeted();
        String confirm = isRetweeted ? "リツイートを解除しますか？" : "リツイートしますか？";
        String success = isRetweeted ? "リツイートを解除しました。" : "リツイートしました。";
        String fail = isRetweeted ? "リツイート解除に失敗しました..." : "リツイートに失敗しました...";

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isRetweeted) {
                    TweetMateApp.getTwitter().unRetweetStatus(status.getId());
                } else {
                    TweetMateApp.getTwitter().retweetStatus(status.getId());
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread());

        // Result
        DisposableObserver<Object> disp = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object obj) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                ToastUtils.showShortToast(fail);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
                // Success
                if (isRetweeted) {
                    status.setRetweeted(false);
                    status.setRetweetCount(status.getRetweetCount() - 1);
                    boolean isDeleteTarget = status.isRetweet() && status.isMyTweet();
                    EventBus.getDefault().post(isDeleteTarget ? new StatusEvent(status) : new StatusEvent());
                } else {
                    status.setRetweeted(true);
                    status.setRetweetCount(status.getRetweetCount() + 1);
                    EventBus.getDefault().post(new StatusEvent());
                }
                ToastUtils.showShortToast(success);
            }
        };

        // Confirm
        if (PrefSystem.getConfirmSettings().contains(RETWEET)) {
            DialogUtils.showConfirm(
                    confirm,
                    (dialog, which) -> observable.subscribe(disp)
            );
        } else {
            observable.subscribe(disp);
        }
    }

    public static void favorite(TwitterStatus status) {
        // Create text
        boolean isFavorited = status.isFavorited();
        String fav = PrefAppearance.getLikeFavText();
        String confirm = isFavorited ? fav + "を解除しますか？" : fav + "しますか？";
        String success = isFavorited ? fav + "を解除しました。" : fav + "しました。";
        String fail = isFavorited ? fav + "解除に失敗しました..." : fav + "に失敗しました...";

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isFavorited) {
                    TweetMateApp.getTwitter().destroyFavorite(status.getId());
                } else {
                    TweetMateApp.getTwitter().createFavorite(status.getId());
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread());

        // Result
        DisposableObserver<Object> disp = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object obj) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                ToastUtils.showShortToast(fail);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
                // Success
                if (isFavorited) {
                    status.setFavorited(false);
                    status.setFavoriteCount(status.getFavoriteCount() - 1);
                } else {
                    status.setFavorited(true);
                    status.setFavoriteCount(status.getFavoriteCount() + 1);
                }
                // Notify event
                EventBus.getDefault().post(new StatusEvent());
                ToastUtils.showShortToast(success);
            }
        };

        // Confirm
        if (PrefSystem.getConfirmSettings().contains(LIKE)) {
            DialogUtils.showConfirm(
                    confirm,
                    (dialog, which) -> observable.subscribe(disp)
            );
        } else {
            observable.subscribe(disp);
        }
    }

    public static void delete(TwitterStatus status) {
        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                TweetMateApp.getTwitter().destroyStatus(status.getId());
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread());

        // Result
        DisposableObserver<Object> disp = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object obj) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                ToastUtils.showShortToast("ツイートの削除に失敗しました...");
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
                // Success
                EventBus.getDefault().post(new StatusEvent(status));
                ToastUtils.showShortToast("ツイートを削除しました。");
            }
        };

        // Confirm
        if (PrefSystem.getConfirmSettings().contains(DELETE)) {
            DialogUtils.showConfirm(
                    "ツイートを削除しますか？",
                    (dialog, which) -> observable.subscribe(disp)
            );
        } else {
            observable.subscribe(disp);
        }
    }

    public static void post(StatusUpdate update, List<Uri> imageUris) {
        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                Twitter twitter = TweetMateApp.getTwitter();
                Activity act = TweetMateApp.getActivity();

                // Check media
                if (!imageUris.isEmpty()) {
                    long[] mediaIds = new long[imageUris.size()];
                    for (int i = 0; i < imageUris.size(); i++) {
                        Uri uri = imageUris.get(i);
                        File file = StorageUtils.fileFromUri(act, uri);
                        file = new Compressor(act).setQuality(100).compressToFile(file);
                        mediaIds[i] = twitter.uploadMedia(file).getMediaId();
                    }
                    update.setMediaIds(mediaIds);
                }

                // Post tweet
                twitter.updateStatus(update);
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .doFinally(DialogUtils::dismissProgress);

        // Result
        DisposableObserver<Object> disp = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object obj) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                ToastUtils.showShortToast("ツイートに失敗しました...");
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
                // Success
                EventBus.getDefault().post(new PostEvent());
                ToastUtils.showShortToast("ツイートしました。");
            }
        };

        // Confirm
        if (PrefSystem.getConfirmSettings().contains(TWEET)) {
            DialogUtils.showConfirm(
                    "ツイートしますか？",
                    (dialog, which) -> {
                        DialogUtils.showProgress("送信中...", TweetMateApp.getActivity());
                        observable.subscribe(disp);
                    }
            );
        } else {
            DialogUtils.showProgress("送信中...", TweetMateApp.getActivity());
            observable.subscribe(disp);
        }
    }

}
