package com.playerhub.ui.dashboard.chat;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.playerhub.R;
import com.playerhub.Util;
import com.playerhub.ui.base.BaseActivity;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoUploadTestActivity extends BaseActivity {

    private static final String TAG = "VideoUploadTestActivity";
    private static final int REQUEST_TAKE_VIDEO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload_test);
    }

    public void onUpload(View view) {

        Intent takeVideoIntent = new Intent();
        takeVideoIntent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
        takeVideoIntent.setAction(Intent.ACTION_GET_CONTENT);
        takeVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK) {

            if (data != null && data.getData() != null) {


                String filePath = null;
                try {
                    filePath = Util.getFilePath(getApplicationContext(), data.getData());
                    File file = new File(filePath);

                    Log.e(TAG, "onActivityResult: " + filePath);

                    uploadVideo(file);


                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }

        }

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
}
