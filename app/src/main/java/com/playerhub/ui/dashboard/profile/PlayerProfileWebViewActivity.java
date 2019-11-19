package com.playerhub.ui.dashboard.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.playerhub.R;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerProfileWebViewActivity extends BaseActivity {

    private static final String TAG = "ChangeCreditCardWebView";
    private static final String EXTRA_ID = "id";
    private static final String EXTRA_TITLE = "title";


    @BindView(R.id.webview)
    WebView webview;

    private boolean isFirstTime = true;


    public static void startActivity(Context context, int id, String title) {

        Intent intent = new Intent(context, PlayerProfileWebViewActivity.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_TITLE, title);

        context.startActivity(intent);

    }

    private int id;
    private String title;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_credit_card_web_view);
        ButterKnife.bind(this);
        setBackButtonEnabledAndTitleBold("Player Profile");

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID) && intent.hasExtra(EXTRA_TITLE)) {

            id = intent.getIntExtra(EXTRA_ID, 0);
            title = intent.getStringExtra(EXTRA_TITLE);
            setBackButtonEnabledAndTitleBold(title);


//        String url = "https://www.playerhub.io/card-update";
//        String url = "https://www.playerhub.io/loginas/" + Preferences.INSTANCE.getUserId();

//        String encodeURL = null;
//        try {
//            encodeURL = URLEncoder.encode(Preferences.INSTANCE.getUserId(), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

            byte[] data = new byte[0];

            try {

                data = Preferences.INSTANCE.getUserId().getBytes("UTF-8");

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            }
            String encodeURL = Base64.encodeToString(data, Base64.DEFAULT);

            String url = "https://www.playerhub.io/login/api/" + encodeURL;

//        Log.e(TAG, "onCreate: " + encodeURL + "  " + Preferences.INSTANCE.getUserId());

//
            webview.getSettings().setJavaScriptEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                webview.getSettings().setSafeBrowsingEnabled(true);
            }
            webview.getSettings().setUseWideViewPort(true);
            webview.getSettings().setDomStorageEnabled(false);
            webview.getSettings().setAppCacheEnabled(false);
            webview.setWebChromeClient(new WebChromeClient());
            webview.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

//                if (isFirstTime)
                    view.loadUrl("https://www.playerhub.io/player-profile/" + id);
//                isFirstTime = false;

                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);

                    showLoading();

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    hideLoading();
//                webview.loadUrl("https://www.playerhub.io/card-update");
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    hideLoading();
                }
            });
            MyJavaScriptInterface myJavaScriptInterface = new MyJavaScriptInterface(this);

            webview.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");
            webview.getSettings().setJavaScriptEnabled(true);

            webview.loadUrl(url);
        }

        /*
        todo this is for communication between web and Android



//        MyJavaScriptInterface myJavaScriptInterface = new MyJavaScriptInterface(this);
//
//        webview.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");
//        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new MyWebChromeClient());
//        webview.setWebViewClient(new WebViewClient());
//        webview.loadUrl(url);

        webview.loadUrl("file:///android_asset/webpage/index.html");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webview.loadUrl("javascript:callFromActivity(\"" + "This Example is working fine" + "\")");
            }
        }, 3000);

        */

    }

    public class MyJavaScriptInterface {
        Context mContext;

        MyJavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
            finish();
        }

        @JavascriptInterface
        public void openAndroidDialog() {
            AlertDialog.Builder myDialog
                    = new AlertDialog.Builder(mContext);
            myDialog.setTitle("DANGER!");
            myDialog.setMessage("You can do what you want!");
            myDialog.setPositiveButton("ON", null);
            myDialog.show();
        }

    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, final JsPromptResult result) {
            return true;
        }

    }

}
