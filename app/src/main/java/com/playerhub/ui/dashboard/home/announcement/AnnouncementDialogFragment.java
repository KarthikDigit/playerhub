package com.playerhub.ui.dashboard.home.announcement;


import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.playerhub.R;
import com.playerhub.common.ActivityStats;
import com.playerhub.databinding.FragmentAnnouncementDialogBinding;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.network.response.EventListApi.EventListResponseApi;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;

import org.parceler.Parcels;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnnouncementDialogFragment extends BottomSheetDialogFragment {

    private static final String KEY_ANNOUNCEMENT_DATE = "announcement_data";
    private static final String KEY_IS_FROM_SERVER = "isfromserver";

    FragmentAnnouncementDialogBinding binding;

    public AnnouncementDialogFragment() {
        // Required empty public constructor
    }


    public static AnnouncementDialogFragment getInstance(AnnouncementApi.Datum datum, boolean isFromServer) {

        AnnouncementDialogFragment fragment = new AnnouncementDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_ANNOUNCEMENT_DATE, Parcels.wrap(datum));
        bundle.putBoolean(KEY_IS_FROM_SERVER, isFromServer);

        fragment.setArguments(bundle);

        return fragment;
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(final Bundle savedInstanceState) {
//
//        // the content
//        final RelativeLayout root = new RelativeLayout(getActivity());
//        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//
//        // creating the fullscreen dialog
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(root);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        return dialog;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_announcement_dialog, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_announcement_dialog, container, false);


        if (getArguments() != null && getArguments().getBoolean(KEY_IS_FROM_SERVER)) {

            AnnouncementApi.Datum datum = Parcels.unwrap(getArguments().getParcelable(KEY_ANNOUNCEMENT_DATE));

            binding.setData(datum);

            if (datum != null) {

                getAnnouncementFromServer(datum);
            }
        } else {


            AnnouncementApi.Datum datum = null;
            if (getArguments() != null) {
                datum = Parcels.unwrap(getArguments().getParcelable(KEY_ANNOUNCEMENT_DATE));
                binding.setData(datum);
            }


        }

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        return binding.getRoot();

    }

    private static final String TAG = "AnnouncementDialogFragm";

    private void getAnnouncementFromServer(AnnouncementApi.Datum datum) {


        Observable<String> observable = RetrofitAdapter.getNetworkApiServiceClient().getAnnouncementById(Preferences.INSTANCE.getAuthendicate(), datum.getId());


        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String value) {

                        Log.e(TAG, "onNext: " + value);

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
