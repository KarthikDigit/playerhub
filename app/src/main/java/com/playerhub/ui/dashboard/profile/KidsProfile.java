package com.playerhub.ui.dashboard.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.cameraorgallery.CameraAndGallary;
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

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Response;

public class KidsProfile extends MultiStateViewActivity implements CameraAndGallary.CameraAndGallaryCallBack {


    private static final String EXTRA_ID = "id";

    private static final String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_PER_CAMERA = 145;

    @BindView(R.id.actionBar)
    RelativeLayout actionBar;
    @BindView(R.id.constraintLayout2)
    RelativeLayout constraintLayout2;
    @BindView(R.id.fullImage)
    ImageView fullImage;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.firstName)
    TextInputLayout firstName;
    @BindView(R.id.lastName)
    TextInputLayout lastName;
    @BindView(R.id.save_edit)
    ImageView saveEdit;
    @BindView(R.id.camera)
    ImageView camera;
    @BindView(R.id.team_name)
    TextInputLayout teamName;
    @BindView(R.id.coach_name)
    TextInputLayout coachName;
    @BindView(R.id.organation)
    TextInputLayout organation;
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


    private boolean isSave = false;

    private CameraAndGallary cameraAndGallary;

    public static Intent getInstance(Context context, int id) {

        Intent intent = new Intent(context, KidsProfile.class);

        intent.putExtra(EXTRA_ID, id);

        return intent;

    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public int getLayoutByID() {

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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
//
//        transparentStatusAndNavigation();

        return R.layout.activity_kids_profile_backup;
    }

    @Override
    public void initViews() {

//        transparentStatusAndNavigation();
        cameraAndGallary = new CameraAndGallary(this, this);


        camera.setVisibility(View.GONE);
        onRetryOrCallApi();
    }

    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onRetryOrCallApi() {

        int id = getKidId();

        showViewLoading();
        RetrofitAdapter.getNetworkApiServiceClient().fetchKidDetailsById(Preferences.INSTANCE.getAuthendicate(), id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyCallBack<KidInfoResponse>(this, this, false, false) {
                    @Override
                    public void onSuccess(KidInfoResponse kidInfoResponse) {

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

        ImageUtility.loadImage(profileImage, profileImage, data.getAvatar_image());
        ImageUtility.loadImage(profileImage, fullImage, data.getAvatar_image());
        camera.setVisibility(View.VISIBLE);

    }

    @OnClick({R.id.save_edit})
    public void onSaveEdit(View view) {

        isSave = !isSave;

        editMode();

    }


    private void editMode() {


        int drawabelId = isSave ? R.drawable.ic_check_black_24dp : R.drawable.ic_edit_black_24dp;
        saveEdit.setImageResource(drawabelId);

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


}
