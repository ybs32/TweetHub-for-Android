package com.ybsystem.tweethub.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.PhotoActivity;
import com.ybsystem.tweethub.activities.TimelineActivity;
import com.ybsystem.tweethub.libs.eventbus.UserEvent;
import com.ybsystem.tweethub.models.entities.twitter.TwitterURLEntity;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.storages.PrefSystem;
import com.ybsystem.tweethub.usecases.UserUseCase;
import com.ybsystem.tweethub.utils.GlideUtils;
import com.ybsystem.tweethub.utils.ResourceUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

import static com.ybsystem.tweethub.models.enums.ColumnType.*;
import static com.ybsystem.tweethub.models.enums.ImageOption.*;

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

        // Set contents
        setUserInfo(view);
        setDescription(view);
        setFollowButton(view);
        setFollowFollowerAndList(view);

        return view;
    }

    @Subscribe
    public void onEvent(UserEvent event) {
        setFollowButton(getView());
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
        profileImg.setOnClickListener(v -> {
            Activity activity = getActivity();
            Intent intent = new Intent(activity, PhotoActivity.class);
            intent.putExtra("TYPE", "PROFILE");
            intent.putExtra("PAGER_POSITION", 0);
            intent.putExtra("IMAGE_URLS", new ArrayList<>(Arrays.asList(profileURL)));
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
        });

        // Set banner
        String bannerURL = PrefSystem.getBannerByQuality(mUser);
        ImageView bannerImg = view.findViewById(R.id.image_banner);
        GlideUtils.load(bannerURL, bannerImg, NONE);
        bannerImg.setOnClickListener(v -> {
            if (bannerURL != null) {
                Activity activity = getActivity();
                Intent intent = new Intent(activity, PhotoActivity.class);
                intent.putExtra("TYPE", "BANNER");
                intent.putExtra("PAGER_POSITION", 0);
                intent.putExtra("IMAGE_URLS", new ArrayList<>(Arrays.asList(bannerURL)));
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
            }
        });

        // Set verify, lock mark
        ImageView verify = view.findViewById(R.id.image_verify_mark);
        ImageView lock = view.findViewById(R.id.image_lock_mark);
        if (mUser.isVerified()) {
            verify.setVisibility(View.VISIBLE);
        } else {
            verify.setVisibility(View.GONE);
        }
        if (mUser.isProtected()) {
            lock.setVisibility(View.VISIBLE);
        } else {
            lock.setVisibility(View.GONE);
        }
    }

    private void setDescription(View view) {
        // Change description url
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
        String text;
        int textColor;
        int background;

        // Check relation
        if (mUser.isMyself()) {
            text = "あなた";
            textColor = Color.parseColor("#FFFFFF");
            background = R.drawable.bg_rounded_purple;
        } else if (mUser.isBlocking()) {
            text = "ブロック中";
            textColor = ResourceUtils.getAccentColor();
            background = R.drawable.bg_rounded_reverse;
        } else if (mUser.isFriend() && mUser.isFollower()) {
            text = "相互フォロー";
            textColor = Color.parseColor("#FFFFFF");
            background = R.drawable.bg_rounded_purple;
        } else if (mUser.isFriend()) {
            text = "フォロー中";
            textColor = Color.parseColor("#FFFFFF");
            background = R.drawable.bg_rounded_purple;
        } else if (mUser.isFollower()) {
            text = "フォロワー";
            textColor = ResourceUtils.getAccentColor();
            background = R.drawable.bg_rounded_reverse;
        } else {
            text = "フォロー";
            textColor = ResourceUtils.getAccentColor();
            background = R.drawable.bg_rounded_reverse;
        }

        // Set follow button
        Button button = view.findViewById(R.id.button_follow);
        button.setText(text);
        button.setTextColor(textColor);
        button.setBackground(AppCompatResources.getDrawable(getContext(), background));
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
            // TODO: リスト実装
            ToastUtils.showShortToast("リストを開く");
        });
    }

}
