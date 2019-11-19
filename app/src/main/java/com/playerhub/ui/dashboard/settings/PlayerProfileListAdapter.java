package com.playerhub.ui.dashboard.settings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.network.response.Kid;
import com.playerhub.network.response.NotificationApi;
import com.playerhub.ui.base.OnItemClickListener;
import com.playerhub.utils.AnimUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerProfileListAdapter extends RecyclerView.Adapter<PlayerProfileListAdapter.MyViewHolder> {
    private Context context;
    private List<Kid> cartList;

    private OnItemClickListener<Kid> onItemClickListener;

    public Kid get(int adapterPosition) {

        return cartList.get(adapterPosition);
    }

    public void setOnItemClickListener(OnItemClickListener<Kid> onItemClickListener) {

        this.onItemClickListener = onItemClickListener;
    }

    public List<Kid> getList() {
        return cartList;
    }

    public int getSize() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.icon)
        ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public PlayerProfileListAdapter(Context context, List<Kid> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    public void updateList(List<Kid> cartList) {
        this.cartList = new ArrayList<>();
        this.cartList.addAll(cartList);

        notifyDataSetChanged();
    }

    public void updateData(Kid notification, int position) {

        this.cartList.set(position, notification);
        notifyItemChanged(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_profile_view_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Kid item = cartList.get(position);

        holder.name.setText(item.getFirstname() + "'s Profile");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null)
                    onItemClickListener.onItemClicked(v, item, position);

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

    public void restoreItem(Kid item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }


}
