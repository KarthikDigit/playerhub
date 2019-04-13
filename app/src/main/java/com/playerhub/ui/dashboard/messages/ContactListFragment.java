package com.playerhub.ui.dashboard.messages;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.playerhub.R;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.ContactListApi;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.recyclerHelper.ItemOffsetDecoration;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.dashboard.chat.ChatActivity;
import com.playerhub.ui.dashboard.chat.creategroupchat.CreateGroupChatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactListFragment extends MessageBaseFragment {


    private static final String KEY_CONTACT_NAME = "contact_name";

    @BindView(R.id.message_view)
    RecyclerView messageView;

    @BindView(R.id.create_group)
    FloatingActionButton floatingActionButton;

    Unbinder unbinder;

    private String contactName = "recent";

    //create the ItemAdapter holding your Items
    ItemAdapter<ContactListApi.Datum> itemAdapter = new ItemAdapter<ContactListApi.Datum>();
    //create the managing FastAdapter, by passing in the itemAdapter
    FastAdapter fastAdapter = FastAdapter.with(itemAdapter);

    public ContactListFragment() {
        // Required empty public constructor
    }


    public static ContactListFragment getInstance(String name) {

        ContactListFragment fragment = new ContactListFragment();

        Bundle bundle = new Bundle();

        bundle.putString(KEY_CONTACT_NAME, name);

        fragment.setArguments(bundle);

        return fragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        messageView.setLayoutManager(new LinearLayoutManager(getContext()));

//        productCategoryListView.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.GRID));
        messageView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.offset));

//        //create the ItemAdapter holding your Items
//        ItemAdapter<ProductCategory> itemAdapter = new ItemAdapter<ProductCategory>();
//        //create the managing FastAdapter, by passing in the itemAdapter
//        FastAdapter fastAdapter = FastAdapter.with(itemAdapter);

        //set our adapters to the RecyclerView
        messageView.setAdapter(fastAdapter);

        //set the items to your ItemAdapter


        fastAdapter.withOnClickListener(new OnClickListener<ContactListApi.Datum>() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter adapter, ContactListApi.Datum item, int position) {


                Intent intent = new Intent(v.getContext(), ChatActivity.class);

                intent.putExtra("user", item);

                startActivity(intent);

                return false;
            }
        });


        try {

            if (Preferences.INSTANCE.getUserType().toLowerCase().equalsIgnoreCase("coach".toLowerCase())) {

                floatingActionButton.setVisibility(View.VISIBLE);
            }

        } catch (NullPointerException e) {

        }

        if (getArguments() != null)
            contactName = getArguments().getString(KEY_CONTACT_NAME);

        getAllUsersFromFirebase(contactName);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.create_group})
    public void onCreateGroup(View view) {

        startActivity(new Intent(getContext(), CreateGroupChatActivity.class));


    }


    public void updateAdapter() {

//        if (messageView != null && messageView.getAdapter() != null)
//            messageView.getAdapter().notifyDataSetChanged();
        getAllUsersFromFirebase(contactName);
    }


    public void getAllUsersFromFirebase(String name) {

//        showToast("Recent Called");
//        itemAdapter.clear();
//
//
//        Observable observable = RetrofitAdapter.getNetworkApiServiceClient().fetchContactList(Preferences.INSTANCE.getAuthendicate());
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Observable observable = RetrofitAdapter.getNetworkApiServiceClient().fetchContactList(Preferences.INSTANCE.getAuthendicate()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        observable.subscribe(new Observer<ContactListApi>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ContactListApi value) {

                List<ContactListApi.Datum> data = value.getData();

                itemAdapter.setNewList(data, false);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


//        observable.concatMap(new Function<ContactListApi, Observable<ContactListApi.Datum>>() {
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

    }

    @Override
    protected void refreshData() {
        updateAdapter();
    }
}