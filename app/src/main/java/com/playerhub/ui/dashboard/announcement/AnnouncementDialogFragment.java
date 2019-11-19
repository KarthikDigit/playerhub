package com.playerhub.ui.dashboard.announcement;


import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.databinding.FragmentAnnouncementDialogBinding;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.preference.Preferences;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnnouncementDialogFragment extends DialogFragment {

    private static final String KEY_ANNOUNCEMENT_DATE = "announcement_data";
    private static final String KEY_IS_FROM_SERVER = "isfromserver";

    FragmentAnnouncementDialogBinding binding;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.line_view)
    View lineView;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.btn_close)
    ImageView btnClose;

    Unbinder unbinder;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {


//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.fragment_add_event, null);
//        builder.setView(view);
//        Dialog dialog = builder.create();
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//
//        return dialog;

//        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());

//        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        dialog.setTitle("Announcement");
        dialog.setContentView(root);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
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

//    @Override
//    public void setupDialog(Dialog dialog, int style) {
////        super.setupDialog(dialog,style);
//        View contentView = View.inflate(getContext(), R.layout.fragment_announcement_dialog, null);
//        dialog.setContentView(contentView);
//
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent())
//                .getLayoutParams();
//        CoordinatorLayout.Behavior behavior = params.getBehavior();
//        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
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


        unbinder = ButterKnife.bind(this, binding.getRoot());

        title.setTranslationY(100);
        title.setAlpha(0);
        title.animate().translationY(0).alpha(1).setStartDelay(500).start();

        lineView.setTranslationY(100);
        lineView.setAlpha(0);
        lineView.animate().translationY(0).alpha(1).setStartDelay(500).start();

        description.setTranslationY(100);
        description.setAlpha(0);
        description.animate().translationY(0).alpha(1).setStartDelay(500).start();


        btnClose.animate().scaleX(1).scaleY(1).setStartDelay(700).start();


        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((View) getView().getParent()).setBackgroundColor(Color.TRANSPARENT);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_close)
    public void onViewClicked() {

        dismiss();

    }
}
