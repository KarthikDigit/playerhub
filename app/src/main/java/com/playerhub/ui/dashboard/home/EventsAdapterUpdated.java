package com.playerhub.ui.dashboard.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.utils.AnimUtils;
import com.playerhub.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.playerhub.ui.base.BaseFragment.TAG;

public class EventsAdapterUpdated extends RecyclerView.Adapter<EventsAdapterUpdated.EventsRow> {


    private Context context;
    private List<UpcommingEvent> list;
    private OnItemClickListener onItemClickListener;
    private long DURATION = 200;
    private boolean on_attach = true;

    public EventsAdapterUpdated(Context context, List<UpcommingEvent> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updateList(List<UpcommingEvent> list) {
        this.list = new ArrayList<>();
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventsRow onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_events_row_updated, viewGroup, false);


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


        if (!events.isEmptyView()) {

            eventsRow.rootView.setVisibility(View.VISIBLE);
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
//            setColorLine(eventsRow.lineView, color);


            eventsRow.team_name.setText(events.getTeam().getName() != null ? events.getTeam().getName() : "");

//            if (events.getType() != null) {
//                eventsRow.name2.setText(String.format("%s trip", events.getType().getName()));
//                eventsRow.name2.setVisibility(View.VISIBLE);
//            } else {
//                eventsRow.name2.setVisibility(View.GONE);
//            }

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

            AnimUtils.setFadeAnimation(eventsRow.itemView);
        } else {

            eventsRow.rootView.setVisibility(View.INVISIBLE);
        }

//        FromRightToLeft(eventsRow.itemView, i);

    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                Log.d(TAG, "onScrollStateChanged: Called " + newState);
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        super.onAttachedToRecyclerView(recyclerView);
    }

    private void FromRightToLeft(View itemView, int i) {
        if (!on_attach) {
            i = -1;
        }
        boolean not_first_item = i == -1;
        i = i + 1;
        itemView.setTranslationX(itemView.getX() + 600);
        itemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(itemView, "translationX", itemView.getX() + 400, 0);
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(itemView, "alpha", 1.f);
        ObjectAnimator.ofFloat(itemView, "alpha", 0.f).start();
        animatorTranslateY.setStartDelay(not_first_item ? DURATION : (i * DURATION));
        animatorTranslateY.setDuration((not_first_item ? 2 : 1) * DURATION);
        animatorSet.playTogether(animatorTranslateY, animatorAlpha);
        animatorSet.start();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class EventsRow extends RecyclerView.ViewHolder {


        private TextView dates, time, name, name2, name3, team_name;
        private View roundView, lineView, rootView;

        EventsRow(@NonNull View itemView) {
            super(itemView);

            dates = (TextView) itemView.findViewById(R.id.txt_date);
            time = (TextView) itemView.findViewById(R.id.txt_time);
            name = (TextView) itemView.findViewById(R.id.name);
            name2 = (TextView) itemView.findViewById(R.id.name2);
            name3 = (TextView) itemView.findViewById(R.id.name3);
            team_name = (TextView) itemView.findViewById(R.id.team_name);
            roundView = (View) itemView.findViewById(R.id.view2);
//            lineView = (View) itemView.findViewById(R.id.view3);
            rootView = (View) itemView.findViewById(R.id.rootview);
        }
    }

    public interface OnItemClickListener<T> {

        void OnItemClick(View view, T t, int position);
    }
}
