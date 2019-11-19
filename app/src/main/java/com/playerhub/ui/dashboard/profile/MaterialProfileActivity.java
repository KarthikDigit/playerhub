package com.playerhub.ui.dashboard.profile;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.Visibility;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commit451.elasticdragdismisslayout.ElasticDragDismissFrameLayout;
import com.commit451.elasticdragdismisslayout.ElasticDragDismissListener;
import com.playerhub.R;
import com.playerhub.cameraorgallery.CameraAndGallary;
import com.playerhub.customview.MultiStateView;
import com.playerhub.network.APIErrorUtil;
import com.playerhub.network.LoginAPIError;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.request.UpdateKidDetail;
import com.playerhub.network.response.KidDetailsUpdatedResponse;
import com.playerhub.network.response.KidInfoResponse;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.MultiStateViewActivity;
import com.playerhub.utils.ImageUtility;
import com.playerhub.utils.ImageUtils;
import com.playerhub.utils.TextInputUtil;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Response;

public class MaterialProfileActivity extends MultiStateViewActivity implements CameraAndGallary.CameraAndGallaryCallBack {

    private static final int REQUEST_CODE = 123;
    private static final String EXTRA_ID = "id";
    public static final String EXTRA_LOGO = "logo";

    private static final String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_PER_CAMERA = 145;
    @BindView(R.id.fullImage)
    ImageView fullImage;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.camera)
    ImageView camera;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.team_name)
    TextInputLayout teamName;
    @BindView(R.id.coach_name)
    TextInputLayout coachName;
    @BindView(R.id.organation)
    TextInputLayout organation;
    @BindView(R.id.firstName)
    TextInputLayout firstName;
    @BindView(R.id.lastName)
    TextInputLayout lastName;
    @BindView(R.id.dob)
    TextInputLayout dob;
    @BindView(R.id.phone_number)
    TextInputLayout phoneNumber;
    @BindView(R.id.parent_email)
    TextInputLayout parentEmail;
    @BindView(R.id.parent_name)
    TextInputLayout parentName;
    @BindView(R.id.join_date)
    TextInputLayout joinDate;
    @BindView(R.id.constraintLayout)
    NestedScrollView constraintLayout;
    @BindView(R.id.multiStateView)
    MultiStateView multiStateView;
    @BindView(R.id.actionBar_layout)
    RelativeLayout mActionBarLayout;
    @BindView(R.id.rootview)
    ElasticDragDismissFrameLayout rootview;
    @BindView(R.id.edit)
    FloatingActionButton mEditButton;

    //    @BindView(R.id.collapsing_toolbar)
//    CollapsingToolbarLayout mCollapsingToolbarLayout;
//
    private boolean isSave = false;
    private boolean isShow = false;

    private boolean isUpdated = false;

    private CameraAndGallary cameraAndGallary;

    public static Intent getInstance(Context context, int id) {

        Intent intent = new Intent(context, MaterialProfileActivity.class);

        intent.putExtra(EXTRA_ID, id);

        return intent;

    }

    private static void setStatusBarTransparent(Activity activity, boolean isEdit) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
////            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//            if (isEdit)
//                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }

//        else {
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
    }

    @Override
    public int getLayoutByID() {

        setStatusBarTransparent(this, false);

//        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
//            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
//        }
//        if (Build.VERSION.SDK_INT >= 19) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
//        //make fully Android Transparent Status bar
//        if (Build.VERSION.SDK_INT >= 21) {
//            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }

//        getWindow().setStatusBarColor(Color.TRANSPARENT);

        return R.layout.activity_material_profile;
    }


    private void setupWindowAnimation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition fade = new Slide();
//            fade.setMode(Visibility.MODE_IN);
            fade.setDuration(500);
            getWindow().setEnterTransition(fade);

            fade.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {

                    mEditButton.animate().scaleY(1).scaleX(1).start();
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });

            Fade fadeOut = new Fade();
            fadeOut.setMode(Visibility.MODE_OUT);
            fadeOut.setDuration(100);
            getWindow().setReturnTransition(fadeOut);

//            Slide slide = new Slide();
//            slide.setSlideEdge(Gravity.BOTTOM);
//            slide.setDuration(1000);
//            getWindow().setReturnTransition(slide);
        }

    }

    @Override
    protected void initViews() {

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle("Profile");

            setActionBarTitleBold("Profile");


        }

        cameraAndGallary = new CameraAndGallary(this, this);

        camera.setVisibility(View.VISIBLE);
        appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener);


        setupWindowAnimation();

        onRetryOrCallApi();


        rootview.addListener(new ElasticDragDismissListener() {
            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {

            }

            @Override
            public void onDragDismissed() {

                ActivityCompat.finishAffinity(MaterialProfileActivity.this);
            }
        });
    }

    public static <T> ObservableTransformer<T, T> apply() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    @Override
    protected void onRetryOrCallApi() {


        String logo = null;

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_LOGO)) {

            logo = intent.getStringExtra(EXTRA_LOGO);
        }

        if (logo != null) {

            Picasso.get().load(logo).error(R.drawable.avatar_mini).into(profileImage);
            Picasso.get().load(logo).error(R.drawable.avatar_mini).into(fullImage);
        }


        int id = getKidId();

        showViewLoading();
        final String finalLogo = logo;
        RetrofitAdapter.getNetworkApiServiceClient().fetchKidDetailsById(Preferences.INSTANCE.getAuthendicate(), id)

                .compose(MaterialProfileActivity.<KidInfoResponse>apply())
                .subscribe(new MyCallBack<KidInfoResponse>(this, this, false, false) {
                    @Override
                    public void onSuccess(KidInfoResponse kidInfoResponse) {

                        showViewContent();

                        if (kidInfoResponse != null && kidInfoResponse.getData() != null) {

                            setData(kidInfoResponse.getData().getKidinfo(), finalLogo);

                        } else {

                            showViewEmpty("There is no data ");

                        }
                    }
                });

    }

    private int getKidId() {
        return getIntent().getIntExtra(EXTRA_ID, 235);
    }

    @Override
    public void onManuallyParseError(Response<?> response, boolean isToastMsg) {

        LoginAPIError loginAPIError = APIErrorUtil.parseErrorTest(LoginAPIError.class, response);

        if (loginAPIError != null) {

            if (isToastMsg) {

                showToast(loginAPIError.getMessage());

            } else {

                showViewError(loginAPIError.getMessage());

            }

        }

    }


    @Override
    public void showViewContent() {
        super.showViewContent();

        showHideActionBarLayout(false);


    }

    @Override
    public void showViewError(String errorMsg) {
        super.showViewError(errorMsg);
        showHideActionBarLayout(true);
    }

    @Override
    public void showViewError() {
        super.showViewError();

        showHideActionBarLayout(true);
    }

    private void showHideActionBarLayout(boolean isHide) {
        isShow = !isHide;
//        mActionBarLayout.setVisibility(isHide ? View.GONE : View.VISIBLE);

        appBarLayout.setExpanded(isShow, isShow);
        invalidateOptionsMenu();
    }

    private void setData(KidInfoResponse.Kidinfo data, String finalLogo) {


        name.setText(String.format("%s %s", data.getFirstname(), data.getLastname()));

        TextInputUtil.setText(teamName, data.getTeam());
        TextInputUtil.setText(coachName, data.getCoach());
        TextInputUtil.setText(organation, data.getOrganization());
        TextInputUtil.setText(firstName, data.getFirstname());
        TextInputUtil.setText(lastName, data.getLastname());
        TextInputUtil.setText(dob, data.getBirthday() != null ? data.getBirthday() : " ");
        TextInputUtil.setText(phoneNumber, data.getPhone());
        TextInputUtil.setText(parentEmail, data.getEmail());
        TextInputUtil.setText(parentName, data.getParentName());
        TextInputUtil.setText(joinDate, data.getJoinedOn());

        if (finalLogo == null) {
            ImageUtility.loadImage(profileImage, profileImage, data.getAvatar_image());
            ImageUtility.loadImage(profileImage, fullImage, data.getAvatar_image());
        }

        camera.setVisibility(View.VISIBLE);


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void editMode() {

        FloatingActionButton fab = findViewById(R.id.edit);

        String fName = TextInputUtil.getText(firstName);
        String lName = TextInputUtil.getText(lastName);

        Intent intent = UpdateProfileActivity.getInstance(this, getKidId(), fName, lName);

        startActivityForResult(intent, REQUEST_CODE);


//        Intent intent = new Intent(MainActivity.this, DialogActivity.class);
//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab, getString(R.string.transition_dialog));
//        startActivityForResult(intent, REQUEST_CODE, options.toBundle());


//        int drawabelId = isSave ? R.drawable.ic_check_black_24dp : R.drawable.ic_edit_black_24dp;
//        saveEdit.setImageResource(drawabelId);

//        invalidateOptionsMenu();
//
//        TextInputUtil.setEnable(firstName, isSave);
//        TextInputUtil.setEnable(lastName, isSave);
//
//        if (isSave) {
//            TextInputUtil.setFocusable(firstName, true);
//            KeyboardUtils.requestFocus(getWindow(), firstName);
//            KeyboardUtils.openKeyboard(this, firstName.getEditText());
//        } else {
//            KeyboardUtils.closeKeyboard(this);
//            updateKidDetails();
//        }
//
//        setStatusBarTransparent(this, !isSave);

    }


    @OnClick({R.id.camera, R.id.profile_image})
    public void onKidImageUpload(View view) {

        requestCameraPermission();

    }


    @AfterPermissionGranted(REQUEST_PER_CAMERA)
    public void requestCameraPermission() {

        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            cameraAndGallary.selectImageProfileUpadte();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_and_location_rationale),
                    REQUEST_PER_CAMERA, perms);
        }

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        onBack(null);
    }

    public void onBack(View view) {


        if (isUpdated) {


            setResult(RESULT_OK, new Intent());

            finish();


        } else {

            finish();
//            onBackPressed();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        cameraAndGallary.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            String fName = data.getStringExtra(UpdateProfileActivity.EXTRA_FIRSTNAME);
            String lName = data.getStringExtra(UpdateProfileActivity.EXTRA_LASTNAME);

            name.setText(String.format("%s %s", fName, lName));
            TextInputUtil.setText(firstName, fName);
            TextInputUtil.setText(lastName, lName);
            isUpdated = true;

        }

    }

    @Override
    public void onSelectFromGalleryResult(Bitmap bitmap) {

        fullImage.setImageBitmap(bitmap);
        profileImage.setImageBitmap(bitmap);

        String encoded = ImageUtils.convertImageToBase64(bitmap);

        int id = getKidId();

        RetrofitAdapter.getNetworkApiServiceClient().updateKidProfileImage1(Preferences.INSTANCE.getAuthendicate(), encoded, id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyCallBack<String>(this, this, true, true) {
                    @Override
                    public void onSuccess(String response) {

                        showToast("Kid profile image is successfully updated...");

                    }
                });


    }

    @Override
    public void onVideo(File file) {

    }

    private void updateKidDetails() {


        UpdateKidDetail detail = new UpdateKidDetail();
        detail.setFirstname(TextInputUtil.getText(firstName));
        detail.setLastname(TextInputUtil.getText(lastName));

        int id = getKidId();

        RetrofitAdapter.getNetworkApiServiceClient().updateKidDetails(Preferences.INSTANCE.getAuthendicate(), detail, id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyCallBack<KidDetailsUpdatedResponse>(this, this, true, true) {
                    @Override
                    public void onSuccess(KidDetailsUpdatedResponse response) {

                        if (response != null) {
                            showToast(response.getMessage());
                        }

                    }
                });

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.edit)
    public void onFabEditClick(View view) {

        isSave = !isSave;

        editMode();

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.edit, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//
//
//        int drawabelId = isSave ? R.drawable.ic_check_black_24dp : R.drawable.ic_edit_black_24dp;
//
//        menu.findItem(R.id.save_edit).setIcon(drawabelId);
//
//        menu.findItem(R.id.save_edit).setVisible(isShow);
//
//        return super.onPrepareOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == R.id.save_edit) {
//
//            isSave = !isSave;
//
//            editMode();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    private AppBarLayout.OnOffsetChangedListener onOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                collapsingToolbar.setTitle("Profile");

            } else {
                //Expanded
                collapsingToolbar.setTitle("");

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


}
