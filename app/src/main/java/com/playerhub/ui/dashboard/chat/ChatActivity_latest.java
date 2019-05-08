package com.playerhub.ui.dashboard.chat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.playerhub.R;
import com.playerhub.cameraorgallery.CameraAndGallary;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.ContactListApi;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.Messages;
import com.playerhub.ui.dashboard.messages.User;
import com.playerhub.utils.KeyboardUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity_latest extends ChatBaseActivity implements ChatRecyclerAdapter.OnImageClickListener, CameraAndGallary.CameraAndGallaryCallBack {

    private static final String TAG = "ChatActivity";

    private static final String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int REQUEST_CAMERA_PERMISSION_CODE = 234;

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

    @BindView(R.id.temp_image)
    ImageView tempImage;

    private Bitmap tempBitmap = null;

    private CameraAndGallary cameraAndGallary;

    private ChatRecyclerAdapter mChatRecyclerAdapter;
    private Conversations conversations = new Conversations();

    private Object user;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        cameraAndGallary = new CameraAndGallary(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        mRecyclerViewChat.setLayoutManager(layoutManager);

        mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Messages>(), this);
        mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        final List<String> users = new ArrayList<>();


        user = getIntent().getSerializableExtra("user");
        showLoading();

        String title = "Chat";
        String secondUser = "";

        if (user instanceof ContactListApi.Datum) {

            ContactListApi.Datum user1 = (ContactListApi.Datum) user;
            title = user1.getName() != null ? user1.getName() : "Chat";
            secondUser = user1.getUserID();

        } else if (user instanceof User) {

            User user1 = (User) user;
            title = user1.name != null ? user1.name : "Chat";
            secondUser = user1.id;

        }


        users.add(secondUser);
        users.add(Preferences.INSTANCE.getMsgUserId());

        Log.e(TAG, "onCreate:  ids " + Preferences.INSTANCE.getMsgUserId() + " secondUser " + secondUser);


        setBackButtonEnabledAndTitle(title);

        conversations.setUsers(users);
        conversations.setStatus(true);
        conversations.setTimestamp(new Date().getTime());

        final String id = databaseReference.push().getKey();

        conversations.setMessage_id(id);

        databaseReference.child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).getRef().addListenerForSingleValueEvent(new ValueListener() {

            @Override
            public void onSuccess(@NonNull DataSnapshot dataSnapshot) {


                boolean isAlreadyCreated = checkAlreadyConversationMessageIdCreated(dataSnapshot, users);


                if (!isAlreadyCreated) {

                    createConversationMessageId(databaseReference, id);

                }


                Log.e(TAG, "onSuccess: isAlready " + isAlreadyCreated + "   " + new Gson().toJson(conversations));


                getAllMessages(conversations.getMessage_id());


            }

        });


        comments.addTextChangedListener(new TextWatcherListener() {
            @Override
            public void onChanged(CharSequence s) {

                setIsTyping(databaseReference, s.toString(), false);

            }

            @Override
            public void afterChanged(Editable s) {


                setIsTyping(databaseReference, "", true);
            }
        });


    }


    private void setIsTyping(final DatabaseReference databaseReference, String charSequence, boolean isTyping) {


        if (isTyping) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setIsTypingNone(databaseReference);

                }
            }, 1000);


        } else {

            if (charSequence.length() > 0) {

                setIsTypingYes(databaseReference);

            } else {
                setIsTypingNone(databaseReference);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        setIsTypingNone(databaseReference);
    }

    private void setIsTypingYes(DatabaseReference databaseReference) {


        Conversations.Is_Typing is_typing = new Conversations.Is_Typing();
        is_typing.setName(Preferences.INSTANCE.getUserName());
        is_typing.setId(Preferences.INSTANCE.getMsgUserId());

        changeIsTypingInFireBase(databaseReference, is_typing);


    }

    private void setIsTypingNone(DatabaseReference databaseReference) {

        Conversations.Is_Typing is_typing = new Conversations.Is_Typing();
        is_typing.setName("");
        is_typing.setId("");

        changeIsTypingInFireBase(databaseReference, is_typing);


    }


    private void changeIsTypingInFireBase(DatabaseReference databaseReference, Conversations.Is_Typing is_typing) {
        conversations.setIs_typing(is_typing);
        if (user instanceof ContactListApi.Datum) {
            databaseReference.child(Constants.ARG_CONVERSATION).child(((ContactListApi.Datum) user).getUserID()).child(conversations.getMessage_id()).setValue(conversations);
        } else if (user instanceof User) {
            databaseReference.child(Constants.ARG_CONVERSATION).child(((User) user).id).child(conversations.getMessage_id()).setValue(conversations);
        }
    }


    private void createConversationMessageId(DatabaseReference databaseReference, String id) {

        conversations.setType("private");
        databaseReference.child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(id).setValue(conversations);

    }

    private boolean checkAlreadyConversationMessageIdCreated(DataSnapshot
                                                                     dataSnapshot, List<String> users) {

        boolean isThere = false;

        try {

            //                List<Conversations> messagesList = new ArrayList<>();
            for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                Conversations value = dataSnapshotChild.getValue(Conversations.class);

                if (value != null && value.getType() != null && !TextUtils.isEmpty(value.getType()) && !value.getType().toLowerCase().equalsIgnoreCase("group")) {
                    Log.e(TAG, "onDataChange: " + new Gson().toJson(value.getMessage_id()));
                    List<String> ne = new ArrayList<>();
                    ne.add(users.get(1));
                    ne.add(users.get(0));

                    try {
                        if (users != null && (value.getUsers().containsAll(users) || value.getUsers().containsAll(ne))) {
                            conversations = value;
                            isThere = true;
                            return isThere;
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG, "onDataChange: " + e.getMessage());
                    }

                }

            }
        } catch (DatabaseException e) {

            Log.e(TAG, "onDataChange: " + e.getMessage());
        }


        return isThere;


    }

    private void fetchWhoIsTyping(String messageID) {

        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(messageID).getRef().addValueEventListener(new ValueListener() {
            @Override
            public void onSuccess(@NonNull DataSnapshot dataSnapshot) {

                updateWhoIsTypingUI(dataSnapshot);


            }
        });

    }

    private void updateWhoIsTypingUI(DataSnapshot dataSnapshot) {

        Conversations value = dataSnapshot.getValue(Conversations.class);
        boolean isTyping = false;
        String nameOfThePerson = "";

        if (value != null && value.getIs_typing() != null && !value.getIs_typing().getId().equalsIgnoreCase("")) {
            isTyping = true;
            nameOfThePerson = value.getIs_typing().getName() + " is typing...";
        }

        editorName.setVisibility(isTyping ? View.VISIBLE : View.GONE);
        chatPersonName.setText(nameOfThePerson);


    }


    private void getAllMessages(String messageID) {

        setUnReadCount(messageID);

        fetchWhoIsTyping(messageID);

        fetchMessageFromFirebaseDataBase(messageID);


    }

    private void setUnReadCount(String messageID) {

        conversations.setIs_typing(new Conversations.Is_Typing());
        conversations.setUnread(0);

        //Update unRead messsages to 0
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(messageID).setValue(conversations);


    }


    private void fetchMessageFromFirebaseDataBase(String messageID) {

//        Log.e(TAG, "fetchMessageFromFirebaseDataBase: " + messageID);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        databaseReference.child(Constants.ARG_MESSAGES).child(messageID).getRef().addChildEventListener(new ChildListener() {
            @Override
            public void onAdded(DataSnapshot dataSnapshot) {

                addMessageToList(dataSnapshot);
            }
        });
    }


    private void addMessageToList(DataSnapshot dataSnapshot) {
//        Log.e(TAG, "addMessageToList: ");
        Messages value = dataSnapshot.getValue(Messages.class);
//        Log.e(TAG, "onDataChange: called " + new Gson().toJson(value));
        mChatRecyclerAdapter.add(value);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);

    }


    @OnClick({R.id.send_comments, R.id.attachment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.send_comments:


//                if (tempBitmap != null) {
//
//                    imageUploadToFirebase();
//
//                } else {

                sendMessages(null);
//                }

                break;

            case R.id.attachment:


                addAttachment();

                break;
        }
    }


    private void addAttachment() {

        cameraPermission();


    }


    @AfterPermissionGranted(REQUEST_CAMERA_PERMISSION_CODE)
    public void cameraPermission() {
        if (EasyPermissions.hasPermissions(this, CAMERA_PERMISSION)) {
//            // Already have permission, do the thing

            cameraAndGallary.selectImage();
//            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_rationale),
                    REQUEST_CAMERA_PERMISSION_CODE, CAMERA_PERMISSION);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraAndGallary.onActivityResult(requestCode, resultCode, data);
    }

    private void sendMessages(String imageUrl) {

        String msg = comments.getText().toString();

        msg = TextUtils.isEmpty(msg) ? "" : msg;

        Messages messages = new Messages();
        messages.setMsg(msg);
//        messages.setImg_url(imageUrl);
        if (!TextUtils.isEmpty(imageUrl)) {

            messages.setImg_url(imageUrl);

        }

        showLoading();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final String id = databaseReference.push().getKey();


        messages.setName(Preferences.INSTANCE.getUserName());

        messages.setSender(Preferences.INSTANCE.getMsgUserId());
        messages.setTimestamp(new Date().getTime());
        messages.setUpload_status(1);

        databaseReference.child(Constants.ARG_MESSAGES).child(conversations.getMessage_id()).child(id).setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isComplete()) {
                    long count = conversations.getUnread() + 1;
                    conversations.setUnread(count);
                    Conversations.Last_Conversation last_conversation = new Conversations.Last_Conversation();
                    last_conversation.setContent(comments.getText().toString());
                    last_conversation.setSender(Preferences.INSTANCE.getUserName());
                    last_conversation.setStatus(true);
                    last_conversation.setTimestamp(new Date().getTime());
                    last_conversation.setType("text");
                    conversations.setLast_conversation(last_conversation);


                    //Update unRead messsages to 0
                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(0)).child(conversations.getMessage_id()).setValue(conversations);


                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(conversations.getMessage_id()).child("last_conversation").setValue(last_conversation);


                    comments.setText("");
                    comments.clearFocus();

                    KeyboardUtils.hideKeyboard(ChatActivity_latest.this);


                } else if (task.isCanceled()) {

                    Log.e(TAG, "onComplete: message send something wrong ");
                }
            }
        });


        if (user instanceof ContactListApi.Datum) {

//            showToast("Test");

            ContactListApi.Datum datum = (ContactListApi.Datum) user;

            String token_Id = datum.getTokenId();

            if (token_Id != null) {

                String m = comments.getText().toString();

                if (TextUtils.isEmpty(m)) {

                    m = "You have a message from " + datum.getName();
                }

                sendPushMessage("New Message", m, token_Id);
            }

//            else {
//
//                showToast("There is no token id");
//            }

        } else if (user instanceof User) {

            User user1 = (User) user;

            String token_Id = user1.token_id;

            if (token_Id != null && !TextUtils.isEmpty(token_Id)) {


                String m = comments.getText().toString();

                if (TextUtils.isEmpty(m)) {

                    m = "You have a message from " + user1.name;
                }

                sendPushMessage("New Message", m, token_Id);

            }

//            else {
//
//                showToast("There is no token id");
//
//            }

        }

    }

    private void sendPushMessage(String title, String body, String sender_id) {


        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);

        Map<String, Object> rawParameters = new Hashtable<String, Object>();
        rawParameters.put("data", new JSONObject(data));
        rawParameters.put("to", sender_id);


        RetrofitAdapter.getNetworkApiServiceClient().sendPustNotification(new JSONObject(rawParameters).toString())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        Log.e(TAG, "accept: " + s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Log.e(TAG, "accept: error " + throwable.getMessage() + "  " + ((HttpException) throwable).response().errorBody().string());

                    }
                });


    }

//    private void sendPushMessage(String title, String body, String sender_id) {
//
//
//        HashMap data = new HashMap<>();
//        data.put("title", title);
//        data.put("body", body);
//
//        Map rawParameters = new Hashtable();
//        rawParameters.put("data", new JSONObject(data));
//        rawParameters.put("to", sender_id);
//
//
//        RetrofitAdapter.getNetworkApiServiceClient().sendPustNotification(new JSONObject(rawParameters).toString())
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//
//                        Log.e(TAG, "accept: " + s);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//
//                        Log.e(TAG, "accept: error " + throwable.getMessage() + "  " + ((HttpException) throwable).response().errorBody().string());
//
//                    }
//                });
//
//
//    }

    @Override
    public void onImageShow(String image_url) {

        FullScreenImageView fullScreenImageView = FullScreenImageView.getInstance(image_url);

        fullScreenImageView.show(getSupportFragmentManager(), "FullScreenImage");


    }

    @Override
    public void onSelectFromGalleryResult(Bitmap bitmap) {


//        tempBitmap = bitmap;
//
//        tempImage.setImageBitmap(bitmap);
//        tempImage.setVisibility(View.VISIBLE);

        imageUploadToFirebase(bitmap);


    }


    private void imageUploadToFirebase(Bitmap bitmap) {


        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference().child("photos");

        String name = new SimpleDateFormat("yyyyddMMHHmmss").format(new Date()) + "" + System.nanoTime();

        Log.e(TAG, "onSelectFromGalleryResult: " + name);

        final StorageReference imagesRef = storageRef.child(name);


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        showLoading();

        final UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
//                tempBitmap = null;
//                tempImage.setImageBitmap(null);
//                tempImage.setVisibility(View.GONE);
                hideLoading();
                Log.e(TAG, "onFailure: " + exception.getMessage());

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                hideLoading();
//                tempBitmap = null;
//                tempImage.setImageBitmap(null);
//                tempImage.setVisibility(View.GONE);
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        hideLoading();
                        Log.e(TAG, "onSuccess: " + uri.toString());
//                Log.e(TAG, "onSuccess: " + uri.getPath());

                        sendMessages(uri.toString());
                    }
                });
            }
        });

    }
}
