package com.playerhub.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class PoppinsBoldTextview extends AppCompatTextView {

    public PoppinsBoldTextview(Context context) {
        super(context);
        init();
    }

    public PoppinsBoldTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public PoppinsBoldTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/poppins_bold.ttf"));//Set Typeface from MyApplication
    }
}
