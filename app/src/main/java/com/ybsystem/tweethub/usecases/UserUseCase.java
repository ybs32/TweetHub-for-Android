package com.ybsystem.tweethub.usecases;

import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.libs.eventbus.UserEvent;
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

import static com.ybsystem.tweethub.models.enums.ConfirmAction.*;

public class UserUseCase {

    private UserUseCase() {
    }

    public static void follow(TwitterUser user) {
        // Create text
        boolean isFriend = user.isFriend();
        String confirm = isFriend ? "フォローを解除しますか？" : "フォローしますか？";
        String success = isFriend ? "フォローを解除しました。" : "フォローしました。";
        String fail = isFriend ? "フォロー解除に失敗しました..." : "フォローに失敗しました...";

        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isFriend) {
                    TweetHubApp.getTwitter().destroyFriendship(user.getId());
                } else {
                    TweetHubApp.getTwitter().createFriendship(user.getId());
                }
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
                ToastUtils.showShortToast(fail);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
            }

            @Override
            public void onComplete() {
                // Success
                if (isFriend) {
                    user.setFriend(false);
                    TweetHubApp.getMyUser().getFriendIds().remove(user.getId());
                } else {
                    user.setFriend(true);
                    TweetHubApp.getMyUser().getFriendIds().add(user.getId());
                }
                // Notify event
                EventBus.getDefault().post(new UserEvent());
                ToastUtils.showShortToast(success);
            }
        };

        // Show confirm dialog
        if (PrefSystem.getConfirmSettings().contains(FOLLOW)) {
            DialogUtils.showConfirmDialog(
                    confirm,
                    (dialog, which) -> observable
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(disposable)
            );
        } else {
            observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(disposable);
        }
    }

    public static void mute(TwitterUser user) {
        // Create text
        boolean isMuting = user.isMuting();
        String confirm = isMuting ? "ミュートを解除しますか？" : "ミュートしますか？";
        String success = isMuting ? "ミュートを解除しました。" : "ミュートしました。";
        String fail = isMuting ? "ミュート解除に失敗しました..." : "ミュートに失敗しました...";

        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isMuting) {
                    TweetHubApp.getTwitter().destroyMute(user.getId());
                } else {
                    TweetHubApp.getTwitter().createMute(user.getId());
                }
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
                ToastUtils.showShortToast(fail);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
            }

            @Override
            public void onComplete() {
                // Success
                if (isMuting) {
                    user.setMuting(false);
                    TweetHubApp.getMyUser().getMuteIds().remove(user.getId());
                } else {
                    user.setMuting(true);
                    TweetHubApp.getMyUser().getMuteIds().add(user.getId());
                }
                // Notify event
                EventBus.getDefault().post(new UserEvent());
                ToastUtils.showShortToast(success);
            }
        };

        // Show confirm dialog
        if (PrefSystem.getConfirmSettings().contains(MUTE)) {
            DialogUtils.showConfirmDialog(
                    confirm,
                    (dialog, which) -> observable
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(disposable)
            );
        } else {
            observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(disposable);
        }
    }

    public static void block(TwitterUser user) {
        // Create text
        boolean isBlocking = user.isBlocking();
        String confirm = isBlocking ? "ブロックを解除しますか？" : "ブロックしますか？";
        String success = isBlocking ? "ブロックを解除しました。" : "ブロックしました。";
        String fail = isBlocking ? "ブロック解除に失敗しました..." : "ブロックに失敗しました...";

        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isBlocking) {
                    TweetHubApp.getTwitter().destroyBlock(user.getId());
                } else {
                    TweetHubApp.getTwitter().createBlock(user.getId());
                }
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
                ToastUtils.showShortToast(fail);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
            }

            @Override
            public void onComplete() {
                // Success
                if (isBlocking) {
                    user.setBlocking(false);
                    TweetHubApp.getMyUser().getBlockIds().remove(user.getId());
                } else {
                    user.setBlocking(true);
                    user.setFriend(false);
                    TweetHubApp.getMyUser().getBlockIds().add(user.getId());
                    TweetHubApp.getMyUser().getFriendIds().remove(user.getId());
                }
                // Notify event
                EventBus.getDefault().post(new UserEvent());
                ToastUtils.showShortToast(success);
            }
        };

        // Show confirm dialog
        if (PrefSystem.getConfirmSettings().contains(BLOCK)) {
            DialogUtils.showConfirmDialog(
                    confirm,
                    (dialog, which) -> observable
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(disposable)
            );
        } else {
            observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(disposable);
        }
    }

    public static void subscribeList(TwitterUserList usetList) {
        // Create text
        boolean isSubscribing = usetList.isFollowing();
        String confirm = isSubscribing ? "購読を解除しますか？" : "リストを購読しますか？";
        String success = isSubscribing ? "購読を解除しました。" : "リストを購読しました。";
        String fail = isSubscribing ? "購読解除に失敗しました..." : "購読に失敗しました...";

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
        });

        DisposableObserver<Object> disposable = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object obj) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                TwitterException e = (TwitterException) t;
                ToastUtils.showShortToast(fail);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
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

        // Show confirm dialog
        if (PrefSystem.getConfirmSettings().contains(SUBSCRIBE)) {
            DialogUtils.showConfirmDialog(
                    confirm,
                    (dialog, which) -> observable
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(disposable)
            );
        } else {
            observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(disposable);
        }
    }


    public static void updateListUser(UserList list, TwitterUser user, boolean isRegistering) {
        // Create text
        String name = "「" + list.getName() + "」";
        String success = name + (isRegistering ? "から削除しました。" : "に追加しました。");
        String fail = name + (isRegistering ? "から削除に失敗しました..." : "への追加に失敗しました...");

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

        DisposableObserver<Object> disposable = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object obj) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                TwitterException e = (TwitterException) t;
                ToastUtils.showShortToast(fail);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
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
