package com.playerhub.ui.dashboard.home;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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



    public void setOnItemClicklistener(OnItemClicklistener onItemClicklistener) {
        this.onItemClicklistener = onItemClicklistener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ConstraintLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.row_parent_child_coach_view, container, false);

        final ParentChild parentChild = mList.get(position);

        CircleImageView profileImage = itemView.findViewById(R.id.profile_image);
        TextView name = itemView.findViewById(R.id.name);
        TextView whois = itemView.findViewById(R.id.whois);

        ImageUtility.loadImage(profileImage, parentChild.getImgUrl());
        name.setText(parentChild.getName());
        whois.setText(parentChild.getWhoIs());

        container.addView(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClicklistener != null)
                    onItemClicklistener.OnItemClick(parentChild, position);

            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }


    public interface OnItemClicklistener {

        void OnItemClick(ParentChild parentChild, int position);

    }


}
