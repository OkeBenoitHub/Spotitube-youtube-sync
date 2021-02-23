package com.syncrotube.www.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.syncrotube.www.R;
import com.syncrotube.www.utils.MainUtil;

import static com.syncrotube.www.utils.AppConstUtil.VIDEO_ITEM_ID_EXTRA;
import static com.syncrotube.www.utils.AppConstUtil.VIDEO_ITEM_PERFORM_ACTION_TEXT;
import static com.syncrotube.www.utils.AppConstUtil.VIDEO_ITEM_POSITION_EXTRA;

public class YoutubePlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private String video_intent_id;
    private int video_intent_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        MainUtil.setStatusBarBackgroundColor(this,getWindow(),R.color.bottom_black_color);

        // check if app is in night mode
        if (MainUtil.isAppInNightMode(this)) {
            MainUtil.setStatusBarBackgroundColor(this,getWindow(),R.color.colorPrimaryDark);
        }

        String API_KEY = getString(R.string.youtube_api_key_syncrotube);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(VIDEO_ITEM_ID_EXTRA)) {
                video_intent_id = intent.getStringExtra(VIDEO_ITEM_ID_EXTRA);
            }
            if (intent.hasExtra(VIDEO_ITEM_POSITION_EXTRA)) {
                video_intent_position = intent.getIntExtra(VIDEO_ITEM_POSITION_EXTRA,-1);
            }
            if (intent.hasExtra(VIDEO_ITEM_PERFORM_ACTION_TEXT)) {
                Button returnDataIntentBtn = findViewById(R.id.returnDataIntentBtn);
                returnDataIntentBtn.setText(intent.getStringExtra(VIDEO_ITEM_PERFORM_ACTION_TEXT));
            }
        }

        // Initializing YouTube player view
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.initialize(API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if(youTubePlayer == null) return;
        // Start buffering
        if (!wasRestored) {
            if (video_intent_id.length() == 11) {
                youTubePlayer.loadVideo(video_intent_id);
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        MainUtil.showToastMessage(this, getString(R.string.failed_to_play_video_text));
    }

    public void returnDataIntent(View view) {
        Intent returnIntent = new Intent();
        if (video_intent_id.length() == 11 && video_intent_position != -1) {
            returnIntent.putExtra(VIDEO_ITEM_ID_EXTRA,video_intent_id);
            returnIntent.putExtra(VIDEO_ITEM_POSITION_EXTRA,video_intent_position);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }
}