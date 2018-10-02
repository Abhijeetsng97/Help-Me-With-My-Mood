package com.abhijeet.helpmewithmymood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InstagramLogin extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_login);

        String CLIENTID = "f128bbdd2257493995e40551efb1ec53";
        String REDIRECTURI = "http://abhijeetsng97.github.io";
        String url = "https://www.instagram.com/oauth/authorize/?client_id=" + CLIENTID +
                "&redirect_uri=" + REDIRECTURI + "&response_type=token";

        webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new InstagramBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
    }

    private class InstagramBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            if(url.substring(0,35).contains("http://abhijeetsng97.github.io")){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("token",url.substring(url.indexOf("#")+14));
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
            return true;
        }
    }
}
