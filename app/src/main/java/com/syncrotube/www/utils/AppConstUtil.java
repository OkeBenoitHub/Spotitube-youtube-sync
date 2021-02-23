package com.syncrotube.www.utils;

final public class AppConstUtil {
    /**
     * Firestore collections names constants
     */
    public static final String PLAYLIST_COLLECTION_NAME = "Playlists";
    public static final String USERS_COLLECTION_NAME = "Users";

    /**
     * Shared Preferences constants
     */
    public static final String IS_PLAYLIST_CREATED_PREF_EXTRA = "is_playlist_created_pref_extra";
    public static final String USER_PLAYLIST_ID_PREF_EXTRA = "user_playlist_id_pref_extra";
    public static final String USER_NAME_PREF_EXTRA = "user_name_pref_extra";

    /**
     * Photo Util constants
     */
    public static final int PICK_PHOTO_FROM_PHONE_GALLERY = 300;
    public static final int VIEW_PHOTO_REQUEST_CODE = 200;
    public static final int REQUEST_PHOTO_CAPTURE = 100;
    public final static String PROFILE_PHOTO_PATH_EXTRA = "profile_photo_path_extra";

    /**
     * App permissions constants
     */
    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 250;

    /**
     * Playlist Fragment constants
     */
    public final static String PLAYLIST_ITEM_EXTRA = "playlist_item_extra";

    /**
     * Youtube Util constants
     */
    public final static String YT_PLAYER_STATE_PAUSED = "PAUSED";
    public final static String YT_PLAYER_STATE_READY = "READY";
    public final static String YT_PLAYER_STATE_PLAYING = "PLAYING";
    public final static String YT_PLAYER_STATE_ENDED = "ENDED";
    public final static String YT_PLAYER_STATE_BUFFERING = "BUFFERING";
    public final static String PLAYBACK_INDEX_KEY_PREF = "playback_index_key_pref";

    public static final String VIDEO_SEARCH_QUERY_EXTRA = "search_query_extra";
    public static final String MAX_RESULT_QUERY_EXTRA = "max_result_query_extra";
    public static final int YOUTUBE_SEARCH_API_LOADER = 1;
    public static String VIDEO_ITEM_POSITION_EXTRA = "video_item_position_extra";
    public static final String VIDEO_ITEM_ID_EXTRA = "video_item_id_extra";
    public static final int LAUNCH_YOUTUBE_PLAYER_ACTIVITY = 11;
    public static final String VIDEO_ITEM_PERFORM_ACTION_TEXT = "video_item_perform_action";

}
