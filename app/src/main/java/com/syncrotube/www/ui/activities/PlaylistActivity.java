package com.syncrotube.www.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.syncrotube.www.R;
import com.syncrotube.www.utils.MainUtil;
import com.syncrotube.www.utils.SharedPrefUtil;

import static com.syncrotube.www.utils.AppConstUtil.IS_PLAYLIST_CREATED_PREF_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.USER_PLAYLIST_ID_PREF_EXTRA;

public class PlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        MainUtil.setActionBarBackgroundColor(this,getSupportActionBar(),R.color.colorPrimary);
        MainUtil.setStatusBarBackgroundColor(this,getWindow(),R.color.bottom_black_color);
    }

    /**
     * Go to back to Main Activity after deleting user playlist
     */
    public void goBackToMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        SharedPrefUtil.clearPreferenceDataByKey(this, IS_PLAYLIST_CREATED_PREF_EXTRA);
        SharedPrefUtil.clearPreferenceDataByKey(this, USER_PLAYLIST_ID_PREF_EXTRA);
        finish();
        MainUtil.showToastMessage(this, getString(R.string.playlist_deleted_text));
        startActivity(intent);
    }
}