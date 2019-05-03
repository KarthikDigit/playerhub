package com.playerhub.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.playerhub.R;
import com.squareup.picasso.Picasso;

public class ImageUtility {


    public static void loadImage(ImageView imageView, String url) {

        if (url != null && !TextUtils.isEmpty(url))

            Picasso.get().load(url).error(R.drawable.avatar_mini).into(imageView);

        else {

            Picasso.get().load(R.drawable.avatar_mini).error(R.drawable.avatar_mini).into(imageView);

        }

    }


}
