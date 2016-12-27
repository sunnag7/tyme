package com.san.tyme.utils;

/**
 * Created by Sanny on 10/18/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.san.tyme.Database.Database;
import com.san.tyme.R;
import com.san.tyme.activity.Details;
import com.san.tyme.model.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.TimerTask;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    Context context;
    View view1;
    ViewHolder viewHolder1;
    ArrayList<Timer> tArr;
    public static final String PREFS_NAME = "TymePref";
    String subdomain= "",email = "";
    //private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    String dt;
    private int lastPosition = -1, pos = 0;
    Timer aTime ;
    RecyclerViewAdapter adptr;

    public RecyclerViewAdapter(Context context1,String date, ArrayList<Timer> ar){
        context = context1;
        dt = date;
        tArr = ar;
        adptr = this;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        String restoredText = prefs.getString("id", null);
        if (restoredText != null) {
            subdomain = prefs.getString("subdomain", "");
            email = prefs.getString("email", "");//0 is the default value.
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView, textDescp, textTimer, textTask, textClient;
        ImageView plPsimg, updimg;
        FrameLayout container;

        public ViewHolder(View v){

            super(v);
            container = (FrameLayout) v.findViewById(R.id.item_layout_container);
            plPsimg = (ImageView) v.findViewById(R.id.img5);
            updimg = (ImageView) v.findViewById(R.id.imageView3);
            textDescp = (TextView)v.findViewById(R.id.textView10);
            textView = (TextView)v.findViewById(R.id.textView9);
            textTimer = (TextView)v.findViewById(R.id.textView13);
            textTask = (TextView)v.findViewById(R.id.textView12);
            textClient = (TextView)v.findViewById(R.id.textView22);
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        view1 = LayoutInflater.from(context).inflate(R.layout.time_tasker,parent,false);
        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position){
        String text ="";
        final java.util.Timer t = new java.util.Timer();
        final String strTime = tArr.get(position).getStarttime();
        final String totTime = tArr.get(position).getTotal();

        float uptime = System.currentTimeMillis()/1000.0f;
        float diffInMillies = 0f;

        if(!strTime.equals("0")){
            diffInMillies = Float.valueOf(totTime).floatValue()+( uptime - Float.valueOf(strTime).floatValue());
            final Animation startAnimation = AnimationUtils.loadAnimation(context, R.anim.blink_anim);
            holder.plPsimg.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
            holder.plPsimg.startAnimation(startAnimation);  // TODO: 10/31/2016
            holder.itemView.setSelected(true);
            pos = position;
        }
        else{
            diffInMillies = Float.valueOf(tArr.get(position).getTotal()).floatValue();
            //pos = position;
        }

        SimpleDateFormat df = new SimpleDateFormat("hh:mm"); // HH for 0-23
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        //String time = df.format(d);

        int m = Math.round(diffInMillies)/60;
        int h = m/60;

        String min = "";
        if(m>59){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, h);
            calendar.set(Calendar.MINUTE, m);
            min = new SimpleDateFormat("mm").format(calendar.getTime());
        }
        else
        if(m<10){
            min = "0"+m;
        }
        else{
            min = ""+m;
        }

        //long days = TimeUnit.MINUTES.toDays(diffInMillies);
        holder.textTimer.setText(h+":"+min);

        if(!strTime.toString().equals("0")) {

            final Handler mHandler = new Handler();
            TimerTask scanTask = new TimerTask() {
                public void run() {
                    mHandler.post(new Runnable() {
                        public void run() {
                            float uptime = System.currentTimeMillis()/1000.0f;
                            float diffInMillies;
                            diffInMillies = Float.valueOf(totTime).floatValue()+(uptime - Float.valueOf(strTime).floatValue());

                            SimpleDateFormat df = new SimpleDateFormat("hh:mm"); // HH for 0-23
                            df.setTimeZone(TimeZone.getTimeZone("GMT"));

                            int m = Math.round(diffInMillies)/60;
                            int h = m/60;
                            String min = "";

                            if(m>59){
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, h);
                                calendar.set(Calendar.MINUTE, m);
                                min = new SimpleDateFormat("mm").format(calendar.getTime());
                            }
                            else
                            if(m<10){
                                min = "0"+m;
                            }
                            else {
                                min = ""+m;
                            }

                            //long days = TimeUnit.MINUTES.toDays(diffInMillies);
                            holder.textTimer.setText(h+":"+min);
                        }
                    });
                }
            };
            t.schedule(scanTask, 0, 6000);
        }

        holder.plPsimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    t.cancel();
                    aTime = new Timer();
                    aTime = tArr.get(position);

                    if (!aTime.getStarttime().equals("0") ) {
                        Toast.makeText(context, "" + aTime.getId(), Toast.LENGTH_LONG).show();
                        AsyncTaskSubmit aSub = new AsyncTaskSubmit();
                        aSub.execute("0");

                        holder.plPsimg.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                    }
                    else if(aTime.getStarttime().equals("0") ){
                        AsyncTaskSubmit aSub = new AsyncTaskSubmit();
                        aSub.execute("1");

                        holder.plPsimg.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                        //holder.plPsimg.startAnimation(startAnimation);
                        //adptr.notifyDataSetChanged();
                        //pos = position;
                        //notifyItemChanged(pos);
                        //pos = position;
                    }
                }
                else{
                    displayDialog("No Network!","Unable to connect.");
                }
                 //notifyDataSetChanged();
            }
        });

        holder.updimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aTime = new Timer();
                aTime = tArr.get(position);
                deleteConfirmDialog(position);
            }
        });

        byte[] data = Base64.decode(""+tArr.get(position).getDescp().toString(), Base64.DEFAULT);
        try {
            text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        holder.textTask.setText(tArr.get(position).getTask());
        holder.textClient.setText("("+tArr.get(position).getClient().toString()+")");
        holder.textDescp.setText(text);
        holder.textView.setText(tArr.get(position).getProject().toString());

        setAnimation(holder.container, position);
    }

    @Override
    public int getItemCount(){
        return tArr.size();
    }

    private class AsyncTaskSubmit extends AsyncTask<String, String, String> {

        private String status;
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            try {
                    if (params[0].equals("0")){
                        status = postTask(Constants.mainUrl + Constants.updateReport,0);
                    } else if (params[0].equals("1")){
                        status = postTask(Constants.mainUrl + Constants.updateResume,1);
                    } else if (params[0].equals("2")){
                        status = postTask(Constants.mainUrl + Constants.deleteReport,2);
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return status;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            System.out.println("in Postexe: "+result);
            int k = 0,idUpd = 0;
            Database db = new Database(context);
            JSONObject mJsonObj;
            try {
                mJsonObj = new JSONObject(result);
                String stat = mJsonObj.getString("status");
                if(stat.equals("success")) {
                    int id = Integer.parseInt(mJsonObj.getString("id"));
                    String tot = ""+mJsonObj.getString("total");
                    db.updateTime(id,tot,"0");
                    k=1;idUpd = id;

                } else if (stat.equals("success_resume")){
                    int id =  Integer.parseInt(mJsonObj.getString("id"));
                    String strTime = ""+mJsonObj.getString("resume_starttime");
                    db.updateTime(id,"0",strTime);
                    k=2;idUpd = id;

                } else if (stat.equals("success_resume_pause")){
                    int id = mJsonObj.getInt("id");
                    String tot = ""+mJsonObj.getString("total");
                    db.updateTime(id,tot,"0");
                    tArr.get(pos).setStarttime("0");
                    String strTime = ""+mJsonObj.getString("resume_starttime");
                    int resID = mJsonObj.getInt("resume_id");
                    db.updateTime(resID,"0",strTime);
                    k=3;idUpd = id;

                } else if (stat.equals("delete_success")){
                    int id = mJsonObj.getInt("id");
                    db.deleteUser(""+id);
                    k=0;idUpd = id;
                    //notifyDataSetChanged();
                }
                else if (stat.equals("failure")){
                    Toast.makeText(context,"Something went wrong, please try again",Toast.LENGTH_LONG).show();
                }

                if (k!=0) {
                    Intent intent = new Intent(context, Details.class);
                    intent.putExtra("id", idUpd);
                    intent.putExtra("type", k);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    // notifyDataSetChanged();
                }
            } catch (JSONException e) {
                Toast.makeText(context,"Something went wrong, please try again",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Please wait... ");
            progressDialog.setMessage("Updating.");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
        }
    }

    OkHttpClient client = new OkHttpClient();
    String postTask(String url,int type) throws IOException {
        // RequestBody body = RequestBody.create(JSON, json);

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("subdomain", ""+subdomain);
        if(type==0) {
            float stoptime = System.currentTimeMillis()/1000.0f;
            float diffInMillies = 0f;

            diffInMillies = stoptime - Float.valueOf(aTime.getStarttime()).floatValue();
            formBuilder.add("total", "" + diffInMillies)
                    .add("starttime", "" + aTime.getStarttime())
                    .add("stoptime", "" + stoptime)
                    .add("id", "" + aTime.getId());
        }
        else if(type==1){
            float stoptime = System.currentTimeMillis()/1000.0f;
            float diffInMillies = 0f;

            diffInMillies = stoptime - Float.valueOf(aTime.getStarttime()).floatValue();
            formBuilder.add("total", "" + diffInMillies)
                    .add("starttime", "" + aTime.getStarttime())
                    .add("stoptime", "" + stoptime)
                    .add("id", "" + aTime.getId())
                    .add("date", ""+aTime.getDate())
                    .add("email", ""+email);
        }
        else if(type==2){
            formBuilder
                    .add("email", ""+email)
                    .add("id", ""+ aTime.getId());
        }

        RequestBody formBody = formBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            return response.body().string();
        }
        else{
            return "Failed";
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    public void displayDialog(String message, String title){
        new AlertDialog.Builder(context)
                .setTitle(""+title)
                .setMessage(""+message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public void deleteConfirmDialog(final int position) {
        final AlertDialog.Builder alert;
        alert = new AlertDialog.Builder(context);
        alert.setTitle("Confirm Delete.");
        alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AsyncTaskSubmit aSub = new AsyncTaskSubmit();
                aSub.execute("2");
                removeAt(position);
                notifyDataSetChanged();
            }
        });
        alert.setNegativeButton("CANCEL", null);
        alert.setCancelable(true);
        //alert.setInverseBackgroundForced(true);
        //alert.setView(dialogView);
        alert.show();
        //alert.setInverseBackgroundForced(true);
    }

    public void removeAt(int position) {
        tArr.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tArr.size());
    }

    private void setAnimation(View viewToAnimate, int position){
        if (position > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}