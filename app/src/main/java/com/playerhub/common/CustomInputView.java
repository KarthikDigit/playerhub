package com.playerhub.common;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;

public class CustomInputView extends TextInputEditText {

    public CustomInputView(Context context) {
        super(context);
    }

    public CustomInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public CustomInputView(Context context) {
//        super(context);
//
//    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        init();
    }

    private void init() {

        // set font to EditText itself
        Typeface editTextTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Barlow-Regular.ttf");
//        EditText editText = (EditText) findViewById(R.id.normalEditText);
//        editText.setTypeface(editTextTypeface);

        setTypeface(editTextTypeface);


//        // set font to Hint
//        Typeface hintTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Barlow-Bold.ttf");
////        TypefaceSpan typefaceSpan = new CustomTypefaceSpan(hintTypeface);
//        SpannableString spannableString = new SpannableString(getHint());
//        spannableString.setSpan(new CustomTypefaceSpanMetric(hintTypeface), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        setHint(spannableString);

        Typeface newTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Barlow-Bold.ttf");
        CustomHint customHint = new CustomHint(newTypeface, getHint(), Typeface.BOLD, 60f);
        //        CustomHint customHint = new CustomHint(newTypeface, "Enter some text", Typeface.BOLD_ITALIC);
        //        CustomHint customHint = new CustomHint(newTypeface, "Enter some text", 60f);
        //        CustomHint customHint = new CustomHint("Enter some text", Typeface.BOLD_ITALIC, 60f);
        //        CustomHint customHint = new CustomHint("Enter some text", Typeface.BOLD_ITALIC);
        //        CustomHint customHint = new CustomHint("Enter some text", 60f);

        setHint(customHint);

    }


    public static class CustomHint extends SpannableString {
        public CustomHint(final CharSequence source, final int style) {
            this(null, source, style, null);
        }

        public CustomHint(final CharSequence source, final Float size) {
            this(null, source, size);
        }

        public CustomHint(final CharSequence source, final int style, final Float size) {
            this(null, source, style, size);
        }

        public CustomHint(final Typeface typeface, final CharSequence source, final int style) {
            this(typeface, source, style, null);
        }

        public CustomHint(final Typeface typeface, final CharSequence source, final Float size) {
            this(typeface, source, null, size);
        }

        public CustomHint(final Typeface typeface, final CharSequence source, final Integer style, final Float size) {
            super(source);

            MetricAffectingSpan typefaceSpan = new CustomMetricAffectingSpan(typeface, style, size);
            setSpan(typefaceSpan, 0, source.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    public static class CustomMetricAffectingSpan extends MetricAffectingSpan {
        private final Typeface _typeface;
        private final Float _newSize;
        private final Integer _newStyle;

        public CustomMetricAffectingSpan(Float size) {
            this(null, null, size);
        }

        public CustomMetricAffectingSpan(Float size, Integer style) {
            this(null, style, size);
        }

        public CustomMetricAffectingSpan(Typeface type, Integer style, Float size) {
            this._typeface = type;
            this._newStyle = style;
            this._newSize = size;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyNewSize(ds);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyNewSize(paint);
        }

        private void applyNewSize(TextPaint paint) {
            if (this._newStyle != null)
                paint.setTypeface(Typeface.create(this._typeface, this._newStyle));
            else
                paint.setTypeface(this._typeface);

            if (this._newSize != null)
                paint.setTextSize(this._newSize);
        }
    }


    public static class CustomTypefaceSpanMetric extends TypefaceSpan {
        private final Typeface typeface;

        public CustomTypefaceSpanMetric(final Typeface typeface) {
            super("");
            this.typeface = typeface;
        }

        @Override
        public void updateDrawState(final TextPaint drawState) {
            apply(drawState);
        }

        @Override
        public void updateMeasureState(final TextPaint paint) {
            apply(paint);
        }

        private void apply(final Paint paint) {

            int style;
            Typeface old = paint.getTypeface();
            if (old == null) {
                style = Typeface.NORMAL;
            } else {
                style = old.getStyle();
            }
            final Typeface styledTypeface = Typeface.create(typeface, style);
            int fake = style & ~styledTypeface.getStyle();

            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(styledTypeface);

//            final Typeface oldTypeface = paint.getTypeface();
//            final int oldStyle = oldTypeface != null ? oldTypeface.getStyle() : 0;
//            final int fakeStyle = oldStyle & ~typeface.getStyle();
//
//            if ((fakeStyle & Typeface.BOLD) != 0) {
//                paint.setFakeBoldText(true);
//            }
//
//            if ((fakeStyle & Typeface.ITALIC) != 0) {
//                paint.setTextSkewX(-0.25f);
//            }
//
//            paint.setTypeface(typeface);
        }
    }

    public static class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface mNewType;

        public CustomTypefaceSpan(Typeface type) {
            super("");
            mNewType = type;
        }

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            mNewType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, mNewType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, mNewType);
        }

        private static void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }
    }
}
