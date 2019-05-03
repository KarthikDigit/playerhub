package com.playerhub.ui.dashboard.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.playerhub.utils.KeyboardUtils;
import com.playerhub.utils.TextInputUtil;

import butterknife.BindView;
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


    private static final String EXTRA_ID = "id";

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

    private boolean isSave = false;

    private CameraAndGallary cameraAndGallary;

    public static Intent getInstance(Context context, int id) {

        Intent intent = new Intent(context, MaterialProfileActivity.class);

        intent.putExtra(EXTRA_ID, id);

        return intent;

    }

    @Override
    public int getLayoutByID() {
        return R.layout.activity_material_profile;
    }


    @Override
    protected void initViews() {

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle("Profile");
        }

        cameraAndGallary = new CameraAndGallary(this, this);

        camera.setVisibility(View.VISIBLE);
        appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener);


        onRetryOrCallApi();
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

        int id = getKidId();

        showViewLoading();
        RetrofitAdapter.getNetworkApiServiceClient().fetchKidDetailsById(Preferences.INSTANCE.getAuthendicate(), id)

                .compose(MaterialProfileActivity.<KidInfoResponse>apply())
                .subscribe(new MyCallBack<KidInfoResponse>(this, this, false, false) {
                    @Override
                    void onSuccess(KidInfoResponse kidInfoResponse) {

                        showViewContent();

                        if (kidInfoResponse != null && kidInfoResponse.getData() != null) {

                            setData(kidInfoResponse.getData().getKidinfo());

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


    private void setData(KidInfoResponse.Kidinfo data) {


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

        ImageUtility.loadImage(profileImage, data.getAvatar_image());
        ImageUtility.loadImage(fullImage, data.getAvatar_image());
        camera.setVisibility(View.VISIBLE);


    }


    private void editMode() {


//        int drawabelId = isSave ? R.drawable.ic_check_black_24dp : R.drawable.ic_edit_black_24dp;
//        saveEdit.setImageResource(drawabelId);

        invalidateOptionsMenu();

        TextInputUtil.setEnable(firstName, isSave);
        TextInputUtil.setEnable(lastName, isSave);

        if (isSave) {
            TextInputUtil.setFocusable(firstName, true);
            KeyboardUtils.requestFocus(getWindow(), firstName);
            KeyboardUtils.openKeyboard(this, firstName.getEditText());
        } else {
            KeyboardUtils.closeKeyboard(this);
            updateKidDetails();
        }

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
            cameraAndGallary.selectImage();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_and_location_rationale),
                    REQUEST_PER_CAMERA, perms);
        }

    }


    public void onBack(View view) {

        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        cameraAndGallary.onActivityResult(requestCode, resultCode, data);

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
                    void onSuccess(String response) {

                        showToast("Kid profile image is successfully updated...");

                    }
                });


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
                    void onSuccess(KidDetailsUpdatedResponse response) {

                        if (response != null) {
                            showToast(response.getMessage());
                        }

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        int drawabelId = isSave ? R.drawable.ic_check_black_24dp : R.drawable.ic_edit_black_24dp;

        menu.findItem(R.id.save_edit).setIcon(drawabelId);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_edit) {

            isSave = !isSave;

            editMode();
        }

        return super.onOptionsItemSelected(item);
    }


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
}
