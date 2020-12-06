package com.ybsystem.tweethub.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.viewpager.widget.ViewPager;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.adapters.pager.SearchPagerAdapter;
import com.ybsystem.tweethub.adapters.pager.TrendTopicPagerAdapter;
import com.ybsystem.tweethub.application.TweetHubApp;

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

        // Set contents
        setContentView(R.layout.activity_pager);

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
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (edit.length() != 0) {
                    // Close keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // Intent to SearchActivity
                    Activity act = TweetHubApp.getActivity();
                    Intent intent = new Intent(act, SearchActivity.class);
                    intent.putExtra("SEARCH_WORD", edit.getText().toString());
                    act.startActivity(intent);
                    act.overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
                }
                return true;
            }
            return false;
        });
    }

}
