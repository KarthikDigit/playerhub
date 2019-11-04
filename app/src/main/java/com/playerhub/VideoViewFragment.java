package com.playerhub;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.playerhub.ui.dashboard.videos.VideosFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoViewFragment extends DialogFragment {


    private static final String AUG_VIDEO_URL = "video_url";

    @BindView(R.id.videoView)
    VideoView videoView;
    Unbinder unbinder;

    public VideoViewFragment() {
        // Required empty public constructor
    }


    public static VideoViewFragment getInstance(String videoUrl) {

        VideoViewFragment fragment = new VideoViewFragment();

        Bundle bundle = new Bundle();

        bundle.putString(AUG_VIDEO_URL, videoUrl);

        fragment.setArguments(bundle);

        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_view, container, false);
        unbinder = ButterKnife.bind(this, view);


        if (getArguments() != null && getArguments().containsKey(AUG_VIDEO_URL)) {


            String url = getArguments().getString(AUG_VIDEO_URL);
            Uri uri = Uri.parse(url);

            videoView.setVideoURI(uri);
            MediaController mediaController = new MediaController(videoView.getContext());
            mediaController.setAnchorView(videoView);

            videoView.setMediaController(mediaController);
            videoView.start();

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {


                    dismiss();

                }
            });


        }


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();


    }
}
