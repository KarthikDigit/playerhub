package com.playerhub.customview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.recyclerHelper.EqualSpacingItemDecoration;
import com.playerhub.recyclerHelper.SimpleDividerItemDecoration;
import com.playerhub.ui.dashboard.home.EventsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventView extends FrameLayout {

    @BindView(R.id.event_name)
    TextView eventName;
    @BindView(R.id.load_more_event)
    LinearLayout loadMoreEvents;
    @BindView(R.id.eventList)
    RecyclerView eventList;
    @BindView(R.id.error_msg_event)
    TextView errorMsgEvent;
    @BindView(R.id.today_event_content)
    RelativeLayout todayEventContent;

    private OnClickListener onClickListener;
    private EventsAdapter.OnItemClickListener onItemClickListener;

    private EventsAdapter eventsAdapter;

    public EventView(@NonNull Context context) {
        super(context);
        initViews();
    }

    public EventView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public EventView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EventView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews();
    }


    private void initViews() {

        View view = inflate(getContext(), R.layout.home_event_list, this);

        ButterKnife.bind(this, view);

        eventList.setLayoutManager(new LinearLayoutManager(getContext()));

        eventsAdapter = new EventsAdapter(getContext(), new ArrayList<UpcommingEvent>(), onItemClickListener);
        eventList.setAdapter(eventsAdapter);
        eventList.setNestedScrollingEnabled(false);
//        eventList.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.VERTICAL));
//        eventList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        loadMoreEvents.setOnClickListener(onClickListener);
    }

    public void setEvent(String title, List<UpcommingEvent> list, String error_msg) {

        eventName.setText(title);


        if (list != null && !list.isEmpty()) {

            eventsAdapter.updateList(list);
            eventList.setVisibility(View.VISIBLE);
            errorMsgEvent.setVisibility(View.GONE);

        } else {

            setErrorMsg(error_msg);
        }


    }

    public void setErrorMsg(String errorMsg) {

        errorMsgEvent.setText(errorMsg);
        eventList.setVisibility(View.GONE);
        errorMsgEvent.setVisibility(View.VISIBLE);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        loadMoreEvents.setOnClickListener(this.onClickListener);
    }

    public void setOnItemClickListener(EventsAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;

        eventsAdapter.setOnItemClickListener(this.onItemClickListener);
    }


}
