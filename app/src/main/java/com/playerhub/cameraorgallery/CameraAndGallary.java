package com.playerhub.cameraorgallery;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.playerhub.Util;
import com.playerhub.ui.dashboard.profile.KidsProfile;
import com.playerhub.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class CameraAndGallary {
    private static final String TAG = "CameraAndGallary";
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private static final int REQUEST_TAKE_VIDEO = 200;
    private String userChoosenTask;
    private CameraAndGallaryCallBack cameraAndGallaryCallBack;
    private Context mContext;
    private Fragment fragment;

    private File imageFile;

    public CameraAndGallary(Context mContext, CameraAndGallaryCallBack cameraAndGallaryCallBack) {

        this.cameraAndGallaryCallBack = cameraAndGallaryCallBack;
        this.mContext = mContext;

    }

    public CameraAndGallary(Fragment fragment, CameraAndGallaryCallBack cameraAndGallaryCallBack) {

        this.cameraAndGallaryCallBack = cameraAndGallaryCallBack;
        this.fragment = fragment;

    }


    public void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Video", "Cancel"};

        Context context = getCurrentContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                } else if (items[item].toString().toLowerCase().equals("Video".toLowerCase())) {
                    callVideo();
                } else {
                    userChoosenTask = items[item].toString();
                    callCameraOrGallery();
                }
            }
        });
        builder.show();
    }


    private void callVideo() {

        Intent takeVideoIntent = new Intent();
        takeVideoIntent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
        takeVideoIntent.setAction(Intent.ACTION_GET_CONTENT);
        takeVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);

        if (mContext instanceof Activity) {

            ((Activity) mContext).startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);

        } else {

            fragment.startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);

        }


    }

    private void callCameraOrGallery() {

        if (userChoosenTask.equals("Take Photo")) {
            userChoosenTask = "Take Photo";

            cameraIntent();

        } else if (userChoosenTask.equals("Choose from Library")) {
            userChoosenTask = "Choose from Library";

            galleryIntent();

        } else {
            Log.e(TAG, "callCameraOrGallery: somthing went wrong ");
        }

    }


    private void galleryIntent() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//

        if (mContext instanceof Activity) {

            ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

        } else {

            fragment.startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

        }

    }

    private void cameraIntent() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        File pictureDictionary = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        imageFile = new File(pictureDictionary, "myImage.jpg");

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));


//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.Images.ImageColumns.ORIENTATION, 90);

//
//        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
//
//        Uri photoURI = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID+".provider", f);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);


        if (mContext instanceof Activity) {

            ((Activity) mContext).startActivityForResult(i, REQUEST_CAMERA);

        } else {

            fragment.startActivityForResult(i, REQUEST_CAMERA);

        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {


            if (requestCode == REQUEST_TAKE_VIDEO) {

                onVideoCall(data);

            } else if (requestCode == SELECT_FILE)

                onSelectFromGalleryResult(data);

            else if (requestCode == REQUEST_CAMERA)

                onCaptureImageResult(data);

        }
    }

    private Context getCurrentContext() {

        return mContext instanceof Activity ? mContext : fragment.getContext();
    }


    private void onVideoCall(Intent data) {

        if (data != null && data.getData() != null) {


            String filePath = null;
            try {
                filePath = Util.getFilePath(mContext, data.getData());
                File file = new File(filePath);

                if (cameraAndGallaryCallBack != null) cameraAndGallaryCallBack.onVideo(file);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }

    }

    private void onSelectFromGalleryResult(Intent data) {

        Context context = getCurrentContext();

        Bitmap bm = null;
        if (data != null) {
            try {
                assert context != null;
                bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (cameraAndGallaryCallBack != null)
            cameraAndGallaryCallBack.onSelectFromGalleryResult(bm);
    }

    private void onCaptureImageResult(Intent data) {


//        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
////        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//
//        File destination = new File(Environment.getExternalStorageDirectory(),
//                System.currentTimeMillis() + ".jpg");
//
//
//        FileOutputStream fo;
//        try {
//            destination.createNewFile();
//            fo = new FileOutputStream(destination);
//            fo.write(bytes.toByteArray());
//            fo.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Bitmap thumbnail = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        Bitmap thumbnail = ImageUtils.decodeImageFromFiles(imageFile.getAbsolutePath(), /* your desired width*/300, /*your desired height*/ 300);
        if (thumbnail != null) {

            ExifInterface exifInterface = null;

            try {
                exifInterface = new ExifInterface(imageFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Matrix matrix = new Matrix();

            if (exifInterface != null) {

                int o = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);


                switch (o) {

                    case ExifInterface.ORIENTATION_UNDEFINED:

                        matrix.setRotate(90);

                        break;

                    case ExifInterface.ORIENTATION_ROTATE_90:

                        matrix.setRotate(90);

                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:

                        matrix.setRotate(180);

                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:

                        matrix.setRotate(270);

                        break;
                }

                Log.e(TAG, "onCaptureImageResult: " + o);

            }

            Bitmap rotatedImage = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);


            if (cameraAndGallaryCallBack != null)
                cameraAndGallaryCallBack.onSelectFromGalleryResult(rotatedImage);

        } else {

            Log.e(TAG, "onCaptureImageResult: photo not captured check in onActivityResult ");
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public interface CameraAndGallaryCallBack {

        void onSelectFromGalleryResult(Bitmap bitmap);

        void onVideo(File file);

    }

}