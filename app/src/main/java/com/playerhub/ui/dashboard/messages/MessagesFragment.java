package com.playerhub.ui.dashboard.messages;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.playerhub.R;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.viewpageadapter.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends BaseFragment {


    Unbinder unbinder;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private ViewPagerAdapter pagerAdapter;

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        unbinder = ButterKnife.bind(this, view);

        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        pagerAdapter.addFragment(RecentFragment.getInstance("recent"), "Recent");
        pagerAdapter.addFragment(ContactListFragment.getInstance("contacts"), "Contacts");

        viewPager.setAdapter(pagerAdapter);

        tabs.setupWithViewPager(viewPager);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public void updateAdapter() {

        if (viewPager != null) {
//            pagerAdapter.notifyDataSetChanged();
            int pos = viewPager.getCurrentItem();

            MessageBaseFragment fragment = (MessageBaseFragment) pagerAdapter.getItem(pos);

            fragment.refreshData();
        }


//        if (messageView != null && messageView.getAdapter() != null)
//            messageView.getAdapter().notifyDataSetChanged();

    }


    public void getAllUsersFromFirebase() {


//        itemAdapter.clear();
//
//
//        Observable observable = RetrofitAdapter.getNetworkApiServiceClient().fetchContactList(Preferences.INSTANCE.getAuthendicate());
//        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//        setObservableAndObserver(observable, new Observers<ContactListApi>(getContext(), true) {
//            @Override
//            protected void onSuccess(ContactListApi response) {
//
//                if (response.getSuccess()) {
//
//
//                    List<ContactListApi.Datum> data = response.getData();
//
//                    for (ContactListApi.Datum datum : data
//                            ) {
//
//                        loadData(reference, datum);
//                    }
//
//                }
//
//            }
//
//            @Override
//            protected void onFail(Throwable e) {
//
//                showToast("There is no contacts ");
//                Log.e(TAG, "onFail: " + e.getMessage());
//
//            }
//        });


//        Observable observable1 = RetrofitAdapter.getNetworkApiServiceClient().fetchContactList(Preferences.INSTANCE.getAuthendicate());
//
//
//        observable1.concatMap(new Function<ContactListApi, Observable<ContactListApi.Datum>>() {
//            @Override
//            public Observable<ContactListApi.Datum> apply(final ContactListApi o) {
//
//
//                return Observable.create(new ObservableOnSubscribe<ContactListApi.Datum>() {
//                    @Override
//                    public void subscribe(ObservableEmitter<ContactListApi.Datum> e) throws Exception {
//                        List<ContactListApi.Datum> data = o.getData();
//
//                        for (int i = 0; i < data.size(); i++) {
//
//                            if (!e.isDisposed()) {
//                                e.onNext(data.get(i));
//                            }
//                        }
//
//                        if (!e.isDisposed()) {
//                            e.onComplete();
//                        }
//                    }
//                });
//            }
//        }).concatMap(new Function<ContactListApi.Datum, Observable<ContactListApi.Datum>>() {
//            @Override
//            public Observable<ContactListApi.Datum> apply(final ContactListApi.Datum o) throws Exception {
//                return Observable.create(new ObservableOnSubscribe<ContactListApi.Datum>() {
//                    @Override
//                    public void subscribe(final ObservableEmitter<ContactListApi.Datum> e) throws Exception {
//
//
//                        final List<String> users = new ArrayList<>();
//                        users.add(o.getUserID());
//                        users.add(Preferences.INSTANCE.getMsgUserId());
//
//                        reference.child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                long c = 0;
//
//                                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
//
//
//                                    try {
//                                        Conversations value = dataSnapshotChild.getValue(Conversations.class);
//                                        if (value != null && value.getUsers().containsAll(users)) {
//
//                                            long v = value.getUnread();
//                                            c += v;
//                                        }
//                                    } catch (NullPointerException e) {
//
//                                        Log.e(TAG, "onDataChange: " + e.getMessage());
//
//                                    } catch (DatabaseException e) {
//                                        Log.e(TAG, "onDataChange: " + e.getMessage());
//                                    }
//
//                                }
//                                o.setNotification(c);
//
//                                if (!e.isDisposed()) {
//                                    e.onNext(o);
//                                }
//
//                                if (!e.isDisposed()) {
//                                    e.onComplete();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//
//                                Log.e(TAG, "onCancelled: message count " + databaseError.getMessage());
//
//                            }
//                        });
//
//
//                    }
//                });
//            }
//        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ContactListApi.Datum>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(ContactListApi.Datum value) {
//
//
//                itemAdapter.add(value);
//                Log.e(TAG, "onNext: feteched " + new Gson().toJson(value));
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//                Log.e(TAG, "onError: " + e.getMessage());
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//
//
////        showLoading();
//
////
////        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
////
////        reference.child(Constants.ARG_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                hideLoading();
////                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
////                List<User> users = new ArrayList<>();
////                while (dataSnapshots.hasNext()) {
////                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
////                    User user = dataSnapshotChild.getValue(User.class);
////                    if (!TextUtils.equals(user.id, Preferences.INSTANCE.getMsgUserId())) {
////                        users.add(user);
////
////                        loadData(reference, user);
////                    }
////
////
////                }
////
//////                itemAdapter.add(users);
//////                itemAdapter.remove(users.size() - 1);
////
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////                hideLoading();
////
////                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
////
////            }
////        });
//    }
//
//
//    private void loadData(DatabaseReference databaseReference, final ContactListApi.Datum user) {
//        final List<String> users = new ArrayList<>();
//        users.add(user.getUserID());
//        users.add(Preferences.INSTANCE.getMsgUserId());
//
//        databaseReference.child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                long c = 0;
//
//                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
//
//
//                    try {
//                        Conversations value = dataSnapshotChild.getValue(Conversations.class);
//                        if (value != null && value.getUsers().containsAll(users)) {
//
//                            long v = value.getUnread();
//                            c += v;
//                        }
//                    } catch (NullPointerException e) {
//
//                        Log.e(TAG, "onDataChange: " + e.getMessage());
//
//                    } catch (DatabaseException e) {
//                        Log.e(TAG, "onDataChange: " + e.getMessage());
//                    }
//
//                }
//                user.setNotification(c);
//
//
////                itemAdapter.getAdapterPosition(user);
//
//                itemAdapter.add(user);
////                itemAdapter.remove(users.size() - 1);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//
//                Log.e(TAG, "onCancelled: message count " + databaseError.getMessage());
//
//            }
//        });
//
//
    }


//    private void loadData() {
//
////        final List<User> list = new ArrayList<>();
////
////        User messageModel = new User();
////        messageModel.name = "Linda Natasha";
////        messageModel.description = "Text message are the best way to inform parents when plans change,we make it easy to scheule mass texts right from your phone.";
////        messageModel.date_time = "6:32 PM";
////        messageModel.count = "2";
////        list.add(messageModel);
////
////        messageModel = new User();
////        messageModel.name = "Steven Swap";
////        messageModel.description = "Need to coordiante bus schedules for away games?\nTha's how the Atlanda Braves have...";
////        messageModel.date_time = "4:32 PM";
////        messageModel.count = "1";
////        list.add(messageModel);
////
////
////        messageModel = new User();
////        messageModel.name = "Craza Heldan";
////        messageModel.description = "That's how hr Atlanda coordiante bus schedules for away games?\nTha's how the Atlanda Braves have...";
////        messageModel.date_time = "2:12 PM";
////        messageModel.count = "0";
////        list.add(messageModel);
////
////        messageModel = new User();
////        messageModel.name = "Richard Steve";
////        messageModel.description = "That's how hr Atlanda coordiante bus schedules for away games?\nTha's how the Atlanda Braves have...";
////        messageModel.date_time = "YESTERDAY";
////        messageModel.count = "0";
////        list.add(messageModel);
////
////        messageModel = new User();
////        messageModel.name = "Carza Heldan";
////        messageModel.description = "That's how hr Atlanda coordiante bus schedules for away games?\nTha's how the Atlanda Braves have...";
////        messageModel.date_time = "YESTERDAY";
////        messageModel.count = "0";
////        list.add(messageModel);
////
////        messageModel = new User();
////        messageModel.name = "Carza Heldan";
////        messageModel.description = "That's how hr Atlanda coordiante bus schedules for away games?\nTha's how the Atlanda Braves have...";
////        messageModel.date_time = "YESTERDAY";
////        messageModel.count = "0";
////        list.add(messageModel);
////
////        messageModel = new User();
////        messageModel.name = "Carza Heldan";
////        messageModel.description = "That's how hr Atlanda coordiante bus schedules for away games?\nTha's how the Atlanda Braves have...";
////        messageModel.date_time = "YESTERDAY";
////        messageModel.count = "0";
////        list.add(messageModel);
////
////        messageModel = new User();
////        messageModel.name = "Carza Heldan";
////        messageModel.description = "That's how hr Atlanda coordiante bus schedules for away games?\nTha's how the Atlanda Braves have...";
////        messageModel.date_time = "YESTERDAY";
////        messageModel.count = "0";
////        list.add(messageModel);
////
////        itemAdapter.add(list);
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tabs, R.id.viewPager})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tabs:
                break;
            case R.id.viewPager:
                break;
        }
    }
}
