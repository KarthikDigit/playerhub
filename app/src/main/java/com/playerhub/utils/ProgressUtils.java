package com.playerhub.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.playerhub.R;

public class ProgressUtils {

    private static final String TAG = "ProgressUtils";
//    private static ProgressDialog progressDialog;

    private static Dialog dialog;

    public static void showProgress(Context context, String msg) {

//        if (progressDialog != null) {
//
//            progressDialog.dismiss();
//        }
//        progressDialog = new ProgressDialog(context);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setMessage(msg);
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        if (dialog != null) dialog.dismiss();

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_loading_layout);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView gifImageView = dialog.findViewById(R.id.custom_loading_imageView);

//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);
//        Glide.with(context)
//                .load(R.drawable.loading1)
//                .asGif()
//                .placeholder(R.drawable.loading1)
//                .centerCrop()
//                .crossFade()
//                .into(gifImageView);

        Glide
                .with(context)
                .load(R.drawable.loading2)
                .asGif()
                .error(R.drawable.loading2)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(gifImageView);

        dialog.show();

    }

    public static void hideProgress() {

        if (dialog != null) dialog.dismiss();

//        if (progressDialog != null) {
//            progressDialog.dismiss();
//            progressDialog = null;
//        }
    }
}
