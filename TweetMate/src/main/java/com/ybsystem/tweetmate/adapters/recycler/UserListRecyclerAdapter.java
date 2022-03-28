package com.ybsystem.tweetmate.adapters.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.holder.FooterRow;
import com.ybsystem.tweetmate.adapters.holder.UserListRow;
import com.ybsystem.tweetmate.libs.eventbus.FooterEvent;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUserList;

import org.greenrobot.eventbus.EventBus;

public class UserListRecyclerAdapter extends RecyclerAdapterBase<TwitterUserList> {

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
            View view = inflater.inflate(R.layout.recycler_item_userlist, parent, false);
            return new UserListRow(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Check instance
        if (holder instanceof UserListRow) {
            UserListRow row = (UserListRow) holder;
            TwitterUserList list = mObjList.get(position);
            this.renderList(row, list);
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

    private void renderList(UserListRow listRow, TwitterUserList list) {
        // Set fields
        listRow.setUserList(list);
        listRow.setName();
        listRow.setUserIcon();
        listRow.setDescription();
        listRow.setMemberCount();
        listRow.setCreator();
        listRow.setMarks();
        listRow.setListClickListener();
    }

}
