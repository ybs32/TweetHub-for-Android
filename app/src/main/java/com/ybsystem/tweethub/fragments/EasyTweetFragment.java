package com.ybsystem.tweethub.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.PostActivity;
import com.ybsystem.tweethub.activities.ProfileActivity;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.libs.eventbus.PostEvent;
import com.ybsystem.tweethub.models.entities.EntityArray;
import com.ybsystem.tweethub.models.enums.ImageOption;
import com.ybsystem.tweethub.storages.PrefAppearance;
import com.ybsystem.tweethub.storages.PrefSystem;
import com.ybsystem.tweethub.usecases.TwitterUseCase;
import com.ybsystem.tweethub.utils.GlideUtils;
import com.ybsystem.tweethub.utils.ResourceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.StatusUpdate;

public class EasyTweetFragment extends Fragment {
    // Edit
    private EditText mPostEdit;
    private TextView mTextCount;

    // Only SearchActivity
    private String mSearchWord;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_easy_tweet, container, false);

        // Init
        mSearchWord = getActivity().getIntent().getStringExtra("SEARCH_WORD");

        // Set contents
        setUserIcon(view);
        setPostEdit(view);
        setPostButton(view);

        return view;
    }

    @Subscribe
    public void onEvent(PostEvent event) {
        // Clear tweet
        mPostEdit.getText().clear();
        mPostEdit.clearFocus();
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

    private void setUserIcon(View view) {
        // Set image
        String url = PrefSystem.getProfileThumbByQuality(TweetHubApp.getMyUser());
        ImageView icon = view.findViewById(R.id.image_user_icon);
        ImageOption option = ImageOption.toEnum(PrefAppearance.getUserIconStyle());
        GlideUtils.load(url, icon, option);

        // Set click listener
        icon.setOnClickListener(v -> {
            Activity act = getActivity();
            if (act.getLocalClassName().equals("activities.MainActivity")) {
                // MainActivity
                DrawerLayout drawer = act.findViewById(R.id.drawer_layout);
                int g = GravityCompat.START;
                if (drawer.isDrawerOpen(g)) {
                    drawer.closeDrawer(g);
                } else {
                    drawer.openDrawer(g);
                }
            } else {
                // Others
                Intent intent = new Intent(act, ProfileActivity.class);
                intent.putExtra("USER_ID", TweetHubApp.getMyUser().getId());
                startActivityForResult(intent, 0);
                act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
            }
        });
    }

    private void setPostEdit(View view) {
        // Init
        mTextCount = view.findViewById(R.id.text_count);
        mPostEdit = view.findViewById(R.id.edit_post);

        // When focused
        mPostEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // If text is empty, then set HashTag
                if (mPostEdit.getText().length() == 0 && hasHashTag()) {
                    mPostEdit.setText("\n" + mSearchWord);
                    new Handler().postDelayed(() -> mPostEdit.setSelection(0), 0);
                }
            }
        });

        // When text changed
        mPostEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.toString().length();
                int color = length > 140
                        ? Color.RED : ResourceUtils.getTextColor();
                // Change count
                mTextCount.setText(Integer.toString(140 - length));
                mTextCount.setTextColor(color);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }

    private void setPostButton(View view) {
        // When clicked
        view.findViewById(R.id.button_post).setOnClickListener(v -> {
            // Check text length
            int length = mPostEdit.getText().length();
            if (length == 0) {
                intentToPost(v.getContext());
                return;
            }
            // Create tweet, then post
            StatusUpdate update = new StatusUpdate(
                    mPostEdit.getText().toString()
            );
            TwitterUseCase.post(update, new ArrayList<>());

            // Save hashtag
            EntityArray<String> hashtags = TweetHubApp.getMyAccount().getHashtags();
            String regex = "[#＃](w*[一-龠_ぁ-ん_ァ-ヴーａ-ｚＡ-Ｚa-zA-Z0-9]+|[a-zA-Z0-9_]+|[a-zA-Z0-9_]w*)";
            Matcher matcher = Pattern.compile(regex).matcher(mPostEdit.getText());
            while (matcher.find()) {
                if (hashtags.size() >= 5) {
                    hashtags.remove(0);
                }
                hashtags.add(matcher.group());
            }
        });

        // When long clicked
        view.findViewById(R.id.button_post).setOnLongClickListener(v -> {
            intentToPost(v.getContext());
            return true;
        });
    }

    private void intentToPost(Context context) {
        Intent intent = new Intent(context, PostActivity.class);
        if (hasHashTag()) intent.putExtra("TWEET_SUFFIX", mSearchWord);
        startActivityForResult(intent, 0);
        getActivity().overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
    }

    private boolean hasHashTag() {
        // Check word
        if (mSearchWord == null) {
            return false;
        }
        if (!mSearchWord.startsWith("#") && !mSearchWord.startsWith("＃")) {
            return false;
        }
        return true;
    }

}
