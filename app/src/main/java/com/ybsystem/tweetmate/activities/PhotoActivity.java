package com.ybsystem.tweetmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.pager.PhotoPagerAdapter;
import com.ybsystem.tweetmate.fragments.dialog.ListDialog;
import com.ybsystem.tweetmate.libs.photo.PhotoViewPager;
import com.ybsystem.tweetmate.usecases.DownloadUseCase;
import com.ybsystem.tweetmate.utils.MediaUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;
import com.ybsystem.tweetmate.utils.StorageUtils;

import java.util.ArrayList;

public class PhotoActivity extends ActivityBase {
    // Pager
    private ViewPager mPhotoPager;

    // Intent
    private String mType; // "PROFILE", "BANNER", "MEDIA"
    private int mSize; // 0(:small), 1(:medium), 2(:large), 3(:orig)
    private int mPagerPosition;
    private ArrayList<String> mImageURLs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view
        setContentView(R.layout.activity_photo);
        mPhotoPager = (PhotoViewPager) findViewById(R.id.view_pager);
        setContentView(mPhotoPager);

        // Init
        mType = getIntent().getStringExtra("TYPE");
        mSize = getIntent().getIntExtra("SIZE", -1);
        mPagerPosition = getIntent().getIntExtra("PAGER_POSITION", -1);
        mImageURLs = (ArrayList<String>) getIntent().getSerializableExtra("IMAGE_URLS");

        // Set pager
        setActionBarTitle();
        setPhotoPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 保存する
            case R.id.item_save:
                if (!StorageUtils.isPermitted(this)) {
                    StorageUtils.requestPermission(this);
                    return true;
                }
                showQualitySelection(true);
                return true;
            // 他サイズで表示
            case R.id.item_resize:
                showQualitySelection(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setActionBarTitle() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("フォト (" + (mPagerPosition + 1) + "/" + mImageURLs.size() + ")");
        }
    }

    private void setPhotoPager() {
        mPhotoPager.setAdapter(new PhotoPagerAdapter(mImageURLs, mSize, mType));
        mPhotoPager.setCurrentItem(mPagerPosition);
        mPhotoPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mPagerPosition = position;
                setActionBarTitle();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void showQualitySelection(boolean isDownload) {
        String[] items = {"小サイズ", "中サイズ", "大サイズ", "オリジナル"};
        ListDialog dialog = new ListDialog().newInstance(items);

        dialog.setOnItemClickListener((parent, view, position, id) -> {
            if (isDownload) {
                String url = mImageURLs.get(mPagerPosition);
                switch (mType) {
                    case "PROFILE":
                        url = MediaUtils.getProfileBySize(url, position);
                        break;
                    case "BANNER":
                        url = MediaUtils.getBannerBySize(url, position);
                        break;
                    case "MEDIA":
                        url = MediaUtils.getMediaBySize(url, position);
                        break;
                }
                DownloadUseCase.downloadImage(url);
            } else {
                Intent intent = getIntent();
                intent.putExtra("TYPE", mType);
                intent.putExtra("SIZE", position);
                intent.putExtra("PAGER_POSITION", mPagerPosition);
                intent.putExtra("IMAGE_URLS", mImageURLs);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
                ToastUtils.showShortToast(items[position] + "を表示");
            }
            dialog.dismiss();
        });

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("ListDialog") == null) {
            dialog.show(fm, "ListDialog");
        }
    }

}
