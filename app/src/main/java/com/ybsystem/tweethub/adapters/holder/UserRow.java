package com.ybsystem.tweethub.adapters.holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.models.enums.ImageOption;
import com.ybsystem.tweethub.storages.PrefAppearance;
import com.ybsystem.tweethub.storages.PrefSystem;
import com.ybsystem.tweethub.storages.PrefTheme;
import com.ybsystem.tweethub.usecases.ClickUseCase;
import com.ybsystem.tweethub.usecases.UserUseCase;
import com.ybsystem.tweethub.utils.CalcUtils;
import com.ybsystem.tweethub.utils.GlideUtils;

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
        this.mUser = user;
    }

    public void setUserName() {
        this.mUserName.setText(mUser.getName());
    }

    public void setScreenName() {
        this.mScreenName.setText("@" + mUser.getScreenName());
    }

    public void setUserIcon() {
        String userIconURL = PrefSystem.getProfileThumbByQuality(mUser);
        ImageOption option = ImageOption.toEnum(PrefAppearance.getUserIconStyle());
        GlideUtils.load(userIconURL, this.mUserIcon, option);
    }

    public void setProfileText() {
        this.mProfileText.setText(mUser.getDescription());
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
            this.mVerifyMark.setVisibility(View.VISIBLE);
        } else {
            this.mVerifyMark.setVisibility(View.GONE);
        }
        // Check protected user
        if (mUser.isProtected()) {
            this.mLockMark.setVisibility(View.VISIBLE);
        } else {
            this.mLockMark.setVisibility(View.GONE);
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
        this.mUserName.setTextColor(PrefTheme.getUserNameColor());
        this.mScreenName.setTextColor(PrefTheme.getUserNameColor());
        this.mProfileText.setTextColor(PrefTheme.getTweetTextColor());
    }

    private void applyAppearanceSetting() {
        // Set font size
        int fontSize = PrefAppearance.getFontSize();
        this.mUserName.setTextSize(fontSize);
        this.mScreenName.setTextSize(fontSize - 1);
        this.mProfileText.setTextSize(fontSize);

        // Set user icon size
        int dpSize = PrefAppearance.getUserIconSize();
        mUserIcon.getLayoutParams().width = CalcUtils.convertDp2Px(dpSize);
        mUserIcon.getLayoutParams().height = CalcUtils.convertDp2Px(dpSize);
    }

}
