package com.ybsystem.tweethub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.ybsystem.tweethub.usecases.SearchUseCase;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.KeyboardUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;
import static com.ybsystem.tweethub.models.enums.ColumnType.SEARCH_SINGLE;

public class SearchActivity extends ActivityBase {

    // Menu
    private Menu mMenu;

    // Trend/Topic
    private EditText mSearchEdit;
    private ViewPager mTrendTopicPager;
    private TrendTopicPagerAdapter mTrendTopicPagerAdapter;

    // Search
    private String mSearchWord;
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
            setTrendTopicPager();
            setSearchEditActionBar();
        } else {
            // Search result
            setSearchPager();
            getSupportActionBar().setTitle(mSearchWord);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.search, menu);
        updateMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 保存した検索
            case R.id.item_saved_search:
                SearchUseCase.showSavedSearch();
                return true;
            // 検索を保存
            case R.id.item_save_search:
                SearchUseCase.saveSearch(mSearchWord);
                return true;
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

    private void updateMenu() {
        MenuItem savedSearch = mMenu.findItem(R.id.item_saved_search);
        MenuItem saveSearch = mMenu.findItem(R.id.item_save_search);
        MenuItem addColumn = mMenu.findItem(R.id.item_add_column);

        if (mSearchWord == null) {
            saveSearch.setVisible(false);
            addColumn.setVisible(false);
        } else {
            savedSearch.setVisible(false);
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

    private void setSearchEditActionBar() {
        // Set custom actionbar
        ViewGroup root = findViewById(android.R.id.content);
        View view = getLayoutInflater().inflate(R.layout.edit_search_bar, root, false);
        getSupportActionBar().setCustomView(view);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        // Set edit text
        mSearchEdit = view.findViewById(R.id.edit_search);
        mSearchEdit.setOnKeyListener((v, keyCode, event) -> {
            // When pressed enter
            if (mSearchEdit.length() != 0 && keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                // Close keyboard
                KeyboardUtils.closeKeyboard(v);

                // Intent to SearchActivity
                ClickUseCase.searchWord(mSearchEdit.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void showAddDialog() {
        if (TweetHubApp.getMyAccount().getColumns().size() >= 8) {
            ToastUtils.showShortToast("これ以上追加できません。");
            return;
        }
        DialogUtils.showConfirmDialog(
                "カラムに追加しますか？",
                (dialog, which) -> {
                    // Add column
                    ColumnArray<Column> columns = TweetHubApp.getMyAccount().getColumns();
                    boolean isSucceeded = columns.add(
                            new Column(mSearchWord.hashCode(), mSearchWord, SEARCH_SINGLE, false));
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
