package com.ybsystem.tweetmate.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

/**
 * Utils class for accessing device storage
 */
public class StorageUtils {

    private StorageUtils() {
    }

    /**
     * Create media uri over Android 10 (API 29)
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri createMediaUri(String rootDir, ContentResolver resolver) {
        String fileName = "TweetMate_" + System.currentTimeMillis() + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.RELATIVE_PATH, rootDir + "/TweetMate");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * Create media file under Android 9 (API 28)
     */
    public static File createMediaFile(String rootDir) {
        String fileName = "TweetMate_" + System.currentTimeMillis() + ".jpg";
        File dir = new File(
                Environment.getExternalStoragePublicDirectory(rootDir), "TweetMate");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new File(dir, fileName);
    }

    /**
     * Get file path from Uri
     */
    public static String pathFromUri(Activity activity, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver()
                .query(uri, projection, null, null, null);
        // Check
        if (cursor == null || !cursor.moveToFirst()) {
            return "";
        }
        // Get path
        String path = cursor.getString(0);
        cursor.close();
        return path;
    }

    /**
     * Get file from Uri
     */
    public static File fileFromUri(Activity activity, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver()
                .query(uri, projection, null, null, null);
        // Check
        if (activity.isDestroyed()) {
            return new File("");
        }
        if (cursor == null || !cursor.moveToFirst()) {
            return new File("");
        }
        // Create file
        File file = new File(cursor.getString(0));
        cursor.close();

        return file;
    }

    /**
     * Check if storage permitted
     */
    public static boolean isPermitted(Context context) {
        int read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return read == PackageManager.PERMISSION_GRANTED
                && write == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request storage permission
     */
    public static void requestPermission(Activity activity) {
        ToastUtils.showShortToast("アクセス許可が必要です。");
        new Handler().postDelayed(() -> {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }, 1500);
    }

}
