package com.playerhub.ui.welcome;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.login.LoginActivity;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private static final String TAG = "WelcomeActivity";
    private String currentVersion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        if (!Preferences.INSTANCE.getIsFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);


//        try {
//
//            currentVersion = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//
//            Log.e("Current Version", "::" + currentVersion);
//
//            new GetVersionCode(currentVersion, this).execute();
//
//        } catch (PackageManager.NameNotFoundException e) {
////            e.printStackTrace();
//
//            Log.e(TAG, "onCreate: " + e.getMessage());
//        }

//        new GooglePlayStoreAppVersionNameLoader().execute();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });


    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        Preferences.INSTANCE.putIsFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        finish();
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    //iyapan pelmarahalli


    //9600924431

    //mamasamu new number

    //9443962593


    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }


//    private static class GetVersionCode extends AsyncTask<Void, String, String> {
//
//        private String currentVersion;
//        private WeakReference<Context> mContext;
//
//        private GetVersionCode(String currentVersion, Context mContext) {
//            this.currentVersion = currentVersion;
//            this.mContext = new WeakReference<>(mContext);
//        }
//
//        @Override
//        protected String doInBackground(Void... voids) {
//
//            String newVersion = null;
//
//            try {
//                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=com.aczone&hl=en")
////                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en")
//                        .timeout(30000)
//                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                        .referrer("http://www.google.com")
//                        .get();
//                if (document != null) {
//                    Elements element = document.getElementsContainingOwnText("Current Version");
//                    for (Element ele : element) {
//                        if (ele.siblingElements() != null) {
//                            Elements sibElemets = ele.siblingElements();
//                            for (Element sibElemet : sibElemets) {
//                                newVersion = sibElemet.text();
//                            }
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "doInBackground: " + e.getMessage());
//            }
//            return newVersion;
//
//        }
//
//
//        @Override
//
//        protected void onPostExecute(String onlineVersion) {
//
//            super.onPostExecute(onlineVersion);
//
//            if (onlineVersion != null && !onlineVersion.isEmpty()) {
//
//                if (onlineVersion.equals(currentVersion)) {
//
//                } else {
//                    AlertDialog alertDialog = new AlertDialog.Builder(mContext.get()).create();
//                    alertDialog.setTitle("Update");
//                    alertDialog.setIcon(R.mipmap.ic_launcher);
//                    alertDialog.setMessage("New Update is available");
//
//                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Update", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            try {
//                                mContext.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.aczone")));
////                                mContext.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.get().getPackageName())));
//                            } catch (android.content.ActivityNotFoundException anfe) {
//                                mContext.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.aczone")));
////                                mContext.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
//                            }
//                        }
//                    });
//
//                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//
//                    alertDialog.show();
//                }
//
//            }
//
//            Log.e(TAG, "Current version " + currentVersion + "playstore version " + onlineVersion);
//
//        }
//    }
}
