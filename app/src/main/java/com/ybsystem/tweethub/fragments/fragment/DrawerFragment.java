package com.ybsystem.tweethub.fragments.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.adapters.array.DrawerArrayAdapter;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.models.enums.ImageOption;
import com.ybsystem.tweethub.storages.PrefAppearance;
import com.ybsystem.tweethub.storages.PrefSystem;
import com.ybsystem.tweethub.usecases.ClickUseCase;
import com.ybsystem.tweethub.utils.GlideUtils;

import static com.ybsystem.tweethub.models.enums.ImageOption.*;

public class DrawerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);

        // Set
        setDrawerProfile(view);
        setDrawerItemList(view);

        return view;
    }

    private void setDrawerProfile(View view) {
        // Set user profile
        TwitterUser user = TweetHubApp.getMyUser();
        TextView userName = view.findViewById(R.id.text_user_name);
        TextView screenName = view.findViewById(R.id.text_screen_name);
        userName.setText(user.getName());
        screenName.setText("@" + user.getScreenName());

        // Set user images
        ImageView userIcon = view.findViewById(R.id.image_user_icon);
        ImageView bannerImage = view.findViewById(R.id.image_banner);
        ImageOption option = ImageOption.toEnum(PrefAppearance.getUserIconStyle());
        GlideUtils.load(PrefSystem.getBannerByQuality(user), bannerImage, NONE);
        GlideUtils.load(PrefSystem.getProfileThumbByQuality(user), userIcon, option);

        // When clicked banner, show user profile
        bannerImage.setOnClickListener(v ->
                ClickUseCase.showUser(user.getId())
        );

        // Set verify mark
        ImageView verify = view.findViewById(R.id.image_verify_mark);
        if (user.isVerified()) {
            verify.setVisibility(View.VISIBLE);
        } else {
            verify.setVisibility(View.GONE);
        }

        // Set lock mark
        ImageView lock = view.findViewById(R.id.image_lock_mark);
        if (user.isProtected()) {
            lock.setVisibility(View.VISIBLE);
        } else {
            lock.setVisibility(View.GONE);
        }
    }

    private void setDrawerItemList(View view) {
        // Prepare
        ListView listView = view.findViewById(R.id.list_drawer);
        DrawerArrayAdapter arrayAdapter = new DrawerArrayAdapter(getActivity());

        // Set adapter and remove divider
        listView.setAdapter(arrayAdapter);
        listView.setDivider(null);

        // Add dummy variables
        for (int i = 0; i < 14; i++) {
            arrayAdapter.add(i);
        }
    }

}
