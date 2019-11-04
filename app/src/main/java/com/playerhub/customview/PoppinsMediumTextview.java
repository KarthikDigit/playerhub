package com.playerhub.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.playerhub.R;

public class PoppinsMediumTextview extends AppCompatTextView {

    public PoppinsMediumTextview(Context context) {
        super(context);
        init();
    }

    public PoppinsMediumTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public PoppinsMediumTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
//        Typeface typeface = getResources().getFont(R.font.myfont);

        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/poppins_medium.ttf"));//Set Typeface from MyApplication
    }
}
