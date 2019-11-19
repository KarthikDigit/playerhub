package com.playerhub.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.ybq.android.spinkit.SpinKitView;
import com.playerhub.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;


public class ImageUtility {


    public static Picasso getPicasso(Context context) {

        OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder();
        okHttpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().header("Cache-Control", "max-age=" + (60 * 60 * 24 * 365)).build();
            }
        });

        okHttpClient.cache(new Cache(context.getCacheDir(), Integer.MAX_VALUE));
        OkHttp3Downloader okHttpDownloader = new OkHttp3Downloader(okHttpClient.build());

//        picasso.setIndicatorsEnabled(true);
//        picasso.setLoggingEnabled(true);

//        Picasso.setSingletonInstance(picasso);


//        okhttp3.OkHttpClient okHttp3Client = new okhttp3.OkHttpClient();
//        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(okHttp3Client);
//
//        return new Picasso.Builder(context)
//
//                .memoryCache(new LruCache(100 * 1024 * 1024))
//                .downloader(okHttp3Downloader)
//                .build();

        return new Picasso.Builder(context).downloader(okHttpDownloader).build();

    }

    private static final String TAG = "ImageUtility";
//    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");


    public static String getImageName(String image_url) throws UnsupportedEncodingException {

        return URLDecoder.decode(image_url, "UTF-8").substring(80, 94);
    }

    public static File getFilePath(String imgName) {

        File path = new File(Environment.getExternalStorageDirectory()
                + "/Playerhub");
        if (!path.exists()) {
            path.mkdirs();
        }

        return new File(path, imgName + ".png");
    }

    public static void downloadImageFromFirebase(final Context mContext, final String image_url) {


//        Log.e(TAG, "downloadImageFromFirebase: " + System.currentTimeMillis() + "" + sdf.format(new Date()));


//            Log.e(TAG, "downloadImageFromFirebase: " + URLDecoder.decode(image_url, "UTF-8"));


//            File direct = new File(Environment.getExternalStorageDirectory()
//                    + "/Playerhub");
////
//            if (!direct.exists()) {
//                direct.mkdirs();
//            }


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String imageName = getImageName(image_url);

                    File imageFile = getFilePath(imageName);
                    if (!imageFile.exists()) {

//                        Bitmap bitmap = Picasso.get().load(image_url).get();
                        Bitmap bitmap = Glide.with(mContext)
                                .asBitmap()
                                .load(image_url).submit().get();

                        saveImageToExternal(mContext, imageName, bitmap);
                    }

//                    Glide.with(mContext).asBitmap()
//                            .load(image_url)
//                            .into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                    saveImageToExternal(mContext, imageName, resource);
//
////                                    String imagepath = saveImage(resource);
////                                    Uri savedImageURI = Uri.parse(imagepath);
//
//// Parse the gallery image url to uri
//
//// Display the saved image to ImageView
////                                    iv_saved.setImageURI(savedImageURI);
//                                }
//
//                            });

                    Log.e(TAG, "downloadImageFromFirebase: " + imageName);
                } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
                } catch (IOException e) {
//            e.printStackTrace();
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                } catch (ExecutionException e) {
//                    e.printStackTrace();
                }
            }
        });


//        Log.e(TAG, "downloadImageFromFirebase: " + image_url);

//

    }

    public static void saveImageToExternal(Context context, String imgName, Bitmap bm) throws IOException {
        //Create Path to save Image
//        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + appFolder); //Creates app specific folder
//        File path = new File(Environment.getExternalStorageDirectory()
//                + "/Playerhub");
//        if (!path.exists()) {
//            path.mkdirs();
//        }
        File imageFile = getFilePath(imgName);//new File(path, imgName + ".png"); // Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);
        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch (Exception e) {
            throw new IOException();
        }
    }


    public static void firebaseLoadImage(final ImageView imageView, final String img_url, boolean isDownload) {


        if (isDownload) {

//        getPicasso(imageView.getContext()).load(img_url).into(imageView);

//        RequestOptions requestOptions = new RequestOptions();
//        requestOptions.placeholder(R.drawable.progress_animation);

//        requestOptions.error(R.drawable.avatar_mini);


//        GlideApp.with(imageView.getContext()).load(img_url).override(Target.SIZE_ORIGINAL).priority(Priority.HIGH).into(imageView);
            try {
                final String imageName = getImageName(img_url);

                final File imageFile = getFilePath(imageName);
                if (imageFile.exists()) {

                    Glide.with(imageView.getContext())
                            .load(imageFile)
                            .apply(new RequestOptions())
//                        .apply(new RequestOptions()
//                                .override(Target.SIZE_ORIGINAL)
//                                .format(DecodeFormat.PREFER_ARGB_8888))
                            .into(imageView);

                } else {
//                Glide.with(imageView.getContext())
//                        .load(img_url)
//
//                        .apply(new RequestOptions()
//                                .override(Target.SIZE_ORIGINAL)
//                                .format(DecodeFormat.PREFER_ARGB_8888))
//                        .into(imageView);


                    Glide.with(imageView.getContext()).asBitmap()
                            .load(img_url)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {


                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                saveImageToExternal(imageView.getContext(), imageName, resource);

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });


                                    imageView.setImageBitmap(resource);

//                                    String imagepath = saveImage(resource);
//                                    Uri savedImageURI = Uri.parse(imagepath);

// Parse the gallery image url to uri

// Display the saved image to ImageView
//                                    iv_saved.setImageURI(savedImageURI);
                                }

                            });


                }

            } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
            }
        } else {

            Glide.with(imageView.getContext())
                    .load(img_url)

                    .apply(new RequestOptions()
                            .override(Target.SIZE_ORIGINAL)
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .into(imageView);

        }


//        Glide.with(imageView.getContext()).applyDefaultRequestOptions(requestOptions).load(img_url).into(imageView);


//        getPicasso(imageView.getContext()).load(img_url).placeholder(R.drawable.progress_animation).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//                getPicasso(imageView.getContext()).load(img_url).placeholder(R.drawable.progress_animation).into(imageView);
//            }
//        });

    }


    public static void loadImage(final CircleImageView profileImage, final ImageView imageView, final SpinKitView spinKitView, String url) {
        if (url != null && !TextUtils.isEmpty(url))

            Picasso.get().load(url).error(R.drawable.avatar_mini).into(profileImage, new Callback() {
                @Override
                public void onSuccess() {
                    profileImage.setVisibility(View.VISIBLE);
                    spinKitView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    profileImage.setVisibility(View.VISIBLE);
                    spinKitView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }
            });

        else {
            imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(R.drawable.avatar_mini).error(R.drawable.avatar_mini).into(imageView);

        }

    }

    public static void loadImage(CircleImageView profileImage, final ImageView imageView, String url) {

        if (url != null && !TextUtils.isEmpty(url))

            Picasso.get().load(url).error(R.drawable.avatar_mini).into(imageView);

        else {

            Picasso.get().load(R.drawable.avatar_mini).error(R.drawable.avatar_mini).into(imageView);

        }

    }

    public static void loadImage(ImageView profileImage,  String url) {

        if (url != null && !TextUtils.isEmpty(url))

            Picasso.get().load(url).error(R.drawable.avatar_mini).into(profileImage);

        else {

            Picasso.get().load(R.drawable.avatar_mini).error(R.drawable.avatar_mini).into(profileImage);

        }

    }

    public static void loadImage(CircleImageView profileImage,  String url) {

        if (url != null && !TextUtils.isEmpty(url))

            Picasso.get().load(url).error(R.drawable.avatar_mini).into(profileImage);

        else {

            Picasso.get().load(R.drawable.avatar_mini).error(R.drawable.avatar_mini).into(profileImage);

        }

    }


    public static void loadImage(final ImageView imageView, final SpinKitView spinKitView, String url) {

        if (url != null && !TextUtils.isEmpty(url))

            Picasso.get().load(url).error(R.drawable.avatar_mini).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    spinKitView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {

                    spinKitView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }
            });

        else {

            Picasso.get().load(R.drawable.avatar_mini).error(R.drawable.avatar_mini).into(imageView);

        }

    }


    public static void loadImage(final ProgressBar progressBar, final ImageView imageView, String url) {

        if (url != null && !TextUtils.isEmpty(url)) {

            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            Picasso.get().load(url).placeholder(R.drawable.progress_animation).error(R.drawable.avatar_mini).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }
            });
        } else {

            Picasso.get().load(R.drawable.avatar_mini).error(R.drawable.avatar_mini).into(imageView);

        }

    }

}
