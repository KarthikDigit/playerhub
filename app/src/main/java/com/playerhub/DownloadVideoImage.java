package com.playerhub;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.playerhub.utils.ImageUtils;

public class DownloadVideoImage extends AsyncTask<String, Void, Bitmap> {

    private OnImageDownloadCallback onImageDownloadCallback;

    public DownloadVideoImage(OnImageDownloadCallback onImageDownloadCallback) {
        this.onImageDownloadCallback = onImageDownloadCallback;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            return ImageUtils.retriveVideoFrameFromVideo(strings[0]);
        } catch (Throwable throwable) {

            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap != null) {

            if (onImageDownloadCallback != null) onImageDownloadCallback.onImageSuccess(bitmap);
        }

    }

    public interface OnImageDownloadCallback {

        void onImageSuccess(Bitmap bitmap);
    }
}
