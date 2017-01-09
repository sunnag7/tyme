package com.san.tyme.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.san.tyme.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    WebView browser;
    String subdomain = "", signup = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            subdomain = extras.getString("subdomain");
            signup = extras.getString("isSign", null);
            //The key argument here must match that used in the other activity
        }

        browser = (WebView) findViewById(R.id.webview);
        browser.setWebViewClient(webViewClient);
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if (signup.equals("0"))
            browser.loadUrl("http://"+subdomain.trim()+".tyme.co/forgotpassword.php");
        else {
            browser.loadUrl("http://tyme.co/signupnew.php");
        }

    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            String urla = browser.getUrl();
            if (url.contains("http://"+subdomain.trim()+".tyme.co/Home.php")) {
                Intent i=new Intent(ForgotPasswordActivity.this, DomainActivity.class);
                startActivity(i);
                finish();
            }
            else {

            }
            // Loading started for URL
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return false;
            // Redirecting to URL
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // Loading finished for URL
        }
    };
}
