package com.playerhub.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.FirebaseDatabase;
import com.playerhub.R;
import com.playerhub.preference.Preferences;
import com.squareup.leakcanary.LeakCanary;

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
        Places.initialize(getApplicationContext(), "AIzaSyDwAJUDnylr3p7h3aFpW2tiNfqC0j3smdY");

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
