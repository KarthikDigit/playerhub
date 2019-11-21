package com.playerhub.ui.dashboard.chat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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
import com.playerhub.downloadservice.DownloadIntentService;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.Messages;
import com.playerhub.ui.dashboard.messages.User;
import com.playerhub.utils.KeyboardUtils;
import com.vincent.videocompressor.VideoCompress;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UploadVideoService extends Service {

    private static final String TAG = "UploadVideoService";
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID_DEFAULT = "default";

    private int mNumTasks = 0;
    private StorageReference storageRef;

    public void taskStarted() {
        changeNumberOfTasks(1);
    }

    public void taskCompleted() {
        changeNumberOfTasks(-1);
    }

    private synchronized void changeNumberOfTasks(int delta) {
        Log.d(TAG, "changeNumberOfTasks:" + mNumTasks + ":" + delta);
        mNumTasks += delta;

        // If there are no tasks left, stop the service
        if (mNumTasks <= 0) {
            Log.d(TAG, "stopping");
            stopSelf();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        storageRef = FirebaseStorage.getInstance().getReference();
        createNotificationChannel();

        showNotification("Uploading video");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Conversations conversations = null;
        Messages messages = null;
        String token_id = "";
        String name = "";
        String url = " ";
        int position = -1;
        int chatUserPosition = -1;
        boolean isGroup = false;

        if (intent != null) {
            messages = (Messages) intent.getSerializableExtra("message");
            conversations = (Conversations) intent.getSerializableExtra("conversation");
            url = intent.getStringExtra("url");
            token_id = intent.getStringExtra("tokenid");
            name = intent.getStringExtra("name");
            isGroup = intent.getBooleanExtra("isGroup", false);
            position = intent.getIntExtra("position", -1);
            chatUserPosition = intent.getIntExtra("chatUserPosition", -1);
        }
//
//        Log.e(TAG, "onStartCommand: Messages  " + new Gson().toJson(messages));
//        Log.e(TAG, "onStartCommand: Conversation  " + new Gson().toJson(conversations));

//        Log.e(TAG, "onStartCommand: " + url + " " + position);

//        firebaseUpdate(conversations);
//        fileCompressing(url, position, conversations, messages, token_id, name, chatUserPosition);
        fileUpload(url, position, conversations, messages, token_id, name, chatUserPosition, isGroup);


        return START_REDELIVER_INTENT;
    }


    private void fileCompressing(String filePath, final int position, final Conversations conversations, final Messages messages, final String token_id, final String name, final int chatUserPosition, final boolean isGroup) {

        File file = new File(filePath);

        File f = new File(getExternalCacheDir() + "/Playerhub/videos/sent/");
        String filename = new SimpleDateFormat("yyyyddMMHHmmss").format(new Date()) + "" + System.nanoTime();

        if (f.mkdirs() || f.isDirectory()) {

            final String destPath = f.getPath() + File.separator + "VID_" + filename + ".mp4";

            VideoCompress.compressVideoLow(file.getPath(), destPath, new VideoCompress.CompressListener() {
                @Override
                public void onStart() {
                    //Start Compress

                }

                @Override
                public void onSuccess() {
                    //Finish successfully
//                    File compresedVideoFile = new File(destPath);

                    Log.e(TAG, "onSuccess: compressed file " + destPath);

                    fileUpload(destPath, position, conversations, messages, token_id, name, chatUserPosition, isGroup);

                }

                @Override
                public void onFail() {
                    //Failed

                    Log.e(TAG, "onFail: ");
                }

                @Override
                public void onProgress(float percent) {
                    //Progress

                    Log.e(TAG, "onProgress: " + percent);
                }
            });

        }
    }


    private void fileUpload(final String filePath, final int position, final Conversations conversations, final Messages messages, final String token_id, final String name, final int chatUserPosition, final boolean isGroup) {

        // [START_EXCLUDE]
        taskStarted();

        showProgressNotification(getString(R.string.progress_uploading) + " " + name, 0, 0);

        File file = new File(filePath);

        String filename = new SimpleDateFormat("yyyyddMMHHmmss").format(new Date()) + "" + System.nanoTime();

        final StorageReference videoRef = storageRef.child("videos").child(filename);//storageRef.child("FolderToCreate").child("NameYoWantToAdd");

//        videoRef = storageRef.child(name);
        videoRef.putFile(Uri.fromFile(file))
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        showProgressNotification(getString(R.string.progress_uploading) + " " + name,
                                taskSnapshot.getBytesTransferred(),
                                taskSnapshot.getTotalByteCount());

                        Long values[] = new Long[]{Long.valueOf((long) progress), Long.valueOf(position), Long.valueOf(chatUserPosition)};
                        publishProgress(values);

                    }
                }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                // Forward any exceptions
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                Log.d(TAG, "uploadFromUri: upload success");

                // Request the public download URL
                return videoRef.getDownloadUrl();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri downloadUri) {
                // Upload succeeded
                Log.d(TAG, "uploadFromUri: getDownloadUri success " + downloadUri);
                messages.setVideo_url(downloadUri.toString());
                messages.setLocalFile(filePath);
                messages.setServerUpdated(true);
                firebaseUpdate(conversations, messages, token_id, isGroup);
                taskCompleted();
                // [END_EXCLUDE]
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                taskCompleted();
                Log.e(TAG, "onFailure: " + exception.getMessage());

            }
        });


    }


    /**
     * Show notification with a progress bar.
     */
    protected void showProgressNotification(String caption, long completedUnits, long totalUnits) {
        int percentComplete = 0;
        if (totalUnits > 0) {
            percentComplete = (int) (100 * completedUnits / totalUnits);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
                .setSmallIcon(R.drawable.upload_animation)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(caption)
                .setProgress(100, percentComplete, false)
                .setOngoing(true)
                .setAutoCancel(false);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private void showNotification(String msg) {

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT).setSmallIcon
                (R.drawable.upload_animation).setContentTitle("Sending video").setContentText(msg).build();

        startForeground(NOTIFICATION_ID, notification);

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = CHANNEL_ID_DEFAULT;
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_DEFAULT, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void publishProgress(Long[] values) {

        Intent intent = new Intent("download_progress");
        intent.putExtra("percentage", values[0]);
        intent.putExtra("position", values[1]);
        intent.putExtra("chatUserPosition", values[2]);
        LocalBroadcastManager.getInstance(UploadVideoService.this).sendBroadcast(intent);

    }

    /**
     * Dismiss the progress notification.
     */
    protected void dismissProgressNotification() {
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(NOTIFICATION_ID);
    }


    private void firebaseUpdate(final Conversations conversations, final Messages messages, final String token_id, final boolean isGroup) {


        if (conversations != null && messages != null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

//            final String id = databaseReference.push().getKey();

//            messages.setMsgId(id);
//            messages.setName(Preferences.INSTANCE.getUserName());
//            messages.setVideo_url("test");
//            messages.setSender(Preferences.INSTANCE.getMsgUserId());
//            messages.setTimestamp(System.currentTimeMillis());
//            messages.setUpload_status(1);


            databaseReference.child(Constants.ARG_MESSAGES).child(conversations.getMessage_id()).child(messages.getMsgId()).setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isComplete()) {
                        long count = conversations.getUnread() + 1;
                        Conversations.Last_Conversation last_conversation = new Conversations.Last_Conversation();
                        last_conversation.setContent("");
                        last_conversation.setSender(Preferences.INSTANCE.getUserName());
                        last_conversation.setStatus(true);
                        last_conversation.setTimestamp(System.currentTimeMillis());
                        last_conversation.setType("text");
                        conversations.setLast_conversation(last_conversation);

                        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(conversations.getMessage_id()).child("last_conversation").setValue(last_conversation);


                        if (conversations.getUsers().get(0).toLowerCase().equalsIgnoreCase(Preferences.INSTANCE.getMsgUserId().toLowerCase())) {
                            FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(1)).child(conversations.getMessage_id()).child("unread").setValue(count);

                        } else {
                            FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(conversations.getUsers().get(0)).child(conversations.getMessage_id()).child("unread").setValue(count);
                        }


                        if (isGroup) {

                            sendPushNotificationToGroupUsers("You have a new video message", conversations);

                        } else {

                            if (!TextUtils.isEmpty(token_id)) {

                                byte[] decodeValue = Base64.decode(Preferences.INSTANCE.getMsgUserId(), Base64.DEFAULT);

                                sendPushMessage(Preferences.INSTANCE.getUserName(), "You have a new video message", Long.parseLong(new String(decodeValue)), token_id);

                            }
                        }


                    } else if (task.isCanceled()) {

                        Log.e(TAG, "onComplete: message send something wrong ");
                    }
                }
            });
        } else Log.e(TAG, "firebaseUpdate: no conversation ");


    }


    private void sendPushNotificationToGroupUsers(final String msg, final Conversations conversations) {

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

                            sendPushMessage(value.name, msg, token_Id, conversations);

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

//                        Log.e(TAG, "accept: " + s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

//                        Log.e(TAG, "accept: error " + throwable.getMessage() + "  " + ((HttpException) throwable).response().errorBody().string());

                    }
                });


    }

    private void sendPushMessage(String title, String body, String sender_id, final Conversations conversations) {


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


}
