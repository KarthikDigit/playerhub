package com.playerhub.ui.dashboard.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.utils.ImageUtility;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParentChildPagerAdapter extends PagerAdapter {


    private Context mContext;
    private List<ParentChild> mList;
    private LayoutInflater mLayoutInflater;
    private OnItemClicklistener onItemClicklistener;

    public ParentChildPagerAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList<>();
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAll(List<ParentChild> mList) {
        this.mList = new ArrayList<>();
        this.mList.addAll(mList);
        notifyDataSetChanged();
    }

    public void add(ParentChild parentChild) {
        this.mList.add(parentChild);
        notifyDataSetChanged();
    }


    public ParentChild getItem(int pos) {

        return mList.get(pos);
    }


    public void setOnItemClicklistener(OnItemClicklistener onItemClicklistener) {
        this.onItemClicklistener = onItemClicklistener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull View container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.row_parent_child_coach_view, container, false);


//        ViewGroup.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//
//        ((FrameLayout.LayoutParams) params).leftMargin=16;
//        ((FrameLayout.LayoutParams) params).rightMargin=16;
//
//        itemView.setLayoutParams(params);

        final ParentChild parentChild = mList.get(position);

        CircleImageView profileImage = itemView.findViewById(R.id.profile_image);
        CircleImageView coachProfileImage = itemView.findViewById(R.id.profile_coach_image);
        TextView name = itemView.findViewById(R.id.name);
        TextView coach_name = itemView.findViewById(R.id.coach_name);
        TextView teeam_name = itemView.findViewById(R.id.team_name);
        TextView whois = itemView.findViewById(R.id.whois);

        ImageUtility.loadImage(profileImage, parentChild.getImgUrl());

        if (!TextUtils.isEmpty(parentChild.getCoachImage())) {
            coachProfileImage.setVisibility(View.VISIBLE);
            ImageUtility.loadImage(coachProfileImage, parentChild.getCoachImage());
        } else coachProfileImage.setVisibility(View.GONE);
        name.setText(parentChild.getName());

        if (!TextUtils.isEmpty(parentChild.getCoachName())) {
            coach_name.setVisibility(View.VISIBLE);
            coach_name.setText(parentChild.getCoachName());
        } else coach_name.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(parentChild.getTeamName())) {
            teeam_name.setVisibility(View.VISIBLE);
            teeam_name.setText(parentChild.getTeamName());
        } else teeam_name.setVisibility(View.GONE);


        whois.setText(parentChild.getWhoIs());

        container.addView(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClicklistener != null)
                    onItemClicklistener.OnItemClick(mList.get(position), position);

            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }


    public interface OnItemClicklistener {

        void OnItemClick(ParentChild parentChild, int position);

    }


}
