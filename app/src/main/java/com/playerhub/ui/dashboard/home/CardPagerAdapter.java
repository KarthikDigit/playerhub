package com.playerhub.ui.dashboard.home;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.network.response.NotificationApi;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<AnnouncementApi.Datum> mData;
    private float mBaseElevation;

    private OnItemClickListener onItemClickListener;

    public CardPagerAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(AnnouncementApi.Datum item) {
        mViews.add(null);
        mData.add(item);
    }

    public void remove(int pos) {
        mViews.remove(pos);
        mData.remove(pos);
    }

    public AnnouncementApi.Datum getItem(int pos) {

        return mData.get(pos);
    }

    public int getSize() {
        return mData.size();
    }

    public void updateList(List<AnnouncementApi.Datum> list) {

        for (int i = 0; i < list.size(); i++) {
            mViews.add(null);
        }


        mData.addAll(list);

        if (!list.isEmpty()) {

            AnnouncementApi.Datum datum = new AnnouncementApi.Datum();
            datum.setMessage("See more announcement");

            mViews.add(null);
            mData.add(datum);
        }

        notifyDataSetChanged();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.notification_home_display_row, container, false);

        if (position == mData.size() - 1) {
            view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.notification_see_more_row, container, false);

//
////            if (position == mData.size() - 1) {
//
//            float dp = view.getContext().getResources().getDimension(R.dimen._120sdp);
//
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) dp, ViewGroup.LayoutParams.MATCH_PARENT);
//
//            view.setLayoutParams(params);
//
//
////            }
//
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {

                    onItemClickListener.OnItemClick(position);
                }
            }
        });

        container.addView(view, 0);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);


//        if (mBaseElevation == 0) {
//            mBaseElevation = cardView.getCardElevation();
//        }

//        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);


        ImageView delete = (ImageView) view.findViewById(R.id.delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onItemClickListener != null) {

                    onItemClickListener.OnItemClick(position);
                }
            }
        });


//        LinearLayout anoun = view.findViewById(R.id.load_more_anou);
//
//        if (position == mData.size() - 1) {
//
//            anoun.setVisibility(View.VISIBLE);
//        } else {
//            anoun.setVisibility(View.GONE);
//        }
//
//
//        anoun.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onItemClickListener != null) {
//
//                    onItemClickListener.OnItemMoreClick(position);
//                }
//            }
//        });

        mViews.set(position, cardView);


        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(AnnouncementApi.Datum item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        titleTextView.setText(item.getMessage());
    }


    public interface OnItemClickListener {

        void OnItemClick(int pos);

        void OnItemDeleteClick(int pos);

        void OnItemMoreClick(int pos);


    }

}