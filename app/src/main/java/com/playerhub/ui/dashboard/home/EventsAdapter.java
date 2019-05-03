package com.playerhub.ui.dashboard.home;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.network.response.EventListApi.EventListResponseApi;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsRow> {


    private Context context;
    private List<UpcommingEvent> list;
    private OnItemClickListener onItemClickListener;

    public EventsAdapter(Context context, List<UpcommingEvent> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updateList(List<UpcommingEvent> list) {
        this.list = new ArrayList<>();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventsRow onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_events_row, viewGroup, false);


        return new EventsRow(view);
    }

    private void setColorLine(View view, int color) {


//        GradientDrawable background = (GradientDrawable) view.getBackground();
//        background.setColor(color);

        view.setBackgroundColor(color);
//        eventsRow.lineView.setBackgroundColor(ContextCompat.getColor(context, R.color.game));
    }


    private void setColor(View view, int color) {


        GradientDrawable background = (GradientDrawable) view.getBackground();
        background.setColor(color);

//        view.setBackgroundColor(color);
//        eventsRow.lineView.setBackgroundColor(ContextCompat.getColor(context, R.color.game));
    }

    private int getColor(String name) {

        int color = ContextCompat.getColor(context, R.color.others);

        if (name.toLowerCase().equalsIgnoreCase("game")) {
            color = ContextCompat.getColor(context, R.color.game);

        } else if (name.toLowerCase().equalsIgnoreCase("training session")) {
            color = ContextCompat.getColor(context, R.color.training_session);

        } else if (name.toLowerCase().equalsIgnoreCase("tournament")) {
            color = ContextCompat.getColor(context, R.color.tournament);

        } else if (name.toLowerCase().equalsIgnoreCase("scrimmage")) {
            color = ContextCompat.getColor(context, R.color.scrimmage);

        } else {
            color = ContextCompat.getColor(context, R.color.others);
        }

        return color;

    }

    @Override
    public void onBindViewHolder(@NonNull EventsRow eventsRow, final int i) {

        final UpcommingEvent events = list.get(i);

        Date date = Utils.convertStringToDate(events.getStartDate(), "yyyy-MM-dd");

        String d = Utils.convertDateToString(date, "dd MMM");

        Date dateTaime = Utils.convertStringToDate(events.getStartTime(), "hh:mm:ss");
//
        String t = Utils.convertDateToString(dateTaime, "hh:mm a");

        eventsRow.dates.setText(d);
        eventsRow.time.setText(t);
        eventsRow.name.setText(events.getName());


        String event_type = events.getType().getName() != null ? events.getType().getName() : "";

        int color = getColor(event_type);
        setColor(eventsRow.roundView, color);
        setColorLine(eventsRow.lineView, color);


        eventsRow.team_name.setText(events.getTeam().getName() != null ? events.getTeam().getName() : "");

        if (events.getType() != null) {
            eventsRow.name2.setText(String.format("%s trip", events.getType().getName()));
            eventsRow.name2.setVisibility(View.VISIBLE);
        } else {
            eventsRow.name2.setVisibility(View.GONE);
        }

//        if (events.getDescription() != null && events.getDescription().toString().length()>0) {
//
//            eventsRow.name2.setText(events.getDescription().toString());
//            eventsRow.name2.setVisibility(View.VISIBLE);
//        } else {
//
//            eventsRow.name2.setVisibility(View.GONE);
//        }

        eventsRow.name3.setText(events.getLocation());

        eventsRow.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onItemClickListener != null) {

                    onItemClickListener.OnItemClick(view, events, i);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class EventsRow extends RecyclerView.ViewHolder {


        private TextView dates, time, name, name2, name3, team_name;
        private View roundView, lineView;

        EventsRow(@NonNull View itemView) {
            super(itemView);

            dates = (TextView) itemView.findViewById(R.id.txt_date);
            time = (TextView) itemView.findViewById(R.id.txt_time);
            name = (TextView) itemView.findViewById(R.id.name);
            name2 = (TextView) itemView.findViewById(R.id.name2);
            name3 = (TextView) itemView.findViewById(R.id.name3);
            team_name = (TextView) itemView.findViewById(R.id.team_name);
            roundView = (View) itemView.findViewById(R.id.view2);
            lineView = (View) itemView.findViewById(R.id.view3);
        }
    }

    public interface OnItemClickListener<T> {

        void OnItemClick(View view, T t, int position);
    }
}
