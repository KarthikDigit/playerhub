package com.playerhub.ui.dashboard.myreceipt;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.playerhub.R;
import com.playerhub.customview.MultiStateView;
import com.playerhub.recyclerHelper.EqualSpacingItemDecoration;
import com.playerhub.recyclerHelper.ItemOffsetDecoration;
import com.playerhub.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyReceiptActivity extends BaseActivity {

    @BindView(R.id.myReceiptListView)
    RecyclerView myReceiptListView;
    @BindView(R.id.multiStateView)
    MultiStateView multiStateView;
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;
    private MyReceiptListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_receipt);
        ButterKnife.bind(this);

        setBackButtonEnabledAndTitleBold(getString(R.string.my_receipt_title));

        initView();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                loadData();
            }
        }, 2000);

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


    private void initView() {

        myReceiptListView.setLayoutManager(new LinearLayoutManager(this));
//        myReceiptListView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.offset));

        int margin = (int) getResources().getDimension(R.dimen._12sdp);

        myReceiptListView.addItemDecoration(new EqualSpacingItemDecoration(margin, EqualSpacingItemDecoration.VERTICAL));

        adapter = new MyReceiptListAdapter(this, new ArrayList<MyReceipt>());

        myReceiptListView.setAdapter(adapter);


    }


    private void loadData() {

        List<MyReceipt> myReceipts = new ArrayList<>();


        MyReceipt myReceipt = new MyReceipt();

        myReceipt.setOrderId("ID674322");
        myReceipt.setOrderDate("23/11/2019");
        myReceipt.setOrderType("Payment");
        myReceipt.setOrderStatus("Pending");
        myReceipt.setView("View");

        myReceipts.add(myReceipt);

        myReceipt = new MyReceipt();

        myReceipt.setOrderId("ID274322");
        myReceipt.setOrderDate("03/11/2019");
        myReceipt.setOrderType("Payment");
        myReceipt.setOrderStatus("Pending");
        myReceipt.setView("View");

        myReceipts.add(myReceipt);

        myReceipt = new MyReceipt();

        myReceipt.setOrderId("ID174522");
        myReceipt.setOrderDate("02/10/2019");
        myReceipt.setOrderType("Payment");
        myReceipt.setOrderStatus("Completed");
        myReceipt.setView("View");

        myReceipts.add(myReceipt);

        myReceipt = new MyReceipt();

        myReceipt.setOrderId("ID174522");
        myReceipt.setOrderDate("02/10/2019");
        myReceipt.setOrderType("Payment");
        myReceipt.setOrderStatus("Completed");
        myReceipt.setView("View");

        myReceipts.add(myReceipt);

        myReceipt = new MyReceipt();

        myReceipt.setOrderId("ID674322");
        myReceipt.setOrderDate("23/11/2019");
        myReceipt.setOrderType("Payment");
        myReceipt.setOrderStatus("Pending");
        myReceipt.setView("View");

        myReceipts.add(myReceipt);

        myReceipt = new MyReceipt();

        myReceipt.setOrderId("ID174522");
        myReceipt.setOrderDate("02/10/2019");
        myReceipt.setOrderType("Payment");
        myReceipt.setOrderStatus("Completed");
        myReceipt.setView("View");

        myReceipts.add(myReceipt);

        myReceipt = new MyReceipt();

        myReceipt.setOrderId("ID674322");
        myReceipt.setOrderDate("23/11/2019");
        myReceipt.setOrderType("Payment");
        myReceipt.setOrderStatus("Pending");
        myReceipt.setView("View");

        myReceipts.add(myReceipt);

        myReceipt = new MyReceipt();

        myReceipt.setOrderId("ID174522");
        myReceipt.setOrderDate("02/10/2019");
        myReceipt.setOrderType("Payment");
        myReceipt.setOrderStatus("Completed");
        myReceipt.setView("View");

        myReceipts.add(myReceipt);

        adapter.updateList(myReceipts);

        multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);

    }
}
