package com.playerhub.ui.dashboard.announcement;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.playerhub.R;
import com.playerhub.customview.CustomMultiSpinnerInputLayout;
import com.playerhub.customview.CustomSpinnerInputLayout;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.EventTypesResponse;
import com.playerhub.network.response.TeamResponse;
import com.playerhub.preference.Preferences;
import com.playerhub.utils.ProgressUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

//import com.google.android.libraries.places.compat.AutocompletePredictionBufferResponse; // New
//import com.google.android.libraries.places.compat.AutocompletePrediction; //New
//import com.google.android.libraries.places.compat.AutocompleteFilter; //New
//import com.google.android.libraries.places.compat.GeoDataClient; //New
//import com.google.android.libraries.places.compat.Place; //New
//import com.google.android.libraries.places.compat.Places; //New
//import com.google.android.libraries.places.compat.PlaceBufferResponse; //New

/**
 * A simple {@link Fragment} subclass.
 */
public class PostAnnouncementFragment extends DialogFragment {


    private static final String TAG = "AddEventFragment";

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    protected CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R.id.select_team)
    CustomMultiSpinnerInputLayout selectTeam;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.messages)
    EditText messages;
    @BindView(R.id.footerline)
    View footerline;
    @BindView(R.id.close)
    AppCompatButton close;
    @BindView(R.id.cancel)
    AppCompatButton cancel;
    @BindView(R.id.send)
    AppCompatButton send;
    @BindView(R.id.create_event_card)
    CardView sendCardView;
    Unbinder unbinder;


    private TeamResponse teamResponse;

    public PostAnnouncementFragment() {
        // Required empty public constructor
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
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());

//        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        dialog.setTitle("Add Event");
        dialog.setContentView(root);
        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_post_announcement, container, false);
        unbinder = ButterKnife.bind(this, view);

        Observable<TeamResponse> observableTeam = RetrofitAdapter.getNetworkApiServiceClient().fetchAllTeams(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        ProgressUtils.showProgress(getContext(), "Loading");

        observableTeam.subscribe(new Observer<TeamResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(TeamResponse teamResponse1) {
                ProgressUtils.hideProgress();
                teamResponse = teamResponse1;
            }

            @Override
            public void onError(Throwable e) {
                ProgressUtils.hideProgress();
                Log.e(TAG, "onError: " + e.getMessage());

            }

            @Override
            public void onComplete() {
                ProgressUtils.hideProgress();
            }
        });


//        final String[] teamList = {"Demo Team ", "2001 Boys Pro", "2005 Boys Pro"};


        selectTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (teamResponse != null && teamResponse.data != null && teamResponse.data.teams != null)
                    selectTeam.showPopUp(teamResponse.data.teams, getFragmentManager());
                else Toast.makeText(getActivity(), "There is no team", Toast.LENGTH_SHORT).show();


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
//                getActivity().finish();
            }
        });
        sendCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postAnnouncements();
            }
        });

        sendCardView.setAlpha(0);
        cancel.setAlpha(0);
        sendCardView.animate().alpha(1).scaleY(1).scaleX(1).setStartDelay(500).start();
        cancel.animate().alpha(1).scaleY(1).scaleX(1).setStartDelay(500).start();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void postAnnouncements() {


        String title_txt = title.getText().toString();
        String message_txt = messages.getText().toString();

        List<TeamResponse.Data.Team> teamList = selectTeam.getSelectedTeam();
        List<Integer> teamIds = new ArrayList<>();
        if (teamList != null && !teamList.isEmpty() && !TextUtils.isEmpty(title_txt) && !TextUtils.isEmpty(message_txt)) {


            for (int i = 0; i < teamList.size(); i++) {

                teamIds.add(teamList.get(i).id);
            }

            Map<String, Object> params = new HashMap<>();

            params.put("title", title_txt);
            params.put("message", message_txt);
            params.put("teams", teamIds);

            ProgressUtils.showProgress(getContext(), "Loading");

            RetrofitAdapter.getNetworkApiServiceClient().postAnnouncements(Preferences.INSTANCE.getAuthendicate(), params)
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(String s) {

                            ProgressUtils.hideProgress();

                            dismiss();
//                            getActivity().finish();
                        }

                        @Override
                        public void onError(Throwable e) {

                            ProgressUtils.hideProgress();

                            dismiss();
//                            getActivity().finish();
                            Log.e(TAG, "onError: " + e.getMessage());

                        }

                        @Override
                        public void onComplete() {

                        }
                    });


        } else {

            Toast.makeText(getContext(), "Please select atleast a team", Toast.LENGTH_SHORT).show();
        }

//        ProgressUtils.showProgress(getContext(), "Loading");
//
//        RetrofitAdapter.getNetworkApiServiceClient().postEvents(Preferences.INSTANCE.getAuthendicate(), new HashMap<String, Object>())
//                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//
//                        ProgressUtils.hideProgress();
//
//
//                        Toast.makeText(getContext(), "You successfully posted a new event", Toast.LENGTH_SHORT).show();
//
////                            Log.e(TAG, "onNext: " + s);
//
//                        dismiss();
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ProgressUtils.hideProgress();
//                        Log.e(TAG, "onError: " + e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        ProgressUtils.hideProgress();
//
//                    }
//                });


    }


    private void showInfo() {


        Toast.makeText(getContext(), "Please select atleast a week", Toast.LENGTH_SHORT).show();

    }


}
