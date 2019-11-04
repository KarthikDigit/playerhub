package com.playerhub.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class PoppinsLightTextview extends AppCompatTextView {

    public PoppinsLightTextview(Context context) {
        super(context);
        init();
    }

    public PoppinsLightTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public PoppinsLightTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/poppins_light.ttf"));//Set Typeface from MyApplication
    }
}
