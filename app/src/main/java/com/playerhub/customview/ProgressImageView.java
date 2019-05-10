package com.playerhub.customview;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.playerhub.R;
import com.playerhub.utils.ImageUtility;
import com.playerhub.utils.ImageUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ProgressImageView extends FrameLayout {

    private ImageView imageView;
    private ProgressBar progressBar;

    public ProgressImageView(@NonNull Context context) {
        super(context);

        init();
    }

    public ProgressImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProgressImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ProgressImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        inflate(getContext(), R.layout.progressimageview, this);

        imageView = findViewById(R.id.pm_image);
        progressBar = findViewById(R.id.pm_progress);

    }

    public void showHide(boolean isLoading) {

//        int pV = isLoading ? VISIBLE : GONE;
//        int imV = isLoading ? GONE : VISIBLE;

        imageView.setVisibility(isLoading ? GONE : VISIBLE);
        progressBar.setVisibility(isLoading ? VISIBLE : GONE);
    }

    private static final String TAG = "ProgressImageView";

    public void loadImage(final String url) {


//        Picasso.get()
//                .load(url)
//                .into(imageView, new Callback() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//
//                    }
//
//                });
//        showHide(true);
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(url).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                imageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
//                showHide(false);

//                Log.e(TAG, "onSuccess: " + url);

            }

            @Override
            public void onError(Exception e) {
//                showHide(false);
                Picasso.get().load(R.drawable.avatar_mini).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    public ImageView getImageView() {
        return imageView;
    }

}
