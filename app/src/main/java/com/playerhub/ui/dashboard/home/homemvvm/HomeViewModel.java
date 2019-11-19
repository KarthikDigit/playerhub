package com.playerhub.ui.dashboard.home.homemvvm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.util.Log;

import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.Coach;
import com.playerhub.network.response.EventListApi.Data;
import com.playerhub.network.response.EventListApi.EventListResponseApi;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.network.response.Kid;
import com.playerhub.network.response.KidsAndCoaches;
import com.playerhub.network.response.NotificationApi;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.dashboard.home.ParentChild;
import com.playerhub.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends ViewModel {


    private static final String TAG = "HomeViewModel";


    private MutableLiveData<Boolean> checkIsLoggedAsCoach;

    private MutableLiveData<Integer> notificationCount;

    private MutableLiveData<List<ParentChild>> parentChildList;

    private MutableLiveData<List<UpcommingEvent>> eventList;


    public LiveData<Boolean> getCheckIsLoggedAsCoach() {

        if (checkIsLoggedAsCoach == null) checkIsLoggedAsCoach = new MutableLiveData<>();

        checkCoach();

        return checkIsLoggedAsCoach;
    }

    private void checkCoach() {

        if (Preferences.INSTANCE.getUserType().toLowerCase().equalsIgnoreCase("coach".toLowerCase())) {

            checkIsLoggedAsCoach.setValue(true);
        } else {
            checkIsLoggedAsCoach.setValue(false);
        }

    }

    public LiveData<Integer> getNotificationCount() {

        if (notificationCount == null) notificationCount = new MutableLiveData<>();

        loadNotificationCount();

        return notificationCount;
    }

    private void loadNotificationCount() {


        RetrofitAdapter.getNetworkApiServiceClient().getAllNotification(Preferences.INSTANCE.getAuthendicate())


                .map(new Function<NotificationApi, Integer>() {
                    @Override
                    public Integer apply(NotificationApi notificationApi) throws Exception {

                        NotificationApi.Data data = notificationApi.getData();

                        if (data != null) {

                            List<NotificationApi.Data.Notification> notificationList = notificationApi.getData().getNotifications();

                            if (notificationList != null && !notificationList.isEmpty()) {

                                int count = 0;

                                for (NotificationApi.Data.Notification notification : notificationList
                                        ) {
                                    if (notification.getSeen() == 0) {
                                        count++;
                                    }
                                }

                                return count;

                            }
                        }

                        return 0;
                    }
                })
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer count) {

                        notificationCount.setValue(count);

                    }

                    @Override
                    public void onError(Throwable e) {

                        notificationCount.setValue(0);

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public LiveData<List<ParentChild>> getParentChildList() {

        if (parentChildList == null) parentChildList = new MutableLiveData<>();

        loadParentChild();

        return parentChildList;
    }

    public LiveData<List<UpcommingEvent>> getEventList() {

        if (eventList == null) eventList = new MutableLiveData<>();

        loadEventList();

        return eventList;
    }

    private void loadEventList() {

        RetrofitAdapter.getNetworkApiServiceClient().fetchAllEvents(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EventListResponseApi>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(EventListResponseApi response) {
                        if (response != null && response.getSuccess()) {
                            if (response.getData() != null) {
                                setEventData(response.getData());
//                                setUpComingEventData(response.getData().getUpcommingEvents());
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        setEventData(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void setEventData(Data data) {

        if (data != null) {
            List<UpcommingEvent> events = new ArrayList<>();


            List<UpcommingEvent> todayList = data.getTodayEvents();
            List<UpcommingEvent> upcommingEventList = data.getUpcommingEvents();
            if (todayList != null && !todayList.isEmpty()) {

                events.addAll(todayList);

            }
            if (upcommingEventList != null && !upcommingEventList.isEmpty()) {

                events.addAll(upcommingEventList);

            }


            if (!events.isEmpty()) {

                eventList.setValue(getOnlyFiveInTheList(events));

            } else {

                eventList.setValue(new ArrayList<UpcommingEvent>());

            }
        } else {

            eventList.setValue(new ArrayList<UpcommingEvent>());

        }

    }

    protected String getString(String s) {

        return s != null && s.length() > 0 ? s : "";

    }

    private void loadParentChild() {




        if (Utils.compareString(Preferences.INSTANCE.getUserType(), "coach")) {


            List<ParentChild> childList = new ArrayList<>();
            ParentChild parentChild = new ParentChild();
            parentChild.setName(Preferences.INSTANCE.getUserName());
            parentChild.setImgUrl(Preferences.INSTANCE.getLogo());
            parentChild.setWhoIs(getString(Preferences.INSTANCE.getUserType()));
            parentChild.setType(ParentChild.TYPE.PARENT);
            childList.add(parentChild);
            parentChildList.setValue(childList);


        } else {


            RetrofitAdapter.getNetworkApiServiceClient().fetchKids(Preferences.INSTANCE.getAuthendicate())
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<KidsAndCoaches>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(KidsAndCoaches value) {

                            try {
                                if (value != null && value.getData() != null) {

                                    List<Kid> kid = value.getData().getKids();

                                    List<ParentChild> childList = new ArrayList<>();
//                                showToast("item "+kid.size());
                                    if (kid != null && !kid.isEmpty()) {
//                                    parentChildPagerAdapter.clearAll();

                                        for (Kid kid1 : kid
                                                ) {
                                            Coach coach = kid1.getCoach();

                                            ParentChild parentChild = new ParentChild();
                                            parentChild.setType(ParentChild.TYPE.CHILD);
                                            parentChild.setWhoIs("Kid");
                                            parentChild.setId(kid1.getId());
                                            parentChild.setImgUrl(kid1.getAvatarImage());
                                            parentChild.setName(kid1.getFirstname() + " " + kid1.getLastname());
                                            parentChild.setCoachName(coach.getFirstname() + " " + coach.getLastname());
                                            parentChild.setCoachImage(coach.getAvatarImage());
                                            parentChild.setTeamName(kid1.getTeamName());
                                            childList.add(parentChild);

                                        }

                                        parentChildList.setValue(childList);

                                    }

                                }

                            } catch (Exception e) {

                                Log.e(TAG, "Exception: " + e.getMessage());
                                parentChildList.setValue(new ArrayList<ParentChild>());
                            }


                        }

                        @Override
                        public void onError(Throwable e) {

                            Log.e(TAG, "onNextkids: " + e.getMessage());
                            parentChildList.setValue(new ArrayList<ParentChild>());

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }

    }

    public static <T> List<T> getOnlyFiveInTheList(List<T> list) {
        int s = list.size();
        if (s > 5) {
            list.subList(5, list.size()).clear();
        }
        return list;
    }
}
