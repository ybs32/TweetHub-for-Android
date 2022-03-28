package com.ybsystem.tweetmate.adapters.holder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.activities.UserListActivity;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUserList;
import com.ybsystem.tweetmate.models.enums.ImageOption;
import com.ybsystem.tweetmate.databases.PrefAppearance;
import com.ybsystem.tweetmate.databases.PrefSystem;
import com.ybsystem.tweetmate.utils.GlideUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ybsystem.tweetmate.resources.ResString.*;

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
        mUserList = userList;
    }

    public void setName() {
        mName.setText(mUserList.getName());
    }

    public void setUserIcon() {
        String userIconURL = PrefSystem.getProfileThumbByQuality(mUserList.getUser());
        ImageOption option = ImageOption.toEnum(PrefAppearance.getUserIconStyle());
        GlideUtils.load(userIconURL, mUserIcon, option);
    }

    public void setDescription() {
        String description = mUserList.getDescription();
        if (description.equals("")) {
            mDescription.setVisibility(View.GONE);
        } else {
            mDescription.setText(description);
            mDescription.setVisibility(View.VISIBLE);
        }
    }

    public void setMemberCount() {
        mMemberCount.setText(
                getString(R.string.sentence_members, mUserList.getMemberCount())
        );
    }

    public void setCreator() {
        mCreator.setText(
                getString(R.string.sentence_created_by, mUserList.getUser().getName())
        );
    }

    public void setMarks() {
        if (mUserList.isProtected()) {
            mLockMark.setVisibility(View.VISIBLE);
        } else {
            mLockMark.setVisibility(View.GONE);
        }
    }

    public void setListClickListener() {
        mClickContainer.setOnClickListener(view -> {
            Activity activity = TweetMateApp.getActivity();
            Intent intent = new Intent(activity, UserListActivity.class);
            intent.putExtra("USER_LIST", mUserList);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
        });
    }

}
