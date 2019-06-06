package com.playerhub.ui.dashboard.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.playerhub.R;
import com.playerhub.customview.ProgressImageView;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.dashboard.messages.Messages;
import com.playerhub.utils.ImageUtility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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
            ImageUtility.firebaseLoadImage(myChatViewHolder.imageView, img_url);
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
        Messages chat = mChats.get(position);

        String alphabet = chat.getName() != null && chat.getName().length() > 0 ? chat.getName().substring(0, 1) : "";

        final String img_url = chat.getImg_url();

        if (img_url != null && !TextUtils.isEmpty(img_url)) {
            otherChatViewHolder.imageView.layout(0, 0, 0, 0);
            ImageUtility.firebaseLoadImage(otherChatViewHolder.imageView, img_url);


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

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet, txtTime;
        private ImageView imageView;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtTime = (TextView) itemView.findViewById(R.id.text_view_chat_time);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet, txtTime;
        private ImageView imageView;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            txtTime = (TextView) itemView.findViewById(R.id.text_view_chat_time);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }


    public interface OnImageClickListener {

        void onImageShow(String image_url);

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
