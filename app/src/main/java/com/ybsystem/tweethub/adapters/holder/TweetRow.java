package com.ybsystem.tweethub.adapters.holder;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.listeners.ThumbnailClickListener;
import com.ybsystem.tweethub.listeners.TweetClickListener;
import com.ybsystem.tweethub.models.entities.twitter.TwitterMediaEntity;
import com.ybsystem.tweethub.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUserMentionEntity;
import com.ybsystem.tweethub.models.enums.ClickAction;
import com.ybsystem.tweethub.models.enums.ImageOption;
import com.ybsystem.tweethub.storages.PrefAppearance;
import com.ybsystem.tweethub.storages.PrefClickAction;
import com.ybsystem.tweethub.storages.PrefSystem;
import com.ybsystem.tweethub.storages.PrefTheme;
import com.ybsystem.tweethub.utils.CalcUtils;
import com.ybsystem.tweethub.utils.GlideUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ybsystem.tweethub.models.enums.ImageOption.*;

public class TweetRow extends RecyclerView.ViewHolder {
    // Status
    private TwitterStatus mStatus;
    private TwitterStatus mSource;

    // Mandatory
    @BindView(R.id.text_user_name) TextView mUserName;
    @BindView(R.id.text_screen_name) TextView mScreenName;
    @BindView(R.id.image_user_icon) ImageView mUserIcon;
    @BindView(R.id.image_rt_user_icon) ImageView mRtUserIcon;
    @BindView(R.id.image_rt_arrow) ImageView mRtArrowIcon;
    @BindView(R.id.text_tweet) TextView mTweetText;
    @BindView(R.id.text_relative_time) TextView mRelativeTime;

    // Optional
    @BindView(R.id.text_absolute_time) TextView mAbsoluteTime;
    @BindView(R.id.text_via) TextView mVia;
    @BindView(R.id.text_rt_count) TextView mRtCountText;
    @BindView(R.id.text_fav_count) TextView mFavCountText;
    @BindView(R.id.image_rt_count) ImageView mRtCountIcon;
    @BindView(R.id.image_fav_count) ImageView mFavCountIcon;
    @BindView(R.id.text_retweeted_by) TextView mRetweetedBy;

    // Thumbnail
    private ArrayList<ImageView> mThumbnails;
    @BindView(R.id.image_play) ImageView mPlayIcon;
    @BindView(R.id.image_thumbnail_1) ImageView mThumbnail1;
    @BindView(R.id.image_thumbnail_2) ImageView mThumbnail2;
    @BindView(R.id.image_thumbnail_3) ImageView mThumbnail3;
    @BindView(R.id.image_thumbnail_4) ImageView mThumbnail4;
    @BindView(R.id.relative_thumbnail) RelativeLayout mThumbnailContainer;

    // Quote
    @BindView(R.id.text_user_name_quote) TextView mQuoteUserName;
    @BindView(R.id.text_screen_name_quote) TextView mQuoteScreenName;
    @BindView(R.id.text_tweet_quote) TextView mQuoteTweetText;
    @BindView(R.id.text_relative_time_quote) TextView mQuoteRelativeTime;
    @BindView(R.id.relative_quote) RelativeLayout mQuoteContainer;

    // Detail
    @BindView(R.id.text_rt_count_detail) TextView mDetailRtCount;
    @BindView(R.id.text_fav_count_detail) TextView mDetailFavCount;
    @BindView(R.id.linear_detail) LinearLayout mDetailContainer;

    // Mark
    @BindView(R.id.image_lock_mark) ImageView mLockMark;
    @BindView(R.id.image_verify_mark) ImageView mVerifyMark;
    @BindView(R.id.image_rt_mark) ImageView mRtMark;
    @BindView(R.id.image_favorite_mark) ImageView mFavMark;

    // Click
    @BindView(R.id.view_left_click) View mLeftClick;
    @BindView(R.id.view_middle_click) View mMiddleClick;
    @BindView(R.id.view_right_click) View mRightClick;
    @BindView(R.id.linear_click) LinearLayout mClickContainer;

    public TweetRow(View itemView) {
        super(itemView);

        // ButterKnife
        ButterKnife.bind(this, itemView);

        // Init variables
        this.mThumbnails = new ArrayList<>();
        this.mThumbnails.add(mThumbnail1);
        this.mThumbnails.add(mThumbnail2);
        this.mThumbnails.add(mThumbnail3);
        this.mThumbnails.add(mThumbnail4);

        // Apply app settings
        applyAppearanceSetting();
        if (PrefTheme.isCustomThemeEnabled()) {
            applyCustomThemeSetting();
        }
    }

    public void setStatus(TwitterStatus status) {
        this.mStatus = status;
        this.mSource = mStatus.isRetweet()
                ? mStatus.getRtStatus()
                : mStatus;
    }

    public void setUserName() {
        this.mUserName.setText(mSource.getUser().getName());
    }

    public void setScreenName() {
        this.mScreenName.setText("@" + mSource.getUser().getScreenName());
    }

    public void setUserIcon() {
        String userIconURL = PrefSystem.getProfileThumbByQuality(mSource.getUser());
        String rtUserIconURL = PrefSystem.getProfileThumbByQuality(mStatus.getUser());
        ImageOption option = ImageOption.toEnum(PrefAppearance.getUserIconStyle());

        if (!mStatus.isRetweet()) {
            // When tweet
            this.mRtUserIcon.setVisibility(View.GONE);
            this.mRtArrowIcon.setVisibility(View.GONE);
            GlideUtils.load(userIconURL, this.mUserIcon, option);
        } else {
            // When retweet
            this.mRtUserIcon.setVisibility(View.VISIBLE);
            this.mRtArrowIcon.setVisibility(View.VISIBLE);
            GlideUtils.load(userIconURL, this.mUserIcon, option);
            GlideUtils.load(rtUserIconURL, this.mRtUserIcon, option);
        }
    }

    public void setTweetText() {
        this.mTweetText.setText(Html.fromHtml(mSource.getConvertedText()));
    }

    public void setRelativeTime() {
        this.mRelativeTime.setText(mSource.getConvertedRelativeTime());
    }

    public void setAbsoluteTime() {
        this.mAbsoluteTime.setText(mSource.getConvertedAbsoluteTime());
        this.mAbsoluteTime.setVisibility(View.VISIBLE);
    }

    public void setVia() {
        this.mVia.setText(mSource.getConvertedVia());
        this.mVia.setVisibility(View.VISIBLE);
    }

    public void setRtFavCount() {
        // Set retweet count
        int rtCount = mSource.getRetweetCount();
        if (rtCount == 0) {
            this.mRtCountText.setVisibility(View.GONE);
            this.mRtCountIcon.setVisibility(View.GONE);
        } else {
            this.mRtCountText.setText(String.valueOf(rtCount));
            this.mRtCountText.setVisibility(View.VISIBLE);
            this.mRtCountIcon.setVisibility(View.VISIBLE);
        }
        // Set favorite count
        int favCount = mSource.getFavoriteCount();
        if (favCount == 0) {
            this.mFavCountText.setVisibility(View.GONE);
            this.mFavCountIcon.setVisibility(View.GONE);
        } else {
            this.mFavCountText.setText(String.valueOf(favCount));
            this.mFavCountText.setVisibility(View.VISIBLE);
            this.mFavCountIcon.setVisibility(View.VISIBLE);
        }
    }

    public void setRetweetedBy() {
        if (!mStatus.isRetweet()) {
            this.mRetweetedBy.setVisibility(View.GONE);
            return;
        }
        // Set retweeted by
        this.mRetweetedBy.setText(mStatus.getUser().getName() + " さんがリツイート");
        this.mRetweetedBy.setVisibility(View.VISIBLE);
    }

    public void setThumbnail() {
        TwitterMediaEntity[] medias = mSource.getMediaEntities();

        // When media not exist, hide and exit
        if (medias == null || medias.length == 0) {
            this.mThumbnailContainer.setVisibility(View.GONE);
            return;
        }
        // Init visibility because recycling convertView
        this.mPlayIcon.setVisibility(View.GONE);
        for (ImageView thumbnail : this.mThumbnails) {
            thumbnail.setVisibility(View.GONE);
        }

        // When video exist, show play icon
        if (medias[0].getVideoVariants().length > 0) {
            this.mPlayIcon.setVisibility(View.VISIBLE);
        }
        // Set thumbnails
        for (int i = 0; i < medias.length; i++) {
            String imageURL = PrefSystem.getMediaThumbByQuality(medias[i].getMediaURL());
            ImageView thumbnail = this.mThumbnails.get(i);
            GlideUtils.load(imageURL, thumbnail, NONE);
            thumbnail.setVisibility(View.VISIBLE);
            thumbnail.setOnClickListener(new ThumbnailClickListener(i, medias));
        }
        this.mThumbnailContainer.setVisibility(View.VISIBLE);
    }

    public void setBackgroundColor() {
        // Check if basic theme
        if (!PrefTheme.isCustomThemeEnabled()) {
            if (mStatus.isRetweet())
                mClickContainer.setBackgroundColor(PrefTheme.getRetweetColor());
            else
                mClickContainer.setBackgroundColor(0);
            return;
        }
        // Check if reply
        boolean isReply = false;
        for (TwitterUserMentionEntity mention : mSource.getUserMentionEntities()) {
            if (mention.getId() == TweetHubApp.getMyUser().getId()) {
                isReply = true;
            }
        }
        // Set custom theme
        if (mSource.getUser().getId() == TweetHubApp.getMyUser().getId())
            // My tweet
            mClickContainer.setBackgroundColor(PrefTheme.getMyTweetColor());
        else if (isReply)
            // Reply
            mClickContainer.setBackgroundColor(PrefTheme.getReplyColor());
        else if (mStatus.isRetweet())
            // Retweet
            mClickContainer.setBackgroundColor(PrefTheme.getRetweetColor());
        else
            // Tweet
            mClickContainer.setBackgroundColor(0);
    }

    public void setQuoteTweet() {
        // When tweet has not quote, hide and exit
        if (mSource.getQtStatus() == null) {
            mQuoteContainer.setVisibility(View.GONE);
            return;
        }
        // Set quote tweet
        TwitterStatus qtStatus = mSource.getQtStatus();
        this.mQuoteUserName.setText(qtStatus.getUser().getName());
        this.mQuoteScreenName.setText("@" + qtStatus.getUser().getScreenName());
        this.mQuoteTweetText.setText(Html.fromHtml(qtStatus.getConvertedText()));
        this.mQuoteRelativeTime.setText(qtStatus.getConvertedRelativeTime());
        this.mQuoteContainer.setVisibility(View.VISIBLE);
        this.mQuoteContainer.setOnClickListener(new TweetClickListener(qtStatus, ClickAction.DETAIL));
    }

    public void setDetailTweet() {
        // When tweet is not detail, hide and exit
        if (!mSource.isDetail()) {
            mDetailContainer.setVisibility(View.GONE);
            return;
        }
        // Set detail rt fav
        this.mDetailRtCount.setText(mSource.getRetweetCount() + " リツイート");
        this.mDetailFavCount.setText(mSource.getFavoriteCount() + " " + PrefAppearance.getLikeFavText());
        this.mDetailContainer.setVisibility(View.VISIBLE);
    }

    public void setMarks() {
        // Check retweeted tweet by myself
        if (mStatus.isRetweeted())
            this.mRtMark.setVisibility(View.VISIBLE);
        else
            this.mRtMark.setVisibility(View.GONE);

        // Check favorited tweet by myself
        if (mStatus.isFavorited())
            this.mFavMark.setVisibility(View.VISIBLE);
        else
            this.mFavMark.setVisibility(View.GONE);

        // Check verified user
        if (mSource.getUser().isVerified())
            this.mVerifyMark.setVisibility(View.VISIBLE);
        else
            this.mVerifyMark.setVisibility(View.GONE);

        // Check protected user
        if (mSource.getUser().isProtected())
            this.mLockMark.setVisibility(View.VISIBLE);
        else
            this.mLockMark.setVisibility(View.GONE);
    }

    public void setTweetClickListener() {
        // Set click listener
        this.mLeftClick.setOnClickListener(new TweetClickListener(mStatus, PrefClickAction.getLeftClick()));
        this.mMiddleClick.setOnClickListener(new TweetClickListener(mStatus, PrefClickAction.getMiddleClick()));
        this.mRightClick.setOnClickListener(new TweetClickListener(mStatus, PrefClickAction.getRightClick()));

        // Set long click listener
        this.mLeftClick.setOnLongClickListener(new TweetClickListener(mStatus, PrefClickAction.getLeftLongClick()));
        this.mMiddleClick.setOnLongClickListener(new TweetClickListener(mStatus, PrefClickAction.getMiddleLongClick()));
        this.mRightClick.setOnLongClickListener(new TweetClickListener(mStatus, PrefClickAction.getRightLongClick()));
    }

    public void hideOptionalFields() {
        this.mAbsoluteTime.setVisibility(View.GONE);
        this.mVia.setVisibility(View.GONE);
        this.mRtCountText.setVisibility(View.GONE);
        this.mRtCountIcon.setVisibility(View.GONE);
        this.mFavCountText.setVisibility(View.GONE);
        this.mFavCountIcon.setVisibility(View.GONE);
        this.mRetweetedBy.setVisibility(View.GONE);
        this.mThumbnailContainer.setVisibility(View.GONE);
        this.mQuoteContainer.setVisibility(View.GONE);
        this.mDetailContainer.setVisibility(View.GONE);
    }

    private void applyAppearanceSetting() {
        // Set font size
        int fontSize = PrefAppearance.getFontSize();
        this.mUserName.setTextSize(fontSize);
        this.mScreenName.setTextSize(fontSize - 1);
        this.mTweetText.setTextSize(fontSize);
        this.mRelativeTime.setTextSize(fontSize);
        this.mAbsoluteTime.setTextSize(fontSize - 1);
        this.mVia.setTextSize(fontSize - 1);
        this.mRetweetedBy.setTextSize(fontSize - 1);
        this.mRtCountText.setTextSize(fontSize - 1);
        this.mFavCountText.setTextSize(fontSize - 1);
        this.mQuoteUserName.setTextSize(fontSize);
        this.mQuoteScreenName.setTextSize(fontSize - 1);
        this.mQuoteTweetText.setTextSize(fontSize);
        this.mQuoteRelativeTime.setTextSize(fontSize);

        // Set user icon size
        int dpSize = PrefAppearance.getUserIconSize();
        mUserIcon.getLayoutParams().width = CalcUtils.convertDp2Px(dpSize);
        mUserIcon.getLayoutParams().height = CalcUtils.convertDp2Px(dpSize);

        // Set like fav style
        mFavMark.setColorFilter(PrefAppearance.getLikeFavColor());
        mFavMark.setImageResource(PrefAppearance.getLikeFavDrawable());
        mFavCountIcon.setImageResource(PrefAppearance.getLikeFavDrawable());
    }

    private void applyCustomThemeSetting() {
        // Set tweet color
        this.mUserName.setTextColor(PrefTheme.getUserNameColor());
        this.mScreenName.setTextColor(PrefTheme.getUserNameColor());
        this.mRelativeTime.setTextColor(PrefTheme.getRelativeTimeColor());
        this.mTweetText.setTextColor(PrefTheme.getTweetTextColor());
        this.mAbsoluteTime.setTextColor(PrefTheme.getAbsoluteTimeColor());
        this.mVia.setTextColor(PrefTheme.getViaColor());
        this.mRtCountText.setTextColor(PrefTheme.getRtFavColor());
        this.mRtCountIcon.setColorFilter(PrefTheme.getRtFavColor());
        this.mFavCountText.setTextColor(PrefTheme.getRtFavColor());
        this.mFavCountIcon.setColorFilter(PrefTheme.getRtFavColor());
        this.mRetweetedBy.setTextColor(PrefTheme.getRetweetedByColor());

        // Set quote color
        this.mQuoteUserName.setTextColor(PrefTheme.getUserNameColor());
        this.mQuoteScreenName.setTextColor(PrefTheme.getUserNameColor());
        this.mQuoteRelativeTime.setTextColor(PrefTheme.getRelativeTimeColor());
        this.mQuoteTweetText.setTextColor(PrefTheme.getTweetTextColor());

        // Set detail color
        this.mDetailRtCount.setTextColor(PrefTheme.getRtFavColor());
        this.mDetailFavCount.setTextColor(PrefTheme.getRtFavColor());
    }

}
