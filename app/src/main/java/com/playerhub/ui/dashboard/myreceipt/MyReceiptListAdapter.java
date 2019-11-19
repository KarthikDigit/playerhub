package com.playerhub.ui.dashboard.myreceipt;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.network.response.NotificationApi;
import com.playerhub.ui.base.OnItemClickListener;
import com.playerhub.utils.AnimUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyReceiptListAdapter extends RecyclerView.Adapter<MyReceiptListAdapter.MyViewHolder> {

    private Context context;
    private List<MyReceipt> cartList;

    private OnItemClickListener onItemClickListener;

    public MyReceipt get(int adapterPosition) {

        return cartList.get(adapterPosition);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        this.onItemClickListener = onItemClickListener;
    }

    public List<MyReceipt> getList() {
        return cartList;
    }

    public int getSize() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.orderId)
        TextView orderId;
        @BindView(R.id.order_date)
        TextView orderDate;
        @BindView(R.id.order_status)
        TextView order_status;
        @BindView(R.id.order_type)
        TextView orderType;
        @BindView(R.id.order_view)
        TextView order_view;
        @BindView(R.id.line_view)
        View lineView;


        public MyViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

        }
    }


    public MyReceiptListAdapter(Context context, List<MyReceipt> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    public void updateList(List<MyReceipt> cartList) {
        this.cartList = new ArrayList<>();
        this.cartList.addAll(cartList);

        notifyDataSetChanged();
    }

    public void updateData(MyReceipt notification, int position) {

        this.cartList.set(position, notification);
        notifyItemChanged(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_receipt_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MyReceipt item = cartList.get(position);
        String status = item.getOrderStatus();
        holder.orderId.setText(item.getOrderId());
        holder.orderDate.setText(item.getOrderDate());
        holder.order_status.setText(status);
        holder.order_view.setText(item.getView());
        holder.orderType.setText(item.getOrderType());

        if (status.toLowerCase().equalsIgnoreCase("completed")) {

            holder.lineView.setBackgroundColor(ContextCompat.getColor(holder.lineView.getContext(), R.color.green_500));

        } else {

            holder.lineView.setBackgroundColor(ContextCompat.getColor(holder.lineView.getContext(), R.color.yellow));

        }

        AnimUtils.setFadeAnimation(holder.itemView);

    }


    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public int getCount() {

        return cartList != null && !cartList.isEmpty() ? cartList.size() : 0;
    }

    public void removeItem(int position) {
        cartList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(MyReceipt item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }


}
