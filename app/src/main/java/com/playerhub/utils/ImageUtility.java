package com.playerhub.utils;

import android.widget.ImageView;

import com.playerhub.R;
import com.squareup.picasso.Picasso;

public class ImageUtility {


    public static void loadImage(ImageView imageView, String url) {


        Picasso.get().load(url).error(R.drawable.profile).into(imageView);

    }


}
