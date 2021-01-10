package com.ybsystem.tweethub.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.chrisbanes.photoview.PhotoView;
import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.dialog.ListDialog;
import com.ybsystem.tweethub.libs.photo.PhotoViewPager;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.GlideUtils;
import com.ybsystem.tweethub.utils.ToastUtils;
import com.ybsystem.tweethub.utils.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import static com.ybsystem.tweethub.models.enums.ImageOption.*;

public class PhotoActivity extends ActivityBase {

    private ViewPager mPhotoPager;

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

        // Set contents
        setPhotoPager();
        getSupportActionBar().setTitle("フォト (" + (mPagerPosition + 1) + "/" + mImageURLs.size() + ")");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.item_save: // 保存する
                // Check permission
                if (!StorageUtils.isPermitted(this)) {
                    StorageUtils.requestPermission(this);
                    return false;
                }
                showQualitySelection(true);
                return true;
            case R.id.item_resize: // 他サイズで表示
                showQualitySelection(false);
                return true;
            default:
                return false;
        }
    }

    private void setPhotoPager() {
        mPhotoPager.setAdapter(new PhotoPagerAdapter());
        mPhotoPager.setCurrentItem(mPagerPosition);
        mPhotoPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mPagerPosition = position;
                getSupportActionBar().setTitle("フォト (" + (mPagerPosition + 1) + "/" + mImageURLs.size() + ")");
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private class PhotoPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImageURLs.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());

            String url = mImageURLs.get(position);
            if (mSize != -1) {
                switch (mType) {
                    case "PROFILE":
                        url = getProfileBySize(url, mSize);
                        break;
                    case "BANNER":
                        url = getBannerBySize(url, mSize);
                        break;
                    case "MEDIA":
                        url = getMediaBySize(url, mSize);
                        break;
                }
            }
            GlideUtils.load(url, photoView, NONE);

            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private void showQualitySelection(boolean isDownload) {
        String[] items = {"小サイズ", "中サイズ", "大サイズ", "オリジナル"};
        ListDialog dialog = new ListDialog().newInstance(items);

        dialog.setOnItemClickListener((parent, view, position, id) -> {
            if (isDownload) {
                String url = mImageURLs.get(mPagerPosition);
                switch (mType) {
                    case "PROFILE":
                        url = getProfileBySize(url, position);
                        break;
                    case "BANNER":
                        url = getBannerBySize(url, position);
                        break;
                    case "MEDIA":
                        url = getMediaBySize(url, position);
                        break;
                }
                new DownloadTask().execute(url);
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

        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentByTag("ListDialog") == null) {
            dialog.show(manager, "ListDialog");
        }
    }

    private static class DownloadTask extends AsyncTask<String, Void, Exception> {
        private String path;

        @Override
        protected void onPreExecute() {
            DialogUtils.showProgressDialog("通信中...", TweetHubApp.getActivity());
        }

        @Override
        protected Exception doInBackground(String... url) {
            FileOutputStream out = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Over Android 10
                    ContentResolver resolver = TweetHubApp.getActivity().getContentResolver();
                    Uri uri = StorageUtils.createMediaUri(Environment.DIRECTORY_PICTURES, resolver);
                    out = new FileOutputStream(resolver.openFileDescriptor(uri, "w").getFileDescriptor());
                    path = StorageUtils.pathFromUri(TweetHubApp.getActivity(), uri);
                } else {
                    // Under Android 9
                    File file = StorageUtils.createMediaFile(Environment.DIRECTORY_PICTURES);
                    out = new FileOutputStream(file);
                    path = file.getAbsolutePath();
                }
                // Write
                InputStream input = new java.net.URL(url[0]).openStream();
                BitmapFactory.decodeStream(input)
                        .compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();

                return null;
            } catch (Exception e) {
                return e;
            } finally {
                // Close
                try {
                    if (out != null) out.close();
                } catch (Exception ignored) {}
            }
        }

        @Override
        protected void onPostExecute(Exception e) {
            if (e == null) {
                ToastUtils.showShortToast("画像を保存しました。");
                ToastUtils.showLongToast("保存場所：" + path);
            } else {
                ToastUtils.showShortToast("エラーが発生しました...");
                ToastUtils.showShortToast(e.getMessage());
            }
            DialogUtils.dismissProgressDialog();
        }
    }

    private String getProfileBySize(String url, int size) {
        String extension = url.substring(url.lastIndexOf('.'));
        url = url.substring(0, url.lastIndexOf('_'));
        switch (size) {
            case 0:
                return url + "_mini" + extension;
            case 1:
                return url + "_normal" + extension;
            case 2:
                return url + "_bigger" + extension;
            case 3:
                return url + extension;
            default:
                return null;
        }
    }

    private String getBannerBySize(String url, int size) {
        url = url.substring(0, url.lastIndexOf('/'));
        switch (size) {
            case 0:
                return url + "/mobile";
            case 1:
                return url + "/mobile_retina";
            case 2:
                return url + "/web_retina";
            case 3:
                return url + "/1500x500";
            default:
                return null;
        }
    }

    private String getMediaBySize(String url, int size) {
        url = url.substring(0, url.lastIndexOf(':'));
        switch (size) {
            case 0:
                return url + ":small";
            case 1:
                return url + ":medium";
            case 2:
                return url + ":large";
            case 3:
                return url + ":orig";
            default:
                return null;
        }
    }

}
