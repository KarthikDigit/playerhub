package com.playerhub.utils;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.playerhub.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageUtility {


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
