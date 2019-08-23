package com.playerhub.ui.dashboard.messages.recent;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.ui.dashboard.messages.ConversationsLayout;
import com.playerhub.ui.dashboard.messages.User;
import com.playerhub.utils.CommonUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Context mContext;
    private List<Object> contactList;
    private List<Object> contactListFiltered;
    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public RecentAdapter(Context mContext, List<Object> mList, OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.mContext = mContext;
        this.contactList = mList;
        this.contactListFiltered = mList;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }


    public void addAll(List<Object> list) {

        this.contactList = new ArrayList<>();
        this.contactList.addAll(list);

        this.contactListFiltered = new ArrayList<>();
        this.contactListFiltered.addAll(list);

        notifyDataSetChanged();
    }

    public void add(Object o) {

        this.contactList.add(o);
        this.contactListFiltered.add(o);
        notifyItemInserted(contactListFiltered.size() - 1);
    }

    Object getItem(int position) {

        return contactListFiltered.get(position);
    }

    List<Object> getList() {
        return contactListFiltered;
    }

    public void clearAll() {

        contactList.clear();
        contactList = new ArrayList<>();
        contactListFiltered.clear();
        contactListFiltered = new ArrayList<>();
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        if (i == 0) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);

            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);

            return new GroupViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

        final Object o = getItem(i);

        if (o instanceof User) {

            ((UserViewHolder) viewHolder).bindView(o);

        } else if (o instanceof ConversationsLayout) {

            ((GroupViewHolder) viewHolder).bindView(o);

        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onRecyclerItemClickListener != null)
                    onRecyclerItemClickListener.OnItemClick(v, o, viewHolder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactListFiltered != null ? contactListFiltered.size() : 0;
    }


    @Override
    public int getItemViewType(int position) {

        if (contactListFiltered.get(position) instanceof ConversationsLayout) {

            return 1;
        } else return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<Object> filteredList = new ArrayList<>();
                    for (Object row : contactList) {


                        if (row instanceof User) {

                            User user= (User) row;

                            if (user.name.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }

                        } else if (row instanceof ConversationsLayout) {

                            ConversationsLayout user= (ConversationsLayout) row;

                            if (user.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        // name match condition. this might differ depending on your requirement
//                        // here we are looking for name or phone number match
//                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)) {
//                            filteredList.add(row);
//                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Object>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon)
        CircleImageView icon;
        @BindView(R.id.date_time)
        TextView dateTime;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.count_lay)
        RelativeLayout countLay;
        @BindView(R.id.root)
        RelativeLayout root;
        @BindView(R.id.useris_online)
        View userIsOnline;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }


        void bindView(Object o) {
            final User item = (User) o;

            count.setText(item.notification + "");
            name.setText(item.name);
//            dateTime.setText(item.date_time);

            countLay.setVisibility(View.INVISIBLE);
            if (item.notification == 0) {
                countLay.setVisibility(View.INVISIBLE);
            }

//            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).getRef();
            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (item.icon != null && item.icon.length() > 0)
                        Picasso.get().load(item.icon).placeholder(R.drawable.progress_animation).error(R.drawable.avatar_mini).resize(120, 120).into(icon);

                }
            });

            CommonUtil.checkIsInOnline(item.id, new CommonUtil.ObserverListener<Boolean>() {
                @Override
                public void onNext(Boolean value) {

                    userIsOnline.setVisibility(value ? View.VISIBLE : View.GONE);

                }

            });


            CommonUtil.getMessageCount(item.id, new CommonUtil.ObserverListener<Long>() {
                @Override
                public void onNext(Long value) {

                    item.notification = value;
                    count.setText(String.format(Locale.ENGLISH, "%d", item.notification));
                    countLay.setVisibility(View.VISIBLE);
                    if (item.notification == 0) {
                        countLay.setVisibility(View.INVISIBLE);
                    }


                }
            });


        }
    }


    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon)
        CircleImageView icon;
        @BindView(R.id.date_time)
        TextView dateTime;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.count_lay)
        RelativeLayout countLay;
        @BindView(R.id.root)
        RelativeLayout root;
        @BindView(R.id.useris_online)
        View userIsOnline;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }


        void bindView(Object o) {

            final ConversationsLayout item = (ConversationsLayout) o;

            count.setText(String.format(Locale.ENGLISH, "%d", item.getUnread()));

            countLay.setVisibility(View.VISIBLE);
            if (item.getUnread() == 0) {
                countLay.setVisibility(View.INVISIBLE);
            }

            if (item.getTitle() != null && !TextUtils.isEmpty(item.getTitle())) {

                name.setText(item.getTitle());
            } else {

                name.setText("Undefined Group");
            }
//            dateTime.setText(item.date_time);

            countLay.setVisibility(View.INVISIBLE);

            Picasso.get().load(R.mipmap.ic_launcher).resize(120, 120).into(icon);


            CommonUtil.getGroupMessageCount(item.getMessage_id(), new CommonUtil.CallBackCount() {
                @Override
                public void showCount(long counta) {
                    item.setUnread(counta);
                    count.setText(String.format(Locale.ENGLISH, "%d", counta));
                    countLay.setVisibility(View.VISIBLE);
                    if (item.getUnread() == 0) {
                        countLay.setVisibility(View.INVISIBLE);
                    }
                }
            });

        }

    }


}
