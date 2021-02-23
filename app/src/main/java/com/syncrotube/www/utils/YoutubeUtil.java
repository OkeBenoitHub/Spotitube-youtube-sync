package com.syncrotube.www.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.syncrotube.www.R;
import com.syncrotube.www.adapters.YtSearchVideoAdapter;
import com.syncrotube.www.models.YtVideoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.syncrotube.www.utils.AppConstUtil.MAX_RESULT_QUERY_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.VIDEO_SEARCH_QUERY_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.YOUTUBE_SEARCH_API_LOADER;

/**
 * Youtube Util :: contain every recurring task dealing with Youtube
 */
final public class YoutubeUtil implements LoaderManager.LoaderCallbacks<String> {
    private final static String API_KEY_PARAM = "key";
    private static YouTubePlayerView mYouTubePlayerView;
    private static YouTubePlayer mYoutubePlayer;
    private static String mPlayerState;
    private final Context mContext;

    private RecyclerView mYtVideoResultsRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mEmptyResultsView;
    private setUpYtVideoResultsFoundWithAdapterCallback mSetUpYtVideoResultsFoundWithAdapterCallback;

    public YoutubeUtil(Context context) {
        mContext = context;
    }

    /*
     * /////////////////////////// Youtube API search Utils ///////////////////////////////////////////
     */

    /**
     * Build Youtube search video Url
     * @param context :: context
     * @param searchQuery :: search query
     * @param maxResult :: max result
     * @return :: url of search
     */
    public static URL buildYoutubeSearchUrl(Context context, String searchQuery, int maxResult) {
        String YOUTUBE_SEARCH_API_BASE_URL = context.getString(R.string.youtube_base_api_url);
        String API_KEY_VALUE_TEXT = context.getString(R.string.youtube_api_key_syncrotube);
        Uri builtUri = Uri.parse(YOUTUBE_SEARCH_API_BASE_URL).buildUpon()
                .appendQueryParameter("part","snippet")
                .appendQueryParameter("q",searchQuery)
                .appendQueryParameter("videoCategoryId","10")
                .appendQueryParameter("type","video")
                .appendQueryParameter("videoEmbeddable","true")
                .appendQueryParameter("videoSyndicated","true")
                .appendQueryParameter("maxResults",String.valueOf(maxResult))
                .appendQueryParameter(API_KEY_PARAM,API_KEY_VALUE_TEXT)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Search for Youtube video based on query string
     * @param loaderManager :: loader manager from fragment or activity
     * @param context :: context
     * @param videoSearchQueryValue :: video search query value
     * @param max_result :: number max of results
     * @param ytVideoResultsRecyclerView :: where to display results
     * @param emptyResultsView :: empty view to display errors in case failed
     * @param progressBar :: progress bar for loading purposes
     * @param setUpYtVideoResultsFoundWithAdapterCallback :: listener adapter finished attached with recyclerview
     */
    public void searchForYoutubeVideosBasedOnQuery(LoaderManager loaderManager, Context context, String videoSearchQueryValue, int max_result, RecyclerView ytVideoResultsRecyclerView, TextView emptyResultsView, ProgressBar progressBar, setUpYtVideoResultsFoundWithAdapterCallback setUpYtVideoResultsFoundWithAdapterCallback) {
        // initialize...
        mYtVideoResultsRecyclerView = ytVideoResultsRecyclerView;
        mEmptyResultsView = emptyResultsView;
        mProgressBar = progressBar;
        mSetUpYtVideoResultsFoundWithAdapterCallback = setUpYtVideoResultsFoundWithAdapterCallback;

        // check first for internet before proceeding further...
        if (NetworkUtil.isUserConnectedToNetwork(context)) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString(VIDEO_SEARCH_QUERY_EXTRA, videoSearchQueryValue);
            queryBundle.putInt(MAX_RESULT_QUERY_EXTRA, max_result);
            // Call getSupportLoaderManager and store it in a LoaderManager variable
            // Get our Loader by calling getLoader and passing the ID we specified
            Loader<String> loader = loaderManager.getLoader(YOUTUBE_SEARCH_API_LOADER);
            // If the Loader was null, initialize it. Else, restart it.
            if (loader == null) {
                loaderManager.initLoader(YOUTUBE_SEARCH_API_LOADER, queryBundle, this);
            } else {
                loaderManager.restartLoader(YOUTUBE_SEARCH_API_LOADER, queryBundle, this);
            }
        } else {
            // not connected to internet
            mEmptyResultsView.setText(context.getString(R.string.No_internet_available_text));
            showErrorMessage();
        }
    }

    private void showErrorMessage(){
        mEmptyResultsView.setVisibility(View.VISIBLE);
        mYtVideoResultsRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showVideoResultsData(){
        mEmptyResultsView.setVisibility(View.INVISIBLE);
        mYtVideoResultsRecyclerView.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        // Here we will initiate AsyncTaskLoader and handle task in background
        return new AsyncTaskLoader<String>(mContext) {
            String videoFoundResults;
            @Override
            public String loadInBackground() {
                String searchQueryExtra = null;
                if (args != null) {
                    searchQueryExtra = args.getString(VIDEO_SEARCH_QUERY_EXTRA);
                }
                int maxResultExtra = 0;
                if (args != null) {
                    maxResultExtra = args.getInt(MAX_RESULT_QUERY_EXTRA);
                }
                // Think of this as AsyncTask doInBackground() method, here we will actually initiate Network call, or any work that need to be done on background
                URL YoutubeSearchApiUrl = buildYoutubeSearchUrl(mContext, searchQueryExtra,maxResultExtra);
                try {
                    videoFoundResults = NetworkUtil.getResponseFromHttpUrl(YoutubeSearchApiUrl); // This just create a HTTPUrlConnection and return result in strings
                    return videoFoundResults;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onStartLoading() {
                if (videoFoundResults != null) {
                    // To skip loadInBackground call
                    deliverResult(videoFoundResults);
                } else {
                    mYtVideoResultsRecyclerView.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(String data) {
                videoFoundResults = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        showVideoResultsData();
        if (data != null && !data.equals("")) {
            String music_title = "empty";
            String music_video_id = "empty";
            String music_published_date = "empty";
            String music_thumbnail_path = "empty";
            ArrayList<YtVideoModel> videoResultsArrayList = new ArrayList<>();
            try {
                JSONObject musicJsonRootObject = new JSONObject(data);
                if (musicJsonRootObject.has("items")) {
                    JSONArray musicItemsJsonArray = musicJsonRootObject.optJSONArray("items");

                    if (musicItemsJsonArray != null) {
                        if (musicItemsJsonArray.length() > 0) {
                            for (int i = 0; i < musicItemsJsonArray.length(); i++) {
                                JSONObject musicItemJsonObject = musicItemsJsonArray.optJSONObject(i);
                                if (musicItemJsonObject.has("id")) {
                                    JSONObject musicItemIdJsonObject = musicItemJsonObject.optJSONObject("id");
                                    if (musicItemIdJsonObject != null && musicItemIdJsonObject.has("videoId")) {
                                        music_video_id = musicItemIdJsonObject.optString("videoId");
                                    }
                                }
                                if (musicItemJsonObject.has("snippet")) {
                                    JSONObject musicItemSnippetJsonObject = musicItemJsonObject.optJSONObject("snippet");
                                    if (musicItemSnippetJsonObject != null && musicItemSnippetJsonObject.has("publishedAt")) {
                                        music_published_date = musicItemSnippetJsonObject.optString("publishedAt");
                                        music_published_date = music_published_date.substring(0, 4);
                                    }
                                    if (musicItemSnippetJsonObject != null && musicItemSnippetJsonObject.has("title")) {
                                        music_title = musicItemSnippetJsonObject.optString("title");
                                        music_title = String.valueOf(Html.fromHtml(music_title));
                                    }
                                    if (musicItemSnippetJsonObject != null && musicItemSnippetJsonObject.has("thumbnails")) {
                                        JSONObject musicItemThumbnailJsonObject = musicItemSnippetJsonObject.optJSONObject("thumbnails");
                                        if (musicItemThumbnailJsonObject != null && musicItemThumbnailJsonObject.has("medium")) {
                                            JSONObject musicItemDefaultThumbnailJsonObject = musicItemThumbnailJsonObject.optJSONObject("medium");
                                            if (musicItemDefaultThumbnailJsonObject != null && musicItemDefaultThumbnailJsonObject.has("url")) {
                                                music_thumbnail_path = musicItemDefaultThumbnailJsonObject.optString("url");
                                            }
                                        }
                                    }
                                }
                                YtVideoModel ytVideoModel = new YtVideoModel(music_title, music_video_id, music_thumbnail_path, music_published_date);
                                // add a new music item to array list
                                videoResultsArrayList.add(ytVideoModel);
                            }
                            setUpYtVideoResultsFoundWithAdapter(videoResultsArrayList, mSetUpYtVideoResultsFoundWithAdapterCallback);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mEmptyResultsView.setText(mContext.getString(R.string.no_videos_found));
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public interface setUpYtVideoResultsFoundWithAdapterCallback {
        void onSetUpYtVideoResultsFoundWithAdapter(YtVideoModel ytVideoModel, int clickedItemIndex);
    }
    /**
     * Set up video results found from Youtube with Adapter
     * @param videoResultsFoundArrayList :: array list of youtube custom items
     */
    private void setUpYtVideoResultsFoundWithAdapter(ArrayList<YtVideoModel> videoResultsFoundArrayList, setUpYtVideoResultsFoundWithAdapterCallback setUpYtVideoResultsFoundWithAdapterCallback) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mYtVideoResultsRecyclerView.setLayoutManager(layoutManager);
        mYtVideoResultsRecyclerView.setHasFixedSize(true);
        YtSearchVideoAdapter ytSearchVideoAdapter = new YtSearchVideoAdapter(mContext, videoResultsFoundArrayList, clickedItemIndex -> {
            YtVideoModel ytVideoModel = videoResultsFoundArrayList.get(clickedItemIndex);
            if (ytVideoModel.getVideoId().length() == 11)
                setUpYtVideoResultsFoundWithAdapterCallback.onSetUpYtVideoResultsFoundWithAdapter(ytVideoModel, clickedItemIndex);
        });
        mYtVideoResultsRecyclerView.setAdapter(ytSearchVideoAdapter);
        if (videoResultsFoundArrayList.size() <= 0) {
            showErrorMessage();
        } else {
            showVideoResultsData();
        }
    }

    /*
     * ////////////////////////////////////// Youtube Player Utils //////////////////////////////////
     */

    /**
     * Initialize Youtube Player
     * @param rootView :: root view of youtube player
     * @param YoutubePlayerViewId :: Youtube player view res id
     */
    public static YouTubePlayerView initializeYoutubePlayer(Lifecycle lifecycle, View rootView, int YoutubePlayerViewId, boolean enableBackgroundPlayback, abstractYouTubePlayerListenerCallback abstractYouTubePlayerListenerCallback) {
        // Initializing YouTube player view
        mYouTubePlayerView = rootView.findViewById(YoutubePlayerViewId);
        if (!enableBackgroundPlayback) {
            lifecycle.addObserver(mYouTubePlayerView);
        } else {
            mYouTubePlayerView.enableBackgroundPlayback(enableBackgroundPlayback);
        }
        mYouTubePlayerView.addYouTubePlayerListener(abstractYouTubePlayerListener(abstractYouTubePlayerListenerCallback));
        return mYouTubePlayerView;
    }

    public interface abstractYouTubePlayerListenerCallback {
        void onAbstractYouTubePlayerListener(YouTubePlayer youTubePlayer, boolean isListenSuccessful, String playerState);
    }
    /**
     *
     * @return :: listener
     */
    public static AbstractYouTubePlayerListener abstractYouTubePlayerListener(abstractYouTubePlayerListenerCallback abstractYouTubePlayerListenerCallback) {
        return new AbstractYouTubePlayerListener() {
            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);
                abstractYouTubePlayerListenerCallback.onAbstractYouTubePlayerListener(youTubePlayer,false,error.toString());
            }

            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                abstractYouTubePlayerListenerCallback.onAbstractYouTubePlayerListener(youTubePlayer,true,"READY");
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                String mPlayerState = null;
                if (state == PlayerConstants.PlayerState.ENDED) mPlayerState = AppConstUtil.YT_PLAYER_STATE_ENDED;
                if (state == PlayerConstants.PlayerState.BUFFERING) mPlayerState = AppConstUtil.YT_PLAYER_STATE_BUFFERING;
                if (state == PlayerConstants.PlayerState.PAUSED) mPlayerState = AppConstUtil.YT_PLAYER_STATE_PAUSED;
                if (state == PlayerConstants.PlayerState.PLAYING) mPlayerState = AppConstUtil.YT_PLAYER_STATE_PLAYING;
                if (mPlayerState != null)
                    abstractYouTubePlayerListenerCallback.onAbstractYouTubePlayerListener(youTubePlayer, true, mPlayerState);
            }
        };
    }

    public static YouTubePlayer getYoutubePlayer() {
        return YoutubeUtil.mYoutubePlayer;
    }

    public static void setYoutubePlayer(YouTubePlayer mYoutubePlayer) {
        YoutubeUtil.mYoutubePlayer = mYoutubePlayer;
    }

    public static String getPlayerState() {
        return YoutubeUtil.mPlayerState;
    }

    public static void setPlayerState(String mPlayerState) {
        YoutubeUtil.mPlayerState = mPlayerState;
    }

    public static YouTubePlayerView getYouTubePlayerView() {
        return YoutubeUtil.mYouTubePlayerView;
    }

    public static void setYouTubePlayerView(YouTubePlayerView mYouTubePlayerView) {
        YoutubeUtil.mYouTubePlayerView = mYouTubePlayerView;
    }

    /**
     * Load Youtube video by id
     * @param videoId :: video id
     */
    public static void loadYoutubeVideoById(String videoId, float position) {
        if (getYoutubePlayer() != null) {
            // check for Youtube video length 11
            if (videoId.length() == 11) getYoutubePlayer().loadVideo(videoId, position);
        }
    }

    /**
     * hide Youtube player view
     */
    public static void hideYoutubePlayerView() {
        if (getYouTubePlayerView() != null) getYouTubePlayerView().setVisibility(View.GONE);
    }

    /**
     * Show Youtube player view
     */
    public static void showYoutubePlayerView() {
        if (getYouTubePlayerView() != null) getYouTubePlayerView().setVisibility(View.VISIBLE);
    }
}
