package com.playerhub.ui.dashboard.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.NotificationApi;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.dashboard.DashBoardActivity;
import com.playerhub.ui.dashboard.notification.NotificationActivity;
import com.playerhub.ui.dashboard.profile.ProfileDetailsActivity;
import com.playerhub.utils.ImageUtility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.noti_img)
    ImageView notiImg;
    @BindView(R.id.textView2)
    TextView notificationCount;
    Unbinder unbinder;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.textView4)
    TextView team;
    @BindView(R.id.imageView3)
    ImageView settingsmenu;

    private CircleImageView mProfileImage;
    private Animation blink;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, view);
        mProfileImage = (CircleImageView) view.findViewById(R.id.profile_image);

        ImageUtility.loadImage(mProfileImage, Preferences.INSTANCE.getLogo());
        name.setText(getString(Preferences.INSTANCE.getUserName()));


        team.setText(getString(Preferences.INSTANCE.getUserType()));

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProfileDetailsActivity.class));
            }
        });

        unbinder = ButterKnife.bind(this, view);
        blink = AnimationUtils.loadAnimation(getContext(), R.anim.blink);

        return view;
    }


    @OnClick({R.id.noti_img, R.id.textView2})
    public void onClick(View view) {


        startActivity(new Intent(getContext(), NotificationActivity.class));

    }


    public void setNotificationCount() {

        Log.e(TAG, "setNotificationCount: ");
//        notificationCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        final int count = Preferences.INSTANCE.getNotCount();
        notificationCount.setVisibility(View.VISIBLE);
        notificationCount.setText(count + "");

        notificationCount.startAnimation(blink);

        blink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                notificationCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.imageView3, R.id.back})
    public void onViewClicked() {
//        if (getActivity() != null && getActivity() instanceof DashBoardActivity)
//            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(R.id.navigation_settings);


    }

    @Override
    public void onResume() {
        super.onResume();
        setNotificationCount();
//        callNotificationCount();

    }


//    private void callNotificationCount() {
//
//        Log.e(TAG, "callNotificationCount: called ");
//
//        final Observable<NotificationApi> observableNoti = RetrofitAdapter.getNetworkApiServiceClient().getAllNotification(Preferences.INSTANCE.getAuthendicate()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
//
//        observableNoti.subscribe(new Consumer<NotificationApi>() {
//            @Override
//            public void accept(NotificationApi notificationApi) throws Exception {
//
//                List<NotificationApi.Data.Notification> list = notificationApi.getData().getNotifications() != null ? notificationApi.getData().getNotifications() : new ArrayList<NotificationApi.Data.Notification>();
//
//                int count = 0;
//
//                for (NotificationApi.Data.Notification notification : list) {
//
//                    if (notification.getSeen() == 0) {
//                        count++;
//                    }
//                }
//
//                setNotificationCount(count);
//
//            }
//        });
//
//
////        Observable.create(new ObservableOnSubscribe<NotificationApi>() {
////
////            @Override
////            public void subscribe(final ObservableEmitter<NotificationApi> e) {
////                observableNoti.subscribe(new Consumer<NotificationApi>() {
////                    @Override
////                    public void accept(NotificationApi notificationApi) throws Exception {
////
////                        if (!e.isDisposed()) {
////                            e.onNext(notificationApi);
////                        }
////                    }
////                }, new Consumer<Throwable>() {
////                    @Override
////                    public void accept(Throwable throwable) throws Exception {
////
////                        e.isDisposed();
////                        Log.e(TAG, "accept: " + throwable.getMessage());
////                    }
////                });
////
////            }
////        }).map(new Function<NotificationApi, Integer>() {
////            @Override
////            public Integer apply(NotificationApi notificationApi) throws Exception {
////                List<NotificationApi.Data.Notification> list = notificationApi.getData().getNotifications() != null ? notificationApi.getData().getNotifications() : new ArrayList<NotificationApi.Data.Notification>();
////
////                int count = 0;
////
////                for (NotificationApi.Data.Notification notification : list) {
////
////                    if (notification.getSeen() == 0) {
////                        count++;
////                    }
////                }
////
////                return count;
////            }
////        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
////                .subscribe(new Consumer<Integer>() {
////                    @Override
////                    public void accept(Integer integer) throws Exception {
////                        setNotificationCount(integer);
////                    }
////                }, new Consumer<Throwable>() {
////                    @Override
////                    public void accept(Throwable throwable) throws Exception {
////
////                        Log.e(TAG, "accept: " + throwable.getMessage());
////                    }
////                });
//
//
//    }


}
