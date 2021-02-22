package com.ybsystem.tweethub.adapters.pager;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.github.chrisbanes.photoview.PhotoView;
import com.ybsystem.tweethub.utils.GlideUtils;
import com.ybsystem.tweethub.utils.MediaUtils;

import java.util.ArrayList;

import static com.ybsystem.tweethub.models.enums.ImageOption.NONE;

public class PhotoPagerAdapter extends PagerAdapter {

    private ArrayList<String> mImageURLs;
    private int mSize;
    private String mType;

    public PhotoPagerAdapter(ArrayList<String> imageURLs,
                             int size, String type) {
        this.mImageURLs = imageURLs;
        this.mSize = size;
        this.mType = type;
    }

    @Override
    public int getCount() {
        return mImageURLs.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        // Prepare
        String url = mImageURLs.get(position);
        PhotoView photoView = new PhotoView(container.getContext());

        // Load image
        if (mSize != -1) {
            switch (mType) {
                case "PROFILE":
                    url = MediaUtils.getProfileBySize(url, mSize);
                    break;
                case "BANNER":
                    url = MediaUtils.getBannerBySize(url, mSize);
                    break;
                case "MEDIA":
                    url = MediaUtils.getMediaBySize(url, mSize);
                    break;
            }
        }
        GlideUtils.load(url, photoView, NONE);

        // Add view
        final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
        container.addView(photoView, MATCH_PARENT, MATCH_PARENT);

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
