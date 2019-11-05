package com.playerhub.ui.dashboard.announcement.announcementviewmodel;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.playerhub.R;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.ui.base.MultiStateViewFragment;
import com.playerhub.ui.dashboard.announcement.AnnouncementDialogFragment;
import com.playerhub.ui.dashboard.home.AnnouncementAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Response;

public class AnnouncementListFragment extends MultiStateViewFragment implements AnnouncementAdapter.OnItemClickListener<AnnouncementApi.Datum> {

    private static final String TAG = "MoreEventsActivity";

    @BindView(R.id.announcementView)
    RecyclerView announcementView;
    Unbinder unbinder;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.swipetoReferesh)
    SwipeRefreshLayout swipetoReferesh;

    private AnnouncementAdapter announcementAdapter;

    private AnnouncementListViewModel viewModel;


    private static final String isBackEnable = "isBackEnable";

    public static AnnouncementListFragment getInstance(boolean isEnabled) {

        AnnouncementListFragment fragment = new AnnouncementListFragment();

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        AnnouncementListViewModel vm=viewModel
    }

    @Override
    protected void initViews() {


        viewModel = ViewModelProviders.of(this).get(AnnouncementListViewModel.class);

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


        viewModel.getAnnouncementList().observe(this, new Observer<List<AnnouncementApi.Datum>>() {
            @Override
            public void onChanged(@Nullable List<AnnouncementApi.Datum> data) {

                announcementAdapter.updateList(data);

            }
        });

        viewModel.isLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {

                if (aBoolean != null && aBoolean) {
                    showLoading();
                    swipetoReferesh.setRefreshing(true);
                } else {
                    hideLoading();
                    swipetoReferesh.setRefreshing(false);
                }

            }
        });

        viewModel.dataStatus.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {

                showHideError(aBoolean);
            }
        });

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


        viewModel.getAnnouncementList();

    }


    @Override
    public void OnAnnouncemnetClick(View view, AnnouncementApi.Datum datum, int position) {
        AnnouncementDialogFragment.getInstance(datum, false).show(getChildFragmentManager(), "Announcement");
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
