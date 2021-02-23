package com.syncrotube.www.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.syncrotube.www.R;
import com.syncrotube.www.ui.fragments.WallTabFragment;
import com.syncrotube.www.ui.fragments.SyncTabFragment;
import com.syncrotube.www.ui.fragments.VideosTabFragment;

public class PlaylistTabsAdapter extends FragmentPagerAdapter {
    private final String[] tabTitles;

    public PlaylistTabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        tabTitles = new String[] {
                context.getString(R.string.videos_fragment_title_text),
                context.getString(R.string.sync_fragment_title_text),
                context.getString(R.string.wall_fragment_title_text)};
    }


    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new VideosTabFragment();
        } else if (position == 1) {
            return new SyncTabFragment();
        } else {
            return new WallTabFragment();
        }
    }

    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
