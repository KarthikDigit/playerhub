package com.playerhub.ui.dashboard.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dmitrymalkovich.android.ProgressFloatingActionButton;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.playerhub.R;
import com.playerhub.Util;
import com.playerhub.cameraorgallery.CameraAndGallary;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.utils.ProgressUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoUploadTestActivity extends BaseActivity implements CameraAndGallary.CameraAndGallaryCallBack {

    private static final String TAG = "VideoUploadTestActivity";
    private static final int REQUEST_TAKE_VIDEO = 200;

    @BindView(R.id.progressBar1)
    ProgressBar progressBar1;

    private int downloadId = -1;


    private CameraAndGallary cameraAndGallary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload_test);
        ButterKnife.bind(this);

        cameraAndGallary = new CameraAndGallary(this, this);
    }

    private static int count = 0;

    public void fileDownload(View view) {


        cameraAndGallary.callVideo();

//        count++;
//
//        Data data = new Data.Builder()
//                .putString(MyWorker.TASK_DESC, count + " The task data passed from MainActivity")
//                .build();
//
//        final OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
//                .setInputData(data)
//                .build();
//        WorkManager.getInstance().enqueue(workRequest);

//        String url = "https://firebasestorage.googleapis.com/v0/b/playerhub-prod.appspot.com/o/videos%2F20191811113621476779898270778?alt=media&token=d070c742-c368-4d8d-9924-4bf7fce0cce7";
//
//        String filename = url.substring(73, 111);
//
//        Log.e(TAG, "fileDownload: " + filename);
//
//
//        File file = getExternalCacheDir();
//
//
//        if (Status.PAUSED == PRDownloader.getStatus(downloadId)) {
//            PRDownloader.resume(downloadId);
//            return;
//        }
//
//
//        if (file != null) {
//            downloadId = PRDownloader.download(url, file.getPath() + "/Playerhub/videos", "play.mp4")
//                    .build()
//                    .setOnProgressListener(new com.downloader.OnProgressListener() {
//                        @RequiresApi(api = Build.VERSION_CODES.N)
//                        @Override
//                        public void onProgress(Progress progress) {
//
//                            progressBar1.setMax((int) progress.totalBytes);
//                            progressBar1.setProgress((int) progress.currentBytes, true);
//                            Log.e(TAG, "onProgress: " + progress.currentBytes + " " + progress.totalBytes);
//
//                        }
//                    })
//                    .start(new OnDownloadListener() {
//                        @RequiresApi(api = Build.VERSION_CODES.N)
//                        @Override
//                        public void onDownloadComplete() {
//                            progressBar1.setProgress(0, true);
//                            ProgressUtils.hideProgress();
//                        }
//
//                        @RequiresApi(api = Build.VERSION_CODES.N)
//                        @Override
//                        public void onError(Error error) {
//                            progressBar1.setProgress(0, true);
//                        }
//
//                    });
//        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (downloadId != -1)
            PRDownloader.pause(downloadId);
    }

    @Override
    protected void onDestroy() {
        ProgressUtils.hideProgress();
        super.onDestroy();

    }

    public void onUpload(View view) {

//        Intent takeVideoIntent = new Intent();
//        takeVideoIntent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
//        takeVideoIntent.setAction(Intent.ACTION_GET_CONTENT);
//        takeVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        cameraAndGallary.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK) {
//
//            if (data != null && data.getData() != null) {
//
//                String filePath = null;
//
//                try {
//
//                    filePath = Util.getFilePath(getApplicationContext(), data.getData());
//                    File file = new File(filePath);
//
//                    Log.e(TAG, "onActivityResult: " + filePath);
//
//                    uploadVideo(file);
//
//
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//        }

    }

    private void uploadVideo(File file) {

        String name = new SimpleDateFormat("yyyyddMMHHmmss").format(new Date()) + "" + System.nanoTime();


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference videoRef = storageRef.child("videos").child(name);//storageRef.child("FolderToCreate").child("NameYoWantToAdd");
// add File/URI


        showLoading();

//        videoRef = storageRef.child(name);
        videoRef.putFile(Uri.fromFile(file))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded

                        hideLoading();

                        videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                hideLoading();
                                Log.e(TAG, "onSuccess: url " + uri.toString());
//                Log.e(TAG, "onSuccess: " + uri.getPath());

//                                sendMessages(uri.toString());
                            }
                        });

//                        Log.e(TAG, "onSuccess: " + taskSnapshot.getUploadSessionUri().getPath());

                        Toast.makeText(getApplicationContext(), "Upload Success...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        hideLoading();
                        Toast.makeText(getApplicationContext(), "Upload failed...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(
                new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
//                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });


    }

    @Override
    public void onSelectFromGalleryResult(Bitmap bitmap) {

    }


    @Override
    public void onVideo(File file) {


        count++;

        Log.e(TAG, "onVideo: " + file.getPath() + " " + count);

//        Intent intent = new Intent(this, UploadVideoService.class);
//        intent.putExtra("url", file.getPath());
//        intent.putExtra("position", count);
//        startService(intent);


    }
}
