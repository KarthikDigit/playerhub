package com.playerhub.ui.dashboard.notification;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;
import com.playerhub.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationModel extends AbstractItem<NotificationModel, NotificationModel.ViewHolder> {
    public String name;
    public String description;

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.root;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.notification_item;
    }

    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends FastAdapter.ViewHolder<NotificationModel> {

        @BindView(R.id.description)
        TextView description;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void bindView(NotificationModel item, List<Object> payloads) {

//            StringHolder.applyToOrHide(item.description, description);

            description.setText(item.description);
        }

        @Override
        public void unbindView(NotificationModel item) {
            description.setText(null);
        }
    }
}