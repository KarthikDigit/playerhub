package com.playerhub.ui.dashboard.home.eventdetails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.playerhub.R;
import com.playerhub.network.response.EventDetailsApi;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表适配器
 * Created by SouthernBox on 2018/1/18.
 */

public class KidsViewAdapter extends RecyclerView.Adapter<KidsViewAdapter.TextHolder> {

    private Context mContext;
    private List<EventDetailsApi.Data.Kid> mList;
    private OnItemChangeListener onItemChangeListener;

    public KidsViewAdapter(Context context, List<EventDetailsApi.Data.Kid> list, OnItemChangeListener onItemChangeListener) {
        mContext = context;
        mList = list;
        this.onItemChangeListener = onItemChangeListener;
    }

    @NonNull
    @Override
    public TextHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_kids, parent, false);
        return new TextHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TextHolder holder, final int position) {
        final EventDetailsApi.Data.Kid kid = mList.get(position);

        holder.kidName.setText(String.format("%s %s", kid.getFirstname(), kid.getLastname()));

        if (kid.getYes() == 1) {

            holder.kidYes.setChecked(true);
            holder.kidNo.setChecked(false);


        } else {

            holder.kidYes.setChecked(false);
            holder.kidNo.setChecked(true);

        }

        holder.kidsYesNo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton rb = (RadioButton) group.findViewById(checkedId);


//                && checkedId > -1
                if (null != rb) {

                    if (rb.getText().toString().toLowerCase().equalsIgnoreCase("yes")) {

                        mList.get(position).setYes(1);
                        mList.get(position).setNo(0);

                    } else {

                        mList.get(position).setYes(0);
                        mList.get(position).setNo(1);

                    }


                    if (onItemChangeListener != null)
                        onItemChangeListener.onItemChange(kid, rb.getText().toString());

//                    Toast.makeText(MainActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void update(List<EventDetailsApi.Data.Kid> kids) {

        mList = new ArrayList<>();
        mList.clear();
        mList.addAll(kids);

        notifyDataSetChanged();

    }

    class TextHolder extends RecyclerView.ViewHolder {

        TextView kidName;
        RadioButton kidYes;
        RadioButton kidNo;
        RadioGroup kidsYesNo;


        TextHolder(View itemView) {
            super(itemView);

            kidName = itemView.findViewById(R.id.kid_name);
            kidYes = itemView.findViewById(R.id.kid_yes);
            kidNo = itemView.findViewById(R.id.kid_no);
            kidsYesNo = itemView.findViewById(R.id.kid_yes_no);

        }
    }

    interface OnItemChangeListener {

        void onItemChange(EventDetailsApi.Data.Kid kid, String value);

    }
}
