package com.playerhub.ui.dashboard.chat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.playerhub.R;
import com.playerhub.cameraorgallery.CameraAndGallary;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.Messages;
import com.playerhub.utils.KeyboardUtils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class GroupChatActivity extends BaseActivity implements ChatRecyclerAdapter.OnImageClickListener, CameraAndGallary.CameraAndGallaryCallBack {

    private static final String TAG = "ChatActivity";

    private static final String[] CAMERA_PERMISSION = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

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

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        conversations = (Conversations) getIntent().getSerializableExtra("user");

        final List<String> users = new ArrayList<>(conversations.getUsers());


//        Log.e(TAG, "onCreate: before user " + new Gson().toJson(users) + " " + Preferences.INSTANCE.getMsgUserId());

        users.remove(Preferences.INSTANCE.getMsgUserId());


//        Log.e(TAG, "onCreate: After user " + new Gson().toJson(users));

        getAllMessages(conversations.getMessage_id());

        String title = conversations.getTitle();

        title = !TextUtils.isEmpty(title) ? title : "Chat";

        setBackButtonEnabledAndTitle(title);

        comments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


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
                    conversations.setIs_typing(is_typing);

                }


                for (int j = 0; j < users.size(); j++) {

                    databaseReference.child(Constants.ARG_CONVERSATION).child(users.get(j)).child(conversations.getMessage_id()).setValue(conversations);

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

                        for (int j = 0; j < users.size(); j++) {

                            databaseReference.child(Constants.ARG_CONVERSATION).child(users.get(j)).child(conversations.getMessage_id()).setValue(conversations);

                        }


                    }
                }, 1000);

            }
        });

    }


    private void getAllMessages(String messageID) {

        conversations.setIs_typing(new Conversations.Is_Typing());
        conversations.setUnread(0);

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


    @OnClick({R.id.send_comments, R.id.attachment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.send_comments:

                sendMessages(null);

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

//        Messages messages = new Messages();

        messages.setName(Preferences.INSTANCE.getUserName());
//        messages.setMsg(comments.getText().toString());
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
//                    conversations.setIs_typing(new Conversations.Is_Typing());
                    conversations.setLast_conversation(last_conversation);


                    //Update unRead messsages to 0
                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(0)).child(conversations.getMessage_id()).setValue(conversations);


                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(conversations.getMessage_id()).child("last_conversation").setValue(last_conversation);


                    comments.setText("");
                    comments.clearFocus();

                    KeyboardUtils.hideKeyboard(GroupChatActivity.this);


                } else if (task.isCanceled()) {

                    Log.e(TAG, "onComplete: message send something wrong ");
                }
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
