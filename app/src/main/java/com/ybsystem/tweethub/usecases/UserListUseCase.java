package com.ybsystem.tweethub.usecases;

import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.libs.eventbus.UserListEvent;
import com.ybsystem.tweethub.models.entities.UserList;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUserList;
import com.ybsystem.tweethub.storages.PrefSystem;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ExceptionUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.TwitterException;

import static com.ybsystem.tweethub.models.enums.ConfirmAction.SUBSCRIBE;

public class UserListUseCase {

    private UserListUseCase() {
    }

    public static void subscribeList(TwitterUserList usetList) {
        // Create text
        boolean isSubscribing = usetList.isFollowing();
        String confirm = isSubscribing ? "購読を解除しますか？" : "リストを購読しますか？";
        String success = isSubscribing ? "購読を解除しました。" : "リストを購読しました。";
        String fail = isSubscribing ? "購読解除に失敗しました..." : "購読に失敗しました...";

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isSubscribing) {
                    TweetHubApp.getTwitter().destroyUserListSubscription(usetList.getId());
                } else {
                    TweetHubApp.getTwitter().createUserListSubscription(usetList.getId());
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
                if (isSubscribing) {
                    usetList.setFollowing(false);
                } else {
                    usetList.setFollowing(true);
                }
                // Notify event
                EventBus.getDefault().post(new UserListEvent());
                ToastUtils.showShortToast(success);
            }
        };

        // Confirm
        if (PrefSystem.getConfirmSettings().contains(SUBSCRIBE)) {
            DialogUtils.showConfirm(
                    confirm,
                    (dialog, which) -> observable.subscribe(disp)
            );
        } else {
            observable.subscribe(disp);
        }
    }

    public static void updateListUser(UserList list, TwitterUser user, boolean isRegistering) {
        // Create text
        String name = "「" + list.getName() + "」";
        String success = name + (isRegistering ? "から削除しました。" : "に追加しました。");
        String fail = name + (isRegistering ? "から削除に失敗しました..." : "への追加に失敗しました...");

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isRegistering) {
                    TweetHubApp.getTwitter().destroyUserListMember(list.getId(), user.getId());
                } else {
                    TweetHubApp.getTwitter().createUserListMember(list.getId(), user.getId());
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        // Result
        DisposableObserver<Object> disposable = new DisposableObserver<Object>() {
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
                ToastUtils.showShortToast(success);
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable);
    }

}
