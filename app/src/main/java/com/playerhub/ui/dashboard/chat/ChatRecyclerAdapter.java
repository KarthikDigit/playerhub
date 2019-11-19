package com.playerhub.ui.dashboard.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.playerhub.DownloadVideoImage;
import com.playerhub.ExoPlayerActivity;
import com.playerhub.R;
import com.playerhub.VideoPlayerActivity;
import com.playerhub.VideoViewFragment;
import com.playerhub.customview.ChatVideoView;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.dashboard.messages.Messages;
import com.playerhub.utils.ImageUtility;
import com.playerhub.utils.ImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private List<Messages> mChats;
    private OnImageClickListener onImageClickListener;

    public ChatRecyclerAdapter(List<Messages> chats, OnImageClickListener onImageClickListener) {
        mChats = chats;
        this.onImageClickListener = onImageClickListener;
    }

    public void add(Messages chat) {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (TextUtils.equals(mChats.get(position).getSender(),
                Preferences.INSTANCE.getMsgUserId())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }

    }

    private void configureMyChatViewHolder(final MyChatViewHolder myChatViewHolder, int position) {
        Messages chat = mChats.get(position);

        String alphabet = chat.getName() != null && chat.getName().length() > 0 ? chat.getName().substring(0, 1) : "";

        final String img_url = chat.getImg_url();

        if (img_url != null && !TextUtils.isEmpty(img_url)) {

            myChatViewHolder.imageView.layout(0, 0, 0, 0);
            ImageUtility.firebaseLoadImage(myChatViewHolder.imageView, img_url, Preferences.INSTANCE.getAutoImageDownload());
//            ImageUtility.downloadImageFromFirebase(myChatViewHolder.imageView.getContext(), img_url);

            myChatViewHolder.imageView.setVisibility(View.VISIBLE);
            myChatViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onImageClickListener != null) onImageClickListener.onImageShow(img_url);
                }
            });

            myChatViewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    shareImage(v.getContext(), myChatViewHolder.imageView);

                    return true;
                }
            });


        } else {
            myChatViewHolder.imageView.setVisibility(View.GONE);
        }

        final String video_url = chat.getVideo_url();


        if (!TextUtils.isEmpty(video_url)) {
            myChatViewHolder.videoLayout.loadImage(video_url);
            myChatViewHolder.videoLayout.setVisibility(View.VISIBLE);
            myChatViewHolder.videoLayout.layout(0, 0, 0, 0);
            myChatViewHolder.videoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    ExoPlayerActivity.startActivity(v.getContext(), video_url, false);
//                    VideoPlayerActivity.startActivity(v.getContext(), video_url);

//                    FragmentManager manager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
//
//                    VideoViewFragment viewFragment = VideoViewFragment.getInstance(video_url);
//
//                    viewFragment.show(manager, "Video");


                }
            });

//            new DownloadVideoImage(new DownloadVideoImage.OnImageDownloadCallback() {
//                @Override
//                public void onImageSuccess(Bitmap bitmap) {
//                    myChatViewHolder.videoLayout.setVisibility(View.VISIBLE);
//                    myChatViewHolder.videoLayout.layout(0, 0, 0, 0);
//                    myChatViewHolder.videoView.setImageBitmap(bitmap);
//
//                }
//            }).execute(video_url);

//            myChatViewHolder.videoView.setVisibility(View.VISIBLE);
//            myChatViewHolder.videoView.layout(0, 0, 0, 0);
////            GlideApp
////                    .with(myChatViewHolder.videoView.getContext())
////                    .asBitmap()
////                    .load(Uri.fromFile(new File(video_url)))
////                    .into(myChatViewHolder.videoView);
//
//            try {
//                Bitmap bitmap = ImageUtils.retriveVideoFrameFromVideo(video_url);
//                myChatViewHolder.videoView.setImageBitmap(bitmap);
//            } catch (Throwable throwable) {
//                myChatViewHolder.videoView.setVisibility(View.GONE);
//                Log.e(TAG, "configureOtherChatViewHolder: " + throwable.getMessage());
//            }

        } else {

            myChatViewHolder.videoLayout.setVisibility(View.GONE);
        }


        String msg = chat.getMsg();

        myChatViewHolder.txtChatMessage.setText(msg);
        myChatViewHolder.txtUserAlphabet.setText(alphabet);

        myChatViewHolder.txtTime.setText(getDate(chat.getTimestamp()));
        if (TextUtils.isEmpty(msg)) {
            myChatViewHolder.txtChatMessage.setVisibility(View.GONE);
        } else {
            myChatViewHolder.txtChatMessage.setVisibility(View.VISIBLE);
        }


    }

    private void configureOtherChatViewHolder(final OtherChatViewHolder otherChatViewHolder, int position) {
        final Messages chat = mChats.get(position);

        String alphabet = chat.getName() != null && chat.getName().length() > 0 ? chat.getName().substring(0, 1) : "";

        final String img_url = chat.getImg_url();

        if (img_url != null && !TextUtils.isEmpty(img_url)) {
            otherChatViewHolder.imageView.layout(0, 0, 0, 0);
            ImageUtility.firebaseLoadImage(otherChatViewHolder.imageView, img_url, Preferences.INSTANCE.getAutoImageDownload());

//            ImageUtility.downloadImageFromFirebase(otherChatViewHolder.imageView.getContext(), img_url);

            otherChatViewHolder.imageView.setVisibility(View.VISIBLE);

            otherChatViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onImageClickListener != null) onImageClickListener.onImageShow(img_url);
                }
            });

            otherChatViewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    shareImage(v.getContext(), otherChatViewHolder.imageView);

                    return true;
                }
            });

        } else {


            otherChatViewHolder.imageView.setVisibility(View.GONE);
        }

        final String video_url = chat.getVideo_url();


        if (!TextUtils.isEmpty(video_url)) {

            otherChatViewHolder.videoLayout.loadImage(video_url);
            otherChatViewHolder.videoLayout.setVisibility(View.VISIBLE);
            otherChatViewHolder.videoLayout.layout(0, 0, 0, 0);


//            if (!TextUtils.isEmpty(chat.getDownloadId())) {
//                boolean isAlreadyDownloaded = otherChatViewHolder.videoLayout.checkFileAlreadyDownloaded(video_url);
//
//                if (!isAlreadyDownloaded) {
//
//                    Log.e(TAG, "configureOtherChatViewHolder: called reuseme ");
//
//                    otherChatViewHolder.videoLayout.resumeVideoDownload(chat.getDownloadId(), video_url);
//
//
//                }
//            }

            otherChatViewHolder.videoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean isAlreadyDownloaded = otherChatViewHolder.videoLayout.checkFileAlreadyDownloaded(video_url);

                    if (isAlreadyDownloaded) {

                        ExoPlayerActivity.startActivity(v.getContext(), otherChatViewHolder.videoLayout.localVideoFilePath(video_url), true);

                    } else {


                        if (TextUtils.isEmpty(chat.getDownloadId())) {

                            Log.e(TAG, "onClick: Downloading .....");


                            otherChatViewHolder.videoLayout.downloadFile(video_url);
                            ExoPlayerActivity.startActivity(v.getContext(), video_url, false);

                            String downloadId = otherChatViewHolder.videoLayout.getDownloadID();

                            if (onImageClickListener != null)
                                onImageClickListener.updateDownloadID(chat.getMsgId(), downloadId);

                        } else {

                            Log.e(TAG, "onClick: resume Called ");

                            otherChatViewHolder.videoLayout.resumeVideoDownload(chat.getDownloadId(), video_url);
                            ExoPlayerActivity.startActivity(v.getContext(), video_url, false);

                        }
                    }
//                    VideoPlayerActivity.startActivity(v.getContext(), video_url);
//                    ExoPlayerActivity.startActivity(v.getContext(), video_url, false);
//                    FragmentManager manager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
//
//                    VideoViewFragment viewFragment = VideoViewFragment.getInstance(video_url);
//
//                    viewFragment.show(manager, "Video");


                }
            });

//            new DownloadVideoImage(new DownloadVideoImage.OnImageDownloadCallback() {
//                @Override
//                public void onImageSuccess(Bitmap bitmap) {
//                    otherChatViewHolder.videoLayout.setVisibility(View.VISIBLE);
//                    otherChatViewHolder.videoLayout.layout(0, 0, 0, 0);
//                    otherChatViewHolder.videoView.setImageBitmap(bitmap);
//
//                }
//            }).execute(video_url);

//            otherChatViewHolder.videoView.setVisibility(View.VISIBLE);
//            otherChatViewHolder.videoView.layout(0, 0, 0, 0);
////            GlideApp
////                    .with(otherChatViewHolder.videoView.getContext())
////                    .asBitmap()
////                    .load(Uri.fromFile(new File(video_url)))
////                    .into(otherChatViewHolder.videoView);
//            try {
//                Bitmap bitmap = ImageUtils.retriveVideoFrameFromVideo(video_url);
//                otherChatViewHolder.videoView.setImageBitmap(bitmap);
//            } catch (Throwable throwable) {
//                otherChatViewHolder.videoView.setVisibility(View.GONE);
//                Log.e(TAG, "configureOtherChatViewHolder: " + throwable.getMessage());
//            }

        } else {

            otherChatViewHolder.videoLayout.setVisibility(View.GONE);
        }


        String msg = chat.getMsg();

        otherChatViewHolder.txtChatMessage.setText(msg);
        otherChatViewHolder.txtUserAlphabet.setText(alphabet);
        otherChatViewHolder.txtTime.setText(getDate(chat.getTimestamp()));

        if (TextUtils.isEmpty(msg)) {
            otherChatViewHolder.txtChatMessage.setVisibility(View.GONE);
        } else {
            otherChatViewHolder.txtChatMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (holder instanceof OtherChatViewHolder) {

            ((OtherChatViewHolder) holder).onViewAttachedToWindow();

        } else if (holder instanceof MyChatViewHolder) {

            ((MyChatViewHolder) holder).onViewAttachedToWindow();
        }

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);


        if (holder instanceof OtherChatViewHolder) {

            ((OtherChatViewHolder) holder).onViewDetachedFromWindow();

        } else if (holder instanceof MyChatViewHolder) {

            ((MyChatViewHolder) holder).onViewDetachedFromWindow();
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("hh:mm a", cal).toString();
//        return DateFormat.format("dd-MMM-yyyy hh:mm a", cal).toString();
    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).getSender(),
                Preferences.INSTANCE.getMsgUserId())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    private class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet, txtTime;
        private ImageView imageView;
        private ChatVideoView videoLayout;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            videoLayout = (ChatVideoView) itemView.findViewById(R.id.video_layout);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtTime = (TextView) itemView.findViewById(R.id.text_view_chat_time);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }

        public void onViewDetachedFromWindow() {

            if (!TextUtils.isEmpty(videoLayout.getDownloadID())) {
                videoLayout.pauseVideoDownload(videoLayout.getDownloadID());
            }

        }


        public void onViewAttachedToWindow() {

            Messages messages = mChats.get(getAdapterPosition());

            if (!TextUtils.isEmpty(videoLayout.getDownloadID())) {
                videoLayout.resumeVideoDownload(videoLayout.getDownloadID(), messages.getVideo_url());
            }

        }
    }

    private class OtherChatViewHolder extends RecyclerView.ViewHolder {

        private TextView txtChatMessage, txtUserAlphabet, txtTime;
        private ImageView imageView;
        private ChatVideoView videoLayout;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            videoLayout = (ChatVideoView) itemView.findViewById(R.id.video_layout);
            txtTime = (TextView) itemView.findViewById(R.id.text_view_chat_time);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }


        public void onViewDetachedFromWindow() {

            if (!TextUtils.isEmpty(videoLayout.getDownloadID())) {
                videoLayout.pauseVideoDownload(videoLayout.getDownloadID());
            }

        }


        public void onViewAttachedToWindow() {
            Messages messages = mChats.get(getAdapterPosition());
            if (!TextUtils.isEmpty(videoLayout.getDownloadID())) {
                videoLayout.resumeVideoDownload(videoLayout.getDownloadID(), messages.getVideo_url());
            }
        }


    }


    public interface OnImageClickListener {

        void onImageShow(String image_url);

        void updateDownloadID(String msgID, String downloadID);

    }

    private static final String TAG = "ChatRecyclerAdapter";

    private void shareImage(final Context context, final ImageView imageView) {

        imageView.post(new Runnable() {
            @Override
            public void run() {

//                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                StrictMode.setVmPolicy(builder.build());

                Uri bmpUri = getLocalBitmapUri(imageView);

                if (bmpUri != null) {

                    // Construct a ShareIntent with link to image

                    Intent shareIntent = new Intent();

                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);

                    shareIntent.setType("image/*");

                    // Launch sharing dialog for image

                    context.startActivity(Intent.createChooser(shareIntent, "Share Image"));

                } else {

                    Toast.makeText(context, "Sharing went wrong", Toast.LENGTH_SHORT).show();

                    // ...sharing failed, handle error

                }

            }
        });


//        Bitmap bitmap = imageView.getDrawingCache();
//        File root = Environment.getExternalStorageDirectory();
//        File cachePath = new File(root.getAbsolutePath() + "/DCIM/Camera/image.jpg");
//        try {
//            cachePath.createNewFile();
//            FileOutputStream ostream = new FileOutputStream(cachePath);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
//            ostream.close();
//        } catch (Exception e) {
//            Log.e(TAG, "shareImage: " + e.getMessage());
//        }
//
//        Intent share = new Intent(Intent.ACTION_SEND);
//        share.setType("image/*");
//        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cachePath));
//        context.startActivity(Intent.createChooser(share, "Share via"));


    }


    // Returns the URI path to the Bitmap displayed in specified ImageView

    public Uri getLocalBitmapUri(ImageView imageView) {

        // Extract Bitmap from ImageView drawable

        Drawable drawable = imageView.getDrawable();

        Bitmap bmp = null;

        if (drawable instanceof BitmapDrawable) {

            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        } else {

            return null;

        }

        // Store image to default external storage directory

        Uri bmpUri = null;

        try {

            // Use methods on Context to access package-specific directories on external storage.

            // This way, you don't need to request external read/write permission.

            // See https://youtu.be/5xVh-7ywKpE?t=25m25s

            File file = new File(imageView.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");

            FileOutputStream out = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);

            out.close();

            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.

//            bmpUri = Uri.fromFile(file);

            bmpUri = FileProvider.getUriForFile(imageView.getContext(), "com.playerhub.fileProvider", file);


        } catch (IOException e) {

            e.printStackTrace();

        }

        return bmpUri;

    }
}
