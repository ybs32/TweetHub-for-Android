package com.ybsystem.tweetmate.adapters.array;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.models.entities.Account;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;

public class AccountArrayAdapter extends ArrayAdapter<Account> {

    public AccountArrayAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Inflate
        View cv = convertView;
        if (cv == null) {
            cv = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_common, parent, false);
        }

        // Find
        ImageView image = cv.findViewById(R.id.image_item);
        TextView text = cv.findViewById(R.id.text_item);

        // Set icon on current account
        Account account = getItem(position);
        if (account.isCurrentAccount()) {
            image.setImageResource(R.drawable.ic_check);
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.INVISIBLE);
        }

        // Set account name
        TwitterUser user = account.getUser();
        text.setText(user.getName() + " (@" + user.getScreenName() + ")");

        return cv;
    }

}
