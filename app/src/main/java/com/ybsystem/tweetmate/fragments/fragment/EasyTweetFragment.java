package com.ybsystem.tweetmate.fragments.fragment;

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

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.activities.PostActivity;
import com.ybsystem.tweetmate.activities.ProfileActivity;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.libs.eventbus.PostEvent;
import com.ybsystem.tweetmate.models.entities.EntityArray;
import com.ybsystem.tweetmate.models.enums.ImageOption;
import com.ybsystem.tweetmate.storages.PrefAppearance;
import com.ybsystem.tweetmate.storages.PrefSystem;
import com.ybsystem.tweetmate.usecases.StatusUseCase;
import com.ybsystem.tweetmate.utils.GlideUtils;
import com.ybsystem.tweetmate.utils.KeyboardUtils;
import com.ybsystem.tweetmate.utils.ResourceUtils;

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

        // Set
        setUserIcon(view);
        setPostEdit(view);
        setPostButton(view);

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
    public void onEvent(PostEvent event) {
        // Clear tweet
        mPostEdit.getText().clear();

        // Close keyboard
        View focus = getActivity().getCurrentFocus();
        if (focus != null) {
            KeyboardUtils.closeKeyboard(focus);
            focus.clearFocus();
        }
    }

    private void setUserIcon(View view) {
        // Set icon image
        String url = PrefSystem.getProfileThumbByQuality(TweetMateApp.getMyUser());
        ImageView icon = view.findViewById(R.id.image_user_icon);
        ImageOption option = ImageOption.toEnum(PrefAppearance.getUserIconStyle());
        GlideUtils.load(url, icon, option);

        // When icon clicked
        icon.setOnClickListener(v -> {
            Activity act = getActivity();
            if (TweetMateApp.getActivityName(act.getLocalClassName()).equals("MainActivity")) {
                // If MainActivity
                DrawerLayout d = act.findViewById(R.id.drawer_layout);
                int s = GravityCompat.START;
                if (d.isDrawerOpen(s)) {
                    d.closeDrawer(s);
                } else {
                    d.openDrawer(s);
                }
            } else {
                // If other activities
                Intent intent = new Intent(act, ProfileActivity.class);
                intent.putExtra("USER_ID", TweetMateApp.getMyUser().getId());
                startActivityForResult(intent, 0);
                act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
            }
        });
    }

    private void setPostEdit(View view) {
        // Init
        mTextCount = view.findViewById(R.id.text_count);
        mPostEdit = view.findViewById(R.id.edit_post);

        // When edit focused
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
                intentToPostActivity(v.getContext());
                return;
            }
            // Create tweet and post
            StatusUpdate update = new StatusUpdate(
                    mPostEdit.getText().toString()
            );
            StatusUseCase.post(update, new ArrayList<>());

            // Save hashtag
            EntityArray<String> hashtags = TweetMateApp.getMyAccount().getHashtags();
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
            intentToPostActivity(v.getContext());
            return true;
        });
    }

    private void intentToPostActivity(Context context) {
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
