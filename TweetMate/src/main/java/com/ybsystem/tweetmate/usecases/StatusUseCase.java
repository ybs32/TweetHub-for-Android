package com.ybsystem.tweetmate.usecases;

import android.app.Activity;
import android.net.Uri;

import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.libs.eventbus.PostEvent;
import com.ybsystem.tweetmate.libs.eventbus.StatusEvent;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweetmate.databases.PrefAppearance;
import com.ybsystem.tweetmate.databases.PrefSystem;
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
import static com.ybsystem.tweetmate.resources.ResString.*;
import static com.ybsystem.tweetmate.resources.ResString.STR_SUCCESS_UNLIKE;

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
        String confirm = isRetweeted ? STR_CONFIRM_UNRETWEET : STR_CONFIRM_RETWEET;
        String success = isRetweeted ? STR_SUCCESS_UNRETWEET : STR_SUCCESS_RETWEET;

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
        boolean isLikeStyle = PrefAppearance.getLikeFavText().equals(STR_LIKE);
        String confirm = isFavorited
                ? (isLikeStyle ? STR_CONFIRM_UNLIKE : STR_CONFIRM_UNFAVORITE)
                : (isLikeStyle ? STR_CONFIRM_LIKE : STR_CONFIRM_FAVORITE);
        String success = isFavorited
                ? (isLikeStyle ? STR_SUCCESS_UNLIKE : STR_SUCCESS_UNFAVORITE)
                : (isLikeStyle ? STR_SUCCESS_LIKE : STR_SUCCESS_FAVORITE);

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
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
                // Success
                EventBus.getDefault().post(new StatusEvent(status));
                ToastUtils.showShortToast(STR_SUCCESS_DESTROY_TWEET);
            }
        };

        // Confirm
        if (PrefSystem.getConfirmSettings().contains(DELETE)) {
            DialogUtils.showConfirm(
                    STR_CONFIRM_DESTROY_TWEET,
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
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
                // Success
                EventBus.getDefault().post(new PostEvent());
                ToastUtils.showShortToast(STR_SUCCESS_TWEET);
            }
        };

        // Confirm
        if (PrefSystem.getConfirmSettings().contains(TWEET)) {
            DialogUtils.showConfirm(
                    STR_CONFIRM_TWEET,
                    (dialog, which) -> {
                        DialogUtils.showProgress(STR_SENDING, TweetMateApp.getActivity());
                        observable.subscribe(disp);
                    }
            );
        } else {
            DialogUtils.showProgress(STR_SENDING, TweetMateApp.getActivity());
            observable.subscribe(disp);
        }
    }

}
