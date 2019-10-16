package com.playerhub.ui.dashboard.messages.holder;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.network.response.ContactListApi;
import com.playerhub.utils.CommonUtil;
import com.squareup.picasso.Picasso;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatViewHolder extends ChildViewHolder {

    @BindView(R.id.icon)
    CircleImageView icon;
    @BindView(R.id.date_time)
    TextView dateTime;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.count)
    TextView count;
    @BindView(R.id.count_lay)
    RelativeLayout countLay;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.useris_online)
    View userIsOnline;

    public ChatViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void onBind(final ContactListApi.Datum item) {
//        StringHolder.applyToOrHide(item.description, description);

//            description.setText(item.team);
//                count.setText(item.notification + "");
        name.setText(item.getName());
//            dateTime.setText(item.date_time);

        countLay.setVisibility(View.INVISIBLE);
        if (item.getNotification() == 0) {
            countLay.setVisibility(View.INVISIBLE);
        }

//            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).getRef();
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (item.getAvatar() != null && item.getAvatar().length() > 0)
                    Picasso.get().load(item.getAvatar()).placeholder(R.drawable.progress_animation).error(R.drawable.avatar_mini).resize(120, 120).into(icon);

            }
        });


        CommonUtil.checkIsInOnline(item.getUserID(), new CommonUtil.ObserverListener<Boolean>() {
            @Override
            public void onNext(Boolean value) {

                userIsOnline.setVisibility(value ? View.VISIBLE : View.GONE);

            }

        });


        CommonUtil.getMessageCount(item.getUserID(), new CommonUtil.ObserverListener<Long>() {
            @Override
            public void onNext(Long value) {

                item.setNotification(value);
                count.setText(String.format(Locale.ENGLISH, "%d", item.getNotification()));
                countLay.setVisibility(View.VISIBLE);
                if (item.getNotification() == 0) {
                    countLay.setVisibility(View.INVISIBLE);
                }


            }
        });
    }
}
