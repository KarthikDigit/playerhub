package com.playerhub.ui.dashboard.messages.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playerhub.R;
import com.playerhub.network.response.ContactListApi;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class GenreAdapter extends ExpandableRecyclerViewAdapter<GroupHolder, ChatViewHolder> {

    private OnItemClickListener onItemClickListener;

    public GenreAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public GroupHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_genre, parent, false);
        return new GroupHolder(view);
    }

    @Override
    public ChatViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ChatViewHolder holder, final int flatPosition, ExpandableGroup group, final int childIndex) {

        final ContactListApi.Datum datum = (ContactListApi.Datum) group.getItems().get(childIndex);
        holder.onBind(datum);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(datum, flatPosition, childIndex, v);
                }

            }
        });

    }

    @Override
    public void onBindGroupViewHolder(GroupHolder holder, final int flatPosition, ExpandableGroup group) {

        holder.setGenreTitle(group);


        holder.itemView.post(new Runnable() {
            @Override
            public void run() {
                if (!isGroupExpanded(flatPosition)) toggleGroup(flatPosition);
            }
        });


//        final GroupName datum = (GroupName) group;
//
//        if (datum.isExpand())
//            if (!isGroupExpanded(flatPosition))
//                toggleGroup(flatPosition);

    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void OnItemClick(ContactListApi.Datum datum, int flatpos, int pos, View view);

    }

}
