package com.san.tyme.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.san.tyme.Database.Database;
import com.san.tyme.R;
import com.san.tyme.TymeActivity;
import com.san.tyme.model.Project;
import com.san.tyme.model.Task;
import com.san.tyme.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Button loginButton;
    EditText editEmail, editPass;
    String subdomain = "", email = "", password = "";
    OkHttpClient client = new OkHttpClient();
    ProgressDialog mProgressDialog;
    public static final String PREFS_NAME = "TymePref";
    TextView forgtPass ;
    RelativeLayout cardRel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        intViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            subdomain = extras.getString("subdomain");
            //The key argument here must match that used in the other activity
        }
        setAnimation(cardRel);
    }

    private void intViews() {
        loginButton =(Button) findViewById(R.id.button);
        editEmail = (EditText) findViewById(R.id.editText);
        editPass = (EditText) findViewById(R.id.editText2);
        forgtPass = (TextView) findViewById(R.id.textView4);
        cardRel = (RelativeLayout) findViewById(R.id.cardRel);

        loginButton.setOnClickListener(this);
        forgtPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button){

            email = editEmail.getText().toString();
            password = MD5_Hash(editPass.getText().toString());

            if(isNetworkConnected()&& email.length()>0 && editPass.getText().toString().length()>0){
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Please wait...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                Log.d("req login"," "+email+" "+password);
                AsyncTaskRunner runner = new AsyncTaskRunner();
                //String sleepTime = time.getText().toString();
                runner.execute(subdomain,email,password);
            }
            else if (email.length()==0 || editPass.getText().toString().length()==0){
               /* new AlertDialog.Builder(this)
                        .setTitle("Invalid Details")
                        *//*.setMessage("It looks like your internet connection is off. Please turn it " +
                                "on and try again")*//*
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();*/
                if(email.length()==0 ){
                    editEmail.setError("Email Id required");
                }

                if(editPass.getText().toString().length()==0){
                    editPass.setError("Password required");
                }
            }
            else if(!isNetworkConnected()){
                new AlertDialog.Builder(this)
                        .setTitle("No Internet Connection")
                        .setMessage("It looks like your internet connection is off. Please turn it " +
                                "on and try again")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        }
        else
            if (v.getId()==R.id.textView4){
                Intent i=new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                i.putExtra("subdomain",subdomain);
                i.putExtra("isSign","0");
                startActivity(i);
                finish();
            }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        private String status;
        @Override
        protected String doInBackground(String... params) {
            try {
                status = post(Constants.mainUrl+Constants.validate,params[0],params[1],params[2]);
            } catch (IOException e) {
                mProgressDialog.dismiss();
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            mProgressDialog.dismiss();
            Log.d("Response login",""+result);

            try {
                JSONObject obj = new JSONObject(result);
                if(obj.getString("status").equals("success")){
                    // JSONArray jsonarray = obj.getJSONArray();
                    HashMap<String,String> hm = new HashMap<String, String>();
                    JSONArray jsonarray = obj.getJSONArray("user");
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        hm.put("id",""+jsonobject.getString("id"));
                        hm.put("email",""+jsonobject.getString("email"));
                        hm.put("password",""+jsonobject.getString("password"));
                        hm.put("type",""+jsonobject.getString("type"));
                        hm.put("Name",""+jsonobject.getString("Name"));
                        hm.put("slacktoken",""+jsonobject.getString("slacktoken"));
                        hm.put("trellokey",""+jsonobject.getString("trellokey"));
                        hm.put("image",""+jsonobject.getString("image"));
                        hm.put("archived",""+jsonobject.getString("archived"));
                    }
                    showToast();
                    setPreference(LoginActivity.this,hm);

                    ArrayList<Project> mProjArr = new ArrayList<Project>();
                    JSONArray mJsonProjArr = obj.getJSONArray("projects");
                    for (int i = 0; i < mJsonProjArr.length(); i++) {
                        JSONObject jsonobject = mJsonProjArr.getJSONObject(i);
                        Project aProject = new Project();
                        aProject.setId(jsonobject.getString("id"));
                        aProject.setProj_name(jsonobject.getString("name"));
                        aProject.setClient_name(jsonobject.getString("client"));
                        aProject.setProj_code(jsonobject.getString("projectcode"));
                        aProject.setStart_date(jsonobject.getString("startdate"));
                        aProject.setEnd_date(jsonobject.getString("enddate"));
                        aProject.setNotes(jsonobject.getString("notes"));
                        aProject.setBudget_value(jsonobject.getString("budgetvalue"));
                        aProject.setArchived(jsonobject.getString("archived"));
                        aProject.setProject_billable(jsonobject.getString("projectbillable"));
                        aProject.setSlack_id(jsonobject.getString("slackid"));
                        aProject.setTrelloboard_id(jsonobject.getString("trelloboardid"));
                        aProject.setTaskList(jsonobject.getString("tasks_list"));

                        mProjArr.add(aProject);
                    }

                    ArrayList<Task> mTaskArr = new ArrayList<Task>();
                    JSONArray mJsonTaskArr = obj.getJSONArray("tasks");
                    for (int i = 0; i < mJsonTaskArr.length(); i++) {
                        JSONObject jsonobjectTask = mJsonTaskArr.getJSONObject(i);
                        Task aTask = new Task();
                        aTask.setTask_id(jsonobjectTask.getString("id"));
                        aTask.setTask_name(jsonobjectTask.getString("name"));
                        aTask.setIsBillable(Integer.parseInt(jsonobjectTask.getString("billable")));

                        mTaskArr.add(aTask);
                    }

                    Database mDb = new Database(LoginActivity.this);
                    if (mDb.getCount()==0) {
                        mDb.createProject(mProjArr);
                        mDb.createTask(mTaskArr);
                    }

                    Intent i=new Intent(LoginActivity.this, TymeActivity.class);
                    i.putExtra("subdomain",subdomain);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // i.putExtra("Two",two);
                    startActivity(i);
                    finish();
                }
                else {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Validation Failed")
                            .setMessage("Enter valid credentials and try again!")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setIcon(R.drawable.ic_error_outline_black_24dp).show();

                    editEmail.setError("Enter valid Email ID");

                    editPass.setError("Enter Valid Password");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
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


    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    String post(String url, String subdomain, String email, String password) throws IOException {
        // RequestBody body = RequestBody.create(JSON, json);
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("username", email)
                .add("password", password)
                .add("subdomain", subdomain);
        RequestBody formBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public static String MD5_Hash(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }

    static public boolean setPreference(Context c, HashMap<String, String> hm) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        Iterator it = hm.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            editor.putString(""+pair.getKey().toString() , ""+pair.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }

        return editor.commit();
    }

    public void showToast(){
        LayoutInflater li = getLayoutInflater();
        //Getting the View object as defined in the customtoast.xml file
        View layout = li.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_layout));

        //Creating the Toast object
        TextView mTextAlert = (TextView) layout.findViewById(R.id.textView2);
        mTextAlert.setText("Login Success.");
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 0);
        toast.setView(layout);//setting the view of custom toast layout
        toast.show();
    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.slide_in);
        viewToAnimate.startAnimation(animation);
    }
}
