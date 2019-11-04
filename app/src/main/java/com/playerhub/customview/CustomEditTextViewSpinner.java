package com.playerhub.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.playerhub.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomEditTextViewSpinner<T> extends FrameLayout implements AdapterView.OnItemClickListener {

    @BindView(R.id.edit_title)
    TextView editTextTitle;
    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.edit_error)
    TextView editError;

    private ListPopupWindow listPopupWindow;
    private List<T> list;
    private int selectedPosition = -1;
    private AdapterView.OnItemClickListener onItemClickListener;
    private boolean isPopupOpen = false;

    public CustomEditTextViewSpinner(Context context) {
        super(context);

        initView(context, null);
    }

    public CustomEditTextViewSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CustomEditTextViewSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomEditTextViewSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
//
//
//        if (title != null && title.toLowerCase().contains("email")) {
//
//            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//        } else if (title != null && (title.toLowerCase().contains("phone") || title.toLowerCase().contains("mobile"))) {
//            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//        }

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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        listPopupWindow = new ListPopupWindow(getContext());
        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setAnchorView(this);
        listPopupWindow.setModal(true);
        editText.setFocusable(false);
        editText.setCompoundDrawablePadding(10);
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);


        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                    getEditText().setFocusable(true);

                if (!isPopupOpen) {

                    setList(list);

                    if (listPopupWindow != null && listPopupWindow.getAnchorView() != null) {

//                        if (listPopupWindow.getListView() != null && listPopupWindow.getListView().getAdapter() != null) {

                        if (list != null && !list.isEmpty()) {
                            listPopupWindow.show();

                        } else {

                            Toast.makeText(getContext(), "Please set list", Toast.LENGTH_SHORT).show();

                        }

                    } else {

                        Toast.makeText(getContext(), "Set anchorview", Toast.LENGTH_SHORT).show();

                    }
                } else {
//                        getEditText().setFocusable(false);
                    onDestory();
                }

                isPopupOpen = !isPopupOpen;
            }
        });

    }


    public void setList(List<T> list) {

        this.list = list;

        if (this.list != null && listPopupWindow != null) {
            ArrayAdapter<T> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);

            listPopupWindow.setAdapter(adapter);
            listPopupWindow.setOnItemClickListener(this);

            adapter.notifyDataSetChanged();
        }

    }

    public void add(T t) {

        this.list.add(t);

        if (listPopupWindow.getListView() != null)
            ((ArrayAdapter) listPopupWindow.getListView().getAdapter()).notifyDataSetChanged();

        int index = this.list.indexOf(t);

        if (index != -1) {

            this.setSelection(index);

            if (onItemClickListener != null) {

                onItemClickListener.onItemClick(null, null, selectedPosition, -1);
            }
        }

    }

    public void setAdapter(ListAdapter adapter) {
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setOnItemClickListener(this);
    }


    public List<T> getList() {

        return list;
    }

    public T getItem() {
        return selectedPosition >= 0 ? list.get(selectedPosition) : null;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        this.selectedPosition = position;
        isPopupOpen = !isPopupOpen;
//        getEditText().setFocusable(false);
        listPopupWindow.dismiss();

        editText.setText(list.get(this.selectedPosition).toString());

        if (onItemClickListener != null) {

            onItemClickListener.onItemClick(parent, view, position, id);
        }

    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {

        this.onItemClickListener = onItemClickListener;

    }

    public void clear() {

        editText.setText("");

        this.selectedPosition = -1;
    }

    public void setSelection(int index) {

        this.selectedPosition = index;
        if (listPopupWindow != null)
            listPopupWindow.dismiss();
        editText.setText(this.list.get(this.selectedPosition).toString());


    }

    public void setViewEnabled(boolean isEnabled) {

        this.setEnabled(isEnabled);

        editText.setEnabled(isEnabled);

    }


    public void onDestory() {

        if (listPopupWindow != null) {

            listPopupWindow.dismiss();
//            listPopupWindow = null;
        }
    }

    public Adapter getAdapter() {

        return listPopupWindow.getListView().getAdapter();
    }
}
