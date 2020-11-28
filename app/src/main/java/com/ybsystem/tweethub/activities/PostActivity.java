package com.ybsystem.tweethub.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.PostFragment;
import com.ybsystem.tweethub.models.entities.EntityArray;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.GlideUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import java.util.ArrayList;

import static com.ybsystem.tweethub.models.enums.ImageOption.NONE;

public class PostActivity extends ActivityBase {

    public static ArrayList<Uri> mImageUris;
    public static final int REQUEST_GALLERY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init
        mImageUris = new ArrayList<>();

        // Set contents
        setPostFragment(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            // 下書き保存
            case R.id.item_draft:
                // Check text
                EditText edit = findViewById(R.id.edit_post);
                if (edit.length() == 0) {
                    ToastUtils.showShortToast("文字が入力されていません。");
                    return false;
                }
                // Save draft
                EntityArray<String> drafts = TweetHubApp.getMyAccount().getDrafts();
                drafts.add(edit.getText().toString());
                ToastUtils.showShortToast("下書きを保存しました。");
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check result
        if (resultCode != RESULT_OK) {
            return;
        }

        // Check request
        if (requestCode == REQUEST_GALLERY) {
            // Store uri
            Uri uri = data.getData();
            mImageUris.add(uri);

            // Get layout
            LinearLayout ll = findViewById(R.id.linear_photo);
            ImageView image = (ImageView) ll.getChildAt(mImageUris.size() - 1);
            image.setOnClickListener(v ->
                    DialogUtils.showConfirmDialog(
                            "写真をキャンセルしますか？",
                            (dialog, which) -> cancelImages()
                    )
            );
            // Show image
            GlideUtils.load(uri.toString(), image, NONE);
            ll.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
        }
    }

    private void setPostFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            PostFragment fragment = new PostFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(android.R.id.content, fragment);
            transaction.commit();
        }
    }

    private void cancelImages() {
        // Clear array
        mImageUris.clear();

        // Hide images
        findViewById(R.id.image_photo_1).setVisibility(View.GONE);
        findViewById(R.id.image_photo_2).setVisibility(View.GONE);
        findViewById(R.id.image_photo_3).setVisibility(View.GONE);
        findViewById(R.id.image_photo_4).setVisibility(View.GONE);
        findViewById(R.id.linear_photo).setVisibility(View.GONE);

        // Show message
        ToastUtils.showShortToast("写真をキャンセルしました。");
    }

}
