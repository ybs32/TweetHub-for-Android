package com.ybsystem.tweetmate.fragments.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.activities.PhotoActivity;
import com.ybsystem.tweetmate.activities.TimelineActivity;
import com.ybsystem.tweetmate.activities.UserListActivity;
import com.ybsystem.tweetmate.libs.eventbus.UserEvent;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterURLEntity;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.storages.PrefSystem;
import com.ybsystem.tweetmate.usecases.UserUseCase;
import com.ybsystem.tweetmate.utils.GlideUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

import static com.ybsystem.tweetmate.models.enums.ColumnType.*;
import static com.ybsystem.tweetmate.models.enums.ImageOption.*;

public class ProfileFragment extends Fragment {

    private TwitterUser mUser;

    public Fragment newInstance(TwitterUser user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("USER", user);
        setArguments(bundle);
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Init
        mUser = (TwitterUser) getArguments().getSerializable("USER");

        // Set view
        setMarks(view);
        setUserInfo(view);
        setDescription(view);
        setFollowButton(view);
        setFollowFollowerAndList(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(UserEvent event) {
        setFollowButton(getView());
    }

    private void setMarks(View view) {
        // Set verify mark
        ImageView verify = view.findViewById(R.id.image_verify_mark);
        if (mUser.isVerified()) {
            verify.setVisibility(View.VISIBLE);
        } else {
            verify.setVisibility(View.GONE);
        }

        // Set lock mark
        ImageView lock = view.findViewById(R.id.image_lock_mark);
        if (mUser.isProtected()) {
            lock.setVisibility(View.VISIBLE);
        } else {
            lock.setVisibility(View.GONE);
        }
    }

    private void setUserInfo(View view) {
        // Set user name
        TextView userName = view.findViewById(R.id.text_user_name);
        TextView screenName = view.findViewById(R.id.text_screen_name);
        userName.setText(mUser.getName());
        screenName.setText("@" + mUser.getScreenName());

        // Set profile icon
        String profileURL = PrefSystem.getProfileByQuality(mUser);
        ImageView profileImg = view.findViewById(R.id.image_user_icon);
        GlideUtils.load(profileURL, profileImg, CIRCLE);

        // When profile icon clicked
        profileImg.setOnClickListener(v -> {
            Activity act = getActivity();
            Intent intent = new Intent(act, PhotoActivity.class);
            intent.putExtra("TYPE", "PROFILE");
            intent.putExtra("PAGER_POSITION", 0);
            intent.putExtra("IMAGE_URLS", new ArrayList<>(Arrays.asList(profileURL)));
            act.startActivity(intent);
            act.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
        });

        // Set banner
        String bannerURL = PrefSystem.getBannerByQuality(mUser);
        ImageView bannerImg = view.findViewById(R.id.image_banner);
        GlideUtils.load(bannerURL, bannerImg, NONE);

        // When banner clicked
        bannerImg.setOnClickListener(v -> {
            if (bannerURL != null) {
                Activity act = getActivity();
                Intent intent = new Intent(act, PhotoActivity.class);
                intent.putExtra("TYPE", "BANNER");
                intent.putExtra("PAGER_POSITION", 0);
                intent.putExtra("IMAGE_URLS", new ArrayList<>(Arrays.asList(bannerURL)));
                act.startActivity(intent);
                act.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
            }
        });
    }

    private void setDescription(View view) {
        // Replace description url
        String desc = mUser.getDescription();
        TwitterURLEntity[] entities = mUser.getDescriptionURLEntities();
        if (entities != null && entities.length != 0) {
            for (TwitterURLEntity url : entities) {
                desc = desc.replace(url.getUrl(), " " + url.getDisplayURL() + " ");
            }
        }

        // Set description
        TextView descText = view.findViewById(R.id.text_description);
        if (desc.length() == 0) {
            descText.setVisibility(View.GONE);
        } else {
            descText.setText(desc);
        }

        // Set location
        String loc = mUser.getLocation();
        TextView locText = view.findViewById(R.id.text_location);
        if (loc.length() == 0) {
            locText.setVisibility(View.GONE);
        } else {
            locText.setText("場所：" + loc);
        }

        // Set url
        String url = mUser.getUrlEntity().getDisplayURL();
        TextView urlText = view.findViewById(R.id.text_url);
        if (url.length() == 0) {
            urlText.setVisibility(View.GONE);
        } else {
            urlText.setText(url);
        }
    }

    private void setFollowButton(View view) {
        // Set follow button
        Button button = view.findViewById(R.id.button_follow);
        button.setText(mUser.getRelationText());
        button.setTextColor(mUser.getRelationTextColor());
        button.setBackground(mUser.getRelationBackground());

        // When follow button clicked
        button.setOnClickListener(v -> {
            if (mUser.isMyself() || mUser.isBlocking()) {
                return;
            }
            if (mUser.isProtected() && !mUser.isFriend()) {
                return;
            }
            UserUseCase.follow(mUser);
        });
    }

    private void setFollowFollowerAndList(View view) {
        // Set follow
        TextView followText = view.findViewById(R.id.text_follow);
        followText.setText("フォロー\n" + mUser.getFriendsCount());
        followText.setOnClickListener(v -> {
            if (mUser.isPrivate()) {
                return;
            }
            Activity activity = getActivity();
            Intent intent = new Intent(activity, TimelineActivity.class);
            intent.putExtra("USER", mUser);
            intent.putExtra("COLUMN_TYPE", FOLLOW);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
        });

        // Set follower
        TextView followerText = view.findViewById(R.id.text_follower);
        followerText.setText("フォロワー\n" + mUser.getFollowersCount());
        followerText.setOnClickListener(v -> {
            if (mUser.isPrivate()) {
                return;
            }
            Activity activity = getActivity();
            Intent intent = new Intent(activity, TimelineActivity.class);
            intent.putExtra("USER", mUser);
            intent.putExtra("COLUMN_TYPE", FOLLOWER);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
        });

        // Set list
        TextView listText = view.findViewById(R.id.text_list);
        listText.setText("リスト\n" + mUser.getListedCount());
        listText.setOnClickListener(v -> {
            if (mUser.isPrivate()) {
                return;
            }
            Activity activity = getActivity();
            Intent intent = new Intent(activity, UserListActivity.class);
            intent.putExtra("USER", mUser);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
        });
    }

}
