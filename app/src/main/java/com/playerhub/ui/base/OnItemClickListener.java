package com.playerhub.ui.base;

import android.view.View;

public interface OnItemClickListener<T> {

    void onItemClicked(View view, T t, int position);

}
