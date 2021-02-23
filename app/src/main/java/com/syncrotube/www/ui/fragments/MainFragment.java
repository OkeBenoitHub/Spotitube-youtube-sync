package com.syncrotube.www.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syncrotube.www.R;
import com.syncrotube.www.databinding.FragmentMainBinding;
import com.syncrotube.www.firebase.firestore.FirestoreBase;
import com.syncrotube.www.models.PlaylistModel;
import com.syncrotube.www.ui.activities.MainActivity;
import com.syncrotube.www.utils.MainUtil;
import com.syncrotube.www.utils.NetworkUtil;
import com.syncrotube.www.utils.SharedPrefUtil;

import static com.syncrotube.www.utils.AppConstUtil.IS_PLAYLIST_CREATED_PREF_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.PLAYLIST_COLLECTION_NAME;
import static com.syncrotube.www.utils.AppConstUtil.USER_PLAYLIST_ID_PREF_EXTRA;

public class MainFragment extends Fragment {
    private FragmentMainBinding mFragmentMainBinding;
    private FloatingActionButton mFavoritesFloatingActionButton;
    private Button mCreatePlaylistButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainUtil.getUniqueID(requireContext());
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentMainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        // share app button tapped
        mFragmentMainBinding.shareAppButton.setOnClickListener(view -> MainUtil.shareApp(requireContext()));

        // favorites button tapped
        mFavoritesFloatingActionButton = mFragmentMainBinding.openFavoritesFloatingActionButton;
        mFavoritesFloatingActionButton.setEnabled(true);

        mFavoritesFloatingActionButton.setOnClickListener(view -> {
            // prevent crash from double fast tapping
            if (mFavoritesFloatingActionButton.isEnabled()) {
                mFavoritesFloatingActionButton.setEnabled(false);
                // go to favorites fragment screen
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.action_MainFragment_to_FavoritesFragment);
            }
        });

        // playlist button
        mCreatePlaylistButton = mFragmentMainBinding.newPlaylistButton;
        mCreatePlaylistButton.setEnabled(true);

        /*
         * Check if user has already created a playlist
         * if yes :: show go to playlist button -> take user to playlist activity
         * if no :: show create playlist button -> take user to playlist fragment screen
         */
        boolean is_playlist_pref_created = SharedPrefUtil.getDataBooleanFromSharedPreferences(requireContext(),IS_PLAYLIST_CREATED_PREF_EXTRA);
        if (!is_playlist_pref_created) {
            mCreatePlaylistButton.setText(R.string.create_playlist_button_text);
        } else {
            mCreatePlaylistButton.setText(R.string.go_to_playlist_button_text);
        }

        mCreatePlaylistButton.setOnClickListener(view -> {
            if (!is_playlist_pref_created) { // new user :: has not created playlist yet
                // prevent crash from double fast tapping
                if (mCreatePlaylistButton.isEnabled()) {
                    mCreatePlaylistButton.setEnabled(false);
                    // go to playlist fragment screen
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_MainFragment_to_PlaylistFragment);
                }
            } else { // user has already created playlist
                // go to Playlist Activity
                String userPlaylistIdPref = SharedPrefUtil.getDataStringFromSharedPreferences(requireContext(), USER_PLAYLIST_ID_PREF_EXTRA);
                if (!userPlaylistIdPref.equals("")) {
                    // go to playlist activity
                    ((MainActivity) requireActivity()).goToPlaylistActivity(userPlaylistIdPref,null, false);
                } else {
                    MainUtil.showToastMessage(requireContext(),getString(R.string.failed_operation_text));
                }
            }
        });

        return mFragmentMainBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentMainBinding = null;
    }
}
