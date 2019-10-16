package com.playerhub.ui.dashboard.chat;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.playerhub.R;
import com.playerhub.preference.Preferences;
import com.playerhub.utils.ImageUtility;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FullScreenImageView extends DialogFragment {


    private static final String KEY_ARG_IMAGE_URL = "img_url";

    public FullScreenImageView() {
        // Required empty public constructor
    }


    public static FullScreenImageView getInstance(String img_url) {

        FullScreenImageView fullScreenImageView = new FullScreenImageView();

        Bundle bundle = new Bundle();
        bundle.putString(KEY_ARG_IMAGE_URL, img_url);

        fullScreenImageView.setArguments(bundle);

        return fullScreenImageView;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_image_view, container, false);


        ImageView imageView = view.findViewById(R.id.img);

        if (getArguments() != null) {

            String img = getArguments().getString(KEY_ARG_IMAGE_URL);

//            Picasso.get().load(img).into(imageView);

            ImageUtility.firebaseLoadImage(imageView, img, Preferences.INSTANCE.getAutoImageDownload());

        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

    }

}
