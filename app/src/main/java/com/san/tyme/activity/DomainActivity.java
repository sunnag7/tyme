package com.san.tyme.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.san.tyme.R;
import com.san.tyme.TymeActivity;
import com.san.tyme.utils.Constants;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DomainActivity extends AppCompatActivity implements View.OnClickListener{
    Button domainCheck;
    String dom_name;
    EditText domainName;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    ProgressDialog mProgressDialog;
    LinearLayout linText;
    public static final String PREFS_NAME = "TymePref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("id", null);
        if (restoredText != null) {
            //dom_name = prefs.getString("subdomain", null);
            Intent i=new Intent(DomainActivity.this, TymeActivity.class);
            startActivity(i);
            finish();
        }
        initViews();
        setAnimation(linText);
    }

    private void initViews() {
        domainCheck = (Button) findViewById(R.id.button4);
        domainName = (EditText) findViewById(R.id.editText4);
        linText = (LinearLayout) findViewById(R.id.linText);
        domainCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.button4){
            //validate whether domain exists
            if (isNetworkConnected() && domainName.getText().toString().length()>0) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Please wait...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                dom_name = domainName.getText().toString().trim();

                AsyncTaskRunner runner = new AsyncTaskRunner();
                //String sleepTime = time.getText().toString();
                runner.execute();

            } else if(! isNetworkConnected() ) {
                new AlertDialog.Builder(this)
                        .setTitle("No Internet Connection")
                        .setMessage("It looks like your internet connection is off. Please turn it " +
                                "on and try again")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setCancelable(false).setIcon(R.drawable.tyme_).show();
            }
            else if(domainName.getText().toString().length()==0){
                domainName.setError("Domain name cannot be empty");
            }
        }
    }

    String post(String url, String subdomain) throws IOException {
       // RequestBody body = RequestBody.create(JSON, json);
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("subdomain", subdomain);
        RequestBody formBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();

        //mProgressDialog.dismiss();
        //dynamically add more parameter like this:
        //formBuilder.add("phone", "000000");

        if (response.isSuccessful())
            return response.body().string();
        else
            return null;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    static public boolean setPreference(Context c, String value, String key) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String status;
        @Override
        protected String doInBackground(String... params) {
            try {
                 status = post(Constants.mainUrl+Constants.CheckDomain,dom_name);
            } catch (IOException e) {
                if (mProgressDialog!=null)
                mProgressDialog.dismiss();
                e.printStackTrace();
            }

            return status;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            mProgressDialog.dismiss();
            if(result.equals("success")){

                Toast.makeText(DomainActivity.this,
                        "Domain verified successfully",Toast.LENGTH_LONG).show();
                setPreference(DomainActivity.this,dom_name , "subdomain");
                Intent i=new Intent(DomainActivity.this, LoginActivity.class);
                i.putExtra("subdomain",dom_name);
                // i.putExtra("Two",two);
                startActivity(i);
            }
            else{
                domainName.setError("Domain is not registered!");
                new AlertDialog.Builder(DomainActivity.this)
                        .setTitle("Invalid domain")
                        .setMessage("Please Enter valid name.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }
            //finalResult.setText(result);
        }


        @Override
        protected void onPreExecute() {
            /*progressDialog = ProgressDialog.show(MainActivity.this,
                    "ProgressDialog",
                    "Wait for "+time.getText().toString()+ " seconds");*/
        }

        @Override
        protected void onProgressUpdate(String... text) {
           // finalResult.setText(text[0]);
        }
    }

    private void setAnimation(View viewToAnimate) {
            Animation animation = AnimationUtils.loadAnimation(DomainActivity.this, R.anim.slide_in);
            viewToAnimate.startAnimation(animation);
    }
 }
