package com.playerhub;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.playerhub.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoPlayerActivity extends BaseActivity {

    private static final String EXTRA_VIDEO_URL = "video_url";

    @BindView(R.id.videoview)
    VideoView videoView;
    //    @BindView(R.id.videoViewWrapper)
//    FrameLayout videoViewWrapper;
    private MediaController mediaController;

    public static void startActivity(Context context, String videoUrl) {


        Intent intent = new Intent(context, VideoPlayerActivity.class);

        intent.putExtra(EXTRA_VIDEO_URL, videoUrl);

        context.startActivity(intent);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_VIDEO_URL)) {

            String url = intent.getStringExtra(EXTRA_VIDEO_URL);
            Uri uri = Uri.parse(url);

            videoView.setVideoURI(uri);
//            mediaController = new MediaController(videoView.getContext());
//            mediaController.setAnchorView(videoView);

//            videoView.setMediaController(mediaController);
            videoView.start();

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {


                    finish();

                }
            });

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {


                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                            mediaController = new MediaController(videoView.getContext());
                            videoView.setMediaController(mediaController);
                            mediaController.setAnchorView(videoView);

//                            ((ViewGroup) mediaController.getParent()).removeView(mediaController);
//
//                            ((FrameLayout) findViewById(R.id.videoViewWrapper))
//                                    .addView(mediaController);
//                            mediaController.setVisibility(View.VISIBLE);

                        }
                    });


                }
            });

        }
    }
}
