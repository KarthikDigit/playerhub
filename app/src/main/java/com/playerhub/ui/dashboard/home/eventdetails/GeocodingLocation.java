package com.playerhub.ui.dashboard.home.eventdetails;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingLocation {

    private static final String TAG = "GeocodingLocation";

    public static void getAddressFromLocation(final String locationAddress,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                Double lat = null;
                Double lng = null;
                try {
                    List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);

                        lat = address.getLatitude();
                        lng = address.getLongitude();
//                        StringBuilder sb = new StringBuilder();
//                        sb.append(address.getLatitude()).append("\n");
//                        sb.append(address.getLongitude()).append("\n");
//                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (lat != null && lng != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
//                        result = "Address: " + locationAddress +
//                                "\n\nLatitude and Longitude :\n" + result;
                        bundle.putDouble("lat", lat);
                        bundle.putDouble("lng", lng);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        lat = 0.0;
                        lng = 0.0;
//                        result = "Address: " + locationAddress +
//                                "\n Unable to get Latitude and Longitude for this address location.";
//                        bundle.putString("address", result);
                        bundle.putDouble("lat", lat);
                        bundle.putDouble("lng", lng);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}
