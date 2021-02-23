package com.syncrotube.www.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.syncrotube.www.R;
import com.syncrotube.www.utils.MainUtil;
import com.syncrotube.www.utils.PhotoUtil;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

import static com.syncrotube.www.utils.AppConstUtil.MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE;
import static com.syncrotube.www.utils.AppConstUtil.PROFILE_PHOTO_PATH_EXTRA;

public class ViewPhotoActivity extends AppCompatActivity {
    private Intent mIntent;
    private PhotoEditor mPhotoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        MainUtil.setActionBarBackgroundColor(this,getSupportActionBar(),R.color.colorPrimary);
        MainUtil.setStatusBarBackgroundColor(this,getWindow(),R.color.bottom_black_color);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.profile_photo_label_text);
        }

        // check if app is in night mode
        if (MainUtil.isAppInNightMode(this)) {
            MainUtil.setStatusBarBackgroundColor(this,getWindow(),R.color.colorPrimaryDark);
        }

        mIntent = getIntent();
        PhotoEditorView photoEditorView = findViewById(R.id.photoEditorView);
        if (mIntent != null) {
            if (mIntent.hasExtra(PROFILE_PHOTO_PATH_EXTRA)) {
                Uri photoIntentUri = Uri.parse(mIntent.getStringExtra(PROFILE_PHOTO_PATH_EXTRA));
                mPhotoEditor = PhotoUtil.photoEditorView(this,photoIntentUri,photoEditorView);
            }
        }
    }

    public void saveEditedPhoto(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
            return;
        }
        PhotoUtil.savePhotoFile(this, mPhotoEditor, isSuccessful -> {
            if (isSuccessful) {
                // photo saved to device successfully
                returnBackIntent();
            } else {
                // failed to saved
                MainUtil.showToastMessage(getApplicationContext(), getString(R.string.failed_to_save_photo_text));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay!
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    PhotoUtil.savePhotoFile(this, mPhotoEditor, isSuccessful -> {
                        if (isSuccessful) {
                            // photo saved to device successfully
                            returnBackIntent();
                        } else {
                            // failed to saved
                            MainUtil.showToastMessage(getApplicationContext(), getString(R.string.failed_to_save_photo_text));
                        }
                    });
                }

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                MainUtil.showToastMessage(getApplicationContext(), getString(R.string.needs_access_storage_warning));
            }
        }
    }

    private void returnBackIntent() {
        mIntent = new Intent();
        String photoFilePath = PhotoUtil.getPhotoFilePath();
        mIntent.putExtra(PROFILE_PHOTO_PATH_EXTRA, photoFilePath);
        setResult(ViewPhotoActivity.RESULT_OK, mIntent);
        finish();
    }
}