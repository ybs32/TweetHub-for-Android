package com.ybsystem.tweethub.adapters.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.adapters.holder.FooterRow;
import com.ybsystem.tweethub.adapters.holder.UserListRow;
import com.ybsystem.tweethub.libs.eventbus.FooterEvent;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUserList;

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
