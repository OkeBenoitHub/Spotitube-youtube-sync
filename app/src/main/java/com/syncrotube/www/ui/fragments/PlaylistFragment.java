package com.syncrotube.www.ui.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.android.material.tabs.TabLayout;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.syncrotube.www.R;
import com.syncrotube.www.adapters.PlaylistTabsAdapter;
import com.syncrotube.www.databinding.FragmentNewPlaylistBinding;
import com.syncrotube.www.databinding.FragmentPlaylistRoomBinding;
import com.syncrotube.www.firebase.firestore.FirestoreBase;
import com.syncrotube.www.firebase.storage.StorageBase;
import com.syncrotube.www.models.PlaylistModel;
import com.syncrotube.www.models.PlaylistViewModel;
import com.syncrotube.www.models.UserModel;
import com.syncrotube.www.sqlitebase.viewModels.YtVideoEntriesViewModel;
import com.syncrotube.www.ui.activities.MainActivity;
import com.syncrotube.www.ui.activities.ViewPhotoActivity;
import com.syncrotube.www.utils.AppConstUtil;
import com.syncrotube.www.utils.DialogUtil;
import com.syncrotube.www.utils.MainUtil;
import com.syncrotube.www.utils.NetworkUtil;
import com.syncrotube.www.utils.PhotoUtil;
import com.syncrotube.www.utils.SharedPrefUtil;
import com.syncrotube.www.utils.YoutubeUtil;
import com.theartofdev.edmodo.cropper.CropImage;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.syncrotube.www.utils.AppConstUtil.IS_PLAYLIST_CREATED_PREF_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.PICK_PHOTO_FROM_PHONE_GALLERY;
import static com.syncrotube.www.utils.AppConstUtil.PLAYBACK_INDEX_KEY_PREF;
import static com.syncrotube.www.utils.AppConstUtil.PLAYLIST_COLLECTION_NAME;
import static com.syncrotube.www.utils.AppConstUtil.PROFILE_PHOTO_PATH_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.REQUEST_PHOTO_CAPTURE;
import static com.syncrotube.www.utils.AppConstUtil.USERS_COLLECTION_NAME;
import static com.syncrotube.www.utils.AppConstUtil.USER_PLAYLIST_ID_PREF_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.VIEW_PHOTO_REQUEST_CODE;

public class PlaylistFragment extends Fragment {
    private FragmentNewPlaylistBinding mFragmentNewPlaylistBinding;
    private FragmentPlaylistRoomBinding mFragmentPlaylistRoomBinding;
    private PlaylistViewModel mPlaylistViewModel;
    private boolean mIsErrorFound;

    /**
     * Hide Youtube Player View
     */
    public void hideYoutubePlayerView() {
        YoutubeUtil.hideYoutubePlayerView();
    }

    /**
     * Show Youtube Player View
     */
    public void showYoutubePlayerView() {
        YoutubeUtil.showYoutubePlayerView();
    }

    /**
     * Get Youtube Player
     * @return :: player
     */
    public YouTubePlayer getYoutubePlayer() {
        return YoutubeUtil.getYoutubePlayer();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPlaylistViewModel = ViewModelProviders.of(this).get(PlaylistViewModel.class);
        mPlaylistViewModel.setUserId(MainUtil.getUniqueID(requireContext()));

        mPlaylistViewModel.setPlaylistCreated(SharedPrefUtil.getDataBooleanFromSharedPreferences(requireContext(),IS_PLAYLIST_CREATED_PREF_EXTRA));
        mPlaylistViewModel.setPlaylistId(SharedPrefUtil.getDataStringFromSharedPreferences(requireContext(),USER_PLAYLIST_ID_PREF_EXTRA));

        SharedPrefUtil.clearPreferenceDataByKey(requireContext(),PLAYBACK_INDEX_KEY_PREF);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (YoutubeUtil.getYouTubePlayerView() != null && YoutubeUtil.getPlayerState() != null) {
            if (YoutubeUtil.getYoutubePlayer() != null) {
                if (YoutubeUtil.getPlayerState().equals(AppConstUtil.YT_PLAYER_STATE_PAUSED))
                    YoutubeUtil.getYoutubePlayer().play();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        if (YoutubeUtil.getYouTubePlayerView() != null) {
            if (YoutubeUtil.getYoutubePlayer() != null && mPlaylistViewModel.isAllVideosDonePlaying())
                YoutubeUtil.getYouTubePlayerView().release();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (YoutubeUtil.getYouTubePlayerView() != null) {
            mPlaylistViewModel.setAllVideosDonePlaying(true);
            YoutubeUtil.getYouTubePlayerView().release();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (YoutubeUtil.getYouTubePlayerView() != null) {
            // check if device orientation is landscape or portrait
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // enter player full screen
                YoutubeUtil.getYouTubePlayerView().enterFullScreen();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                // exit full screen
                YoutubeUtil.getYouTubePlayerView().exitFullScreen();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
         * Check if user has already created a playlist
         * if yes :: show go to playlist button -> take user to playlist activity
         * if no :: show create playlist button -> take user to playlist fragment screen
         */
        if (!mPlaylistViewModel.isPlaylistCreated()) {
            // new user :: has not created playlist yet
            mFragmentNewPlaylistBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_playlist, container, false);
            setFragmentNewPlaylistBindingTapEvents();
            return mFragmentNewPlaylistBinding.getRoot();
        } else {
            // user has already created playlist
            mFragmentPlaylistRoomBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist_room, container, false);
            setUpPlaylistTabsAdapterWithViewPager(mFragmentPlaylistRoomBinding.getRoot());
            // initialize Youtube player
            YoutubeUtil.setYouTubePlayerView(YoutubeUtil.initializeYoutubePlayer(getLifecycle(), mFragmentPlaylistRoomBinding.getRoot(), R.id.player_view, true, (youTubePlayer, isListenSuccessful, playerState) -> {
                YoutubeUtil.setYoutubePlayer(youTubePlayer);
                YoutubeUtil.setPlayerState(playerState);
                if (isListenSuccessful) {
                    YtVideoEntriesViewModel ytVideoEntriesViewModel = ViewModelProviders.of(this).get(YtVideoEntriesViewModel.class);
                    ytVideoEntriesViewModel.getYtVideoEntries().observe(getViewLifecycleOwner(), ytVideoEntries -> {
                        if (ytVideoEntries.size() > 0) {
                            if (playerState.equals(AppConstUtil.YT_PLAYER_STATE_READY)) {
                                // load first video from playlist
                                YoutubeUtil.loadYoutubeVideoById(ytVideoEntries.get(0).getVideoId(), 0);
                                SharedPrefUtil.writeDataIntToSharedPreferences(requireContext(),PLAYBACK_INDEX_KEY_PREF,0);
                            } else if (playerState.equals(AppConstUtil.YT_PLAYER_STATE_ENDED)) {
                                int next_playback_index_key_value = SharedPrefUtil.getDataIntFromSharedPreferences(requireContext(),PLAYBACK_INDEX_KEY_PREF) + 1;
                                if (next_playback_index_key_value < ytVideoEntries.size()) {
                                    YoutubeUtil.loadYoutubeVideoById(ytVideoEntries.get(next_playback_index_key_value).getVideoId(), next_playback_index_key_value);
                                    SharedPrefUtil.writeDataIntToSharedPreferences(requireContext(),PLAYBACK_INDEX_KEY_PREF,next_playback_index_key_value);

                                    // get current tab fragment from parent
                                    Fragment page = requireActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + mFragmentPlaylistRoomBinding.viewPager.getCurrentItem());
                                    // call method from tab fragment :: 0 is first tab fragment
                                    if (mFragmentPlaylistRoomBinding.viewPager.getCurrentItem() == 0 && page != null) {
                                        ((VideosTabFragment)page).getPlaylistVideosAdapter().notifyDataSetChanged();
                                    }
                                }
                            }
                            //YoutubeUtil.getYoutubePlayer().pause();
                        } else {
                            hideYoutubePlayerView();
                        }
                    });
                }
            }));
            return mFragmentPlaylistRoomBinding.getRoot();
        }
    }

    private void setFragmentNewPlaylistBindingTapEvents() {
        // cancel create new playlist button
        Button cancelCreatePlaylistBtn = mFragmentNewPlaylistBinding.cancelButton;
        cancelCreatePlaylistBtn.setEnabled(true);
        // tapped
        cancelCreatePlaylistBtn.setOnClickListener(view -> {
            // prevent double fast tapping crash
            if (cancelCreatePlaylistBtn.isEnabled()) {
                cancelCreatePlaylistBtn.setEnabled(false);
                // go back to Main fragment screen
                NavHostFragment.findNavController(PlaylistFragment.this).navigateUp();
            }
        });

        // create playlist button
        Button createPlaylistBtn = mFragmentNewPlaylistBinding.createButton;
        // tapped
        createPlaylistBtn.setOnClickListener(view -> {
            // check for errors before creating a playlist
            checkForErrors();
            if (!mIsErrorFound) {
                // create new playlist
                createNewPlaylist();
            }
        });

        // set profile photo cached from preferences
        setProfilePhotoSavedFromPreferences();

        // pick photo from gallery button tapped
        mFragmentNewPlaylistBinding.takePictureGallery.setOnClickListener(view -> startActivityForResult(PhotoUtil.pickPhotoFromGallery(requireContext()), PICK_PHOTO_FROM_PHONE_GALLERY));

        // take photo from camera button tapped
        mFragmentNewPlaylistBinding.takePictureCamera.setOnClickListener(view -> {
            Intent takePictureFromCameraIntent = PhotoUtil.capturePhoto(requireContext());
            if (takePictureFromCameraIntent != null) {
                mPlaylistViewModel.setUserProfilePicPath(PhotoUtil.getPhotoFilePath());
                startActivityForResult(takePictureFromCameraIntent, REQUEST_PHOTO_CAPTURE);
            }
        });
    }

    /**
     * Set up playlist tabs adapter with viewpager
     * @param rootView :: root parent view
     */
    private void setUpPlaylistTabsAdapterWithViewPager(View rootView){
        // Create an adapter that knows which fragment should be shown on each page
        PlaylistTabsAdapter playlistTabsAdapter = new PlaylistTabsAdapter(requireActivity().getSupportFragmentManager(), requireContext());
        ViewPager viewPager = rootView.findViewById(R.id.view_pager);
        // Set the adapter onto the view pager
        viewPager.setAdapter(playlistTabsAdapter);
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = rootView.findViewById(R.id.tab_layout);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Create user playlist
     */
    private void createNewPlaylist() {
        // check for internet connection before proceeding
        if (!NetworkUtil.checkForInternetAvailability(requireContext())) return;
        String profilePathPref = SharedPrefUtil.getDataStringFromSharedPreferences(requireContext(), PROFILE_PHOTO_PATH_EXTRA);
        if (mPlaylistViewModel.getUserProfilePicPath() != null && !mIsErrorFound && profilePathPref.equals("")) {
            // if profile photo not saved in viewModel or shared preferences
            // show progress dialog
            DialogUtil.showProgressDialog(requireContext(),getString(R.string.please_wait_message_text),false);
            String profilePhotoPath = mPlaylistViewModel.getUserProfilePicPath();
            String rootDirectoryName = mPlaylistViewModel.getUserId() + "/photos/";

            // start by uploading profile picture to storage
            StorageBase.uploadFileToStorageReference(profilePhotoPath, rootDirectoryName, "image/jpg", (isSuccessful, uploadedFileUri) -> {
                if (isSuccessful) {
                    // upload file is successful
                    // save profile photo to shared preferences
                    SharedPrefUtil.writeDataStringToSharedPreferences(requireContext(), PROFILE_PHOTO_PATH_EXTRA, uploadedFileUri.toString());
                    addUserAndPlaylistDataToDb(uploadedFileUri.toString());
                } else {
                    MainUtil.showToastMessage(requireContext(), getString(R.string.failed_upload_profile_photo));
                }
                // hide dialog
                DialogUtil.hideProgressDialog(requireContext());
            });

        } else if (mPlaylistViewModel.getUserProfilePicPath() == null && !mIsErrorFound && !profilePathPref.equals("")) {
            // if profile photo not saved in viewModel but cached to shared preferences
            // show progress dialog
            DialogUtil.showProgressDialog(requireContext(),getString(R.string.please_wait_message_text),false);
            addUserAndPlaylistDataToDb(profilePathPref);
        }
    }

    private void addUserAndPlaylistDataToDb(String userProfilePhotoPath) {
        // get current user data based on ID
        FirestoreBase.getDocumentByIdFromCollection(USERS_COLLECTION_NAME, mPlaylistViewModel.getUserId(), false, (isSuccessful, documentObject, errorMessage) -> {
            String lastOnlineTime = new Date().getTime() + "";
            String lastOnlineDate = DateTimeUtils.formatWithPattern(new Date(), "EEEE, MMMM dd, yyyy");
            // create a new User
            UserModel userData = new UserModel(mPlaylistViewModel.getUserId(),mPlaylistViewModel.mUserName,userProfilePhotoPath, lastOnlineTime, lastOnlineDate);
            if (!isSuccessful && documentObject == null) { // no user data found in collection
                // add new user data to collection
                FirestoreBase.addDocumentByIdToCollection(USERS_COLLECTION_NAME, mPlaylistViewModel.getUserId(), userData, (isSuccessful12, errorMessage12) -> {
                    if (isSuccessful12) {
                        // once user data saved successfully :: go ahead and add playlist data
                        mPlaylistViewModel.setUserProfilePicPath(userProfilePhotoPath);
                        addNewPlaylistToDatabase(lastOnlineTime, lastOnlineDate);
                    } else {
                        // hide progress dialog
                        DialogUtil.hideProgressDialog(requireContext());
                        MainUtil.showToastMessage(requireContext(),getString(R.string.failed_operation_text));
                    }
                });
            } else {
                // if user data is already saved :: we just update user new data
                Map<String, Object> updatedDocFieldsData = new HashMap<>();
                updatedDocFieldsData.put("name",mPlaylistViewModel.getPlaylistName());
                updatedDocFieldsData.put("profilePicPath",userProfilePhotoPath);
                updatedDocFieldsData.put("id",mPlaylistViewModel.getUserId());
                updatedDocFieldsData.put("lastOnlineTime",lastOnlineTime);
                updatedDocFieldsData.put("lastOnlineDate",lastOnlineDate);
                FirestoreBase.updateDocumentByIdFromCollection(USERS_COLLECTION_NAME, mPlaylistViewModel.getUserId(),updatedDocFieldsData, (isSuccessful1, errorMessage1) -> {
                    if (isSuccessful && errorMessage == null) {
                        // once user data saved successfully :: go ahead and add playlist data
                        mPlaylistViewModel.setUserProfilePicPath(userProfilePhotoPath);
                        addNewPlaylistToDatabase(lastOnlineTime, lastOnlineDate);
                    } else {
                        // hide progress dialog
                        DialogUtil.hideProgressDialog(requireContext());
                        MainUtil.showToastMessage(requireContext(),getString(R.string.failed_operation_text));
                    }
                });
            }
        });
    }

    /**
     * Add new playlist data to database
     *
     * @param createdAtTime :: time
     * @param createdOnDate :: date
     */
    private void addNewPlaylistToDatabase(String createdAtTime, String createdOnDate) {
        List<String> currentSyncList = new ArrayList<>();
        PlaylistModel playlistData = new PlaylistModel("", mPlaylistViewModel.getPlaylistName(), mPlaylistViewModel.getUserId(), createdAtTime, createdOnDate, new ArrayList<>(),currentSyncList, new ArrayList<>());
        FirestoreBase.pushDocumentWithoutIdToCollection(PLAYLIST_COLLECTION_NAME, playlistData, (isSuccessful, documentRefId, errorMessage) -> {
            if (isSuccessful && errorMessage == null) {
                Map<String, Object> updatedUserFieldsData = new HashMap<>();
                updatedUserFieldsData.put("id",documentRefId);
                FirestoreBase.updateDocumentByIdFromCollection(PLAYLIST_COLLECTION_NAME,documentRefId, updatedUserFieldsData, (isSuccessful1, errorMessage1) -> {
                    if (isSuccessful1 && errorMessage1 == null) {
                        // hide progress dialog
                        DialogUtil.hideProgressDialog(requireContext());
                        MainUtil.showToastMessage(requireContext(), getString(R.string.playlist_created_text));
                        SharedPrefUtil.writeDataStringToSharedPreferences(requireContext(), USER_PLAYLIST_ID_PREF_EXTRA,documentRefId);
                        // go to playlist activity
                        ((MainActivity) requireActivity()).goToPlaylistActivity(documentRefId,mPlaylistViewModel.getUserName(),true);
                    } else {
                        MainUtil.showToastMessage(requireContext(),getString(R.string.failed_operation_text));
                    }
                });
            } else {
                // hide progress dialog
                DialogUtil.hideProgressDialog(requireContext());
                MainUtil.showToastMessage(requireContext(),getString(R.string.failed_operation_text));
            }
        });
    }

    /**
     * Check for input errors before creating user playlist
     */
    private void checkForErrors() {
        // check for username
        EditText userName = mFragmentNewPlaylistBinding.userNameEdt;
        String userNameValue = userName.getText().toString().trim();
        mPlaylistViewModel.setUserName(null);
        if (userNameValue.isEmpty()) {
            userName.setError(getString(R.string.enter_user_name_error_text));
            mIsErrorFound = true;
            return;
        } else if (!MainUtil.isValidName(userNameValue)) {
            userName.setError(getString(R.string.only_letters_name_error_text));
            mIsErrorFound = true;
            return;
        } else if (userNameValue.length() < 5) {
            userName.setError(getString(R.string.five_characters_error_text));
            mIsErrorFound = true;
            return;
        } else {
            userName.setError(null);
            mIsErrorFound = false;
            mPlaylistViewModel.setUserName(userNameValue);
        }

        // check for playlist name
        EditText playlistName = mFragmentNewPlaylistBinding.playlistNameEdt;
        String playlistNameValue = playlistName.getText().toString().trim();
        mPlaylistViewModel.setPlaylistName(null);
        if (playlistNameValue.isEmpty()) {
            playlistName.setError(getString(R.string.enter_playlist_name_error_text));
            mIsErrorFound = true;
            return;
        } else if (!MainUtil.isValidName(playlistNameValue)) {
            playlistName.setError(getString(R.string.only_letters_name_error_text));
            mIsErrorFound = true;
            return;
        } else if (playlistNameValue.length() < 5) {
            playlistName.setError(getString(R.string.five_characters_error_text));
            mIsErrorFound = true;
            return;
        } else {
            playlistName.setError(null);
            mIsErrorFound = false;
            mPlaylistViewModel.setPlaylistName(playlistNameValue);
        }

        String profilePathPref = SharedPrefUtil.getDataStringFromSharedPreferences(requireContext(), PROFILE_PHOTO_PATH_EXTRA);
        // check for profile photo
        if (mPlaylistViewModel.getUserProfilePicPath() == null && profilePathPref.equals("")) {
            MainUtil.showToastMessage(requireContext(), getString(R.string.profile_photo_add_error));
            mIsErrorFound = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO_CAPTURE && resultCode == RESULT_OK) {
            PhotoUtil.cropPhoto(requireContext(),mPlaylistViewModel.getUserProfilePicPath(),this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // after cropping photo is successful
                Uri resultUri = result.getUri();
                Intent intent = new Intent(getActivity(), ViewPhotoActivity.class);
                intent.putExtra(PROFILE_PHOTO_PATH_EXTRA, resultUri.toString());
                startActivityForResult(intent, VIEW_PHOTO_REQUEST_CODE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // failed to crop photo
                Exception error = result.getError();
                MainUtil.showToastMessage(requireContext(), error.getMessage());
            }
        } else if (requestCode == PICK_PHOTO_FROM_PHONE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                CropImage.activity(selectedImage)
                        .start(requireContext(), this);
            }
        } else if (requestCode == VIEW_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            // edit picture result path..
            if (data != null) {
                mPlaylistViewModel.setUserProfilePicPath(data.getStringExtra(PROFILE_PHOTO_PATH_EXTRA));
                mPlaylistViewModel.setUserProfilePicPath(mPlaylistViewModel.getUserProfilePicPath());
                PhotoUtil.addPhotoToPhoneGallery(requireContext(), Uri.fromFile(new File(mPlaylistViewModel.getUserProfilePicPath())));
                mFragmentNewPlaylistBinding.profileThumbnail.setImageURI(Uri.fromFile(new File(mPlaylistViewModel.getUserProfilePicPath())));
                SharedPrefUtil.clearPreferenceDataByKey(requireContext(),PROFILE_PHOTO_PATH_EXTRA);
            }
        }
    }

    /**
     * set profile photo cached from preferences
     */
    private void setProfilePhotoSavedFromPreferences() {
        String profilePathPref = SharedPrefUtil.getDataStringFromSharedPreferences(requireContext(),PROFILE_PHOTO_PATH_EXTRA);
        if (mPlaylistViewModel.getUserProfilePicPath() != null) {
            mFragmentNewPlaylistBinding.profileThumbnail.setImageURI(Uri.fromFile(new File(mPlaylistViewModel.getUserProfilePicPath())));
        } else if (!profilePathPref.equals("")) {
            PhotoUtil.loadPhotoFileWithGlide(requireContext(),profilePathPref,mFragmentNewPlaylistBinding.profileThumbnail, R.drawable.default_profile);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentNewPlaylistBinding = null;
        mFragmentPlaylistRoomBinding = null;
    }
}
