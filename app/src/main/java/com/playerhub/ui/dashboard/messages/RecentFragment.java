package com.playerhub.ui.dashboard.messages;


import android.annotation.SuppressLint;
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
import com.playerhub.ui.dashboard.messages.recent.OnRecyclerItemClickListener;
import com.playerhub.ui.dashboard.messages.recent.RecentAdapter;
import com.playerhub.utils.Utils;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends MessageBaseFragment implements OnRecyclerItemClickListener {


    private static final String KEY_CONTACT_NAME = "contact_name";

    private static final int REQUEST_CODE = 234;

    @BindView(R.id.message_view)
    RecyclerView messageView;


    @BindView(R.id.create_group)
    FloatingActionButton floatingActionButton;

    Unbinder unbinder;

    private String contactName = "recent";

    private RecentAdapter recentAdapter;


    public RecentFragment() {
        // Required empty public constructor
    }


    public static RecentFragment getInstance(String name) {

        RecentFragment fragment = new RecentFragment();

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

                ((MessagesFragment) getParentFragment()).mFilter.setVisibility(View.GONE);


            }
        }
    }


    @OnClick({R.id.create_group})
    public void onCreateGroup(View view) {

        startActivity(new Intent(getContext(), CreateGroupChatActivity.class));


    }

    @Override
    public void onResume() {
        super.onResume();
        callRecentContact();

    }

    private void callRecentContact() {

        if (getArguments() != null)
            contactName = getArguments().getString(KEY_CONTACT_NAME);

        getAllUsersFromFirebase(contactName);

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
    public int getLayoutByID() {
        return 0;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_list_recent, container, false);
        unbinder = ButterKnife.bind(this, view);

        recentAdapter = new RecentAdapter(getContext(), new ArrayList<Object>(), this);

        messageView.setLayoutManager(new LinearLayoutManager(getContext()));

        messageView.setNestedScrollingEnabled(false);
//        productCategoryListView.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.GRID));
        messageView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.offset));

        messageView.setAdapter(recentAdapter);

        showFabGroupCreateButton(floatingActionButton);


        return view;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void onRetryOrCallApi() {

    }

    @Override
    public void onManuallyParseError(Response<?> response, boolean isToastMsg) {

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


        if (recentAdapter != null) {
            recentAdapter.clearAll();

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.ARG_CONVERSATION)
                    .child(Preferences.INSTANCE.getMsgUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            addGroupAndUsers(dataSnapshot);
                        }
                    });

        }
    }


    private void addGroupAndUsers(DataSnapshot dataSnapshot) {

        List<ConversationsLayout> conversationsList = new ArrayList<>();

        for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
            try {
                ConversationsLayout value = dataSnapshotChild.getValue(ConversationsLayout.class);
                conversationsList.add(value);
            } catch (DatabaseException e) {
                Log.e(TAG, "onDataChange: " + e.getMessage());
            }
        }


//        for (int i = 0; i < conversationsList.size(); i++) {
//
//
//            Conversations.Last_Conversation lastConversation = conversationsList.get(i).getLast_conversation();
//
//            Log.e(TAG, "addGroupAndUsers: " + new Gson().toJson(lastConversation));
//
//        }


//
        Collections.sort(conversationsList, new Comparator<ConversationsLayout>() {
            @Override
            public int compare(ConversationsLayout o1, ConversationsLayout o2) {


                if (o2.getLast_conversation() != null && o1.getLast_conversation() != null) {
                    return getTime(o2.getLast_conversation().getTimestamp()).compareTo(getTime(o1.getLast_conversation().getTimestamp()));
                } else {
                    return 0;
                }
            }
        });


        getUserList(conversationsList);

    }


    public static Date getTime(long timestamp) {
        return new Date(timestamp);
    }

    private void getUserList(final List<ConversationsLayout> list) {


//        Log.e(TAG, "getUserList: id " + Preferences.INSTANCE.getMsgUserId());

        recentAdapter.clearAll();

//        final List<Object> objectList = new ArrayList<>();

//
//        Observable.fromIterable(list).map(new Function<ConversationsLayout, Object>() {
//        })


        for (int i = 0; i < list.size(); i++) {

            final ConversationsLayout conversations = list.get(i);

            if (conversations != null && !TextUtils.isEmpty(conversations.getTypeName())) {

                if (!Utils.check(conversations.getTypeName(), "group")) {

                    List<String> users = conversations.getUsers();


                    recentAdapter.add(new User());

                    if (users != null && !users.isEmpty()) {

                        String userId = "";

                        if (!Utils.check(users.get(0), Preferences.INSTANCE.getMsgUserId())) {

                            userId = users.get(0);

                        } else {

                            userId = users.get(1);
                        }

                        final int finalI = i;
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child(Constants.ARG_USERS)
                                .child(userId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try {
                                            User value = dataSnapshot.getValue(User.class);
//                                            recentAdapter.add(value);
//                                            if (value == null) {
//
//                                                recentAdapter.remove(finalI);
//                                            } else {
////                                            value.setTimestamp(conversations.getTimestamp());
                                            recentAdapter.update(finalI, value);
//                                            }
                                        } catch (DatabaseException e) {
                                            Log.e(TAG, "onDataChange: " + e.getMessage());
                                        }
                                    }
                                });
                    }
                } else {

//                    objectList.add(conversations);
                    recentAdapter.add(conversations);

                }

            }

        }


//        Collections.sort(objectList, new Comparator<Object>() {
//            @Override
//            public int compare(Object o1, Object o2) {
//
//
//                if (o1 instanceof ConversationsLayout && o2 instanceof ConversationsLayout) {
//
//                    return getTime(((ConversationsLayout) o2).getTimestamp()).compareTo(getTime(((ConversationsLayout) o1).getTimestamp()));
//                } else {
//                    return getTime(((User) o2).getTimestamp()).compareTo(getTime(((User) o1).getTimestamp()));
//                }
//            }
//
//
//        });
//
//
//        recentAdapter.add(objectList);

    }

    @Override
    public void refreshData() {
        updateAdapter();
    }

    @Override
    public void showFilteredList(String teamName) {

    }

    @Override
    public void searchData(String s) {

        Log.e(TAG, "searchData: ");

        if (recentAdapter != null && recentAdapter.getFilter() != null)
            recentAdapter.getFilter().filter(s);
    }

    @Override
    public void OnItemClick(View view, Object o, int position) {


        if (o instanceof User) {

            callUserChat((User) o);
        } else if (o instanceof ConversationsLayout) {

            callGroupChat((ConversationsLayout) o);

        }


    }


    private void callUserChat(User user) {

        Intent intent = new Intent(getContext(), ChatActivity.class);

        intent.putExtra("user", user);

        startActivity(intent);

    }

    private void callGroupChat(ConversationsLayout conversationsLayout) {

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

        Intent intent = new Intent(getContext(), GroupChatActivity.class);

        intent.putExtra("user", (Serializable) conversations);

        startActivity(intent);

    }
}
