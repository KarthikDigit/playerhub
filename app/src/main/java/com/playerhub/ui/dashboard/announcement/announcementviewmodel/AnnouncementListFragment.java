package com.playerhub.ui.dashboard.announcement.announcementviewmodel;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.playerhub.R;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.recyclerHelper.EqualSpacingItemDecoration;
import com.playerhub.recyclerHelper.ItemOffsetDecoration;
import com.playerhub.recyclerHelper.SimpleDividerItemDecorationFullLine;
import com.playerhub.ui.base.MultiStateViewFragment;
import com.playerhub.ui.dashboard.announcement.AnnouncementDialogFragment;
import com.playerhub.ui.dashboard.home.AnnouncementAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;
    Unbinder unbinder1;

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

//        announcementView.addItemDecoration(new EqualSpacingItemDecoration(48, EqualSpacingItemDecoration.VERTICAL));

//        announcementView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.offset));

        if (getArguments() != null) {
            boolean b = getArguments().getBoolean(isBackEnable, false);

            back.setVisibility(!b ? View.VISIBLE : View.INVISIBLE);
        }

        swipetoReferesh.setColorSchemeColors(getColor(R.color.colorPrimary));
        swipetoReferesh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start animating Shimmer and hide the layout
//                mShimmerViewContainer.setVisibility(View.VISIBLE);
//                mShimmerViewContainer.startShimmerAnimation();
//                announcementView.setVisibility(View.GONE);
//
//                swipetoReferesh.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                callEventListApi();
//                    }
//                }, 2000);

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
//                    showLoading();
                    swipetoReferesh.setRefreshing(true);

                    mShimmerViewContainer.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.startShimmerAnimation();
                    announcementView.setVisibility(View.GONE);


                } else {
                    // stop animating Shimmer and hide the layout
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    announcementView.setVisibility(View.VISIBLE);
//                    hideLoading();
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
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
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
        unbinder1.unbind();
    }


    private void callEventListApi() {


        viewModel.getAnnouncementList();

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void OnAnnouncemnetClick(View view, AnnouncementApi.Datum datum, int position) {

        View title = view.findViewById(R.id.title);
        View des = view.findViewById(R.id.description);


        AnnouncementDialogFragment fragment = AnnouncementDialogFragment.getInstance(datum, false);
        fragment.setEnterTransition(new Slide());
        fragment.setExitTransition(new Slide());
        fragment.show(getChildFragmentManager(), "Announcement");

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }
}
