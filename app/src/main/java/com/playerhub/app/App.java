package com.playerhub.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.FirebaseDatabase;
import com.playerhub.R;
import com.playerhub.preference.Preferences;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import io.fabric.sdk.android.Fabric;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.place_apikey));

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);


        Fabric.with(this, new Crashlytics());

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
        // Normal app init code...

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        Preferences.INSTANCE.createPreferences(this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.setSingletonInstance(getCustomPicasso());
    }

    private Picasso getCustomPicasso() {
        Picasso.Builder builder = new Picasso.Builder(this);
        //set 12% of available app memory for image cache
        builder.memoryCache(new LruCache(getBytesForMemCache(12)));
        //set request transformer
        Picasso.RequestTransformer requestTransformer = new Picasso.RequestTransformer() {
            @Override
            public Request transformRequest(Request request) {
                Log.d("image request", request.toString());
                return request;
            }
        };
        builder.requestTransformer(requestTransformer);

        return builder.build();
    }

    //returns the given percentage of available memory in bytes
    private int getBytesForMemCache(int percent) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)
                getSystemService(ACTIVITY_SERVICE);
        if (activityManager != null) {
            activityManager.getMemoryInfo(mi);
        }

        double availableMemory = mi.availMem;

        return (int) (percent * availableMemory / 100);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


//    //    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(ViewPumpContextWrapper.wrap(base));
//    }
}
