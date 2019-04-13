package com.playerhub.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

public class Utility {


    public static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

//    public static final List<ProductsApi.Data.Product> enquireProduct = new ArrayList<>();
//
//
//    public static void saveWhishList(ProductsApi.Data.Product product) {
//
//        enquireProduct.add(product);
//        Preferences.INSTANCE.putWhishListProducts(new Gson().toJson(enquireProduct));
//
//    }

//    public static void removeWhishList(int product) {
//
//        enquireProduct.remove(product);
//        Preferences.INSTANCE.putWhishListProducts(new Gson().toJson(enquireProduct));
//    }


    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 160);
        return noOfColumns;
    }


    public static float convertPixelsToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
//http://stackoverflow.com/questions/4605527/converting-pixels-to-dp
//The above method results accurate method compared to below methods
//http://stackoverflow.com/questions/8309354/formula-px-to-dp-dp-to-px-android


    public static int convertDpToPx(int dp) {
        return Math.round(dp * (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    public static int convertPxToDp(int px) {
        return Math.round(px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


//    public static List<Product> getAllProduct(Context context) {
//
//        final List<String> list = Arrays.asList(context.getResources().getStringArray(R.array.images));
//
//        final List<Product> productCategories = new ArrayList<>();
//
//        for (int i = 0; i < list.size(); i++) {
//
//            Product category = new Product().withImgNo(list.get(i)).withDesignNo("D.No. 2698");
//
//            productCategories.add(category);
//        }
//
//        return productCategories;
//
//    }


}
