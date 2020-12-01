package com.ybsystem.tweethub.adapters.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.libs.eventbus.FooterEvent;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.adapters.holder.FooterRow;
import com.ybsystem.tweethub.adapters.holder.UserRow;

import org.greenrobot.eventbus.EventBus;

public class UserRecyclerAdapter extends RecyclerAdapterBase<TwitterUser> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflater
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Check type
        if (viewType == TYPE_FOOTER) {
            mFooterView = inflater.inflate(R.layout.recycler_item_footer, parent, false);
            EventBus.getDefault().post(new FooterEvent());
            return new FooterRow(mFooterView);
        } else {
            View view = inflater.inflate(R.layout.recycler_item_user, parent, false);
            return new UserRow(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Check instance
        if (holder instanceof UserRow) {
            UserRow row = (UserRow) holder;
            TwitterUser user = mObjList.get(position);
            this.renderUser(row, user);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position == getItemCount() - 1) {
            return -1;
        } else {
            return mObjList.get(position).getId();
        }
    }

    private void renderUser(UserRow userRow, TwitterUser user) {
        // Set fields
        userRow.setUser(user);
        userRow.setUserName();
        userRow.setScreenName();
        userRow.setUserIcon();
        userRow.setProfileText();
        userRow.setFollowButton();
        userRow.setMarks();
        userRow.setUserClickListener();
    }

}
