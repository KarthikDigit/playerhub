package com.playerhub.ui.dashboard.home.moreevent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.common.ActivityStats;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.network.response.EventListApi.EventListResponseApi;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.preference.Preferences;
import com.playerhub.recyclerHelper.SimpleDividerItemDecoration;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.dashboard.DashBoardActivity;
import com.playerhub.ui.dashboard.home.AnnouncementAdapter;
import com.playerhub.ui.dashboard.home.EventsAdapter;
import com.playerhub.ui.dashboard.home.announcement.AnnouncementDialogFragment;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsFragment;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class MoreAnnouncementFragment extends BaseFragment implements AnnouncementAdapter.OnItemClickListener<AnnouncementApi.Datum> {
    private static final String TAG = "MoreEventsActivity";

    @BindView(R.id.announcementView)
    RecyclerView announcementView;
    @BindView(R.id.progressActivity)
    ProgressRelativeLayout progressActivity;
    Unbinder unbinder;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.swipetoReferesh)
    SwipeRefreshLayout swipetoReferesh;
    @BindView(R.id.msg_announcement_event)
    TextView msgAnnouncementEvent;
    private AnnouncementAdapter announcementAdapter;


    private static final String isBackEnable = "isBackEnable";

    public static MoreAnnouncementFragment getInstance(boolean isEnabled) {

        MoreAnnouncementFragment fragment = new MoreAnnouncementFragment();

        Bundle bundle = new Bundle();

        bundle.putBoolean(isBackEnable, isEnabled);

        fragment.setArguments(bundle);

        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_more_announcement, container, false);

        unbinder = ButterKnife.bind(this, view);


        announcementAdapter = new AnnouncementAdapter(getContext(), new ArrayList<AnnouncementApi.Datum>(), this);

        announcementView.setLayoutManager(new LinearLayoutManager(getContext()));

        announcementView.setAdapter(announcementAdapter);

        announcementView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));


        if (getArguments() != null) {
            boolean b = getArguments().getBoolean(isBackEnable, false);

            back.setVisibility(!b ? View.VISIBLE : View.INVISIBLE);
        }

        swipetoReferesh.setColorSchemeColors(getColor(R.color.colorPrimary));
        swipetoReferesh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callEventListApi();
            }
        });

        callEventListApi();

        return view;
    }


    private int getColor(int color) {
        return ContextCompat.getColor(getContext(), color);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.progressActivity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.progressActivity:
                break;
        }
    }

    private void callEventListApi() {

        swipetoReferesh.setRefreshing(true);
        setProgressActivity(ActivityStats.LOADING);

        Observable<AnnouncementApi> observable = RetrofitAdapter.getNetworkApiServiceClient().fetchAnnouncements(Preferences.INSTANCE.getAuthendicate());

        setObservableAndObserver(observable, new Observers<AnnouncementApi>(getContext(), false) {

            @Override
            protected void onSuccess(AnnouncementApi response) {
                setProgressActivity(ActivityStats.CONTENT);
                setAnnouncementListContent(response);
                swipetoReferesh.setRefreshing(false);
            }

            @Override
            protected void onFail(Throwable e) {
                setProgressActivity(ActivityStats.ERROR);
                swipetoReferesh.setRefreshing(false);
            }
        });

    }


    @Override
    public void OnAnnouncemnetClick(View view, AnnouncementApi.Datum datum, int position) {
        AnnouncementDialogFragment.getInstance(datum, false).show(getChildFragmentManager(), "Announcement");
    }


    private void setAnnouncementListContent(AnnouncementApi response) {

        if (response != null && response.getSuccess()) {
            if (response.getData() != null) {
                showHideError(false);
                announcementAdapter.updateList(response.getData());

            } else {
                showHideError(true);
            }

        } else {
            showHideError(true);
        }

    }


    private void showHideError(boolean isError) {

        announcementView.setVisibility(isError ? View.GONE : View.VISIBLE);
        msgAnnouncementEvent.setVisibility(isError ? View.VISIBLE : View.GONE);

    }

    private void setProgressActivity(ActivityStats activityStats) {


        switch (activityStats) {
            case LOADING:
                showProgress(progressActivity);
                break;
            case EMPTY:
                showEmpty(progressActivity,
                        "No Data",
                        "There is no event available");

                break;
            case ERROR:
                showError(progressActivity,
                        "Error",
                        "",
                        "Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                callEventListApi();
                            }
                        });

                break;
            case CONTENT:
                showContent(progressActivity);
                break;
        }

    }

    @OnClick(R.id.back)
    public void onViewClicked() {

        if (getActivity() != null)
            getActivity().onBackPressed();

    }
}
