package com.syncrotube.www.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.customview.widget.Openable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.syncrotube.www.R;
import com.syncrotube.www.models.PlaylistModel;
import com.syncrotube.www.utils.MainUtil;
import com.syncrotube.www.utils.SharedPrefUtil;

import org.parceler.Parcels;

import static com.syncrotube.www.utils.AppConstUtil.IS_PLAYLIST_CREATED_PREF_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.PLAYLIST_ITEM_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.USER_NAME_PREF_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.USER_PLAYLIST_ID_PREF_EXTRA;

public class MainActivity extends AppCompatActivity {
    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        MainUtil.setActionBarBackgroundColor(this,getSupportActionBar(),R.color.colorPrimary);
        MainUtil.setStatusBarBackgroundColor(this,getWindow(),R.color.bottom_black_color);

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this,mNavController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, (Openable) null)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String[] toEmails = {"syncrotube.app.base@gmail.com"};
        if (item.getItemId() == R.id.feedback_item) {
            // feedback
            MainUtil.sendUserFeedback(this, toEmails);
        } else if (item.getItemId() == R.id.report_issue_item) {
            // report an issue
            MainUtil.sendUserIssueReport(this, toEmails);
        } else if (item.getItemId() == R.id.share_app_item) {
            // share app
            MainUtil.shareApp(this);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Go to Playlist Activity
     * @param userPlaylistId :: playlist id to send as extra
     */
    public void goToPlaylistActivity(String userPlaylistId, String userName, boolean wasPlaylistJustCreated) {
        Intent intent = new Intent(this,PlaylistActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        SharedPrefUtil.writeDataBooleanToSharedPreferences(this, IS_PLAYLIST_CREATED_PREF_EXTRA, true);
        if (userName != null)
            SharedPrefUtil.writeDataStringToSharedPreferences(this,USER_NAME_PREF_EXTRA,userName);
        intent.putExtra(USER_PLAYLIST_ID_PREF_EXTRA,userPlaylistId);
        if (wasPlaylistJustCreated)
            finish();
        startActivity(intent);
    }
}