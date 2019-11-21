package com.playerhub.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;
import com.github.ybq.android.spinkit.SpinKitView;
import com.playerhub.R;
import com.playerhub.utils.ProgressUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;

public class ChatVideoView extends FrameLayout {

    @BindView(R.id.videoView)
    ImageView videoView;
    @BindView(R.id.btn_play)
    ImageView btnPlay;
    //    @BindView(R.id.progress_bar)
//    ProgressBar progressBar;
    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;

    private String downloadID;

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


    public void loadImage(String url, boolean isLocal) {

//        Uri uri = Uri.fromFile(new File(url));
        showHideLoading(true);
        Glide.with(getContext()).asBitmap().load(isLocal ? Uri.fromFile(new File(url)) : url)
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


    public boolean checkFileAlreadyDownloaded(String url) {

        String filename = url.substring(73, 111);
        File file = new File(getContext().getExternalCacheDir().getPath() + "/Playerhub/videos/" + filename + ".mp4");

        if (file.exists()) return true;

        return false;
    }

    public String localVideoFilePath(String url) {

        String filename = url.substring(73, 111);
        File file = new File(getContext().getExternalCacheDir().getPath() + "/Playerhub/videos/" + filename + ".mp4");

        return file.getPath();

    }


    @TargetApi(Build.VERSION_CODES.N)
    public void setProgressBar(long progress) {

        Log.e(TAG, "onReceive: " + progress);
        progressBar.setProgress((int) progress, true);
        progressLayout.setVisibility(VISIBLE);
        if (progress <= 0 || progress >= 100) progressLayout.setVisibility(GONE);

    }

    public void downloadFile(String url) {
        String filename = url.substring(73, 111);
        progressLayout.setVisibility(VISIBLE);
        File file = progressLayout.getContext().getExternalCacheDir();
        if (file != null) {
            downloadID = String.valueOf(PRDownloader.download(url, file.getPath() + "/Playerhub/videos/", filename + "video.mp4")
                    .build()
                    .setOnProgressListener(new com.downloader.OnProgressListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onProgress(Progress progress) {

                            progressBar.setMax((int) progress.totalBytes);
                            progressBar.setProgress((int) progress.currentBytes, true);

                        }
                    })
                    .start(new OnDownloadListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDownloadComplete() {

                            progressBar.setProgress(0, true);
                            progressLayout.setVisibility(GONE);

                        }

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onError(Error error) {

                            Log.e(TAG, "onError: " + error.toString());

                            progressBar.setProgress(0, true);
                            progressLayout.setVisibility(GONE);

                        }

                    }));
        }

    }

    public void pauseVideoDownload(String downloadID) {

        PRDownloader.pause(Integer.parseInt(downloadID));

    }

    public void resumeVideoDownload(String downloadId, String url) {

        Status status = PRDownloader.getStatus(Integer.parseInt(downloadId));
        Log.e(TAG, "resumeVideoDownload: " + status.name());


        if (Status.PAUSED == PRDownloader.getStatus(Integer.parseInt(downloadId))) {
            PRDownloader.resume(Integer.parseInt(downloadId));
            return;
        }

        downloadFile(url);

        // PRDownloader.resume(Integer.parseInt(downloadId));
    }

    public String getDownloadID() {
        return downloadID;
    }

}
