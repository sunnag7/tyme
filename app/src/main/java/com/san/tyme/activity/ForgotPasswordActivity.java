package com.san.tyme.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.san.tyme.R;

public class ForgotPasswordActivity extends AppCompatActivity {

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

        WebView browser = (WebView) findViewById(R.id.webview);

        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if (signup.equals("0"))
            browser.loadUrl("http://"+subdomain.trim()+".tyme.co/forgotpassword.php");
        else
        {
            browser.loadUrl("http://tyme.co/signupnew.php");
        }

       //
    }
}
