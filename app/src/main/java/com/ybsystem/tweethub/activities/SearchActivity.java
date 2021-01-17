package com.ybsystem.tweethub.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.adapters.pager.SearchPagerAdapter;
import com.ybsystem.tweethub.adapters.pager.TrendTopicPagerAdapter;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.EasyTweetFragment;
import com.ybsystem.tweethub.libs.eventbus.ColumnEvent;
import com.ybsystem.tweethub.models.entities.Column;
import com.ybsystem.tweethub.models.entities.ColumnArray;
import com.ybsystem.tweethub.storages.PrefSystem;
import com.ybsystem.tweethub.usecases.ClickUseCase;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;
import static com.ybsystem.tweethub.models.enums.ColumnType.SEARCH;

public class SearchActivity extends ActivityBase {

    // SearchWord
    private String mSearchWord;

    // Trend/Topic
    private ViewPager mTrendTopicPager;
    private TrendTopicPagerAdapter mTrendTopicPagerAdapter;

    // Search
    private ViewPager mSearchPager;
    private SearchPagerAdapter mSearchPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init
        mSearchWord = getIntent().getStringExtra("SEARCH_WORD");

        // Set
        setContentView(R.layout.activity_search);
        setTweetAction(savedInstanceState);

        // Check if search word exists
        if (mSearchWord == null) {
            // Trend/Topic
            setSearchEditText();
            setTrendTopicPager();
        } else {
            // Search result
            setSearchPager();
            getSupportActionBar().setTitle(mSearchWord);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mSearchWord != null) {
            getMenuInflater().inflate(R.menu.search, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // カラム追加
            case R.id.item_add_column:
                showAddDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case REBOOT_PREPARATION:
                setResult(REBOOT_PREPARATION);
                break;
        }
    }

    private void setTweetAction(Bundle savedInstanceState) {
        if (PrefSystem.isEasyTweetEnabled()) {
            // EasyTweet
            if (savedInstanceState == null) {
                Fragment fragment = new EasyTweetFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_easy_tweet, fragment);
                transaction.commit();
            }
            // Hide tweet button
            findViewById(R.id.fab).setVisibility(View.GONE);
        } else {
            // TweetButton
            findViewById(R.id.fab).setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), PostActivity.class);
                if (hasHashTag()) intent.putExtra("TWEET_SUFFIX", mSearchWord);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
            });
        }
    }

    private void setTrendTopicPager() {
        // Set trend/topic pager
        mTrendTopicPager = findViewById(R.id.pager_common);
        mTrendTopicPager.setOffscreenPageLimit(2);
        mTrendTopicPagerAdapter = new TrendTopicPagerAdapter(getSupportFragmentManager());
        mTrendTopicPager.setAdapter(mTrendTopicPagerAdapter);
    }

    private void setSearchPager() {
        // Set search pager
        mSearchPager = findViewById(R.id.pager_common);
        mSearchPager.setOffscreenPageLimit(3);
        mSearchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager(), mSearchWord);
        mSearchPager.setAdapter(mSearchPagerAdapter);
    }

    private void setSearchEditText() {
        // Set custom actionbar
        ViewGroup root = findViewById(android.R.id.content);
        View view = getLayoutInflater().inflate(R.layout.actionbar_search, root, false);
        getSupportActionBar().setCustomView(view);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        // Set edit text
        EditText edit = view.findViewById(R.id.edit_search);
        edit.setOnKeyListener((v, keyCode, event) -> {
            // When pressed enter
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (edit.length() != 0) {
                    // Close keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // Intent to SearchActivity
                    ClickUseCase.searchWord(edit.getText().toString());
                }
                return true;
            }
            return false;
        });
    }

    private void showAddDialog() {
        if (TweetHubApp.getMyAccount().getColumns().size() >= 10) {
            ToastUtils.showShortToast("これ以上追加できません。");
            return;
        }
        DialogUtils.showConfirmDialog(
                "検索結果をカラムに追加しますか？",
                (dialog, which) -> {
                    // Add column
                    ColumnArray<Column> columns = TweetHubApp.getMyAccount().getColumns();
                    boolean isSucceeded = columns.add(
                            new Column(mSearchWord.hashCode(), mSearchWord, SEARCH, false));
                    // Check
                    if (!isSucceeded) {
                        return;
                    }
                    // Success
                    ToastUtils.showShortToast("「" + mSearchWord + "」をカラムに追加しました。");
                    EventBus.getDefault().postSticky(new ColumnEvent());
                    setResult(REBOOT_PREPARATION);
                }
        );
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
