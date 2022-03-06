package com.ybsystem.tweetmate.utils;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.libs.glide.GlideApp;
import com.ybsystem.tweetmate.models.enums.ImageOption;

/**
 * Utils class for loading images
 */
public class GlideUtils {

    private GlideUtils() {
    }

    /**
     * Load URL image into the target view
     *
     * @param url Image URL
     * @param view Target image view
     * @param option Bitmap transform option
     */
    public static void load(String url, ImageView view, ImageOption option) {
        RequestOptions options = new RequestOptions();

        switch (option) {
            case NONE:
                options.placeholder(R.drawable.loading_thumbnail_square);
                break;
            case SQUARE:
                options.placeholder(R.drawable.loading_thumbnail_square);
                options.apply(RequestOptions.bitmapTransform(new RoundedCorners(8)));
                break;
            case CIRCLE:
                options.placeholder(R.drawable.loading_thumbnail_circle);
                options.circleCrop();
                break;
        }

        GlideApp.with(view)
                .load(url)
                .apply(options)
                .into(view);
    }

}
