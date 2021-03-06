package com.playerhub.ui.dashboard.home;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.playerhub.R;
import com.playerhub.common.ConnectivityHelper;
import com.playerhub.common.OnPageChangeListener;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.network.response.Coach;
import com.playerhub.network.response.Kid;
import com.playerhub.network.response.KidsAndCoaches;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.RunTestActivity;
import com.playerhub.ui.base.BaseNetworkCheck;
import com.playerhub.ui.dashboard.DashBoardActivity;
import com.playerhub.ui.dashboard.announcement.AnnouncementDialogFragment;
import com.playerhub.ui.dashboard.announcement.MoreAnnouncementFragment;
import com.playerhub.ui.dashboard.announcement.PostAnnouncementFragment;
import com.playerhub.ui.dashboard.home.addevent.AddEventActivity;
import com.playerhub.ui.dashboard.notification.NotificationActivity;
import com.playerhub.ui.dashboard.profile.CoachProfileFragment;
import com.playerhub.ui.dashboard.profile.MaterialProfileActivity;
import com.playerhub.ui.dashboard.profile.ProfileDetailsActivity;
import com.rd.PageIndicatorView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

//import jp.wasabeef.blurry.Blurry;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseNetworkCheck implements ParentChildPagerAdapter.OnItemClicklistener, CardPagerAdapter.OnItemClickListener {

    private static final int REQUEST_CODE = 123;
    @BindView(R.id.noti_img)
    ConstraintLayout notiImg;
    @BindView(R.id.textView2)
    TextView notificationCount;

    RenderScript rs;
    Unbinder unbinder;

//    @BindView(R.id.fullImage)
//    ImageView mFullImage;

//    @BindView(R.id.fab_l)
//    OptionsFabLayout mFloatingMenu;

    @BindView(R.id.viewPager)
    ViewPager profileViewPager;
    //    @BindView(R.id.viewPager1)
//    ViewPager profileViewPager1;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView indicatorView;

//
//    @BindView(R.id.announcement_layout)
//    RelativeLayout announcement_layout;

    Unbinder unbinder1;
    @BindView(R.id.myView)
    View myView;
    //    @BindView(R.id.myrView)
//    View myrView;
    //    @BindView(R.id.blurimage)
//    ImageView mBlurImage;
    @BindView(R.id.myrootView)
    FrameLayout myrootView;
    @BindView(R.id.rootview)
    RelativeLayout rootview;
    @BindView(R.id.btn_announcement)
    FrameLayout btnAnnouncement;
    @BindView(R.id.fab_circle_view)
    ImageView fabCircleView;
    @BindView(R.id.fab_fullView)
    View fabFullView;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    private FloatingActionMenu actionMenu;
    //    @BindView(R.id.testView)
//    RealtimeBlurView testView;
    private ParentChildPagerAdapter parentChildPagerAdapter;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private Animation blink;
    private Bitmap mBlurredBitmap;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutByID() {
        return R.layout.fragment_home;
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void initViews() {

        blink = AnimationUtils.loadAnimation(getContext(), R.anim.blink);

//        profileViewPager.setClipToPadding(false);
//        profileViewPager.setPadding(60, 10, 60, 10);
//        profileViewPager.setPageMargin(50);
        parentChildPagerAdapter = new ParentChildPagerAdapter(getContext(), new ArrayList<ParentChild>());

        profileViewPager.setAdapter(parentChildPagerAdapter);

        profileViewPager.setOffscreenPageLimit(parentChildPagerAdapter.getCount());
        parentChildPagerAdapter.setOnItemClicklistener(this);

        profileViewPager.addOnPageChangeListener(new OnPageChangelistener(indicatorView) {
            @Override
            public void onPageSelected(int i) {
                super.onPageSelected(i);

//                ParentChild parentChild = parentChildPagerAdapter.getItem(i);
//                ImageUtility.loadImage(mFullImage, parentChild.getImgUrl());

            }
        });


//        profileViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

//
//        new TapTargetSequence(getActivity())
//                .targets(
//                        TapTarget.forView(notiImg, "Notifications", "You can see notification alerts here")
//
//
//                ).start();


        mCardAdapter = new CardPagerAdapter(this);
//        mCardAdapter.addCardItem(new CardItem(getString(R.string.sample_date)));

//        mCardShadowTransformer = new ShadowTransformer(profileViewPager1, mCardAdapter);

//        profileViewPager1.setAdapter(mCardAdapter);
////        profileViewPager1.setPageTransformer(false, mCardShadowTransformer);
////        profileViewPager1.setOffscreenPageLimit(3);
//
//        announcement_layout.setVisibility(View.GONE);
//
//        profileViewPager1.setClipChildren(false);
//        profileViewPager1.setPadding(100, 10, 100, 20);
//        profileViewPager1.setPageMargin(0);

//        profileViewPager.setPageTransformer(true, new DepthPageTransformer());

//        addParentProfile();


//        mFloatingMenu.setMainFabOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (mFloatingMenu.isOptionsMenuOpened()) {
//                    mFloatingMenu.closeOptionsMenu();
//                }
//            }
//        });
//
//        mFloatingMenu.setMiniFabsColors(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary);
//
//        mFloatingMenu.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
//            @Override
//            public void onMiniFabSelected(MenuItem fabItem) {
//
//                switch (fabItem.getItemId()) {
//
//
//                    case R.id.fab_add_event:
//
//                        addEvent();
//
//                        break;
//
////                    case R.id.fab_add_paid_event:
////                        addEvent();
////                        break;
//
//                    case R.id.fab_add_announcement:
//
//                        addAnnouncement();
//
//                        break;
//
//
//                }
//
//
//            }
//        });


        try {

            if (Preferences.INSTANCE.getUserType().toLowerCase().equalsIgnoreCase("coach".toLowerCase())) {

                addParentProfile();

                floatingActionButton.setVisibility(View.VISIBLE);
            } else {
                floatingActionButton.setVisibility(View.GONE);
            }

        } catch (NullPointerException e) {

        }

        loadData();


        int color = ContextCompat.getColor(getContext(), R.color.bg_color);

//        makeRoundCorner(color, 20, testView, 0, color);


//        AnimUtils.setSlideDownAnimation(topLayout);
//        AnimUtils.setSlideUpAnimation(bottomLayout);


//        ViewFilter.getInstance(getContext())
//                //Use blur effect or implement your custom IRenderer
//                .setRenderer(new BlurRenderer(10))
//                .applyFilterOnView(myView, rootview);


//        rectangle_layout_overlay.resetShapeSize(30);


//        Blurry.with(getContext())
//                .radius(10)
//                .sampling(8)
//
//                .color(Color.argb(66, 22, 16, 75))
//                .async()
//                .animate(500)
//                .onto(myrootView);


//        Blurry.with(getContext()).radius(20).sampling(8) .color(Color.argb(66, 255, 255, 0)).async().capture(mBlurImage).into(mBlurImage);


//        myView.setBlurredView(rootview);

//
//        if (myView.getWidth() > 0) {
//
////            Bitmap mBitmap1 = BlurBuilder.getScreenshot(myrootView);//loadBitmap(myrootView, myView);
////            setBackgroundOnView(myView, mBitmap1);
////
////            blur(BlurBuilder.getScreenshot(myrView), myView);
//////            Bitmap image = BlurBuilder.blur(myrootView);
//////            myView.setBackgroundDrawable(new BitmapDrawable(getResources(), image));
//        } else {
//
//            myView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//
//                    Bitmap mBitmap1 =BlurBuilder.getScreenshot(myrootView); ;//loadBitmap(rootview, myView);
//                    setBackgroundOnView(myView, mBitmap1);
////                    blur(BlurBuilder.getScreenshot(myrView), myView);
////
//////                    Bitmap image = BlurBuilder.blur(myrootView);
//////                    myView.setBackgroundDrawable(new BitmapDrawable(getResources(), image));
//                }
//            });
//
//        }

//        Bitmap image = BlurBuilder.blur(rootview);
//        myView.setBackgroundDrawable(new BitmapDrawable(getResources(), image));

        setFabCircleView();
    }


    private void setBackgroundOnView(View view, Bitmap bitmap) {
        Drawable d;
        if (bitmap != null) {
            d = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            ((RoundedBitmapDrawable) d).setCornerRadius(getResources().getDimensionPixelOffset(R.dimen.rounded_corner));

        } else {
            d = ContextCompat.getDrawable(getContext(), R.drawable.round_test);
        }
        view.setBackground(d);
    }

    private Bitmap loadBitmap(View backgroundView, View targetView) {
        Rect backgroundBounds = new Rect();
        backgroundView.getHitRect(backgroundBounds);
        if (!targetView.getLocalVisibleRect(backgroundBounds)) {
            // NONE of the imageView is within the visible window
            return null;
        }

        Bitmap blurredBitmap = captureView(backgroundView);
        //capture only the area covered by our target view
        int[] loc = new int[2];
        int[] bgLoc = new int[2];
        backgroundView.getLocationInWindow(bgLoc);
        targetView.getLocationInWindow(loc);
        int height = targetView.getHeight();
        int y = loc[1];
        if (bgLoc[1] >= loc[1]) {
            //view is going off the screen at the top
            height -= (bgLoc[1] - loc[1]);
            if (y < 0)
                y = 0;
        }
        if (y + height > blurredBitmap.getHeight()) {
            height = blurredBitmap.getHeight() - y;
            Log.d("TAG", "Height = " + height);
            if (height <= 0) {
                //below the screen
                return null;
            }
        }
        Matrix matrix = new Matrix();
        //half the size of the cropped bitmap
        //to increase performance, it will also
        //increase the blur effect.
        matrix.setScale(0.5f, 0.5f);
        Bitmap bitmap = Bitmap.createBitmap(blurredBitmap,
                (int) targetView.getX(),
                y,
                targetView.getMeasuredWidth(),
                height,
                matrix,
                true);

        return bitmap;
        //If handling rounded corners yourself.
        //Create rounded corners on the Bitmap
        //keep in mind that our bitmap is half
        //the size of the original view, setting
        //it as the background will stretch it out
        //so you will need to use a smaller value
        //for the rounded corners than you would normally
        //to achieve the correct look.
        //ImageHelper.roundCorners(
        //bitmap,
        //getResources().getDimensionPixelOffset(R.dimen.rounded_corner),
        //false);
    }

    public Bitmap captureView(View view) {
        if (mBlurredBitmap != null) {
            return mBlurredBitmap;
        }
        //Find the view we are after
        //Create a Bitmap with the same dimensions
        mBlurredBitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_4444); //reduce quality and remove opacity
        //Draw the view inside the Bitmap
        Canvas canvas = new Canvas(mBlurredBitmap);
        view.draw(canvas);

        //blur it
        ImageHelper.blurBitmapWithRenderscript(rs, mBlurredBitmap);

        //Make it frosty
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF, 0x00222222); // lighten
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.setColorFilter(filter);
        canvas.drawBitmap(mBlurredBitmap, 0, 0, paint);

        return mBlurredBitmap;
    }


    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();

        float radius = 20;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()),
                (int) (view.getMeasuredHeight()), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);

        canvas.translate(-view.getLeft(), -view.getTop());
        canvas.drawBitmap(bkg, 0, 0, null);

        RenderScript rs = RenderScript.create(getActivity());

        Allocation overlayAlloc = Allocation.createFromBitmap(
                rs, overlay);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                rs, overlayAlloc.getElement());

        blur.setInput(overlayAlloc);

        blur.setRadius(radius);

        blur.forEach(overlayAlloc);

        overlayAlloc.copyTo(overlay);

        view.setBackground(new BitmapDrawable(
                getResources(), overlay));

        rs.destroy();

//        myrView.setVisibility(View.GONE);
//        statusText.setText(System.currentTimeMillis() - startMs + "ms");
    }

    public static void makeRoundCorner(int bgcolor, int radius, View v, int strokeWidth, int strokeColor) {

        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(bgcolor);
        gdDefault.setCornerRadius(radius);
        gdDefault.setStroke(strokeWidth, strokeColor);
        v.setBackgroundDrawable(gdDefault);

    }


    private void loadData() {


        if (ConnectivityHelper.isConnectedToNetwork(getContext())) {

            getAnnouncement();

            getKids();


            showViewContent();

        } else {

            onNetworkError(false);
        }

    }

    @Override
    protected void onRetryOrCallApi() {


//        loadData();

        if (getActivity() instanceof DashBoardActivity) {

            ((DashBoardActivity) getActivity()).addFragment();
        }

//        loadData();
//
//
//        if (getFragmentManager() != null) {
//            HomeEventListFragment fragment = (HomeEventListFragment) getChildFragmentManager().findFragmentById(R.id.fragment);
//
//            if (fragment != null) {
//                fragment.callEventListApi();
//            }
//        }
    }

    @Override
    public void onManuallyParseError(Response<?> response, boolean isToastMsg) {

    }

    private void getKids() {

        RetrofitAdapter.getNetworkApiServiceClient().fetchKids(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<KidsAndCoaches>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(KidsAndCoaches value) {

                        try {
                            if (value != null && value.getData() != null) {

                                List<Kid> kid = value.getData().getKids();

                                List<ParentChild> parentChildList = new ArrayList<>();
//                                showToast("item "+kid.size());
                                if (kid != null && !kid.isEmpty()) {
//                                    parentChildPagerAdapter.clearAll();

                                    for (Kid kid1 : kid
                                            ) {


//                                        Log.e(TAG, "onNext kids: " + new Gson().toJson(kid1));

                                        Coach coach = kid1.getCoach();

                                        ParentChild parentChild = new ParentChild();
                                        parentChild.setType(ParentChild.TYPE.CHILD);
                                        parentChild.setWhoIs("Kid");
                                        parentChild.setId(kid1.getId());
                                        parentChild.setImgUrl(kid1.getAvatarImage());
                                        parentChild.setName(kid1.getFirstname() + " " + kid1.getLastname());
                                        parentChild.setCoachName(coach.getFirstname() + " " + coach.getLastname());
                                        parentChild.setCoachImage(coach.getAvatarImage());
                                        parentChild.setTeamName(kid1.getTeamName());
                                        parentChildList.add(parentChild);
//                                        parentChildPagerAdapter.add(parentChild);

                                    }

                                    parentChildPagerAdapter = new ParentChildPagerAdapter(getContext(), parentChildList);

                                    profileViewPager.setAdapter(parentChildPagerAdapter);

                                    profileViewPager.setOffscreenPageLimit(parentChildList.size());
                                    parentChildPagerAdapter.setOnItemClicklistener(HomeFragment.this);


//                                    profileViewPager.setOffscreenPageLimit(parentChildPagerAdapter.getCount());


                                }

                            }

                        } catch (Exception e) {

                            Log.e(TAG, "Exception: " + e.getMessage());
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.e(TAG, "onNextkids: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addEvent() {

//        if (mFloatingMenu.isOptionsMenuOpened()) {
//            mFloatingMenu.closeOptionsMenu();
//        }
//        AddEventFragment addEventFragment = new AddEventFragment();
////        addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
//        addEventFragment.show(getChildFragmentManager(), "AddFragment");


        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());


        startActivity(new Intent(getContext(), AddEventActivity.class), options.toBundle());

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addAnnouncement() {
//        if (mFloatingMenu.isOptionsMenuOpened()) {
//            mFloatingMenu.closeOptionsMenu();
//        }

        PostAnnouncementFragment addEventFragment = new PostAnnouncementFragment();
        addEventFragment.setSharedElementEnterTransition(new ChangeBounds());
        addEventFragment.setEnterTransition(new Slide());
        addEventFragment.setExitTransition(new Slide());
        addEventFragment.setSharedElementReturnTransition(new ChangeBounds());

//        addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        addEventFragment.show(getChildFragmentManager(), "PostAnnouncement");

    }


    private void getAnnouncement() {

        RetrofitAdapter.getNetworkApiServiceClient().fetchAnnouncements(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AnnouncementApi>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AnnouncementApi value) {
                        setAnnouncementData(value.data);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void setAnnouncementData(List<AnnouncementApi.Datum> upcommingEventList) {


//        if (upcommingEventList != null && !upcommingEventList.isEmpty()) {
//
//            if (profileViewPager1 != null)
//                announcement_layout.setVisibility(View.VISIBLE);
//
//            if (!upcommingEventList.isEmpty()) {
//                mCardAdapter.updateList(HomeEventListFragment.getOnlyFiveInTheList(upcommingEventList));
//
//            } else {
//                if (profileViewPager1 != null) {
//                    announcement_layout.setVisibility(View.GONE);
//
//
//                }
//            }
//
//
//        }

    }

    private void addParentProfile() {

        ParentChild parentChild = new ParentChild();
        parentChild.setName(Preferences.INSTANCE.getUserName());
        parentChild.setImgUrl(Preferences.INSTANCE.getLogo());
        parentChild.setWhoIs(getString(Preferences.INSTANCE.getUserType()));
        parentChild.setType(ParentChild.TYPE.PARENT);
        parentChildPagerAdapter.add(parentChild);

//        parentChild = new ParentChild();
//        parentChild.setName(Preferences.INSTANCE.getUserName());
//        parentChild.setImgUrl(Preferences.INSTANCE.getLogo());
//        parentChild.setWhoIs("Child");
//        parentChild.setType(ParentChild.TYPE.CHILD);
//
//        parentChildPagerAdapter.add(parentChild);
//
//        parentChild = new ParentChild();
//        parentChild.setName(Preferences.INSTANCE.getUserName());
//        parentChild.setImgUrl(Preferences.INSTANCE.getLogo());
//        parentChild.setWhoIs("Coach");
//        parentChild.setType(ParentChild.TYPE.COACH);
//
//        parentChildPagerAdapter.add(parentChild);


    }


    @OnClick({R.id.noti_img, R.id.textView2})
    public void onClick(View view) {


        startActivity(new Intent(getContext(), NotificationActivity.class));


    }


    public void setNotificationCount() {

//        Log.e(TAG, "setNotificationCount: ");
//        notificationCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        final int count = Preferences.INSTANCE.getNotCount();
        notificationCount.setVisibility(View.VISIBLE);
        notificationCount.setText(String.format(Locale.ENGLISH, "%d", count));

        notificationCount.startAnimation(blink);

        blink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                notificationCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }


//    @OnClick({R.id.imageView3, R.id.back})
//    public void onViewClicked() {
////        if (getActivity() != null && getActivity() instanceof DashBoardActivity)
////            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(R.id.navigation_settings);
//
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        setNotificationCount();
//        getKids();

    }

    @Override
    public void OnItemClick(View sharedView, ParentChild parentChild, int position) {


//        MyNotificationManager.getInstance(getContext())
//                .displayNotification("Hello ", "Test", "event", "487");


        if (parentChild.getType() == ParentChild.TYPE.PARENT) {


            Intent i = new Intent(getContext(), ProfileDetailsActivity.class);
            i.putExtra(ProfileDetailsActivity.EXTRA_LOGO, parentChild.getImgUrl());

//            View sharedView = blueIconImageView;
            String transitionName = getString(R.string.transition_image);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());//ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedView, transitionName);

                startActivity(i, transitionActivityOptions.toBundle());
            } else {

                startActivity(i);

            }

//            startActivity(new Intent(getContext(), ProfileDetailsActivity.class));

        } else if (parentChild.getType() == ParentChild.TYPE.CHILD) {


            Intent i = MaterialProfileActivity.getInstance(getContext(), parentChild.getId());
            i.putExtra(ProfileDetailsActivity.EXTRA_LOGO, parentChild.getImgUrl());
//            View sharedView = blueIconImageView;
            String transitionName = getString(R.string.transition_image);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());//ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedView, transitionName);

                startActivityForResult(i, REQUEST_CODE, transitionActivityOptions.toBundle());
            } else {

                startActivityForResult(i, REQUEST_CODE);

            }


//            startActivity(KidsProfile.getInstance(getContext(), parentChild.getId()));
//            startActivity(MaterialProfileActivity.getInstance(getContext(), parentChild.getId()));


//            startActivity(new Intent(getContext(), KidsProfile.class));

        } else if (parentChild.getType() == ParentChild.TYPE.COACH) {

            CoachProfileFragment coachProfileFragment = new CoachProfileFragment();

            coachProfileFragment.show(getFragmentManager(), "Coach Profile");

        } else {

            Log.e(TAG, "OnItemClick: not UserType");
        }
    }

    @Override
    public void OnItemClick(int pos) {

        if (pos == mCardAdapter.getSize() - 1) {

            moveToAnnouncementEventActivity();

        } else {

            AnnouncementApi.Datum datum = mCardAdapter.getItem(pos);

            AnnouncementDialogFragment.getInstance(datum, false).show(getChildFragmentManager(), "Announcement");

        }
    }

    @Override
    public void OnItemDeleteClick(int pos) {

    }

    @Override
    public void OnItemMoreClick(int pos) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void moveToAnnouncementEventActivity() {


        if (getActivity() instanceof DashBoardActivity) {

            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(new MoreAnnouncementFragment());

        }

    }

    @Override
    public void networkConnected() {

        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.e(TAG, "onActivityResult: " + requestCode + " " + resultCode + " " );

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            showToast("Called");

//            Log.e(TAG, "onActivityResult: called ");

            getKids();

        }

    }


    @OnClick(R.id.btn_announcement)
    public void onViewClicked() {


        if (getActivity() instanceof DashBoardActivity) {


            ((DashBoardActivity) getActivity()).showAnnouncement();

        }


    }


    private static class OnPageChangelistener extends OnPageChangeListener {


        private WeakReference<PageIndicatorView> pageIndicator;

        private OnPageChangelistener(PageIndicatorView pageIndicator) {
            this.pageIndicator = new WeakReference<>(pageIndicator);
        }

        @Override
        public void onPageSelected(int i) {

            pageIndicator.get().setSelection(i);

        }
    }


    private void setFabCircleView() {


        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(getActivity());
// repeat many times:
        ImageView itemIcon = new ImageView(getActivity());
        itemIcon.setBackgroundColor(Color.TRANSPARENT);
        itemIcon.setBackgroundResource(R.drawable.ic_iconfinder_announcement_2742787);
        itemIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.SRC_IN);

        ImageView itemIcon1 = new ImageView(getActivity());
        itemIcon1.setBackgroundColor(Color.TRANSPARENT);
        itemIcon1.setBackgroundResource(R.drawable.ic_small_calendar1);
//        itemIcon1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        itemIcon1.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.SRC_IN);


        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();
        SubActionButton button2 = itemBuilder.setContentView(itemIcon1).build();
        button1.setBackgroundColor(Color.TRANSPARENT);
        button2.setBackgroundColor(Color.TRANSPARENT);


        actionMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(button1)
                .addSubActionView(button2)
                .attachTo(floatingActionButton)
                .setRadius(180)
                .build();

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addEvent();
//                Toast.makeText(v.getContext(), "Button2", Toast.LENGTH_SHORT).show();
                actionMenu.close(true);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addAnnouncement();

//                Toast.makeText(v.getContext(), "Button2", Toast.LENGTH_SHORT).show();
                actionMenu.close(true);

            }
        });


        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                fabCircleView.setVisibility(View.VISIBLE);
                fabFullView.setVisibility(View.VISIBLE);
                fabCircleView.animate().alpha(0).start();
//                fabCircleView.setAlpha(0);
                floatingActionButton.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(floatingActionButton, pvhR);
                animation.start();


                PropertyValuesHolder pvhR1 = PropertyValuesHolder.ofFloat(View.ALPHA, .8f);
                ObjectAnimator animation1 = ObjectAnimator.ofPropertyValuesHolder(fabCircleView, pvhR1);
                animation1.start();


//                int x = fabCircleView.getRight();
//                int y = fabCircleView.getBottom();
//
//                int startRadius = 0;
//                int endRadius = (int) Math.hypot(fabCircleView.getWidth(), fabCircleView.getHeight());
//
//                Animator anim = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    anim = ViewAnimationUtils.createCircularReveal(fabCircleView, x, y, startRadius, endRadius);
//                    anim.start();
//                }

//                fabCircleView.setVisibility(View.VISIBLE);


//                animation1.addListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                });

            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
                fabFullView.setVisibility(View.GONE);
                floatingActionButton.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(floatingActionButton, pvhR);
                animation.start();

                PropertyValuesHolder pvhR1 = PropertyValuesHolder.ofFloat(View.ALPHA, 0);
                ObjectAnimator animation1 = ObjectAnimator.ofPropertyValuesHolder(fabCircleView, pvhR1);
                animation1.start();
                animation1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fabCircleView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        });


        fabFullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (actionMenu.isOpen()) actionMenu.close(true);

            }
        });

    }


}
