package com.ybsystem.tweetmate.adapters.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.listeners.ThumbnailClickListener;
import com.ybsystem.tweetmate.listeners.TweetClickListener;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUserMentionEntity;
import com.ybsystem.tweetmate.models.enums.ClickAction;
import com.ybsystem.tweetmate.models.enums.ImageOption;
import com.ybsystem.tweetmate.databases.*;
import com.ybsystem.tweetmate.usecases.ClickUseCase;
import com.ybsystem.tweetmate.utils.*;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT;
import static com.ybsystem.tweetmate.models.enums.ColumnType.*;
import static com.ybsystem.tweetmate.models.enums.ImageOption.*;
import static com.ybsystem.tweetmate.resources.ResColor.*;
import static com.ybsystem.tweetmate.resources.ResString.*;

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

    // Mark
    @BindView(R.id.image_lock_mark) ImageView mLockMark;
    @BindView(R.id.image_verify_mark) ImageView mVerifyMark;
    @BindView(R.id.image_rt_mark) ImageView mRtMark;
    @BindView(R.id.image_favorite_mark) ImageView mFavMark;

    // Thumbnail
    private ArrayList<ImageView> mThumbnails;
    @BindView(R.id.image_play) ImageView mPlayIcon;
    @BindView(R.id.image_thumbnail_1) ImageView mThumbnail1;
    @BindView(R.id.image_thumbnail_2) ImageView mThumbnail2;
    @BindView(R.id.image_thumbnail_3) ImageView mThumbnail3;
    @BindView(R.id.image_thumbnail_4) ImageView mThumbnail4;
    @BindView(R.id.relative_thumbnail) RelativeLayout mThumbnailContainer;

    // Quote
    @BindView(R.id.relative_quote) RelativeLayout mQuoteContainer;
    @BindView(R.id.text_user_name_quote) TextView mQuoteUserName;
    @BindView(R.id.text_screen_name_quote) TextView mQuoteScreenName;
    @BindView(R.id.text_tweet_quote) TextView mQuoteTweetText;
    @BindView(R.id.text_relative_time_quote) TextView mQuoteRelativeTime;

    // Detail
    @BindView(R.id.relative_detail) RelativeLayout mDetailContainer;
    @BindView(R.id.text_rt_count_detail) TextView mDetailRtCount;
    @BindView(R.id.text_fav_count_detail) TextView mDetailFavCount;

    // Click
    @BindView(R.id.linear_click) LinearLayout mClickContainer;
    @BindView(R.id.view_left_click) View mLeftClick;
    @BindView(R.id.view_middle_click) View mMiddleClick;
    @BindView(R.id.view_right_click) View mRightClick;

    public TweetRow(View itemView) {
        super(itemView);

        // ButterKnife
        ButterKnife.bind(this, itemView);

        // Init
        mThumbnails = new ArrayList<>();
        mThumbnails.add(mThumbnail1);
        mThumbnails.add(mThumbnail2);
        mThumbnails.add(mThumbnail3);
        mThumbnails.add(mThumbnail4);

        // Apply app settings
        applyThemeSetting();
        applyAppearanceSetting();
    }

    public void setStatus(TwitterStatus status) {
        mStatus = status;
        mSource = mStatus.isRetweet()
                ? mStatus.getRtStatus()
                : mStatus;
    }

    public void setUserName() {
        mUserName.setText(mSource.getUser().getName());
    }

    public void setScreenName() {
        mScreenName.setText("@" + mSource.getUser().getScreenName());
    }

    public void setUserIcon() {
        String userIconURL = PrefSystem.getProfileThumbByQuality(mSource.getUser());
        String rtUserIconURL = PrefSystem.getProfileThumbByQuality(mStatus.getUser());
        ImageOption option = ImageOption.toEnum(PrefAppearance.getUserIconStyle());

        if (mStatus.isRetweet()) {
            // When retweet
            mRtUserIcon.setVisibility(View.VISIBLE);
            mRtArrowIcon.setVisibility(View.VISIBLE);
            GlideUtils.load(userIconURL, mUserIcon, option);
            GlideUtils.load(rtUserIconURL, mRtUserIcon, option);
        } else {
            // When tweet
            mRtUserIcon.setVisibility(View.GONE);
            mRtArrowIcon.setVisibility(View.GONE);
            GlideUtils.load(userIconURL, mUserIcon, option);
        }
    }

    public void setTweetText() {
        mTweetText.setText(HtmlCompat.fromHtml(mSource.getConvertedText(), FROM_HTML_MODE_COMPACT));
    }

    public void setRelativeTime() {
        mRelativeTime.setText(mSource.getConvertedRelativeTime());
    }

    public void setAbsoluteTime() {
        mAbsoluteTime.setText(mSource.getConvertedAbsoluteTime());
        mAbsoluteTime.setVisibility(View.VISIBLE);
    }

    public void setVia() {
        mVia.setText(mSource.getConvertedVia());
        mVia.setVisibility(View.VISIBLE);
    }

    public void setRtFavCount() {
        // Set retweet count
        int rtCount = mSource.getRetweetCount();
        if (rtCount == 0) {
            mRtCountText.setVisibility(View.GONE);
            mRtCountIcon.setVisibility(View.GONE);
        } else {
            mRtCountText.setText(String.valueOf(rtCount));
            mRtCountText.setVisibility(View.VISIBLE);
            mRtCountIcon.setVisibility(View.VISIBLE);
        }
        // Set favorite count
        int favCount = mSource.getFavoriteCount();
        if (favCount == 0) {
            mFavCountText.setVisibility(View.GONE);
            mFavCountIcon.setVisibility(View.GONE);
        } else {
            mFavCountText.setText(String.valueOf(favCount));
            mFavCountText.setVisibility(View.VISIBLE);
            mFavCountIcon.setVisibility(View.VISIBLE);
        }
    }

    public void setRetweetedBy() {
        if (!mStatus.isRetweet()) {
            mRetweetedBy.setVisibility(View.GONE);
            return;
        }
        // Set retweeted by
        String name = mStatus.getUser().getName();
        String text = getString(R.string.sentence_retweeted_by, name);
        mRetweetedBy.setText(text);
        mRetweetedBy.setVisibility(View.VISIBLE);
    }

    public void setMarks() {
        // Check retweeted tweet by myself
        if (mStatus.isRetweeted())
            mRtMark.setVisibility(View.VISIBLE);
        else
            mRtMark.setVisibility(View.GONE);

        // Check favorited tweet by myself
        if (mStatus.isFavorited())
            mFavMark.setVisibility(View.VISIBLE);
        else
            mFavMark.setVisibility(View.GONE);

        // Check verified user
        if (mSource.getUser().isVerified())
            mVerifyMark.setVisibility(View.VISIBLE);
        else
            mVerifyMark.setVisibility(View.GONE);

        // Check protected user
        if (mSource.getUser().isProtected())
            mLockMark.setVisibility(View.VISIBLE);
        else
            mLockMark.setVisibility(View.GONE);
    }

    public void setThumbnail() {
        ArrayList<String> imageUrls = mSource.getImageUrls();

        // When media not exist, hide and exit
        if (imageUrls.isEmpty()) {
            mThumbnailContainer.setVisibility(View.GONE);
            return;
        }
        // Init visibility because recycling convertView
        mPlayIcon.setVisibility(View.GONE);
        for (ImageView thumbnail : mThumbnails) {
            thumbnail.setVisibility(View.GONE);
        }

        // When video exist, show play icon
        if (mSource.isVideo()) {
            mPlayIcon.setVisibility(View.VISIBLE);
        }
        // Set thumbnails
        for (int i = 0; i < imageUrls.size(); i++) {
            String imageURL = PrefSystem.getMediaThumbByQuality(imageUrls.get(i), mSource.isThirdMedia());
            ImageView thumbnail = mThumbnails.get(i);
            GlideUtils.load(imageURL, thumbnail, NONE);
            thumbnail.setVisibility(View.VISIBLE);
            thumbnail.setOnClickListener(new ThumbnailClickListener(i, mSource));
        }
        mThumbnailContainer.setVisibility(View.VISIBLE);
    }

    public void setQuoteTweet() {
        // When tweet has not quote, hide and exit
        if (mSource.getQtStatus() == null) {
            mQuoteContainer.setVisibility(View.GONE);
            return;
        }
        // Set quote tweet
        TwitterStatus qtStatus = mSource.getQtStatus();
        mQuoteUserName.setText(qtStatus.getUser().getName());
        mQuoteScreenName.setText("@" + qtStatus.getUser().getScreenName());
        mQuoteTweetText.setText(HtmlCompat.fromHtml(qtStatus.getConvertedText(), FROM_HTML_MODE_COMPACT));
        mQuoteRelativeTime.setText(qtStatus.getConvertedRelativeTime());
        mQuoteContainer.setVisibility(View.VISIBLE);
        mQuoteContainer.setOnClickListener(new TweetClickListener(qtStatus, ClickAction.DETAIL));
    }

    public void setDetailTweet() {
        // When tweet is not detail, hide and exit
        if (!mSource.isDetail()) {
            mDetailContainer.setVisibility(View.GONE);
            return;
        }
        // Set detail rt fav count
        int rtCount = mSource.getRetweetCount();
        int favCount = mSource.getFavoriteCount();
        mDetailRtCount.setText(rtCount + " " + STR_RETWEET);
        mDetailFavCount.setText(favCount + " " + PrefAppearance.getLikeFavText());
        mDetailContainer.setVisibility(View.VISIBLE);

        // When clicked rt fav count
        if (rtCount > 0)
            mDetailRtCount.setOnClickListener(view ->
                ClickUseCase.showRtFavUser(mSource, RT_USER)
            );
        if (favCount > 0)
            mDetailFavCount.setOnClickListener(view ->
                ClickUseCase.showRtFavUser(mSource, FAV_USER)
            );
    }

    public void setBackgroundColor() {
        // Check if reply
        boolean isReply = false;
        for (TwitterUserMentionEntity mention : mSource.getUserMentionEntities()) {
            if (mention.getScreenName().equals(TweetMateApp.getMyUser().getScreenName())) {
                isReply = true;
            }
        }
        // Set custom theme
        if (mSource.getUser().getId() == TweetMateApp.getMyUser().getId())
            // My tweet
            mClickContainer.setBackgroundColor(
                    PrefTheme.isCustomThemeEnabled() ? PrefTheme.getBgMyTweetColor() : COLOR_BG_MYTWEET
            );
        else if (isReply)
            // Reply
            mClickContainer.setBackgroundColor(
                    PrefTheme.isCustomThemeEnabled() ? PrefTheme.getBgReplyColor() : COLOR_BG_REPLY
            );
        else if (mStatus.isRetweet())
            // Retweet
            mClickContainer.setBackgroundColor(
                    PrefTheme.isCustomThemeEnabled() ? PrefTheme.getBgRetweetColor() : COLOR_BG_RETWEET
            );
        else
            // Tweet
            mClickContainer.setBackgroundColor(0);
    }

    public void setTweetClickListener() {
        // Set click listener
        mLeftClick.setOnClickListener(new TweetClickListener(mStatus, PrefClickAction.getLeftClick()));
        mMiddleClick.setOnClickListener(new TweetClickListener(mStatus, PrefClickAction.getMiddleClick()));
        mRightClick.setOnClickListener(new TweetClickListener(mStatus, PrefClickAction.getRightClick()));

        // Set long click listener
        mLeftClick.setOnLongClickListener(new TweetClickListener(mStatus, PrefClickAction.getLeftLongClick()));
        mMiddleClick.setOnLongClickListener(new TweetClickListener(mStatus, PrefClickAction.getMiddleLongClick()));
        mRightClick.setOnLongClickListener(new TweetClickListener(mStatus, PrefClickAction.getRightLongClick()));
    }

    public void initVisibilities() {
        // Set visibility
        mAbsoluteTime.setVisibility(View.GONE);
        mVia.setVisibility(View.GONE);
        mRtCountText.setVisibility(View.GONE);
        mRtCountIcon.setVisibility(View.GONE);
        mFavCountText.setVisibility(View.GONE);
        mFavCountIcon.setVisibility(View.GONE);
        mRetweetedBy.setVisibility(View.GONE);
        mThumbnailContainer.setVisibility(View.GONE);
        mQuoteContainer.setVisibility(View.GONE);
        mDetailContainer.setVisibility(View.GONE);
    }

    private void applyThemeSetting() {
        // Check
        if (!PrefTheme.isCustomThemeEnabled()) {
            return;
        }
        // Set tweet color
        mUserName.setTextColor(PrefTheme.getUserNameColor());
        mScreenName.setTextColor(PrefTheme.getUserNameColor());
        mRelativeTime.setTextColor(PrefTheme.getRelativeTimeColor());
        mTweetText.setTextColor(PrefTheme.getTweetTextColor());
        mAbsoluteTime.setTextColor(PrefTheme.getAbsoluteTimeColor());
        mVia.setTextColor(PrefTheme.getViaColor());
        mRtCountText.setTextColor(PrefTheme.getRtFavColor());
        mRtCountIcon.setColorFilter(PrefTheme.getRtFavColor());
        mFavCountText.setTextColor(PrefTheme.getRtFavColor());
        mFavCountIcon.setColorFilter(PrefTheme.getRtFavColor());
        mRetweetedBy.setTextColor(PrefTheme.getRetweetedByColor());

        // Set quote color
        mQuoteUserName.setTextColor(PrefTheme.getUserNameColor());
        mQuoteScreenName.setTextColor(PrefTheme.getUserNameColor());
        mQuoteRelativeTime.setTextColor(PrefTheme.getRelativeTimeColor());
        mQuoteTweetText.setTextColor(PrefTheme.getTweetTextColor());

        // Set detail color
        mDetailRtCount.setTextColor(PrefTheme.getRtFavColor());
        mDetailFavCount.setTextColor(PrefTheme.getRtFavColor());
    }

    private void applyAppearanceSetting() {
        // Set font size
        int fontSize = PrefAppearance.getFontSize();
        mUserName.setTextSize(fontSize);
        mScreenName.setTextSize(fontSize - 1);
        mTweetText.setTextSize(fontSize);
        mRelativeTime.setTextSize(fontSize);
        mAbsoluteTime.setTextSize(fontSize - 1);
        mVia.setTextSize(fontSize - 1);
        mRetweetedBy.setTextSize(fontSize - 1);
        mRtCountText.setTextSize(fontSize - 1);
        mFavCountText.setTextSize(fontSize - 1);
        mQuoteUserName.setTextSize(fontSize);
        mQuoteScreenName.setTextSize(fontSize - 1);
        mQuoteTweetText.setTextSize(fontSize);
        mQuoteRelativeTime.setTextSize(fontSize);

        // Set user icon size
        int dpSize = PrefAppearance.getUserIconSize();
        mUserIcon.getLayoutParams().width = CalcUtils.convertDp2Px(dpSize);
        mUserIcon.getLayoutParams().height = CalcUtils.convertDp2Px(dpSize);

        // Set like fav style
        mFavMark.setColorFilter(PrefAppearance.getLikeFavColor());
        mFavMark.setImageResource(PrefAppearance.getLikeFavDrawable());
        mFavCountIcon.setImageResource(PrefAppearance.getLikeFavDrawable());
    }

}
