package com.playerhub.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.playerhub.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatVideoView extends FrameLayout {

    @BindView(R.id.videoView)
    ImageView videoView;
    @BindView(R.id.btn_play)
    ImageView btnPlay;
//    @BindView(R.id.progress_bar)
//    ProgressBar progressBar;
    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    public ChatVideoView(@NonNull Context context) {
        super(context);

        initView(context, null);
    }

    public ChatVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ChatVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChatVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {

        View view = inflate(context, R.layout.chat_video_view, this);

        ButterKnife.bind(this, view);


    }


    public void loadImage(String url) {

        showHideLoading(true);
        Glide.with(getContext()).asBitmap().load(url)
                .thumbnail(0.3f).apply(new RequestOptions())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                        showHideLoading(false);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        showHideLoading(false);
                        return false;
                    }
                })
                .into(videoView);


    }


    private void showHideLoading(boolean isLoading) {

        if (isLoading) {

            btnPlay.setVisibility(GONE);
            spinKitView.setVisibility(VISIBLE);
        } else {
            btnPlay.setVisibility(VISIBLE);
            spinKitView.setVisibility(GONE);
        }

    }
}
