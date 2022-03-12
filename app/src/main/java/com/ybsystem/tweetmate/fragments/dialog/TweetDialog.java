package com.ybsystem.tweetmate.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterHashtagEntity;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterURLEntity;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUserMentionEntity;
import com.ybsystem.tweetmate.models.enums.TweetMenu;
import com.ybsystem.tweetmate.adapters.holder.TweetRow;
import com.ybsystem.tweetmate.storages.PrefAppearance;
import com.ybsystem.tweetmate.storages.PrefClickAction;
import com.ybsystem.tweetmate.storages.PrefSystem;
import com.ybsystem.tweetmate.usecases.ClickUseCase;
import com.ybsystem.tweetmate.usecases.StatusUseCase;
import com.ybsystem.tweetmate.utils.GlideUtils;

import java.util.Set;

import lombok.Data;

import static com.ybsystem.tweetmate.models.enums.ImageOption.*;
import static com.ybsystem.tweetmate.models.enums.TweetMenu.*;
import static com.ybsystem.tweetmate.resources.ResColor.*;

public class TweetDialog extends DialogFragment {
    // Status
    private TwitterStatus mStatus;
    private TwitterStatus mSource;

    public TweetDialog newInstance(TwitterStatus status) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("STATUS", status);
        setArguments(bundle);
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_tweet, null);

        // Init
        mStatus = (TwitterStatus) getArguments().getSerializable("STATUS");
        mSource = mStatus.isRetweet() ? mStatus.getRtStatus() : mStatus;

        // Set tweet
        TweetRow tweetRow = new TweetRow(view);
        renderTweet(tweetRow);

        // Set menu list
        ListView listView = view.findViewById(R.id.list_view);
        MenuArrayAdapter adapter = new MenuArrayAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setFocusable(false);
        setMenuItems(adapter);

        // Set click listener
        setClickListener(view, listView, adapter);

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dismiss();
    }

    private void renderTweet(TweetRow tweetRow) {
        // Set all fields
        tweetRow.initVisibilities();
        tweetRow.setStatus(mStatus);
        tweetRow.setUserName();
        tweetRow.setScreenName();
        tweetRow.setUserIcon();
        tweetRow.setTweetText();
        tweetRow.setRelativeTime();
        tweetRow.setAbsoluteTime();
        tweetRow.setVia();
        tweetRow.setRtFavCount();
        tweetRow.setRetweetedBy();
        tweetRow.setMarks();
        tweetRow.setThumbnail();
        tweetRow.setQuoteTweet();
        tweetRow.setBackgroundColor();
    }

    @Data
    private static class Menu {
        private int drawable;
        private int color;
        private String label;
        private String userIconURL;
        private Runnable runnable;

        public Menu(int drawable, int color, String label,
                    String userIconURL, Runnable runnable) {
            this.drawable = drawable;
            this.color = color;
            this.label = label;
            this.userIconURL = userIconURL;
            this.runnable = runnable;
        }
    }

    private static class MenuArrayAdapter extends ArrayAdapter<Menu> {

        public MenuArrayAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Inflate
            View cv = convertView;
            if (cv == null) {
                cv = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item_tweet_dialog, parent, false);
            }
            // Get item
            Menu menu = getItem(position);

            // Set image
            ImageView icon = cv.findViewById(R.id.image_icon);
            ImageView userIcon = cv.findViewById(R.id.image_user_icon);
            if (menu.getUserIconURL().equals("")) {
                // Icon
                icon.setImageResource(menu.getDrawable());
                icon.setColorFilter(menu.getColor());
                icon.setVisibility(View.VISIBLE);
                userIcon.setVisibility(View.GONE);
            } else {
                // UserIcon
                GlideUtils.load(menu.getUserIconURL(), userIcon, CIRCLE);
                userIcon.setVisibility(View.VISIBLE);
                icon.setVisibility(View.GONE);
            }

            // Set text
            TextView tv = cv.findViewById(R.id.text_item);
            tv.setText(menu.getLabel());

            return cv;
        }
    }

    private void setMenuItems(MenuArrayAdapter adapter) {
        // Menu setting
        Set<TweetMenu> menuSetting = PrefClickAction.getTweetMenu();

        // Menu drawable
        int replyIcon = R.drawable.ic_reply;
        int retweetIcon = R.drawable.ic_retweet;
        int favoriteIcon = PrefAppearance.getLikeFavDrawable();
        int talkIcon = R.drawable.ic_talk;
        int deleteIcon = R.drawable.ic_garbage;
        int urlIcon = R.drawable.ic_link;
        int hashIcon = R.drawable.ic_hashtag;
        int userIcon = R.drawable.ic_user;
        int copyIcon = R.drawable.ic_copy;
        int detailIcon = R.drawable.ic_doc;
        int shareIcon = R.drawable.ic_share;
        int twitterIcon = R.drawable.ic_twitter;

        // Menu color
        int replyColor = COLOR_STRONG;
        int retweetColor = COLOR_RETWEET;
        int favoriteColor = PrefAppearance.getLikeFavColor();
        int talkColor = COLOR_TALK;
        int deleteColor = COLOR_DELETE;
        int urlColor = COLOR_LINK_WEAK;
        int hashColor = COLOR_LINK_WEAK;
        int userColor = COLOR_STRONG;
        int copyColor = COLOR_STRONG;
        int detailColor = COLOR_STRONG;
        int shareColor = COLOR_STRONG;
        int twitterColor = COLOR_STRONG;

        // Reply
        if (menuSetting.contains(REPLY)) {
            adapter.add(
                    new Menu(replyIcon, replyColor, "返信する", "",
                            () -> ClickUseCase.reply(mSource))
            );
        }
        // Retweet
        if (menuSetting.contains(RETWEET)) {
            if (mStatus.isRetweeted()) {
                adapter.add(
                        new Menu(retweetIcon, retweetColor, "リツイート解除", "",
                                () -> StatusUseCase.retweet(mStatus))
                );
            } else if (mSource.isPublic()) {
                adapter.add(
                        new Menu(retweetIcon, retweetColor, "リツイート", "", null)
                );
            }
        }
        // Favorite
        if (menuSetting.contains(LIKE)) {
            if (mStatus.isFavorited()) {
                adapter.add(
                        new Menu(favoriteIcon, favoriteColor, PrefAppearance.getLikeFavText() + "解除", "",
                                () -> StatusUseCase.favorite(mStatus))
                );
            } else {
                adapter.add(
                        new Menu(favoriteIcon, favoriteColor, PrefAppearance.getLikeFavText(), "",
                                () -> StatusUseCase.favorite(mStatus))
                );
            }
        }
        // Talk
        if (menuSetting.contains(TALK)) {
            if (mSource.isTalk()
                    && !TweetMateApp.getActivity().getSupportActionBar().getTitle().equals("会話")
                    && !TweetMateApp.getActivity().getSupportActionBar().getTitle().equals("詳細")) {
                adapter.add(
                        new Menu(talkIcon, talkColor, "会話を表示", "",
                                () -> ClickUseCase.showTalk(mSource))
                );
            }
        }
        // Delete
        if (menuSetting.contains(DELETE)) {
            if (mSource.isMyTweet() && !mStatus.isRetweet()) {
                adapter.add(
                        new Menu(deleteIcon, deleteColor, "削除する", "",
                                () -> StatusUseCase.delete(mStatus))
                );
            }
        }
        // URL
        if (menuSetting.contains(URL)) {
            for (TwitterURLEntity entity : mSource.getUrlEntities()) {
                adapter.add(
                        new Menu(urlIcon, urlColor, entity.getDisplayURL(), "",
                                () -> ClickUseCase.openURL(entity.getUrl()))
                );
            }
        }
        // Hashtag
        if (menuSetting.contains(HASH)) {
            for (TwitterHashtagEntity entity : mSource.getHashtagEntities()) {
                adapter.add(
                        new Menu(hashIcon, hashColor, "#" + entity.getText(), "", null)
                );
            }
        }
        // User
        if (menuSetting.contains(USER)) {
            adapter.add(
                    new Menu(userIcon, userColor, "@" + mSource.getUser().getScreenName(),
                            PrefSystem.getProfileThumbByQuality(mSource.getUser()),
                            () -> ClickUseCase.showUser(mSource.getUser().getId()))
            );
            if (mStatus.isRetweet()) {
                adapter.add(
                        new Menu(userIcon, userColor, "@" + mStatus.getUser().getScreenName(),
                                PrefSystem.getProfileThumbByQuality(mStatus.getUser()),
                                () -> ClickUseCase.showUser(mStatus.getUser().getId()))
                );
            }
            for (TwitterUserMentionEntity entity : mSource.getUserMentionEntities()) {
                adapter.add(
                        new Menu(userIcon, userColor, "@" + entity.getScreenName(), "",
                                () -> ClickUseCase.showUser(entity.getId()))
                );
            }
        }
        // Copy
        if (menuSetting.contains(COPY)) {
            adapter.add(
                    new Menu(copyIcon, copyColor, "本文をコピー", "",
                            () -> ClickUseCase.copyText(mSource))
            );
        }
        // Detail
        if (menuSetting.contains(DETAIL)) {
            adapter.add(
                    new Menu(detailIcon, detailColor, "ツイート詳細", "",
                            () -> ClickUseCase.showDetail(mSource))
            );
        }
        // Share
        if (menuSetting.contains(SHARE)) {
            adapter.add(
                    new Menu(shareIcon, shareColor, "共有する", "",
                            () -> ClickUseCase.share(mStatus))
            );
        }
        // OpenTwitter
        if (menuSetting.contains(TWITTER)) {
            adapter.add(
                    new Menu(twitterIcon, twitterColor, "Twitterで開く", "",
                            () -> ClickUseCase.openStatus(mStatus))
            );
        }
        // Remove duplication
        for (int i = 0; i < adapter.getCount(); i++) {
            for (int j = adapter.getCount() - 1; j > i; j--) {
                if (adapter.getItem(j).getLabel().equals(adapter.getItem(i).getLabel())) {
                    adapter.remove(adapter.getItem(j));
                }
            }
        }
    }

    private void setClickListener(View dialogView, ListView listView, MenuArrayAdapter adapter) {
        // When tweet clicked
        dialogView.findViewById(R.id.relative_tweet).setOnClickListener(
                v -> dismiss()
        );
        // When menu item clicked
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            runItem(adapter.getItem(i), "NORMAL");
        });
        // When menu item long clicked
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            String label = adapter.getItem(i).getLabel();
            if (label.equals("リツイート") || label.equals("リツイート解除") || label.startsWith("#")) {
                runItem(adapter.getItem(i), "REVERSE");
            }
            return true;
        });
    }

    private void runItem(Menu menu, String action) {
        Runnable runnable;
        String label = menu.getLabel();

        if (label.equals("リツイート")) {
            runnable = PrefClickAction.getClickRetweet().equals(action)
                    ? () -> StatusUseCase.retweet(mStatus)
                    : () -> ClickUseCase.quote(mSource);
        } else if (label.equals("リツイート解除")) {
            runnable = PrefClickAction.getClickRetweet().equals(action)
                    ? () -> StatusUseCase.retweet(mStatus)
                    : () -> ClickUseCase.quote(mSource);
        } else if (label.startsWith("#")) {
            runnable = PrefClickAction.getClickHashtag().equals(action)
                    ? () -> ClickUseCase.searchWord(label)
                    : () -> ClickUseCase.tweetWithSuffix(label);
        } else {
            runnable = menu.getRunnable();
        }

        runnable.run();
        dismiss();
    }

}
