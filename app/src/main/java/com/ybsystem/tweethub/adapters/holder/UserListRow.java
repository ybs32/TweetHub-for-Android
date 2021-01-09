package com.ybsystem.tweethub.adapters.holder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.UserListActivity;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUserList;
import com.ybsystem.tweethub.models.enums.ImageOption;
import com.ybsystem.tweethub.storages.PrefAppearance;
import com.ybsystem.tweethub.storages.PrefSystem;
import com.ybsystem.tweethub.utils.GlideUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListRow extends RecyclerView.ViewHolder {
    // UserList
    private TwitterUserList mUserList;

    // Mandatory
    @BindView(R.id.text_name) TextView mName;
    @BindView(R.id.text_description) TextView mDescription;
    @BindView(R.id.image_user_icon) ImageView mUserIcon;
    @BindView(R.id.text_member_count) TextView mMemberCount;
    @BindView(R.id.text_creator) TextView mCreator;

    // Mark
    @BindView(R.id.image_lock_mark) ImageView mLockMark;

    // Click
    @BindView(R.id.relative_click) RelativeLayout mClickContainer;

    public UserListRow(View itemView) {
        super(itemView);

        // ButterKnife
        ButterKnife.bind(this, itemView);
    }

    public void setUserList(TwitterUserList userList) {
        this.mUserList = userList;
    }

    public void setName() {
        this.mName.setText(mUserList.getName());
    }

    public void setUserIcon() {
        String userIconURL = PrefSystem.getProfileThumbByQuality(mUserList.getUser());
        ImageOption option = ImageOption.toEnum(PrefAppearance.getUserIconStyle());
        GlideUtils.load(userIconURL, this.mUserIcon, option);
    }

    public void setDescription() {
        String description = mUserList.getDescription();
        if (description.equals("")) {
            this.mDescription.setVisibility(View.GONE);
        } else {
            this.mDescription.setText(description);
            this.mDescription.setVisibility(View.VISIBLE);
        }
    }

    public void setMemberCount() {
        this.mMemberCount.setText(mUserList.getMemberCount() + "人のメンバー");
    }

    public void setCreator() {
        this.mCreator.setText(mUserList.getUser().getName() + "さんが作成");
    }

    public void setMarks() {
        if (mUserList.isProtected()) {
            this.mLockMark.setVisibility(View.VISIBLE);
        } else {
            this.mLockMark.setVisibility(View.GONE);
        }
    }

    public void setListClickListener() {
        mClickContainer.setOnClickListener(view -> {
            Activity activity = TweetHubApp.getActivity();
            Intent intent = new Intent(activity, UserListActivity.class);
            intent.putExtra("USER_LIST", mUserList);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
        });
    }

}
