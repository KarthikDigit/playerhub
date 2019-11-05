package com.playerhub.ui.dashboard.notification;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.playerhub.R;
import com.playerhub.common.CallbackWrapper;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.network.response.NotificationApi;
import com.playerhub.network.response.ReadNotification;
import com.playerhub.preference.Preferences;
import com.playerhub.recyclerHelper.ItemOffsetDecoration;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.base.OnItemClickListener;
import com.playerhub.ui.dashboard.announcement.AnnouncementDialogFragment;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsActivity;
import com.playerhub.ui.dashboard.home.paidevent.PaidEventDetailsActivity;
import com.playerhub.utils.AlertUtils;
import com.playerhub.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotificationActivity extends BaseActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {


    @BindView(R.id.notification_view)
    RecyclerView mNotificationView;
    @BindView(R.id.msg)
    TextView msg;
    private NotificationListAdapter fastAdapter;

    private List<NotificationApi.Data.Notification> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.abs_layout);
//            getSupportActionBar().setTitle("NOTIFICATION");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

//        mNotificationView = (RecyclerView) findViewById(R.id.notification_view);
        mNotificationView.setLayoutManager(new LinearLayoutManager(this));

//        productCategoryListView.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.GRID));
        mNotificationView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.offset));

//        //create the ItemAdapter holding your Items
//        ItemAdapter<ProductCategory> itemAdapter = new ItemAdapter<ProductCategory>();
//        //create the managing FastAdapter, by passing in the itemAdapter
//        FastAdapter fastAdapter = FastAdapter.with(itemAdapter);

        //set our adapters to the RecyclerView


        fastAdapter = new NotificationListAdapter(this, new ArrayList<NotificationApi.Data.Notification>());


        mNotificationView.setAdapter(fastAdapter);


        fastAdapter.setOnItemClickListener(new OnItemClickListener<NotificationApi.Data.Notification>() {
            @Override
            public void onItemClicked(View view, NotificationApi.Data.Notification o, int position) {


                setRead(o, String.valueOf(o.getId()), position);


            }
        });


        loadData();
    }


    private void setAllToRead() {

        RetrofitAdapter.getNetworkApiServiceClient().readNotification(Preferences.INSTANCE.getAuthendicate(), "all")
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CallbackWrapper<ReadNotification>(this) {
                    @Override
                    protected void onSuccess(ReadNotification readNotification) {


                        if (readNotification.getSuccess()) {

                            showToast(readNotification.getMessage());


                            List<NotificationApi.Data.Notification> list = fastAdapter.getList();

                            if (list != null && !list.isEmpty()) {

                                for (int i = 0; i < list.size(); i++) {

                                    list.get(i).setSeen(1);
                                }

                                fastAdapter.updateList(list);

                            }


                        }

                    }
                });

    }


    private void setRead(final NotificationApi.Data.Notification notification, String id, final int position) {

        if (notification != null && notification.getSeen() == 0) {
            RetrofitAdapter.getNetworkApiServiceClient().readNotification(Preferences.INSTANCE.getAuthendicate(), id)
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CallbackWrapper<ReadNotification>(this) {
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        protected void onSuccess(ReadNotification readNotification) {


                            if (readNotification.getSuccess()) {

                                notification.setSeen(1);

                                int count = Preferences.INSTANCE.getNotCount() - 1;

                                Preferences.INSTANCE.putNotiCount(count);

                                fastAdapter.updateData(notification, position);

                                if (notification.getType1().toLowerCase().equalsIgnoreCase("announcement")) {

                                    AnnouncementApi.Datum datum = new AnnouncementApi.Datum();

                                    datum.setId(notification.getId());
                                    datum.setTitle(notification.getDescription());

                                    AnnouncementDialogFragment.getInstance(datum, true).show(getSupportFragmentManager(), "Announcement");
                                } else {

                                    moveToEventDetailsActivity(notification);
                                    showToast(readNotification.getMessage());

                                }


                            }

                        }
                    });
        } else {

            if (notification != null && notification.getType1().toLowerCase().equalsIgnoreCase("announcement")) {

                AnnouncementApi.Datum datum = new AnnouncementApi.Datum();
                datum.setId(notification.getId());
                datum.setTitle(notification.getDescription());
                datum.setMessage(notification.getDescription());
                AnnouncementDialogFragment.getInstance(datum, true).show(getSupportFragmentManager(), "Announcement");

            } else {

                moveToEventDetailsActivity(notification);

            }
        }


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void moveToEventDetailsActivity(NotificationApi.Data.Notification notification) {


        if (notification != null)
            if (notification.getNotificationType().toLowerCase().equalsIgnoreCase("event") && !notification.getDescription().toLowerCase().contains("event cancelled")) {

                Intent i = EventDetailsActivity.getIntent(this, notification.getEventId());

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this);

                startActivity(i, transitionActivityOptions.toBundle());

//                startActivity();


            } else if (notification.getNotificationType().toLowerCase().equalsIgnoreCase("paid_event")) {

                Intent i = PaidEventDetailsActivity.getIntent(this, 0);

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this);

                startActivity(i, transitionActivityOptions.toBundle());

            }

    }


    private static final String TAG = "NotificationActivity";

    private void loadData() {


        RetrofitAdapter.getNetworkApiServiceClient()
                .getAllNotification(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CallbackWrapper<NotificationApi>(this) {
                    @Override
                    protected void onSuccess(NotificationApi notificationApi) {

                        if (notificationApi.getSuccess()) {

                            showNotification(notificationApi);
                        } else {

                            showToast(notificationApi.getMessage());
                        }
                    }
                });
    }


    private void showNotification(NotificationApi notificationApi) {


        if (notificationApi.getSuccess() && !Utils.IsNull(notificationApi.getData()) && !Utils.IsNullOrEmpty(notificationApi.getData().getNotifications())) {

            List<NotificationApi.Data.Notification> list = notificationApi.getData().getNotifications();
            msg.setVisibility(View.GONE);
            fastAdapter.updateList(list);


        } else {

            mNotificationView.setVisibility(View.GONE);
            msg.setVisibility(View.VISIBLE);


        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.clearall, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.clear_all).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
        }
//        else if (item.getItemId() == R.id.read_all) {
//
//            if (getItemCount())
//                setAllToRead();
//            else showToast(getString(R.string.no_data_notification));
//
//        }
        else if (item.getItemId() == R.id.clear_all) {
            if (getItemCount()) {

//                AlertUtils.showDialog(this, new OnDialogListener() {
//                    @Override
//                    public void onOkClick() {
//                        deleteAllNofication();
//                    }
//
//                    @Override
//                    public void onCancelClick() {
//
//                    }
//                });

                AlertUtils.showDialog(this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllNofication();
                        dialog.dismiss();
                    }
                });

            } else showToast(getString(R.string.no_data_notification));

        }

        return super.onOptionsItemSelected(item);
    }


    private boolean getItemCount() {

        return fastAdapter.getCount() > 0;
    }


    private void deleteAllNofication() {

        RetrofitAdapter.getNetworkApiServiceClient().deleteAllNotification(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CallbackWrapper<Object>(this) {
                    @Override
                    protected void onSuccess(Object s) {
                        fastAdapter.updateList(new ArrayList<NotificationApi.Data.Notification>());
                        updateUI();
                    }
                });

    }


    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {


        if (viewHolder instanceof NotificationListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
//            String name = fastAdapter.get(viewHolder.getAdapterPosition()).getName();
//
//            // backup of removed item for undo purpose
            final NotificationApi.Data.Notification deletedItem = fastAdapter.get(viewHolder.getAdapterPosition());
//            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view


            AlertUtils.showDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                    RetrofitAdapter.getNetworkApiServiceClient().deleteNotificationById(Preferences.INSTANCE.getAuthendicate(), deletedItem.getId())
                            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new CallbackWrapper<Object>(getApplicationContext()) {
                                @Override
                                protected void onSuccess(Object s) {
                                    fastAdapter.removeItem(viewHolder.getAdapterPosition());
                                    updateUI();
                                }
                            });

                }
            });


//            // showing snack bar with Undo option
//            Snackbar snackbar = Snackbar
//                    .make(mNotificationView, name + " removed from notification!", Snackbar.LENGTH_LONG);
//            snackbar.setAction("UNDO", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    // undo is selected, restore the deleted item
//                    fastAdapter.restoreItem(deletedItem, deletedIndex);
//                }
//            });
//            snackbar.setActionTextColor(Color.YELLOW);
//            snackbar.show();
        }

    }


    private void updateUI() {

        if (!(fastAdapter.getSize() > 0)) {


            mNotificationView.setVisibility(View.GONE);
            msg.setVisibility(View.VISIBLE);

            Toast.makeText(this, getString(R.string.no_data_notification), Toast.LENGTH_SHORT).show();

        } else {
            mNotificationView.setVisibility(View.VISIBLE);
            msg.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.msg)
    public void onViewClicked() {
    }
}
