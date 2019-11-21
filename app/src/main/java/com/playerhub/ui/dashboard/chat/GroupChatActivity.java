package com.playerhub.ui.dashboard.chat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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
import com.playerhub.downloadservice.DownloadIntentService;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.ContactListApi;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.Messages;
import com.playerhub.ui.dashboard.messages.User;
import com.playerhub.utils.KeyboardUtils;
import com.vincent.videocompressor.VideoCompress;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

public class GroupChatActivity extends BaseActivity implements ChatRecyclerAdapter.OnImageClickListener, CameraAndGallary.CameraAndGallaryCallBack {

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
    private static boolean isVideo = false;
    private int chatUserPosition = -1;

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
        chatUserPosition = getIntent().getIntExtra("position", -1);
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

                String msg = comments.getText().toString();

                if (!TextUtils.isEmpty(msg))
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


    private void sendMessages(final String imageUrl) {

        String msg = comments.getText().toString();


        msg = TextUtils.isEmpty(msg) ? "" : msg;

        final Messages messages = new Messages();
        messages.setMsg(msg);
//        messages.setImg_url(imageUrl);
        if (!TextUtils.isEmpty(imageUrl)) {

//            messages.setImg_url(imageUrl);
            if (isVideo) {
                messages.setLocalFile(imageUrl);
            } else {
                messages.setImg_url(imageUrl);
            }

        }

        showLoading();
//        isVideo = false;
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final String id = databaseReference.push().getKey();

//        Messages messages = new Messages();
        messages.setMsgId(id);

        messages.setName(Preferences.INSTANCE.getUserName());
//        messages.setMsg(comments.getText().toString());
        messages.setSender(Preferences.INSTANCE.getMsgUserId());
        messages.setTimestamp(new Date().getTime());
        messages.setUpload_status(1);

        final String finalMsg = !TextUtils.isEmpty(msg) ? msg : "You have a message from " + conversations.getTitle();
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

                    if (!isVideo)
                        sendPushNotificationToGroupUsers(finalMsg);

                } else if (task.isCanceled()) {

                    Log.e(TAG, "onComplete: message send something wrong ");
                }


            }
        });


        if (isVideo) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    String token_Id = "";
                    String userName = conversations.getTitle();

                    int count = mChatRecyclerAdapter.getItemCount() - 1;
                    Intent intent = new Intent(GroupChatActivity.this, UploadVideoService.class);
                    intent.putExtra("tokenid", token_Id);
                    intent.putExtra("name", userName);
                    intent.putExtra("message", messages);
                    intent.putExtra("isGroup", true);
                    intent.putExtra("conversation", conversations);
                    intent.putExtra("url", imageUrl);
                    intent.putExtra("position", count);
                    intent.putExtra("chatUserPosition", chatUserPosition);
                    startService(intent);

                }
            }, 2000);


        }


    }


    private void sendPushNotificationToGroupUsers(final String msg) {

        final List<String> users = new ArrayList<>(conversations.getUsers());
        users.remove(Preferences.INSTANCE.getMsgUserId());

        for (String u : users
                ) {

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(u);


            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        User value = dataSnapshot.getValue(User.class);


                        String token_Id = null;
                        if (value != null) {
                            token_Id = value.token_id;
                        }

                        if (token_Id != null && !TextUtils.isEmpty(token_Id)) {

                            sendPushMessage(value.name, msg, token_Id);

                        }
//                        else {
//
////                            showToast("There is no token id");
//
//                        }

                    } catch (DatabaseException e) {

                        Log.e(TAG, "onDataChange: " + e.getMessage());

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }

    private void sendPushMessage(String title, String body, String sender_id) {


        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);
        data.put("type", "chat");
        data.put("conversation", conversations);


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
    public void updateDownloadID(String msgID, String downloadID) {

    }

    @Override
    public void downloadVideo(int position, String url) {

        File file = getExternalCacheDir();
        startService(DownloadIntentService
                .getDownloadService(this, url, file.getPath().concat("/Playerhub/videos/"), position, chatUserPosition));


    }

    @Override
    public void onSelectFromGalleryResult(Bitmap bitmap) {
//        tempBitmap = bitmap;
//
//        tempImage.setImageBitmap(bitmap);
//        tempImage.setVisibility(View.VISIBLE);

        imageUploadToFirebase(bitmap);

    }

    @Override
    public void onVideo(File file) {

        long maxLength = 50000000;

        if (file.length() > maxLength) {
            //failed length validation

            showToast("Too big file, Please select less than 10MB Video file...");

        } else {

            isVideo = true;

            sendMessages(file.getPath());
            //continue
//            uploadVideo(file);
//            compressVideo(file);
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
                        Log.e(TAG, "onSuccess: " + uri.toString());
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

//        String name = new SimpleDateFormat("yyyyddMMHHmmss").format(new Date()) + "" + System.nanoTime();
//
//
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//        final StorageReference videoRef = storageRef.child("videos").child(name);//storageRef.child("FolderToCreate").child("NameYoWantToAdd");
//// add File/URI
//
//
//        showLoading();
//
////        videoRef = storageRef.child(name);
//        videoRef.putFile(Uri.fromFile(file))
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // Upload succeeded
//
//                        hideLoading();
//
//                        file.delete();
//
//                        videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                hideLoading();
//                                Log.e(TAG, "onSuccess: url " + uri.toString());
////                Log.e(TAG, "onSuccess: " + uri.getPath());
//
//                                sendMessages(uri.toString());
//                            }
//                        });
//
////                        Log.e(TAG, "onSuccess: " + taskSnapshot.getUploadSessionUri().getPath());
//
////                        Toast.makeText(getApplicationContext(), "Upload Success...", Toast.LENGTH_SHORT).show();
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Upload failed
//                        hideLoading();
////                        Toast.makeText(getApplicationContext(), "Upload failed...", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnProgressListener(
//                new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                        //calculating progress percentage
////                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//
//                        //displaying percentage in progress dialog
////                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
//                    }
//                });


    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                progressReceiver, new IntentFilter("download_progress"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(progressReceiver);
    }

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            long percentage = intent.getLongExtra("percentage", 0);
            final long position = intent.getLongExtra("position", -1);
            final long chatUserPos = intent.getLongExtra("chatUserPosition", -1);

//            Log.e(TAG, "onReceive: " + percentage + " pos " + position);

//            System.out.format("%d%% done of element position %d\n", percentage, position);


            if (chatUserPosition != -1 && chatUserPosition == chatUserPos) {

                if (mChatRecyclerAdapter.getAllItems() != null && mChatRecyclerAdapter.getAllItems().size() > 0) {


                    if (mChatRecyclerAdapter.isThere((int) position)) {

                        mChatRecyclerAdapter.getAllItems().get((int) position).setPercentage(percentage);
                        mChatRecyclerAdapter.notifyItemChanged((int) position);

                    }

                }
            }

        }
    };
}
