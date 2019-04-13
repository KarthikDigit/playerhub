package com.playerhub.ui.dashboard.notification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.playerhub.R;
import com.playerhub.network.response.NotificationApi;
import com.playerhub.ui.base.OnItemClickListener;
import com.playerhub.ui.dashboard.home.CardPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.support.constraint.Constraints.TAG;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {
    private Context context;
    private List<NotificationApi.Data.Notification> cartList;

    private OnItemClickListener onItemClickListener;

    public NotificationApi.Data.Notification get(int adapterPosition) {

        return cartList.get(adapterPosition);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        this.onItemClickListener = onItemClickListener;
    }

    public List<NotificationApi.Data.Notification> getList() {
        return cartList;
    }

    public int getSize() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.description)
        TextView description;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            description = view.findViewById(R.id.description);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }


    public NotificationListAdapter(Context context, List<NotificationApi.Data.Notification> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    public void updateList(List<NotificationApi.Data.Notification> cartList) {
        this.cartList = new ArrayList<>();
        this.cartList.addAll(cartList);

        notifyDataSetChanged();
    }

    public void updateData(NotificationApi.Data.Notification notification, int position) {

        this.cartList.set(position, notification);
        notifyItemChanged(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final NotificationApi.Data.Notification item = cartList.get(position);
        holder.description.setText(item.getDescription());
//        holder.description.setText(item.getDescription());
//        holder.price.setText("₹" + item.getPrice());

//        Glide.with(context)
//                .load(item.getThumbnail())
//                .into(holder.thumbnail);

//        Log.e(TAG, "onBindViewHolder: " + new Gson().toJson(item));

        if (item.getSeen() != 0) {

            holder.description.setAlpha(.25f);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onItemClickListener != null) {


//                    item.setSeen(1);
                    notifyItemChanged(position);
                    onItemClickListener.onItemClicked(view, item, position);


                }
            }
        });


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

    public void restoreItem(NotificationApi.Data.Notification item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }


}
