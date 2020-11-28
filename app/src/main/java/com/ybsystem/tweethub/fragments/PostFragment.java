package com.ybsystem.tweethub.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.PostActivity;
import com.ybsystem.tweethub.adapters.holder.TweetRow;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.dialog.ListDialog;
import com.ybsystem.tweethub.models.entities.EntityArray;
import com.ybsystem.tweethub.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUserMentionEntity;
import com.ybsystem.tweethub.storages.PrefTheme;
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
    private String mTweetWord;
    private TwitterStatus mReplyStatus;
    private TwitterStatus mQuoteStatus;

    // Uri
    private List<Uri> mImageUris;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Init
        Intent intent = getActivity().getIntent();
        mTweetWord = intent.getStringExtra("TWEET_WORD");
        mReplyStatus = (TwitterStatus) intent.getSerializableExtra("REPLY_STATUS");
        mQuoteStatus = (TwitterStatus) intent.getSerializableExtra("QUOTE_STATUS");
        mImageUris = ((PostActivity) getActivity()).mImageUris;

        // Set contents
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
        tweetRow.setThumbnail();
        tweetRow.setAbsoluteTime();
        tweetRow.setVia();
        tweetRow.setRtFavCount();
        tweetRow.setRetweetedBy();
        tweetRow.setQuoteTweet();
        tweetRow.setMarks();

        // Check theme setting
        if (PrefTheme.isCustomThemeEnabled()) {
            tweetRow.setCustomBackgroundColor();
        }
    }

    private void setPostEdit(View view) {
        // Init
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
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // Check word
        if (mTweetWord != null) {
            mTweetWord += " ";
            mPostEdit.setText(mTweetWord);
            mPostEdit.setSelection(mTweetWord.length());
        }

        // Check reply
        if (mReplyStatus != null) {
            actionBar.setTitle("返信");
            // TODO: ロジックがカオス
            String str = "@" + mReplyStatus.getUser().getScreenName() + " ";
            TwitterUserMentionEntity[] entities = mReplyStatus.getUserMentionEntities();
            if (entities != null) {
                for (TwitterUserMentionEntity entity : entities) {
                    String my = TweetHubApp.getMyUser().getScreenName();
                    if (!entity.getScreenName().equals(my)) {
                        String mention = "@" + entity.getScreenName() + " ";
                        str = str.contains(mention) ? str : (str + mention);
                    }
                }
            }
            mPostEdit.setText(str);
            mPostEdit.setSelection(str.length());
        }

        // Check quote
        if (mQuoteStatus != null) {
            actionBar.setTitle("引用ツイート");
            mPostEdit.setHint("コメントを追加");
        }
    }

    private void setPostButton(View view) {
        // Post button
        view.findViewById(R.id.button_post).setOnClickListener(v -> {
            // Check text count
            int count = Integer.parseInt(mTextCount.getText().toString());
            if (count == 140 && mImageUris.size() == 0) {
                return;
            }
            // Post tweet
            String quoteURL = mQuoteStatus != null
                    ? " https://twitter.com/" + mQuoteStatus.getUser().getScreenName() + "/status/" + mQuoteStatus.getId()
                    : "";
            StatusUpdate update = new StatusUpdate(
                    mPostEdit.getText().toString() + quoteURL
            );
            update.setInReplyToStatusId(mReplyStatus != null ? mReplyStatus.getId() : -1);
            TwitterUseCase.post(update, mImageUris);
            // Save hashtag
            EntityArray<String> hashtags = TweetHubApp.getMyAccount().getHashtags();
            String regex = "[#＃](w*[一-龠_ぁ-ん_ァ-ヴーａ-ｚＡ-Ｚa-zA-Z0-9]+|[a-zA-Z0-9_]+|[a-zA-Z0-9_]w*)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(mPostEdit.getText());
            while (matcher.find()) {
                if (hashtags.size() >= 5) {
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
            if (drafts.size() == 0) {
                ToastUtils.showShortToast("下書きはありません。");
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
                DialogUtils.showConfirmDialog(
                        "下書きを削除しますか？",
                        (dialog1, which) -> {
                            drafts.remove(position);
                            ToastUtils.showShortToast("下書きを削除しました。");
                        }
                );
                dialog.dismiss();
                return true;
            });
            // Show dialog
            FragmentManager manager = getActivity().getSupportFragmentManager();
            if (manager.findFragmentByTag("DraftDialog") == null) {
                dialog.show(manager, "DraftDialog");
            }
        });
    }

    private void setHashtagButton(View view) {
        // Hashtag button
        view.findViewById(R.id.button_hashtag).setOnClickListener(v -> {
            // Check hashtag
            EntityArray<String> hashtags = TweetHubApp.getMyAccount().getHashtags();
            if (hashtags.size() == 0) {
                ToastUtils.showShortToast("ハッシュタグ履歴はありません。");
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
                DialogUtils.showConfirmDialog(
                        "ハッシュタグを削除しますか？",
                        (dialog1, which) -> {
                            hashtags.remove(position);
                            ToastUtils.showShortToast("ハッシュタグを削除しました。");
                        }
                );
                dialog.dismiss();
                return true;
            });
            // Show dialog
            FragmentManager manager = getActivity().getSupportFragmentManager();
            if (manager.findFragmentByTag("HashtagDialog") == null) {
                dialog.show(manager, "HashtagDialog");
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
            ToastUtils.showShortToast("カメラを起動できません。");
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
