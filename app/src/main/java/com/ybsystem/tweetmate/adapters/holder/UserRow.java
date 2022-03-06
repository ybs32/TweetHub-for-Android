package com.ybsystem.tweetmate.adapters.holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.models.enums.ImageOption;
import com.ybsystem.tweetmate.storages.PrefAppearance;
import com.ybsystem.tweetmate.storages.PrefSystem;
import com.ybsystem.tweetmate.storages.PrefTheme;
import com.ybsystem.tweetmate.usecases.ClickUseCase;
import com.ybsystem.tweetmate.usecases.UserUseCase;
import com.ybsystem.tweetmate.utils.CalcUtils;
import com.ybsystem.tweetmate.utils.GlideUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserRow extends RecyclerView.ViewHolder {
    // User
    private TwitterUser mUser;

    // Mandatory
    @BindView(R.id.text_user_name) TextView mUserName;
    @BindView(R.id.text_screen_name) TextView mScreenName;
    @BindView(R.id.image_user_icon) ImageView mUserIcon;
    @BindView(R.id.text_profile) TextView mProfileText;
    @BindView(R.id.button_follow) Button mFollowButton;

    // Mark
    @BindView(R.id.image_lock_mark) ImageView mLockMark;
    @BindView(R.id.image_verify_mark) ImageView mVerifyMark;

    // Click
    @BindView(R.id.relative_click) RelativeLayout mClickContainer;

    public UserRow(View itemView) {
        super(itemView);

        // ButterKnife
        ButterKnife.bind(this, itemView);

        // Apply app settings
        applyThemeSetting();
        applyAppearanceSetting();
    }

    public void setUser(TwitterUser user) {
        mUser = user;
    }

    public void setUserName() {
        mUserName.setText(mUser.getName());
    }

    public void setScreenName() {
        mScreenName.setText("@" + mUser.getScreenName());
    }

    public void setUserIcon() {
        String userIconURL = PrefSystem.getProfileThumbByQuality(mUser);
        ImageOption option = ImageOption.toEnum(PrefAppearance.getUserIconStyle());
        GlideUtils.load(userIconURL, mUserIcon, option);
    }

    public void setProfileText() {
        mProfileText.setText(mUser.getDescription());
    }

    public void setFollowButton() {
        // Set follow button
        mFollowButton.setText(mUser.getRelationText());
        mFollowButton.setTextColor(mUser.getRelationTextColor());
        mFollowButton.setBackground(mUser.getRelationBackground());

        // When follow button clicked
        mFollowButton.setOnClickListener(view -> {
            if (mUser.isMyself() || mUser.isBlocking()) {
                return;
            }
            if (mUser.isProtected() && !mUser.isFriend()) {
                return;
            }
            UserUseCase.follow(mUser);
        });
    }

    public void setMarks() {
        // Check verified user
        if (mUser.isVerified()) {
            mVerifyMark.setVisibility(View.VISIBLE);
        } else {
            mVerifyMark.setVisibility(View.GONE);
        }
        // Check protected user
        if (mUser.isProtected()) {
            mLockMark.setVisibility(View.VISIBLE);
        } else {
            mLockMark.setVisibility(View.GONE);
        }
    }

    public void setUserClickListener() {
        mClickContainer.setOnClickListener(view ->
                ClickUseCase.showUser(mUser.getId())
        );
    }

    private void applyThemeSetting() {
        // Check
        if (!PrefTheme.isCustomThemeEnabled()) {
            return;
        }
        // Set tweet color
        mUserName.setTextColor(PrefTheme.getUserNameColor());
        mScreenName.setTextColor(PrefTheme.getUserNameColor());
        mProfileText.setTextColor(PrefTheme.getTweetTextColor());
    }

    private void applyAppearanceSetting() {
        // Set font size
        int fontSize = PrefAppearance.getFontSize();
        mUserName.setTextSize(fontSize);
        mScreenName.setTextSize(fontSize - 1);
        mProfileText.setTextSize(fontSize);

        // Set user icon size
        int dpSize = PrefAppearance.getUserIconSize();
        mUserIcon.getLayoutParams().width = CalcUtils.convertDp2Px(dpSize);
        mUserIcon.getLayoutParams().height = CalcUtils.convertDp2Px(dpSize);
    }

}
