package com.playerhub.ui.dashboard.messages;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
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
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
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
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
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


        showFabGroupCreateButton(floatingActionButton);

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
//        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Observable<ContactListApi> observable = RetrofitAdapter.getNetworkApiServiceClient().fetchContactList(Preferences.INSTANCE.getAuthendicate()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        observable.subscribe(new Observer<ContactListApi>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ContactListApi value) {

                List<ContactListApi.Datum> data = value.getData();
                itemAdapter.clear();
                itemAdapter.setNewList(data, false);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    public void refreshData() {
        updateAdapter();
    }


}
