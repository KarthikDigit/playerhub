package com.playerhub.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.playerhub.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomEditTextView extends FrameLayout {

    @BindView(R.id.edit_title)
    TextView editTextTitle;
    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.edit_error)
    TextView editError;

    public CustomEditTextView(Context context) {
        super(context);

        initView(context, null);
    }

    public CustomEditTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CustomEditTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomEditTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);

    }


    private void initView(Context context, AttributeSet attrs) {

        View view = inflate(context, R.layout.custom_edittext_view, this);
        ButterKnife.bind(view, this);


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomEditTextView);

        String hint = typedArray.getString(R.styleable.CustomEditTextView_custom_hint);
        String title = typedArray.getString(R.styleable.CustomEditTextView_custom_title);

//        int i = typedArray.getInt(R.styleable.MaterialEditText_inputType, EditorInfo.TYPE_NULL);

        typedArray.recycle();
        editTextTitle.setText(title);
        editText.setHint(hint);


        if (title != null && title.toLowerCase().contains("email")) {

            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        } else if (title != null && (title.toLowerCase().contains("phone") || title.toLowerCase().contains("mobile") || title.toLowerCase().contains("zipcode") || title.toLowerCase().contains("credit"))) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

    }

    public void setText(String text) {

        editText.setText(text);
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setError(String s) {
        editError.setText(s);
//        editError.setError(s);


    }

    public void setErrorEnabled(boolean b) {

        editError.setVisibility(b ? VISIBLE : GONE);
//        editText.sete(b);

    }
}
