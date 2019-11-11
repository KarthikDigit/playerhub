package com.playerhub.ui.dashboard.announcement.announcementviewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.preference.Preferences;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementListViewModel extends ViewModel {

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> dataStatus = new MutableLiveData<>();

    private MutableLiveData<List<AnnouncementApi.Datum>> announcementList;


    public LiveData<List<AnnouncementApi.Datum>> getAnnouncementList() {


        if (announcementList == null) {

            announcementList = new MutableLiveData<>();
        }

        getAnnouncements();

        return announcementList;
    }

    public void getAnnouncements() {


        isLoading.setValue(true);

        RetrofitAdapter.getNetworkApiServiceClient().fetchAnnouncements(Preferences.INSTANCE.getAuthendicate())
                .delay(2000,TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AnnouncementApi>() {
                    @Override
                    public void accept(AnnouncementApi announcementApi) throws Exception {

                        isLoading.setValue(false);

                        setAnnouncementList(announcementApi);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        isLoading.setValue(false);

                    }
                });

    }


    private void setAnnouncementList(AnnouncementApi response) {


        if (response != null && response.getSuccess()) {
            if (response.getData() != null && !response.getData().isEmpty()) {
                dataStatus.setValue(false);
                announcementList.setValue(response.data);


            } else {
                dataStatus.setValue(true);
            }

        } else {
            dataStatus.setValue(true);
        }

    }
}
