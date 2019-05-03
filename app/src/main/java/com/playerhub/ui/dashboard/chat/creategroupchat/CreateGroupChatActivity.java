package com.playerhub.ui.dashboard.chat.creategroupchat;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.playerhub.R;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.ContactListApi;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.Messages;
import com.playerhub.ui.dashboard.messages.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CreateGroupChatActivity extends BaseActivity implements GroupChatCreateAdapter.OnItemCallback {

    private static final String TAG = "CreateGroupChatActivity";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.display_msg)
    TextView displayMsg;
    @BindView(R.id.group_name)
    TextInputLayout groupName;
    @BindView(R.id.group_message)
    TextInputLayout groupMessage;
    @BindView(R.id.btn_create_group)
    AppCompatButton btnCreateGroup;
    @BindView(R.id.footer)
    LinearLayout footer;

    private GroupChatCreateAdapter adapter;

    private boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat);
        ButterKnife.bind(this);


        setBackButtonEnabledAndTitle("Create Group Conversation");

        adapter = new GroupChatCreateAdapter(this, new ArrayList<ContactListApi.Datum>(), this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        callContactApi();

    }

    private void callContactApi() {


        showLoading();

        RetrofitAdapter.getNetworkApiServiceClient().fetchContactList(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ContactListApi>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ContactListApi value) {

                        hideLoading();

                        List<ContactListApi.Datum> data = value.getData();

                        if (data != null && !data.isEmpty()) {

                            adapter.addNewList(data);

                            recyclerView.setVisibility(View.VISIBLE);
                            footer.setVisibility(View.VISIBLE);
                            displayMsg.setVisibility(View.GONE);

                        } else {
                            recyclerView.setVisibility(View.GONE);
                            footer.setVisibility(View.GONE);
                            displayMsg.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();

                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_create_group_chat, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.create:

                createGroupChat();

                return true;
            case R.id.select_all:


                isSelected = !isSelected;
                selectAllUnselectAll(isSelected);

//                if (isSelected) {
//
//                    isSelected = false;
//
//                    selectAllUnselectAll(false);
////                    adapter.addNewList(getAllGroupUser(false));
//
//                } else {
//                    isSelected = true;
//                    selectAllUnselectAll(true);
////                    adapter.addNewList(getAllGroupUser(true));
//                }
//
//                invalidateOptionsMenu();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void selectAllUnselectAll(boolean isSelected) {

        List<ContactListApi.Datum> datumList = adapter.getList();

        for (int i = 0; i < datumList.size(); i++) {

            datumList.get(i).setChecked(isSelected);

        }

        adapter.addNewList(datumList);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

//        menu.findItem(R.id.create).setVisible(isSelected);
//        menu.findItem(R.id.select_all).setVisible(isSelected);


        return super.onPrepareOptionsMenu(menu);
    }


    @OnClick({R.id.btn_create_group})
    public void onGroupCreate(View view) {

        createGroupChat();

    }

    private void createGroupChat() {

        String gName = groupName.getEditText().getText().toString();
        String gMessage = groupMessage.getEditText().getText().toString();

        if (TextUtils.isEmpty(gName)) {

            showToast("Please enter group name");

            groupName.getEditText().requestFocus();

            return;
        }

        List<ContactListApi.Datum> groupUsers = adapter.getList();

        List<ContactListApi.Datum> tempList = new ArrayList<>();
        List<String> tempUserList = new ArrayList<>();

        for (ContactListApi.Datum groupUser : groupUsers
                ) {

            if (groupUser.isChecked()) {
                tempUserList.add(groupUser.getUserID());
                tempList.add(groupUser);
            }
        }

        if (!(tempList.size() > 1)) {


            showToast("Select atleast two members in contact list");

            return;
        }

        tempUserList.add(Preferences.INSTANCE.getMsgUserId());

//        loge("Group chat list items perfect " + tempList.size());


        createGroupConversation(gName, gMessage, tempList, tempUserList);

    }


    private void createGroupConversation(String title, final String msg, List<ContactListApi.Datum> temp_list, List<String> tempUserList) {


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String messageId = databaseReference.push().getKey();

        Conversations conversations = new Conversations();
        conversations.setTitle(title);
        conversations.setStatus(true);
        conversations.setType("group");
        conversations.setRequest_flag("request");
        conversations.setTimestamp(System.currentTimeMillis());
        conversations.setUsers(tempUserList);
        conversations.setUnread(0);
        conversations.setMessage_id(messageId);
        Conversations.Last_Conversation last_conversation = new Conversations.Last_Conversation();

        last_conversation.setStatus(false);
        last_conversation.setContent("");
        last_conversation.setSender("");
        last_conversation.setTimestamp(System.currentTimeMillis());

        conversations.setLast_conversation(last_conversation);


        databaseReference.child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(messageId).setValue(conversations).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                showToast("Successfully chat group created.");

                if (TextUtils.isEmpty(msg)) {

                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
                showToast("Something went wrong while creating chat group conversation");
            }
        });


        if (!TextUtils.isEmpty(msg)) {


            Messages messages = new Messages();
            messages.setMsg(msg);
            messages.setTimestamp(System.currentTimeMillis());
            messages.setName(Preferences.INSTANCE.getUserName());
            messages.setSender(Preferences.INSTANCE.getMsgUserId());
            messages.setTimestamp(new Date().getTime());
            messages.setUpload_status(1);

            final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();

            final String id = databaseReference1.push().getKey();


            databaseReference.child(Constants.ARG_MESSAGES).child(conversations.getMessage_id()).child(id).setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    showToast("Successfully message sent");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    showToast("Something went wrong while sending message");
                }
            });


        }


    }


    @Override
    public void onItemSelected(List<ContactListApi.Datum> list) {


//        int count = 0;

//        for (GroupUser groupUser : list
//                ) {
//
//            if (groupUser.isChecked()) {
//                count++;
//            }
//        }


//        if (count > 0) {
//
//
//            isSelected = true;
//
//        } else {
//
//            isSelected = false;
//        }
//
//        invalidateOptionsMenu();
    }
}
