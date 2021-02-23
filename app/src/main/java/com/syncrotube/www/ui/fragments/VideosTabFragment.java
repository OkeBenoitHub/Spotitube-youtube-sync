package com.syncrotube.www.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.syncrotube.www.R;
import com.syncrotube.www.adapters.PlaylistVideosAdapter;
import com.syncrotube.www.databinding.FragmentVideosTabBinding;
import com.syncrotube.www.firebase.firestore.FirestoreBase;
import com.syncrotube.www.models.PlaylistViewModel;
import com.syncrotube.www.models.YtVideoModel;
import com.syncrotube.www.sqlitebase.database.YtVideoEntry;
import com.syncrotube.www.sqlitebase.viewModels.YtVideoEntriesViewModel;
import com.syncrotube.www.ui.activities.PlaylistActivity;
import com.syncrotube.www.ui.activities.YoutubePlayerActivity;
import com.syncrotube.www.utils.DialogUtil;
import com.syncrotube.www.utils.MainUtil;
import com.syncrotube.www.utils.NetworkUtil;
import com.syncrotube.www.utils.SQLiteUtil;
import com.syncrotube.www.utils.SharedPrefUtil;
import com.syncrotube.www.utils.YoutubeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;
import static com.syncrotube.www.utils.AppConstUtil.LAUNCH_YOUTUBE_PLAYER_ACTIVITY;
import static com.syncrotube.www.utils.AppConstUtil.PLAYBACK_INDEX_KEY_PREF;
import static com.syncrotube.www.utils.AppConstUtil.PLAYLIST_COLLECTION_NAME;
import static com.syncrotube.www.utils.AppConstUtil.USER_NAME_PREF_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.USER_PLAYLIST_ID_PREF_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.VIDEO_ITEM_ID_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.VIDEO_ITEM_PERFORM_ACTION_TEXT;
import static com.syncrotube.www.utils.AppConstUtil.VIDEO_ITEM_POSITION_EXTRA;

public class VideosTabFragment extends Fragment implements PlaylistVideosAdapter.ListItemClickListener {
    private FragmentVideosTabBinding mFragmentVideosTabBinding;
    private PlaylistFragment mPlaylistFragment;
    private YtVideoModel mYtVideoModel;
    private PlaylistViewModel mPlaylistViewModel;
    private PlaylistVideosAdapter mPlaylistVideosAdapter;
    private List<YtVideoEntry> mYtVideoEntries;

    public PlaylistVideosAdapter getPlaylistVideosAdapter() {
        return mPlaylistVideosAdapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlaylistFragment = (PlaylistFragment)
                requireActivity().getSupportFragmentManager().findFragmentById(R.id.playlist_fragment);
        mPlaylistViewModel = ViewModelProviders.of(this).get(PlaylistViewModel.class);
        mPlaylistViewModel.setUserId(MainUtil.getUniqueID(requireContext()));
        mPlaylistViewModel.setUserName(SharedPrefUtil.getDataStringFromSharedPreferences(requireContext(),USER_NAME_PREF_EXTRA));
        mPlaylistViewModel.setPlaylistId(SharedPrefUtil.getDataStringFromSharedPreferences(requireContext(),USER_PLAYLIST_ID_PREF_EXTRA));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentVideosTabBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_videos_tab, container, false);

        hideAddVideosToPlaylistBox();
        // add video to playlist button tapped
        mFragmentVideosTabBinding.addVideoToPlaylistButton.setOnClickListener(view -> showAddVideosToPlaylistBox());

        // close videos to Add playlist button
        mFragmentVideosTabBinding.addVideosPlaylistBox.findViewById(R.id.closeVideosToAddPlaylistButton).setOnClickListener(view -> hideAddVideosToPlaylistBox());

        // search youtube video edit text
        EditText videoQueryEditText = mFragmentVideosTabBinding.getRoot().findViewById(R.id.video_query_edit_text);
        videoQueryEditText.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER) {
                String videoQueryEditTextValue = videoQueryEditText.getText().toString().trim();
                searchYoutubeVideosByQuery(videoQueryEditTextValue);
                return true;
            }
            return false;
        });

        // search videos button tapped
        ImageView searchVideoButton = mFragmentVideosTabBinding.addVideosPlaylistBox.findViewById(R.id.searchVideoButton);
        searchVideoButton.setOnClickListener(v -> {
            String videoQueryEditTextValue = videoQueryEditText.getText().toString().trim();
            searchYoutubeVideosByQuery(videoQueryEditTextValue);
        });

        deletePlaylistTapped();

        // show progress bar before loading videos from sqlite db
        mFragmentVideosTabBinding.progressVideosBar.setVisibility(View.VISIBLE);

        setYtVideoEntriesWithAdapter();

        return mFragmentVideosTabBinding.getRoot();
    }

    private void setYtVideoEntriesWithAdapter() {
        mFragmentVideosTabBinding.videosPlaylistRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Initialize the adapter and attach it to the RecyclerView
        mPlaylistVideosAdapter = new PlaylistVideosAdapter(requireContext(), new ArrayList<>(), this);
        mFragmentVideosTabBinding.videosPlaylistRv.setAdapter(mPlaylistVideosAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), VERTICAL);
        mFragmentVideosTabBinding.videosPlaylistRv.addItemDecoration(decoration);
        setYtVideoEntriesWithViewModel(mPlaylistVideosAdapter);
    }

    private void setYtVideoEntriesWithViewModel(PlaylistVideosAdapter playlistVideosAdapter) {
        YtVideoEntriesViewModel ytVideoEntriesViewModel = ViewModelProviders.of(this).get(YtVideoEntriesViewModel.class);
        ytVideoEntriesViewModel.getYtVideoEntries().observe(getViewLifecycleOwner(), ytVideoEntries -> {
            if (ytVideoEntries.size() > 0) {
                mYtVideoEntries = ytVideoEntries;
                mFragmentVideosTabBinding.noVideosPlaylistTv.setVisibility(View.INVISIBLE);
                mFragmentVideosTabBinding.progressVideosBar.setVisibility(View.INVISIBLE);
                SharedPrefUtil.writeDataIntToSharedPreferences(requireContext(),PLAYBACK_INDEX_KEY_PREF,0);
                playlistVideosAdapter.setYtVideoEntries(ytVideoEntries);
            } else {
                mFragmentVideosTabBinding.noVideosPlaylistTv.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Search Youtube videos by query
     * @param videoQueryEditTextValue :: query value
     */
    private void searchYoutubeVideosByQuery(String videoQueryEditTextValue) {
        if (!videoQueryEditTextValue.equalsIgnoreCase("")) {
            LoaderManager loaderManager = LoaderManager.getInstance(this);
            YoutubeUtil youtubeUtil = new YoutubeUtil(requireContext());
            // tap youtube video item
            youtubeUtil.searchForYoutubeVideosBasedOnQuery(
                    loaderManager,
                    requireContext(),
                    videoQueryEditTextValue,
                    15,
                    mFragmentVideosTabBinding.getRoot().findViewById(R.id.video_results_recycler_view),
                    mFragmentVideosTabBinding.getRoot().findViewById(R.id.error_text_view),
                    mFragmentVideosTabBinding.getRoot().findViewById(R.id.progress_circular),
                    this::goToYoutubePlayerActivity);
        }
    }


    private void goToYoutubePlayerActivity(YtVideoModel ytVideoModel, int clickedItemIndex) {
        Intent intent = new Intent(requireContext(), YoutubePlayerActivity.class);
        mYtVideoModel = ytVideoModel;
        intent.putExtra(VIDEO_ITEM_ID_EXTRA,mYtVideoModel.getVideoId());
        intent.putExtra(VIDEO_ITEM_POSITION_EXTRA,clickedItemIndex);
        intent.putExtra(VIDEO_ITEM_PERFORM_ACTION_TEXT,getString(R.string.add_video_to_playlist_text));
        if (mPlaylistFragment.getYoutubePlayer() != null) {
            mPlaylistFragment.getYoutubePlayer().pause();
        }
        startActivityForResult(intent, LAUNCH_YOUTUBE_PLAYER_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_YOUTUBE_PLAYER_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                String video_item_id_extra = data.getStringExtra(VIDEO_ITEM_ID_EXTRA);
                if (video_item_id_extra != null && video_item_id_extra.length() == 11) {
                    // create a new Yt video entry
                    YtVideoEntry ytVideoEntry = new YtVideoEntry(
                            mYtVideoModel.getVideoId(),
                            mYtVideoModel.getTitle(),
                            mYtVideoModel.getThumbnailPath(),
                            mYtVideoModel.getPublishedAt(),
                            false,
                            new Date(),
                            mPlaylistViewModel.getUserId(),
                            mPlaylistViewModel.getUserName());
                    // insert entry in db sqlite
                    SQLiteUtil.insertEntryToDb(requireContext(), appDatabase -> appDatabase.mPlaylistDao().insertYtVideoEntry(ytVideoEntry));
                    MainUtil.showToastMessage(requireContext(),getString(R.string.added_successfully_text));
                }
            }
        }
    }

    /**
     * Show add videos to playlist Box
     */
    private void showAddVideosToPlaylistBox() {
        mFragmentVideosTabBinding.addVideosPlaylistBox.setVisibility(View.VISIBLE);
        mFragmentVideosTabBinding.mainHeader.setVisibility(View.GONE);
        if (mPlaylistFragment != null) {
            mPlaylistFragment.hideYoutubePlayerView();
        }
    }

    /**
     * Hide add videos to playlist Box
     */
    private void hideAddVideosToPlaylistBox() {
        mFragmentVideosTabBinding.addVideosPlaylistBox.setVisibility(View.INVISIBLE);
        mFragmentVideosTabBinding.mainHeader.setVisibility(View.VISIBLE);
        if (mPlaylistFragment != null) {
            mPlaylistFragment.showYoutubePlayerView();
        }
    }

    /**
     * Delete playlist button tapped
     */
    private void deletePlaylistTapped() {
        // delete playlist button tapped
        mFragmentVideosTabBinding.deletePlaylistButton.setOnClickListener(view -> {
            String dialogTitle = getString(R.string.delete_playlist_dialog_title);
            String contentMessage = getString(R.string.delete_playlist_warning_msg);
            // alert user before proceeding to delete playlist
            DialogUtil.showBasicAlertDialog(requireContext(), dialogTitle, contentMessage, getString(R.string.yes_button_text), getString(R.string.no_button_text),true, isPositiveBtnTapped -> {
                if (isPositiveBtnTapped) {
                    // yes button tapped
                    //String userPlaylistIdPref = SharedPrefUtil.getDataStringFromSharedPreferences(requireContext(),USER_PLAYLIST_ID_PREF_EXTRA);
                    String userPlaylistIdPref = mPlaylistViewModel.getPlaylistId();
                    // delete user playlist with all data
                    // check for internet
                    if (!NetworkUtil.checkForInternetAvailability(requireContext())) return;
                    DialogUtil.showProgressDialog(requireContext(),getString(R.string.proceeding_msg_text),true);
                    FirestoreBase.deleteDocumentByIdFromCollection(PLAYLIST_COLLECTION_NAME, userPlaylistIdPref, (isSuccessful, errorMessage) -> {
                        if (isSuccessful && errorMessage == null) {
                            // go back to Main Activity
                            DialogUtil.hideProgressDialog(requireContext());
                            ((PlaylistActivity) requireActivity()).goBackToMainActivity();
                        }
                    });
                }
            });
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentVideosTabBinding = null;
    }

    @Override
    public void onListItemClick(YtVideoEntry ytVideoEntry) {
        mPlaylistFragment.getYoutubePlayer().loadVideo(ytVideoEntry.getVideoId(),0);
        for (int i = 0; i < mYtVideoEntries.size(); i++) {
             if (mYtVideoEntries.get(i).getVideoId().equals(ytVideoEntry.getVideoId())) {
                 SharedPrefUtil.writeDataIntToSharedPreferences(requireContext(),PLAYBACK_INDEX_KEY_PREF,i);
                 mPlaylistVideosAdapter.notifyDataSetChanged();
             }
        }
    }
}
