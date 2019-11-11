package com.playerhub.ui.dashboard.chat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
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
import com.vincent.videocompressor.VideoCompress;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity extends ChatBaseActivity implements ChatRecyclerAdapter.OnImageClickListener, CameraAndGallary.CameraAndGallaryCallBack {

    private static final String TAG = "ChatActivity";

    private static final String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int REQUEST_CAMERA_PERMISSION_CODE = 234;
    private static boolean isVideo = false;

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

    private long messageCount = 0;

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
        final List<String> dummyusers = new ArrayList<>();


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

        byte[] decodeValue = Base64.decode(Preferences.INSTANCE.getMsgUserId(), Base64.DEFAULT);

        Preferences.INSTANCE.saveNotification(new String(decodeValue), null);


        users.add(secondUser);
        users.add(Preferences.INSTANCE.getMsgUserId());
        dummyusers.add(Preferences.INSTANCE.getMsgUserId());
        dummyusers.add(secondUser);

        Log.e(TAG, "onCreate:  ids " + Preferences.INSTANCE.getMsgUserId() + " secondUser " + secondUser);


        setBackButtonEnabledAndTitle(title);

        conversations.setUsers(users);
        conversations.setStatus(true);
        conversations.setTimestamp(System.currentTimeMillis());

        final String id = databaseReference.push().getKey();

        conversations.setMessage_id(id);

        databaseReference
                .child(Constants.ARG_CONVERSATION)
                .child(Preferences.INSTANCE.getMsgUserId())
                .getRef()
                .addListenerForSingleValueEvent(new ValueListener() {

                    @Override
                    public void onSuccess(@NonNull DataSnapshot dataSnapshot) {

//                        Log.e(TAG, "onSuccess: " + dataSnapshot.getChildrenCount());

                        boolean isAlreadyCreated = checkAlreadyConversationMessageIdCreated(dataSnapshot, users, dummyusers);


                        if (!isAlreadyCreated) {


//                            Log.e(TAG, "onSuccess: not there  ");
//
                            createConversationMessageId(databaseReference, id);

                        }


//                        Log.e(TAG, "onSuccess: isAlready " + isAlreadyCreated + "   " + new Gson().toJson(conversations));


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
            databaseReference.child(Constants.ARG_CONVERSATION).child(((ContactListApi.Datum) user).getUserID()).child(conversations.getMessage_id()).child("is_typing").setValue(is_typing);
        } else if (user instanceof User) {
            databaseReference.child(Constants.ARG_CONVERSATION).child(((User) user).id).child(conversations.getMessage_id()).child("is_typing").setValue(is_typing);
        }
    }


    private void createConversationMessageId(DatabaseReference databaseReference, String id) {

        conversations.setType("private");
        databaseReference.child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(id).setValue(conversations);

    }

    private boolean checkAlreadyConversationMessageIdCreated(DataSnapshot
                                                                     dataSnapshot, List<String> users, List<String> dummyusers) {

//        Log.e(TAG, "checkAlreadyConversationMessageIdCreated: " + dataSnapshot.getChildrenCount());
        boolean isThere = false;


        //                List<Conversations> messagesList = new ArrayList<>();
        for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
            try {
                Conversations value = dataSnapshotChild.getValue(Conversations.class);

                if (value != null && !TextUtils.isEmpty(value.getType()) && !value.getType().toLowerCase().equalsIgnoreCase("group")) {
//                    Log.e(TAG, "onDataChange: " + new Gson().toJson(value.getMessage_id()));
////                    List<String> ne = new ArrayList<>();
////                    ne.add(users.get(1));
////                    ne.add(users.get(0));
//
                    try {
                        if (users != null && (value.getUsers().containsAll(users) || value.getUsers().containsAll(dummyusers))) {
                            conversations = value;
                            isThere = true;
                            return isThere;
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG, "onDataChange: " + e.getMessage());
                    }
//
                }
            } catch (DatabaseException e) {

                Log.e(TAG, "onDataChange: " + e.getMessage());
            }
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

                sendMessages(null);

                break;

            case R.id.attachment:

                addAttachment();
//                showPopupwindow();
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

//            cameraAndGallary.selectImage();
            showPopupwindow();
//            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_rationale),
                    REQUEST_CAMERA_PERMISSION_CODE, CAMERA_PERMISSION);
        }
    }

    private PopupWindow popupWindow;

    private void showPopupwindow() {


        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        CircleImageView uploadCamera = popupView.findViewById(R.id.upload_camera);
        CircleImageView uploadGallery = popupView.findViewById(R.id.upload_gallery);
        CircleImageView uploadVideo = popupView.findViewById(R.id.upload_video);

        uploadCamera.setOnClickListener(onUploadViewClickListener);
        uploadGallery.setOnClickListener(onUploadViewClickListener);
        uploadVideo.setOnClickListener(onUploadViewClickListener);
//        popupView.setPadding(16, 0, 16, 0);
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
//        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        View view = findViewById(R.id.msgsentview);
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
//        popupWindow.showAtLocation(findViewById(R.id.attachment), Gravity.BOTTOM, 0, -findViewById(R.id.attachment).getHeight() + popupView.getHeight());
//        popupWindow.showAsDropDown(findViewById(R.id.attachment),0, -findViewById(R.id.attachment).getHeight() + popupView.getHeight());
//        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, -(view.getHeight() + popupView.getHeight() + 300));
        popupWindow.showAsDropDown(view, (int) convertDpToPixel(20, this), -(view.getHeight() + popupView.getHeight() + 300));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(8);
        }

//        revealShow(popupView, true, popupWindow);
        //
//        popupWindow.update(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        popupWindow.showAsDropDown(view);
//        // dismiss the popup window when touched
//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                popupWindow.dismiss();
//                return true;
//            }
//        });


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(final View view, boolean b, final PopupWindow dialog) {

//        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        final View fab = findViewById(R.id.attachment);

        int cx = (int) (fab.getX() + (fab.getWidth() / 2));
        int cy = (int) (fab.getY()) + fab.getHeight() + 56;


        if (b) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);

//            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
//                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }


    private View.OnClickListener onUploadViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (popupWindow != null) popupWindow.dismiss();

            switch (v.getId()) {


                case R.id.upload_camera:

                    cameraAndGallary.cameraIntent();

                    break;

                case R.id.upload_gallery:

                    cameraAndGallary.galleryIntent();

                    break;

                case R.id.upload_video:

                    cameraAndGallary.callVideo();

                    break;

            }

        }
    };


    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraAndGallary.onActivityResult(requestCode, resultCode, data);
    }

    private void sendMessages(String imageUrl) {
        messageCount++;
//        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).keepSynced(true);

        String msg = comments.getText().toString();

        msg = TextUtils.isEmpty(msg) ? "" : msg;

        Messages messages = new Messages();
        messages.setMsg(msg);
//        messages.setImg_url(imageUrl);
        if (!TextUtils.isEmpty(imageUrl)) {

            if (isVideo) {
                messages.setVideo_url(imageUrl);
            } else {
                messages.setImg_url(imageUrl);
            }

        } else if (TextUtils.isEmpty(msg)) {


            return;

        }

        showLoading();

        isVideo = false;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final String id = databaseReference.push().getKey();


        messages.setName(Preferences.INSTANCE.getUserName());

        messages.setSender(Preferences.INSTANCE.getMsgUserId());
        messages.setTimestamp(System.currentTimeMillis());
        messages.setUpload_status(1);


        databaseReference.child(Constants.ARG_MESSAGES).child(conversations.getMessage_id()).child(id).setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isComplete()) {
                    long count = conversations.getUnread() + 1;
//                    conversations.setUnread(count);
                    Conversations.Last_Conversation last_conversation = new Conversations.Last_Conversation();
                    last_conversation.setContent(comments.getText().toString());
                    last_conversation.setSender(Preferences.INSTANCE.getUserName());
                    last_conversation.setStatus(true);
                    last_conversation.setTimestamp(System.currentTimeMillis());
                    last_conversation.setType("text");
                    conversations.setLast_conversation(last_conversation);


                    //Update unRead messsages to 0
//                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(0)).child(conversations.getMessage_id()).setValue(conversations);


                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(conversations.getMessage_id()).child("last_conversation").setValue(last_conversation);


                    if (conversations.getUsers().get(0).toLowerCase().equalsIgnoreCase(Preferences.INSTANCE.getMsgUserId().toLowerCase())) {
                        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(1)).child(conversations.getMessage_id()).child("unread").setValue(messageCount);
//                        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(1)).child(conversations.getMessage_id()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                Conversations conversations = dataSnapshot.getValue(Conversations.class);
//
//
//                                if (conversations != null) {
//
//                                    Log.e(TAG, "onDataChange: " + new Gson().toJson(conversations));
//
//                                    Log.e(TAG, "onDataChange :1 count  " + conversations.getUnread());
//                                    long count = conversations.getUnread() + 1;
//
//                                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(1)).child(conversations.getMessage_id()).child("unread").setValue(count);
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
                    } else {

//                        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(0)).child(conversations.getMessage_id()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                Conversations conversations = dataSnapshot.getValue(Conversations.class);
//
//                                if (conversations != null) {
//                                    Log.e(TAG, "onDataChange :0 count  " + conversations.getUnread());
//                                    long count = conversations.getUnread() + 1;
//
//                                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(0)).child(conversations.getMessage_id()).child("unread").setValue(count);
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });

                        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(0)).child(conversations.getMessage_id()).child("unread").setValue(messageCount);
                    }

                    checkIsUserLive();

                    comments.setText("");
                    comments.clearFocus();

                    KeyboardUtils.hideKeyboard(ChatActivity.this);


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

//                sendPushMessage(datum.getName(), m, token_Id);

                byte[] decodeValue = Base64.decode(Preferences.INSTANCE.getMsgUserId(), Base64.DEFAULT);

                sendPushMessage(Preferences.INSTANCE.getUserName(), m, Long.parseLong(new String(decodeValue)), token_Id);

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

                byte[] decodeValue = Base64.decode(Preferences.INSTANCE.getMsgUserId(), Base64.DEFAULT);

                sendPushMessage(Preferences.INSTANCE.getUserName(), m, Long.parseLong(new String(decodeValue)), token_Id);

            }

        }

    }


    private void checkIsUserLive() {


//        //Update unRead messsages to 0
//        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(conversations.getUsers().get(0)).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                try {
//
//                    User user = dataSnapshot.getValue(User.class);
//
//                    if (user.connection == 1) {
//
//                        showToast("He is live ");
//                    }
//
//                } catch (DatabaseException e) {
//
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


    }


    private void sendPushMessage(String title, String body, long id, String sender_id) {


        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);
        data.put("type", "chat");
        data.put("id", id);

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("body", body);
//        notification.put("type", "chat");
//        notification.put("id", id);

        Map<String, Object> rawParameters = new Hashtable<String, Object>();
        rawParameters.put("data", new JSONObject(data));
        rawParameters.put("notification", new JSONObject(notification));
        rawParameters.put("to", sender_id);


        Log.e(TAG, "sendPushMessage: " + new JSONObject(rawParameters).toString());


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


    @Override
    public void onImageShow(String image_url) {

        FullScreenImageView fullScreenImageView = FullScreenImageView.getInstance(image_url);

        fullScreenImageView.show(getSupportFragmentManager(), "FullScreenImage");


    }

    @Override
    public void onSelectFromGalleryResult(Bitmap bitmap) {


        imageUploadToFirebase(bitmap);


    }

    @Override
    public void onVideo(File file) {

        long maxLength = 20000000;

        if (file.length() > maxLength) {
            //failed length validation

            showToast("Too big file, Please select less than 10MB Video file...");

        } else {
            //continue
//            uploadVideo(file);
            compressVideo(file);
        }


    }


    private void imageUploadToFirebase(Bitmap bitmap) {


        isVideo = false;

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
//                        Log.e(TAG, "onSuccess: " + uri.toString());
//                Log.e(TAG, "onSuccess: " + uri.getPath());

                        sendMessages(uri.toString());
                    }
                });
            }
        });

    }


    private void compressVideo(File file) {

        File f = new File(getExternalCacheDir() + "/Playerhub/videos");
        if (f.mkdirs() || f.isDirectory()) {
            final String destPath = f.getPath() + File.separator + "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";

            VideoCompress.compressVideoLow(file.getPath(), destPath, new VideoCompress.CompressListener() {
                @Override
                public void onStart() {
                    //Start Compress
                    showLoading();
                }

                @Override
                public void onSuccess() {
                    //Finish successfully

                    hideLoading();

                    File compresedVideoFile = new File(destPath);
                    uploadVideo(compresedVideoFile);

                }

                @Override
                public void onFail() {
                    //Failed

                    hideLoading();
                }

                @Override
                public void onProgress(float percent) {
                    //Progress
                }
            });
        }

    }


    private void uploadVideo(final File file) {

        isVideo = true;

        String name = new SimpleDateFormat("yyyyddMMHHmmss").format(new Date()) + "" + System.nanoTime();


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference videoRef = storageRef.child("videos").child(name);//storageRef.child("FolderToCreate").child("NameYoWantToAdd");
// add File/URI


        showLoading();

//        videoRef = storageRef.child(name);
        videoRef.putFile(Uri.fromFile(file))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded

                        hideLoading();

                        file.delete();

                        videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                hideLoading();
                                Log.e(TAG, "onSuccess: url " + uri.toString());
//                Log.e(TAG, "onSuccess: " + uri.getPath());

                                sendMessages(uri.toString());
                            }
                        });

//                        Log.e(TAG, "onSuccess: " + taskSnapshot.getUploadSessionUri().getPath());

//                        Toast.makeText(getApplicationContext(), "Upload Success...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        hideLoading();
//                        Toast.makeText(getApplicationContext(), "Upload failed...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(
                new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
//                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
//                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });


    }
}
