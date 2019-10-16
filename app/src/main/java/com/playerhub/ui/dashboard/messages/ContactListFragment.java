package com.playerhub.ui.dashboard.messages;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
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
import com.playerhub.ui.dashboard.messages.holder.GenreAdapter;
import com.playerhub.ui.dashboard.messages.models.GroupName;
import com.playerhub.ui.dashboard.profile.MyCallBack;
import com.playerhub.utils.Utility;
import com.playerhub.utils.Utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactListFragment extends MessageBaseFragment implements GenreAdapter.OnItemClickListener {


    private static final String KEY_CONTACT_NAME = "contact_name";

    @BindView(R.id.message_view)
    RecyclerView messageView;
    @BindView(R.id.errorMsg)
    TextView mErrorMsg;
    @BindView(R.id.create_group)
    FloatingActionButton floatingActionButton;

    Unbinder unbinder;

    private String contactName = "recent";

    //create the ItemAdapter holding your Items
//    ItemAdapter<ContactListApi.Datum> itemAdapter = new ItemAdapter<ContactListApi.Datum>();
//    //create the managing FastAdapter, by passing in the itemAdapter
//    FastAdapter fastAdapter = FastAdapter.with(itemAdapter);


    private GenreAdapter genreAdapter;

    private Map<String, List<ContactListApi.Datum>> listMap = new LinkedHashMap<>();

    private List<ContactListApi.Datum> searchFilterList = new ArrayList<>();

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {

            if (getParentFragment() instanceof MessagesFragment) {

                ((MessagesFragment) getParentFragment()).mFilter.setVisibility(View.VISIBLE);


            }
        }
    }

    @Override
    public int getLayoutByID() {
        return R.layout.fragment_contact_list;
    }


    @Override
    protected void initViews() {


        messageView.setLayoutManager(new LinearLayoutManager(getContext()));

//        productCategoryListView.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.GRID));
        messageView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.offset));


        genreAdapter = new GenreAdapter(new ArrayList<GroupName>());
//        //create the ItemAdapter holding your Items
//        ItemAdapter<ProductCategory> itemAdapter = new ItemAdapter<ProductCategory>();
//        //create the managing FastAdapter, by passing in the itemAdapter
//        FastAdapter fastAdapter = FastAdapter.with(itemAdapter);

        //set our adapters to the RecyclerView
        messageView.setAdapter(genreAdapter);

        //set the items to your ItemAdapter


//        fastAdapter.withOnClickListener(new OnClickListener<ContactListApi.Datum>() {
//            @Override
//            public boolean onClick(@Nullable View v, IAdapter adapter, ContactListApi.Datum item, int position) {
//
//
//                Intent intent = new Intent(v.getContext(), ChatActivity.class);
//
//                intent.putExtra("user", (Parcelable) item);
//
//                startActivity(intent);
//
//                return false;
//            }
//        });


        showFabGroupCreateButton(floatingActionButton);

        onRetryOrCallApi();


    }

    @Override
    protected void onRetryOrCallApi() {
        if (getArguments() != null)
            contactName = getArguments().getString(KEY_CONTACT_NAME);

        getAllUsersFromFirebase(contactName);
    }

    @Override
    public void onManuallyParseError(Response<?> response, boolean isToastMsg) {

        showViewError("Something went wrong ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        unbinder.unbind();
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


        RetrofitAdapter.getNetworkApiServiceClient().fetchContactList(Preferences.INSTANCE.getAuthendicate()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyCallBack<ContactListApi>(getContext(), this, true, false) {
                    @Override
                    public void onSuccess(ContactListApi value) {


                        Log.e(TAG, "onSuccess: " + new Gson().toJson(value));

                        List<ContactListApi.Datum> data = value.getData();

                        filterContactList(data);

//                        itemAdapter.clear();
//                        itemAdapter.setNewList(data, false);
                        showErorMsg("", false);

                        showViewContent();


                    }
                });

    }


    private void filterContactList(List<ContactListApi.Datum> datumList) {

        listMap.put(Utils.Capitalize("all"), datumList);

        Set<String> teamNameWithoutDuplicate = new HashSet<>();

        for (ContactListApi.Datum d : datumList
                ) {


            teamNameWithoutDuplicate.add(Utils.Capitalize(d.getTeam()));

        }

        List<String> teamNameList = new ArrayList<>(teamNameWithoutDuplicate);

        for (int i = 0; i < teamNameList.size(); i++) {

            String teamName = teamNameList.get(i);

            List<ContactListApi.Datum> filteredList = new ArrayList<>();

            for (int j = 0; j < datumList.size(); j++) {

                ContactListApi.Datum datum = datumList.get(j);

                if (teamName.toLowerCase().equalsIgnoreCase(datum.getTeam().toLowerCase())) {

                    filteredList.add(datum);
                }

            }

            listMap.put(teamName, filteredList);

        }

        setFilteredList(listMap);


        filterBasedOnType(listMap.get(Utils.Capitalize("all")), true);


//        searchFilterList.clear();
//        searchFilterList.addAll(listMap.get(Utils.Capitalize("all")));

    }


    private void filterBasedOnType(List<ContactListApi.Datum> datumList, boolean isAdd) {

        if (isAdd) {
            searchFilterList.clear();
            searchFilterList.addAll(datumList);
        }

        if (datumList != null && !datumList.isEmpty()) {

            Set<String> gName = new HashSet<>();

            for (int i = 0; i < datumList.size(); i++) {

                ContactListApi.Datum datum = datumList.get(i);

                gName.add(datum.getType1());

            }

            List<String> nameList = new ArrayList<>(gName);

            Collections.sort(nameList);

            List<GroupName> groupNameList = new ArrayList<>();


            for (int i = 0; i < nameList.size(); i++) {

                String gn = nameList.get(i);

                List<ContactListApi.Datum> datumList1 = new ArrayList<>();

                for (int j = 0; j < datumList.size(); j++) {

                    ContactListApi.Datum datum = datumList.get(j);


                    if (gn.toLowerCase().equalsIgnoreCase(datum.getType1().toLowerCase())) {

                        datumList1.add(datum);
                    }
                }


                groupNameList.add(new GroupName(Utils.Capitalize(gn), datumList1));

            }


            genreAdapter = new GenreAdapter(groupNameList);

            messageView.setAdapter(genreAdapter);

            genreAdapter.setOnItemClickListener(this);



        }
    }


    private void setFilteredList(Map<String, List<ContactListApi.Datum>> filteredMap) {

        List<String> names = new ArrayList<>(filteredMap.keySet());
        if (getParentFragment() instanceof MessagesFragment) {
            ((MessagesFragment) getParentFragment()).teamNameList.clear();
            ((MessagesFragment) getParentFragment()).teamNameList.addAll(names);
        }

    }


    @Override
    public void showFilteredList(String teamName) {

        List<ContactListApi.Datum> list = listMap.get(teamName);

//        itemAdapter.clear();
//        itemAdapter.setNewList(list, false);
        filterBasedOnType(list, true);
        showErorMsg("", false);

        showViewContent();

    }

    private void showErorMsg(String msg, boolean isError) {

        if (isError) {

            messageView.setVisibility(View.GONE);
            mErrorMsg.setVisibility(View.VISIBLE);

            mErrorMsg.setText(msg);

        } else {

            messageView.setVisibility(View.VISIBLE);
            mErrorMsg.setVisibility(View.GONE);

        }

    }

    @Override
    public void refreshData() {
        updateAdapter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        genreAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        genreAdapter.onRestoreInstanceState(savedInstanceState);
    }

    //    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        adapter.onRestoreInstanceState(savedInstanceState);
//    }
    @Override
    public void searchData(final String s) {

//        itemAdapter.filter(s);
//
//        itemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<ContactListApi.Datum>() {
//            @Override
//            public boolean filter(ContactListApi.Datum item, @Nullable CharSequence constraint) {
//                return item.getName().toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
//            }
//        });


        Observable.just(searchFilterList).map(new Function<List<ContactListApi.Datum>, List<ContactListApi.Datum>>() {
            @Override
            public List<ContactListApi.Datum> apply(List<ContactListApi.Datum> datumList) throws Exception {

//                Log.e(TAG, "apply: " + datumList.size());
                if (s.length() > 0) {
                    List<ContactListApi.Datum> d = new ArrayList<>();

                    for (int i = 0; i < datumList.size(); i++) {
                        ContactListApi.Datum datum = datumList.get(i);


                        String name = datum.getName().toLowerCase();
                        String s1 = s.toLowerCase();

//                    Log.e(TAG, "apply: " + s1.contains(name));


                        if (name.contains(s1)) {

                            d.add(datum);
                        }

                    }

                    return d;
                } else {

                    return datumList;
                }

//                Log.e(TAG, "apply: " + d.size());

//                return d;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ContactListApi.Datum>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ContactListApi.Datum> datumList) {


//                        Log.e(TAG, "onNext: " + datumList.size());

                        filterBasedOnType(datumList, false);
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
    public void OnItemClick(ContactListApi.Datum datum, int flatpos, int pos, View view) {

        Intent intent = new Intent(view.getContext(), ChatActivity.class);

        intent.putExtra("user", (Parcelable) datum);

        startActivity(intent);

    }
}
