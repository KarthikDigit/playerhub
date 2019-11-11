package com.playerhub.ui.dashboard.home;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.playerhub.R;
import com.playerhub.network.response.AnnouncementApi;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.EventsRow> {


    private static final String TAG = "AnnouncementAdapter";

    private Context context;
    private List<AnnouncementApi.Datum> list;
    private OnItemClickListener onItemClickListener;

    public AnnouncementAdapter(Context context, List<AnnouncementApi.Datum> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    public AnnouncementAdapter(Context context, List<AnnouncementApi.Datum> list) {
        this.context = context;
        this.list = list;

    }

    public void updateList(List<AnnouncementApi.Datum> list) {
        this.list = new ArrayList<>();
        this.list.addAll(list);
        notifyDataSetChanged();


        Log.e(TAG, "updateList: " + new Gson().toJson(list));
    }

    @NonNull
    @Override
    public EventsRow onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_announcement_row, viewGroup, false);


        return new EventsRow(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsRow eventsRow, final int i) {

        final AnnouncementApi.Datum events = list.get(i);


        eventsRow.title.setText(events.getTitle());
        eventsRow.title.setBackgroundColor(Color.WHITE);
        eventsRow.description.setText(events.getMessage());
        eventsRow.description.setBackgroundColor(Color.WHITE);

        eventsRow.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null) {

                    onItemClickListener.OnAnnouncemnetClick(v, events, i);

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class EventsRow extends RecyclerView.ViewHolder {


        private TextView title, description;

        EventsRow(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);

        }
    }

    public interface OnItemClickListener<T> {

        void OnAnnouncemnetClick(View view, T t, int position);

    }
}
