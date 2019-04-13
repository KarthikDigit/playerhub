package com.playerhub.ui.dashboard.chat.creategroupchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.network.response.ContactListApi;
import com.playerhub.ui.base.BaseRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupChatCreateAdapter extends BaseRecyclerAdapter<ContactListApi.Datum> {

    private static final String TAG = "GroupChatCreateAdapter";

    private OnItemCallback onItemCallback;

    public GroupChatCreateAdapter(Context mContext, List<ContactListApi.Datum> list, OnItemCallback onItemCallback) {
        super(mContext, list);
        this.onItemCallback = onItemCallback;
    }

    @Override
    protected int getLayoutRowId() {
        return R.layout.row_item_create_group_chat;
    }

    @Override
    protected <VH extends RecyclerView.ViewHolder> VH getHolder(View view) {
        return (VH) new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, final int i) {

        final ContactListApi.Datum groupUser = getItem(i);

        final Holder holder = (Holder) vh;

//        holder.checkBox.setText(groupUser.getName());


        if (groupUser.isChecked()) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        if (groupUser.getAvatar() != null && groupUser.getAvatar().length() > 0)
            Picasso.get().load(groupUser.getAvatar()).placeholder(R.mipmap.ic_launcher).resize(120, 120).into(holder.icon);


        holder.name.setText(groupUser.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (groupUser.isChecked()) {

                    groupUser.setChecked(false);
                    getItem(i).setChecked(false);
                    holder.checkBox.setChecked(false);
                } else {

                    groupUser.setChecked(true);
                    getItem(i).setChecked(true);
                    holder.checkBox.setChecked(true);
                }


                if (onItemCallback != null) onItemCallback.onItemSelected(getList());

            }
        });

    }


    private class Holder extends RecyclerView.ViewHolder {

//        @BindView(R.id.checkbox)
//        CheckBox checkBox;

        private CheckBox checkBox;
        private CircleImageView icon;
        private TextView name;

        public Holder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
        }
    }


    public interface OnItemCallback {

        void onItemSelected(List<ContactListApi.Datum> list);

    }

}
