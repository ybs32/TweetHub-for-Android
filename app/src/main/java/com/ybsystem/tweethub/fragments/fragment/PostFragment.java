package com.ybsystem.tweethub.fragments.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.PostActivity;
import com.ybsystem.tweethub.adapters.holder.TweetRow;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.dialog.ListDialog;
import com.ybsystem.tweethub.fragments.dialog.NoticeDialog;
import com.ybsystem.tweethub.models.entities.EntityArray;
import com.ybsystem.tweethub.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUserMentionEntity;
import com.ybsystem.tweethub.usecases.TwitterUseCase;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ResourceUtils;
import com.ybsystem.tweethub.utils.ToastUtils;
import com.ybsystem.tweethub.utils.StorageUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.StatusUpdate;

import static com.ybsystem.tweethub.activities.PostActivity.*;

public class PostFragment extends Fragment {
    // Edit
    private EditText mPostEdit;
    private TextView mTextCount;

    // Intent data
    private String mTweetPrefix;
    private String mTweetSuffix;
    private TwitterStatus mReplyStatus;
    private TwitterStatus mQuoteStatus;

    // Others
    private List<Uri> mImageUris;
    private ActionBar mActionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Init
        Intent intent = getActivity().getIntent();
        mTweetPrefix = intent.getStringExtra("TWEET_PREFIX");
        mTweetSuffix = intent.getStringExtra("TWEET_SUFFIX");
        mReplyStatus = (TwitterStatus) intent.getSerializableExtra("REPLY_STATUS");
        mQuoteStatus = (TwitterStatus) intent.getSerializableExtra("QUOTE_STATUS");
        mImageUris = ((PostActivity) getActivity()).mImageUris;
        mActionBar = ((PostActivity) getActivity()).getSupportActionBar();

        // Set
        setTweet(view);
        setPostEdit(view);
        setIntentData();

        // Set buttons
        setPostButton(view);
        setDraftButton(view);
        setHashtagButton(view);
        setCameraGalleryButtons(view);

        return view;
    }

    private void setTweet(View view) {
        // Get status
        TwitterStatus status = null;
        status = mReplyStatus != null ? mReplyStatus : status;
        status = mQuoteStatus != null ? mQuoteStatus : status;

        // Check if status exist
        if (status == null) {
            view.findViewById(R.id.scroll_tweet).setVisibility(View.GONE);
            return;
        }

        // Render tweet
        TweetRow tweetRow = new TweetRow(view);
        tweetRow.hideOptionalFields();
        tweetRow.setStatus(status);
        tweetRow.setUserName();
        tweetRow.setScreenName();
        tweetRow.setUserIcon();
        tweetRow.setTweetText();
        tweetRow.setRelativeTime();
        tweetRow.setAbsoluteTime();
        tweetRow.setVia();
        tweetRow.setRtFavCount();
        tweetRow.setRetweetedBy();
        tweetRow.setMarks();
        tweetRow.setThumbnail();
        tweetRow.setQuoteTweet();
        tweetRow.setBackgroundColor();
    }

    private void setPostEdit(View view) {
        // Find
        mTextCount = view.findViewById(R.id.text_count);
        mPostEdit = view.findViewById(R.id.edit_post);

        // When edit text changed
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

    private void setIntentData() {
        // Check prefix
        if (mTweetPrefix != null) {
            mTweetPrefix += " ";
            mPostEdit.setText(mTweetPrefix);
            mPostEdit.setSelection(mTweetPrefix.length());
        }

        // Check suffix
        if (mTweetSuffix != null) {
            mPostEdit.setText("\n" + mTweetSuffix);
        }

        // Check reply
        if (mReplyStatus != null) {
            // Append reply user
            String replyScreen = mReplyStatus.getUser().getScreenName();
            String str = "@" + replyScreen + " ";

            // Append mention user
            for (TwitterUserMentionEntity mention : mReplyStatus.getUserMentionEntities()) {
                if (mention.getScreenName().equals(replyScreen)) {
                    continue;
                }
                String myScreen = TweetHubApp.getMyUser().getScreenName();
                if (mention.getScreenName().equals(myScreen)) {
                    continue;
                }
                str = str.concat("@" + mention.getScreenName() + " ");
            }
            // Set text
            mPostEdit.setText(str);
            mPostEdit.setSelection(str.length());
            mActionBar.setTitle("返信");
        }

        // Check quote
        if (mQuoteStatus != null) {
            mPostEdit.setHint("コメントを追加");
            mActionBar.setTitle("引用ツイート");
        }
    }

    private void setPostButton(View view) {
        // Post button
        view.findViewById(R.id.button_post).setOnClickListener(v -> {

            // Check text length
            int length = mPostEdit.getText().length();
            if (length == 0 && mImageUris.isEmpty()) {
                return;
            }
            // Prepare data
            long replyId = mReplyStatus != null ? mReplyStatus.getId() : -1;
            String quoteURL = mQuoteStatus != null
                    ? " https://twitter.com/" + mQuoteStatus.getUser().getScreenName() + "/status/" + mQuoteStatus.getId()
                    : "";

            // Create tweet, then post
            StatusUpdate update = new StatusUpdate(
                    mPostEdit.getText().toString() + quoteURL
            );
            update.setInReplyToStatusId(replyId);
            TwitterUseCase.post(update, mImageUris);

            // Save hashtag
            EntityArray<String> hashtags = TweetHubApp.getMyAccount().getHashtags();
            String regex = "[#＃](w*[一-龠_ぁ-ん_ァ-ヴーａ-ｚＡ-Ｚa-zA-Z0-9]+|[a-zA-Z0-9_]+|[a-zA-Z0-9_]w*)";
            Matcher matcher = Pattern.compile(regex).matcher(mPostEdit.getText());
            while (matcher.find()) {
                if (hashtags.size() >= 8) {
                    hashtags.remove(0);
                }
                hashtags.add(matcher.group());
            }
        });
    }

    private void setDraftButton(View view) {
        // Draft button
        view.findViewById(R.id.button_draft).setOnClickListener(v -> {

            // Check drafts
            EntityArray<String> drafts = TweetHubApp.getMyAccount().getDrafts();
            if (drafts.isEmpty()) {
                NoticeDialog dialog = new NoticeDialog().newInstance("下書きはありません。");
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.findFragmentByTag("NoticeDialog") == null) {
                    dialog.show(fm, "NoticeDialog");
                }
                return;
            }

            // Create dialog
            String[] items = new String[drafts.size()];
            for (int i = 0; i < drafts.size(); i++) {
                items[i] = drafts.get(i);
            }
            ListDialog dialog = new ListDialog().newInstance(items);

            dialog.setOnItemClickListener((parent, v1, position, id) -> {
                String text = mPostEdit.getText() + drafts.get(position) + " ";
                mPostEdit.setText(text);
                mPostEdit.setSelection(text.length());
                dialog.dismiss();
            });
            dialog.setOnItemLongClickListener((parent, v1, position, id) -> {
                DialogUtils.showConfirm(
                        "下書きを削除しますか？",
                        (d, which) -> {
                            drafts.remove(position);
                            ToastUtils.showShortToast("下書きを削除しました。");
                        }
                );
                dialog.dismiss();
                return true;
            });

            // Show dialog
            FragmentManager fm = getActivity().getSupportFragmentManager();
            if (fm.findFragmentByTag("DraftDialog") == null) {
                dialog.show(fm, "DraftDialog");
            }
        });
    }

    private void setHashtagButton(View view) {
        // Hashtag button
        view.findViewById(R.id.button_hashtag).setOnClickListener(v -> {

            // Check hashtag
            EntityArray<String> hashtags = TweetHubApp.getMyAccount().getHashtags();
            if (hashtags.isEmpty()) {
                NoticeDialog dialog = new NoticeDialog().newInstance("ハッシュタグ履歴はありません。");
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.findFragmentByTag("NoticeDialog") == null) {
                    dialog.show(fm, "NoticeDialog");
                }
                return;
            }

            // Create dialog
            String[] items = new String[hashtags.size()];
            for (int i = 0; i < hashtags.size(); i++) {
                items[i] = hashtags.get(i);
            }
            ListDialog dialog = new ListDialog().newInstance(items);

            dialog.setOnItemClickListener((parent, v1, position, id) -> {
                String text = mPostEdit.getText() + hashtags.get(position) + " ";
                mPostEdit.setText(text);
                mPostEdit.setSelection(text.length());
                dialog.dismiss();
            });
            dialog.setOnItemLongClickListener((parent, v1, position, id) -> {
                DialogUtils.showConfirm(
                        "ハッシュタグを削除しますか？",
                        (d, which) -> {
                            hashtags.remove(position);
                            ToastUtils.showShortToast("ハッシュタグを削除しました。");
                        }
                );
                dialog.dismiss();
                return true;
            });

            // Show dialog
            FragmentManager fm = getActivity().getSupportFragmentManager();
            if (fm.findFragmentByTag("HashtagDialog") == null) {
                dialog.show(fm, "HashtagDialog");
            }
        });
    }

    private void setCameraGalleryButtons(View view) {
        // Camera button
        view.findViewById(R.id.button_camera).setOnClickListener(v -> {
            if (!isPicAvailable()) {
                return;
            }
            // Coming soon...
            DialogUtils.showProgress("読み込み中...", getContext());
            new Handler().postDelayed(() -> {
                DialogUtils.dismissProgress();
                ToastUtils.showShortToast("カメラを起動できません。");
            }, 1500);
        });

        // Gallery button
        view.findViewById(R.id.button_gallery).setOnClickListener(v -> {
            if (!isPicAvailable()) {
                return;
            }
            // Open gallery
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            getActivity().startActivityForResult(intent, REQUEST_GALLERY);
        });
    }

    private boolean isPicAvailable() {
        // Check permission
        if (!StorageUtils.isPermitted(getActivity())) {
            StorageUtils.requestPermission(getActivity());
            return false;
        }
        // Check picture count
        if (mImageUris.size() >= 4) {
            ToastUtils.showShortToast("これ以上選択できません。");
            return false;
        }
        return true;
    }

}
