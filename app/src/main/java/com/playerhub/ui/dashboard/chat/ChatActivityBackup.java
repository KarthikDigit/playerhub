package com.playerhub.ui.dashboard.chat;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.playerhub.R;
import com.playerhub.network.response.ContactListApi;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.Messages;
import com.playerhub.ui.dashboard.messages.User;
import com.playerhub.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivityBackup extends BaseActivity implements ChatRecyclerAdapter.OnImageClickListener {

    private static final String TAG = "ChatActivity";
    @BindView(R.id.chatListView)
    RecyclerView mRecyclerViewChat;
    @BindView(R.id.comments)
    AppCompatEditText comments;
    @BindView(R.id.send_comments)
    ImageView sendComments;
    @BindView(R.id.chat_person_name)
    TextView chatPersonName;
    @BindView(R.id.editor_name)
    LinearLayout editorName;

    private ChatRecyclerAdapter mChatRecyclerAdapter;
    private Conversations conversations = new Conversations();

    private Object user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        mRecyclerViewChat.setLayoutManager(layoutManager);

        mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Messages>(), this);
        mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
//        Messages chat = new Messages();
//        chat.setMsg("Hello");
//        chat.setName("Barca");
//        chat.setSender("MQ==");
//        mChatRecyclerAdapter.add(chat);
//        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final List<String> users = new ArrayList<>();


        if (getIntent().getSerializableExtra("user") instanceof ContactListApi.Datum) {
            ContactListApi.Datum user = (ContactListApi.Datum) getIntent().getSerializableExtra("user");
            this.user = user;
            setBackButtonEnabledAndTitle(user.getName() != null ? user.getName() : "Chat");

            showLoading();
//        Preferences.INSTANCE.putMsgUserId("MQ==");


//        byte[] userId = "1".getBytes();
//
//        String base64 = Base64.encodeToString(userId, Base64.NO_WRAP);
//
//
//        Log.e("Base 64 ", "onSuccess: " + base64);
//
//
//        Preferences.INSTANCE.putMsgUserId(base64);

            users.add(user.getUserID());
            users.add(Preferences.INSTANCE.getMsgUserId());
        } else if (getIntent().getSerializableExtra("user") instanceof User) {

            User user = (User) getIntent().getSerializableExtra("user");
            this.user = user;
            setBackButtonEnabledAndTitle(user.name != null ? user.name : "Chat");

            showLoading();
//        Preferences.INSTANCE.putMsgUserId("MQ==");


//        byte[] userId = "1".getBytes();
//
//        String base64 = Base64.encodeToString(userId, Base64.NO_WRAP);
//
//
//        Log.e("Base 64 ", "onSuccess: " + base64);
//
//
//        Preferences.INSTANCE.putMsgUserId(base64);

            users.add(user.id);
            users.add(Preferences.INSTANCE.getMsgUserId());

        }

        conversations.setUsers(users);
        conversations.setStatus(true);
        conversations.setTimestamp(new Date().getTime());

        final String id = databaseReference.push().getKey();

        conversations.setMessage_id(id);

//        databaseReference.child(Constants.ARG_CONVERSATION).child("OTc=").child(id).setValue(conversations);


        databaseReference.child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                hideLoading();

                boolean isThere = false;

                try {

                    //                List<Conversations> messagesList = new ArrayList<>();
                    for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                        Conversations value = dataSnapshotChild.getValue(Conversations.class);
////                    messagesList.add(value);
//                    Log.e(TAG, "onDataChange: " + new Gson().toJson(value));

                        List<String> ne = new ArrayList<>();
                        ne.add(users.get(1));
                        ne.add(users.get(0));

                        try {
                            if (value != null && users != null && (value.getUsers().containsAll(users) || value.getUsers().containsAll(ne))) {
                                conversations = value;
                                isThere = true;
                            }
                        } catch (NullPointerException e) {
                            Log.e(TAG, "onDataChange: " + e.getMessage());
                        }

                    }


                    if (!isThere) {
                        databaseReference.child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(id).setValue(conversations);
                    }

                    Log.e(TAG, "onDataChange: " + isThere);


                    getAllMessages(conversations.getMessage_id());

                } catch (DatabaseException e) {

                    Log.e(TAG, "onDataChange: " + e.getMessage());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                hideLoading();

                Log.e(TAG, "onCancelled: create conversation error  " + databaseError.getMessage());

            }
        });


        comments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                Log.e(TAG, "onTextChanged: " + i + " " + i1 + "   " + i2);

                if (charSequence.length() > 0) {

                    Conversations.Is_Typing is_typing = new Conversations.Is_Typing();

                    is_typing.setName(Preferences.INSTANCE.getUserName());
                    is_typing.setId(Preferences.INSTANCE.getMsgUserId());
//                    conversations.setRequest_flag("request");
                    conversations.setIs_typing(is_typing);

                    Log.e(TAG, "onTextChanged: " + new Gson().toJson(conversations));


                } else {
                    Conversations.Is_Typing is_typing = new Conversations.Is_Typing();

                    is_typing.setName("");
                    is_typing.setId("");
//                    conversations.setRequest_flag("request");
                    conversations.setIs_typing(is_typing);

                }

                if (user instanceof ContactListApi.Datum) {
                    databaseReference.child(Constants.ARG_CONVERSATION).child(((ContactListApi.Datum) user).getUserID()).child(conversations.getMessage_id()).setValue(conversations);

                } else if (user instanceof User) {
                    databaseReference.child(Constants.ARG_CONVERSATION).child(((User) user).id).child(conversations.getMessage_id()).setValue(conversations);


                }


            }

            @Override
            public void afterTextChanged(Editable editable) {


                Conversations.Is_Typing is_typing = new Conversations.Is_Typing();

                is_typing.setName("");
                is_typing.setId("");
//                    conversations.setRequest_flag("request");
                conversations.setIs_typing(is_typing);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (user instanceof ContactListApi.Datum) {

                            databaseReference.child(Constants.ARG_CONVERSATION).child(((ContactListApi.Datum) user).getUserID()).child(conversations.getMessage_id()).setValue(conversations);


                        } else if (user instanceof User) {

                            databaseReference.child(Constants.ARG_CONVERSATION).child(((User) user).id).child(conversations.getMessage_id()).setValue(conversations);

                        }


                    }
                }, 1000);

            }
        });


//        databaseReference.child(Constants.ARG_CONVERSATION).child("OTc=").getRef().addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//
//                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
////                List<Messages> messagesList = new ArrayList<>();
//                while (dataSnapshots.hasNext()) {
//                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
//                    Conversations value = dataSnapshotChild.getValue(Conversations.class);
//
//                }
//
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        })


//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                dataSnapshot.getRef().push().getKey();
//
//                conversations.setMessage_id(dataSnapshot.getRef().push().getKey());
//
//                databaseReference.child(Constants.ARG_CONVERSATION).child("OTc=").child(dataSnapshot.getRef().push().getKey()).setValue(conversations);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
//
//            }
//        });


//        databaseReference.child(Constants.ARG_CONVERSATION).child("OTc=").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
//
//            }
//        });


    }


    private void getAllMessages(String messageID) {


        conversations.setIs_typing(new Conversations.Is_Typing());
        conversations.setUnread(0);

        //Update unRead messsages to 0
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(messageID).setValue(conversations);


        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(messageID).getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Conversations value = dataSnapshot.getValue(Conversations.class);


                if (value != null && value.getIs_typing() != null && !value.getIs_typing().getId().equalsIgnoreCase("")) {

                    editorName.setVisibility(View.VISIBLE);

                    chatPersonName.setText(value.getIs_typing().getName() + " is typing...");

//                    Log.e(TAG, "onDataChange: isTyping true");

                } else {
//                    Log.e(TAG, "onDataChange: isTyping false");
                    chatPersonName.setText("");
                    editorName.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e(TAG, "onCancelled: '" + databaseError.getMessage());
            }
        });


//        FirebaseDatabase.getInstance()
//                .getReference()
//                .child(Constants.ARG_CHAT_ROOMS)
//                .child(messageID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Messages value = dataSnapshot.getValue(Messages.class);
////                    messagesList.add(value);
//                Log.e(TAG, "onDataChange: " + new Gson().toJson(value));
//                mChatRecyclerAdapter.add(value);
//                mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


//        Log.e(TAG, "getAllMessages: " + Preferences.INSTANCE.getMsgUserId() + "  " + user.getUserID() + " Name " + Preferences.INSTANCE.getUserName() + "  " + Preferences.INSTANCE.getUserId());
//
//        Log.e(TAG, "getAllMessages: " + messageID);
//        showLoading();
//
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        databaseReference.child(Constants.ARG_MESSAGES).child(messageID).getRef().addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                hideLoading();
//
////                List<Messages> messagesList = new ArrayList<>();
//                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
//                    Messages value = dataSnapshotChild.getValue(Messages.class);
////                    messagesList.add(value);
//                    Log.e(TAG, "onDataChange: " + new Gson().toJson(value));
//                    mChatRecyclerAdapter.add(value);
//                    mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
//                }
//
//
//            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                hideLoading();

//                List<Messages> messagesList = new ArrayList<>();
//                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                Messages value = dataSnapshot.getValue(Messages.class);
//                    messagesList.add(value);
                Log.e(TAG, "onDataChange: " + new Gson().toJson(value));
                mChatRecyclerAdapter.add(value);
                mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
//                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoading();
                Log.e(TAG, "onCancelled: fetching message error " + databaseError.getMessage());

            }
        });


    }


    @OnClick({R.id.send_comments})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.send_comments:

                sendMessages();

                break;
        }
    }


    private void sendMessages() {


        showLoading();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final String id = databaseReference.push().getKey();

        Messages messages = new Messages();

        messages.setName(Preferences.INSTANCE.getUserName());
        messages.setMsg(comments.getText().toString());
        messages.setSender(Preferences.INSTANCE.getMsgUserId());
        messages.setTimestamp(new Date().getTime());
        messages.setUpload_status(1);

        databaseReference.child(Constants.ARG_MESSAGES).child(conversations.getMessage_id()).child(id).setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isComplete()) {


                    long count = conversations.getUnread() + 1;

                    conversations.setUnread(count);


//                    Conversations newConversations = new Conversations();
//
//                    newConversations.setUnread(conversations.getUnread());
//                    newConversations.setMessage_id(conversations.getMessage_id());
//                    newConversations.setTimestamp(conversations.getTimestamp());
//                    newConversations.setRequest_flag(conversations.getRequest_flag());
//                    newConversations.setType(conversations.getType());
//                    newConversations.setStatus(true);

                    Conversations.Last_Conversation last_conversation = new Conversations.Last_Conversation();

                    last_conversation.setContent(comments.getText().toString());
                    last_conversation.setSender(Preferences.INSTANCE.getUserName());
                    last_conversation.setStatus(true);
                    last_conversation.setTimestamp(new Date().getTime());
                    last_conversation.setType("text");
//                    conversations.setIs_typing(new Conversations.Is_Typing());
                    conversations.setLast_conversation(last_conversation);


                    //Update unRead messsages to 0
                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(0)).child(conversations.getMessage_id()).setValue(conversations);


                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(conversations.getMessage_id()).child("last_conversation").setValue(last_conversation);


                    comments.setText("");
                    comments.clearFocus();

                    KeyboardUtils.hideKeyboard(ChatActivityBackup.this);


                } else if (task.isCanceled()) {

                    Log.e(TAG, "onComplete: message send something wrong ");
                }
            }
        });


//        databaseReference.child(Constants.ARG_MESSAGES).child(conversations.getMessage_id()).child(id).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                hideLoading();
//
//
//                comments.setText("");
//
//                Messages messages1 = dataSnapshot.getValue(Messages.class);
//                mChatRecyclerAdapter.add(messages1);
//
//                Log.e(TAG, "onDataChange: " + new Gson().toJson(messages1));
//                mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//
//                hideLoading();
//
//                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
//            }
//        });


//        databaseReference.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                hideLoading();
//
//                Log.e(TAG, "onDataChange: " + new Gson().toJson(dataSnapshot.getValue()));
//
//                Messages messages1 = dataSnapshot.getValue(Messages.class);
//                mChatRecyclerAdapter.add(messages1);
//                mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);

//                Log.e(TAG, "onDataChange: added ");
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                hideLoading();
//
//                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
//            }
//        });

    }

    @Override
    public void onImageShow(String image_url) {

        FullScreenImageView fullScreenImageView = FullScreenImageView.getInstance(image_url);

        fullScreenImageView.show(getSupportFragmentManager(), "FullScreenImage");


    }

    @Override
    public void updateDownloadID(String msgID, String downloadID) {

    }
}
