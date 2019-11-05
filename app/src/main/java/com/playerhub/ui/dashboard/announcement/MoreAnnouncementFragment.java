package com.playerhub.ui.dashboard.announcement;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.playerhub.R;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.MultiStateViewFragment;
import com.playerhub.ui.dashboard.home.AnnouncementAdapter;
import com.playerhub.ui.dashboard.profile.MyCallBack;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MoreAnnouncementFragment extends MultiStateViewFragment implements AnnouncementAdapter.OnItemClickListener<AnnouncementApi.Datum> {

    private static final String TAG = "MoreEventsActivity";

    @BindView(R.id.announcementView)
    RecyclerView announcementView;
    Unbinder unbinder;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.swipetoReferesh)
    SwipeRefreshLayout swipetoReferesh;

    private AnnouncementAdapter announcementAdapter;


    private static final String isBackEnable = "isBackEnable";

    public static MoreAnnouncementFragment getInstance(boolean isEnabled) {

        MoreAnnouncementFragment fragment = new MoreAnnouncementFragment();

        Bundle bundle = new Bundle();

        bundle.putBoolean(isBackEnable, isEnabled);

        fragment.setArguments(bundle);

        return fragment;

    }

    @Override
    public int getLayoutByID() {
        return R.layout.activity_more_announcement;
    }


    @Override
    protected void initViews() {

        announcementAdapter = new AnnouncementAdapter(getContext(), new ArrayList<AnnouncementApi.Datum>(), this);

        announcementView.setLayoutManager(new LinearLayoutManager(getContext()));

        announcementView.setAdapter(announcementAdapter);

        announcementView.addItemDecoration(new com.playerhub.recyclerHelper.DividerItemDecoration(getContext()));


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
    }

    @Override
    protected void onRetryOrCallApi() {
        callEventListApi();
    }

    @Override
    public void onManuallyParseError(Response<?> response, boolean isToastMsg) {

        showViewEmpty(response.message());
    }


    private int getColor(int color) {
        return ContextCompat.getColor(getContext(), color);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void callEventListApi() {

        swipetoReferesh.setRefreshing(true);


        showViewLoading();

        RetrofitAdapter.getNetworkApiServiceClient().fetchAnnouncements(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyCallBack<AnnouncementApi>(getContext(), this, false, false) {
                    @Override
                    public void onSuccess(AnnouncementApi response) {
                        swipetoReferesh.setRefreshing(false);
                        setAnnouncementListContent(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
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
            if (response.getData() != null && !response.getData().isEmpty()) {
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

        if (isError) showViewError();
        else showViewContent();

    }


    @OnClick(R.id.back)
    public void onViewClicked() {

        if (getActivity() != null)
            getActivity().onBackPressed();

    }
}
