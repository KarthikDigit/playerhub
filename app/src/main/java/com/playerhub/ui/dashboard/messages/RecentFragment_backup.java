package com.playerhub.ui.dashboard.messages;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.playerhub.R;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.recyclerHelper.ItemOffsetDecoration;
import com.playerhub.ui.dashboard.chat.ChatActivity;
import com.playerhub.ui.dashboard.chat.GroupChatActivity;
import com.playerhub.ui.dashboard.chat.creategroupchat.CreateGroupChatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment_backup extends MessageBaseFragment {


    private static final String KEY_CONTACT_NAME = "contact_name";

    private static final int REQUEST_CODE = 234;

    @BindView(R.id.message_view)
    RecyclerView messageView;

    @BindView(R.id.person_view)
    RecyclerView personView;

    @BindView(R.id.create_group)
    FloatingActionButton floatingActionButton;

    Unbinder unbinder;

    private String contactName = "recent";

    //create the ItemAdapter holding your Items
    ItemAdapter itemAdapter = new ItemAdapter();
    //create the managing FastAdapter, by passing in the itemAdapter
    FastAdapter fastAdapter = FastAdapter.with(itemAdapter);

    //create the ItemAdapter holding your Items
    ItemAdapter personAdapter = new ItemAdapter();
    //create the managing FastAdapter, by passing in the itemAdapter
    FastAdapter personfastAdapter = FastAdapter.with(personAdapter);

    public RecentFragment_backup() {
        // Required empty public constructor
    }


    public static RecentFragment_backup getInstance(String name) {

        RecentFragment_backup fragment = new RecentFragment_backup();

        Bundle bundle = new Bundle();

        bundle.putString(KEY_CONTACT_NAME, name);

        fragment.setArguments(bundle);

        return fragment;

    }


    @OnClick({R.id.create_group})
    public void onCreateGroup(View view) {

        startActivity(new Intent(getContext(), CreateGroupChatActivity.class));


    }

    @Override
    public void onResume() {
        super.onResume();


        if (getArguments() != null)
            contactName = getArguments().getString(KEY_CONTACT_NAME);


        getAllUsersFromFirebase(contactName);

//        if (contactName != null && !TextUtils.isEmpty(contactName))
//            getAllUsersFromFirebase(contactName);
//        else Log.e(TAG, "onActivityResult: went wrong getAllUsersFromFirebase");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            if (contactName != null && !TextUtils.isEmpty(contactName))
                getAllUsersFromFirebase(contactName);
            else Log.e(TAG, "onActivityResult: went wrong getAllUsersFromFirebase");

        }

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_list_recent, container, false);
        unbinder = ButterKnife.bind(this, view);

        messageView.setLayoutManager(new LinearLayoutManager(getContext()));
        personView.setLayoutManager(new LinearLayoutManager(getContext()));
        personView.setNestedScrollingEnabled(false);
        messageView.setNestedScrollingEnabled(false);
//        productCategoryListView.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.GRID));
        messageView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.offset));
        personView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.offset));

        messageView.setAdapter(fastAdapter);

        personView.setAdapter(personfastAdapter);

        personfastAdapter.withOnClickListener(new OnClickListener() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter adapter, IItem item, int position) {

                if (item instanceof User) {
                    Log.e(TAG, "onClick: " + ((User) item).id + " " + ((User) item).name);

                    Intent intent = new Intent(v.getContext(), ChatActivity.class);

                    intent.putExtra("user", (User) item);

                    startActivity(intent);
                }

                return false;
            }
        });


        fastAdapter.withOnClickListener(new OnClickListener() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter adapter, IItem item, int position) {
                ConversationsLayout conversationsLayout = (ConversationsLayout) item;
                Conversations conversations = new Conversations();
                conversations.setTitle(conversationsLayout.getTitle());
                conversations.setMessage_id(conversationsLayout.getMessage_id());
                conversations.setUsers(conversationsLayout.getUsers());
                conversations.setType(conversationsLayout.getTypeName());
                conversations.setRequest_flag(conversationsLayout.getRequest_flag());
                conversations.setUnread(conversationsLayout.getUnread());
                conversations.setStatus(conversationsLayout.isStatus());
                conversations.setLast_conversation(conversationsLayout.getLast_conversation());
                conversations.setTimestamp(conversationsLayout.getTimestamp());
                conversations.setIs_typing(conversationsLayout.getIs_typing());

                Log.e(TAG, "onClick: conversations " + new Gson().toJson(item));
                Intent intent = new Intent(v.getContext(), GroupChatActivity.class);

                intent.putExtra("user", (Serializable) conversations);

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


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void updateAdapter() {

        getAllUsersFromFirebase(contactName);
    }


    public void getAllUsersFromFirebase(String name) {


//        showToast("Recent Called");

        itemAdapter.clear();
        personAdapter.clear();
//
//
//        Observable observable = RetrofitAdapter.getNetworkApiServiceClient().fetchContactList(Preferences.INSTANCE.getAuthendicate());
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId());


        Log.e(TAG, "getAllUsersFromFirebase: " + Preferences.INSTANCE.getMsgUserId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());

                List<ConversationsLayout> conversationsList = new ArrayList<>();

                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {

                    try {
//
                        ConversationsLayout value = dataSnapshotChild.getValue(ConversationsLayout.class);

                        Log.e(TAG, "onDataChange:  recent " + new Gson().toJson(value));

                        conversationsList.add(value);
//
                    } catch (DatabaseException e) {

                        Log.e(TAG, "onDataChange: " + e.getMessage());
                    }


                }


                getUserList(conversationsList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void getUserList(final List<ConversationsLayout> list) {

        itemAdapter.clear();
        personAdapter.clear();

        fastAdapter.notifyAdapterDataSetChanged();
        personfastAdapter.notifyAdapterDataSetChanged();


        for (int i = 0; i < list.size(); i++) {

            ConversationsLayout conversations = list.get(i);

            if (conversations != null && !TextUtils.isEmpty(conversations.getTypeName())) {

                if (!conversations.getTypeName().toLowerCase().equalsIgnoreCase("group")) {


                    List<String> users = conversations.getUsers();

                    if (users != null && !users.isEmpty()) {

                        String userId = "";

                        if (!users.get(0).equalsIgnoreCase(Preferences.INSTANCE.getMsgUserId())) {

                            userId = users.get(0);

                        } else {

                            userId = users.get(1);
                        }

                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(userId);


                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User value = dataSnapshot.getValue(User.class);
                                personAdapter.add(value);
                                Log.e(TAG, "onDataChange: user " + new Gson().toJson(value));

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {


                    itemAdapter.add(conversations);

                }

            }

        }

    }

    @Override
    public void refreshData() {
        updateAdapter();
    }
}
