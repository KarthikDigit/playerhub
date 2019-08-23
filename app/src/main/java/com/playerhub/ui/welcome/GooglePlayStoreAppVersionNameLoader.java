package com.playerhub.ui.welcome;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GooglePlayStoreAppVersionNameLoader extends AsyncTask<String, Void, String> {

    private static final String TAG = "GooglePlayStoreAppVersi";
    private String newVersion;

    protected String doInBackground(String... urls) {

        Log.e(TAG, "doInBackground: ");

//        try {
//            return
//                    Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.aczone" + "&hl=en")
//                            .timeout(10000)
//                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                            .referrer("http://www.google.com")
//                            .get()
//                            .select("div[itemprop=softwareVersion]")
//                            .first()
//                            .ownText();
//
//        } catch (Exception e) {
//            return "";
//        }

        String newVersion = "";

        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.aczone" + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            if (document != null) {
                Elements element = document.getElementsContainingOwnText("Current Version");
                for (Element ele : element) {
                    if (ele.siblingElements() != null) {
                        Elements sibElemets = ele.siblingElements();
                        for (Element sibElemet : sibElemets) {
                            newVersion = sibElemet.text();
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: " + e.getMessage());
        }
        return newVersion;
    }

    protected void onPostExecute(String string) {
        newVersion = string;
        Log.e(TAG, "new Version " + newVersion);
    }
}