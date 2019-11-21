package com.playerhub.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.playerhub.R;
import com.playerhub.notification.Constants;
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

import static com.google.android.exoplayer2.ExoPlayerLibraryInfo.TAG;

public class App extends Application {

    FirebaseAuth mAuth;// = FirebaseAuth.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        signInFirebase();
        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

//// Setting timeout globally for the download network requests:
//        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
//                .setReadTimeout(30_000)
//                .setConnectTimeout(30_000)
//                .build();
//        PRDownloader.initialize(getApplicationContext(), config);

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
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
//        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).getDatabase().setPersistenceEnabled(true);
//        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).keepSynced(true);


        Picasso.setSingletonInstance(getCustomPicasso());
    }


    private void signInFirebase() {


        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Log.e(TAG, "onSuccess: " + authResult.getAdditionalUserInfo().getUsername());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "signInAnonymously:FAILURE " + exception.getMessage());
            }
        });
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
