package com.playerhub.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.playerhub.R;
import com.playerhub.common.GlideApp;
import com.playerhub.common.MyAppGlideModule;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

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


    public static void firebaseLoadImage(final ImageView imageView, final String img_url) {

//        getPicasso(imageView.getContext()).load(img_url).into(imageView);

//        RequestOptions requestOptions = new RequestOptions();
//        requestOptions.placeholder(R.drawable.progress_animation);

//        requestOptions.error(R.drawable.avatar_mini);


//        GlideApp.with(imageView.getContext()).load(img_url).override(Target.SIZE_ORIGINAL).priority(Priority.HIGH).into(imageView);


        Glide.with(imageView.getContext())
                .load(img_url)
                .apply(new RequestOptions()
                        .override(Target.SIZE_ORIGINAL)
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .into(imageView);

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


    public static void loadImage(ImageView imageView, String url) {

        if (url != null && !TextUtils.isEmpty(url))

            Picasso.get().load(url).placeholder(R.drawable.progress_animation).error(R.drawable.avatar_mini).into(imageView);

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
