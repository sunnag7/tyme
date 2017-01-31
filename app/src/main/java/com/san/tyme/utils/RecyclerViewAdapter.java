package com.san.tyme.utils;

/**
 * Created by Sanny on 10/18/2016.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.san.tyme.Database.Database;
import com.san.tyme.R;

import com.san.tyme.TymeActivity;
import com.san.tyme.activity.Details;
import com.san.tyme.model.Client;
import com.san.tyme.model.Task;
import com.san.tyme.model.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TimerTask;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private Context context;
    View view1;
    private ViewHolder viewHolder1;
    private ArrayList<Timer> tArr;
    private static final String PREFS_NAME = "TymePref";
    private String subdomain= "",email = "";
    // private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private String dt;
    private int lastPosition = -1, pos = 0;
    private Timer aTime ;
    private RecyclerViewAdapter adptr;
    TymeActivity tymeData;
    private TextView tvHour;
    public RecyclerViewAdapter(Context context1,String date, ArrayList<Timer> ar, TextView tvHours){
        context = context1;
        dt = date;
        tArr = ar;
        adptr = this;
        tvHour = tvHours;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        String restoredText = prefs.getString("id", null);
        if (restoredText != null) {
            subdomain = prefs.getString("subdomain", "");
            email = prefs.getString("email", "");//0 is the default value.
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView, textDescp, textTimer, textTask, textClient;
        //ImageView plPsimg, updimg,editImg;
        Button  updimg,editImg;
        CardView card_view;
        //FrameLayout container;
        Button plPsimg;

        ViewHolder(View v){

            super(v);
            /*container = (FrameLayout) v.findViewById(R.id.item_layout_container);
            plPsimg = (ImageView) v.findViewById(R.id.img5);
            updimg = (ImageView) v.findViewById(R.id.imageView3);
            editImg = (ImageView) v.findViewById(R.id.imageView5);
            textDescp = (TextView)v.findViewById(R.id.textView10);
            textView = (TextView)v.findViewById(R.id.textView9);
            textTimer = (TextView)v.findViewById(R.id.textView13);
            textTask = (TextView)v.findViewById(R.id.textView12);
            textClient = (TextView)v.findViewById(R.id.textView22);
            card_view = (CardView) v.findViewById(R.id.card_view);*/

           // container = (FrameLayout) v.findViewById(R.id.item_layout_container);
            plPsimg = (Button) v.findViewById(R.id.img5);
            updimg = (Button) v.findViewById(R.id.imageView3);
            editImg = (Button) v.findViewById(R.id.imageView5);
            textDescp = (TextView)v.findViewById(R.id.textView10);
            textView = (TextView)v.findViewById(R.id.textView9);
            textTimer = (TextView)v.findViewById(R.id.textView13);
            textTask = (TextView)v.findViewById(R.id.textView12);
            textClient = (TextView)v.findViewById(R.id.textView22);
            card_view = (CardView) v.findViewById(R.id.card_view);
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //view1 = LayoutInflater.from(context).inflate(R.layout.time_tasker,parent,false);
        view1 = LayoutInflater.from(context).inflate(R.layout.timer_item,parent,false);
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
            diffInMillies = Float.valueOf(totTime) +( uptime - Float.valueOf(strTime));
            final Animation startAnimation = AnimationUtils.loadAnimation(context, R.anim.blink_anim);
           // holder.plPsimg.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
            holder.plPsimg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_circle_outline_black_24dp, 0, 0, 0);

            holder.plPsimg.setText("PAUSE");
            holder.plPsimg.startAnimation(startAnimation);  // TODO: 10/31/2016
            holder.itemView.setSelected(true);
            pos = position;
        }
        else{
            diffInMillies = Float.valueOf(tArr.get(position).getTotal());
            //pos = position;
        }

        SimpleDateFormat df = new SimpleDateFormat("hh:mm"); // HH for 0-23
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        //String time = df.format(d);

        int m = Math.round(diffInMillies)/60;
        int h = m/60;

        String min;
        if(m>59){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, h);
            calendar.set(Calendar.MINUTE, m);
            min = new SimpleDateFormat("mm").format(calendar.getTime());
        }
        else if(m<10){
            min = "0"+m;
        }
        else{
            min = ""+m;
        }

        //long days = TimeUnit.MINUTES.toDays(diffInMillies);
        holder.textTimer.setText(h+":"+min);

        if(!strTime.equals("0")) {
            final Handler mHandler = new Handler();
            TimerTask scanTask = new TimerTask() {
                public void run() {
                    mHandler.post(new Runnable() {
                        public void run() {
                            float uptime = System.currentTimeMillis()/1000.0f;
                            float diffInMillies;
                            diffInMillies = Float.valueOf(totTime) +(uptime - Float.valueOf(strTime));

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
                        //Toast.makeText(context, "" + aTime.getId(), Toast.LENGTH_LONG).show();
                        AsyncTaskSubmit aSub = new AsyncTaskSubmit();
                        aSub.execute("0");

                        //   holder.plPsimg.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                        holder.plPsimg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_circle_outline_black_24dp, 0, 0, 0);
                        holder.plPsimg.setText("RESUME");
                    }
                    else if(aTime.getStarttime().equals("0") ){
                        AsyncTaskSubmit aSub = new AsyncTaskSubmit();
                        aSub.execute("1");

                       // holder.plPsimg.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                        holder.plPsimg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_circle_outline_black_24dp, 0, 0, 0);
                        holder.plPsimg.setText("PAUSE");
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

        byte[] data = Base64.decode(""+ tArr.get(position).getDescp(), Base64.DEFAULT);
        try {
            text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        holder.textTask.setText(tArr.get(position).getTask());
        holder.textClient.setText("("+ tArr.get(position).getClient() +")");
        holder.textDescp.setText(text);
        holder.textView.setText(tArr.get(position).getProject());

       // setAnimation(holder.container, position);

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Details.class);
                intent.putExtra("id", tArr.get(position).getId());
                intent.putExtra("type", 4);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        holder.editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strTime .equals("0") ){
                    spinnerExpandedEdit(tArr.get(position).getId());
                }
            }
        });
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
                switch (params[0]) {
                    case "0":
                        status = postTask(Constants.mainUrl + Constants.updateReport, 0);
                        break;
                    case "1":
                        status = postTask(Constants.mainUrl + Constants.updateResume, 1);
                        break;
                    case "2":
                        status = postTask(Constants.mainUrl + Constants.deleteReport, 2);
                        break;
                    case "3":
                        status = postTask1(Constants.mainUrl + Constants.updateWhole, hm);
                        break;
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
                switch (stat) {
                    case "success": {
                        int id = Integer.parseInt(mJsonObj.getString("id"));
                        String tot = "" + mJsonObj.getString("total");
                        db.updateTime(id, tot, "0");
                        k = 1;
                        idUpd = id;

                        break;
                    }
                    case "success_resume": {
                        int id = Integer.parseInt(mJsonObj.getString("id"));
                        String strTime = "" + mJsonObj.getString("resume_starttime");
                        db.updateTime(id, "0", strTime);
                        k = 2;
                        idUpd = id;

                        break;
                    }
                    case "success_resume_pause": {
                        int id = mJsonObj.getInt("id");
                        String tot = "" + mJsonObj.getString("total");
                        db.updateTime(id, tot, "0");
                        tArr.get(pos).setStarttime("0");
                        String strTime = "" + mJsonObj.getString("resume_starttime");
                        int resID = mJsonObj.getInt("resume_id");
                        db.updateTime(resID, "0", strTime);
                        k = 3;
                        idUpd = id;

                        break;
                    }
                    case "delete_success": {
                        int id = mJsonObj.getInt("id");
                        db.deleteUser("" + id);
                        k = 0;
                        idUpd = id;
                        tvHour.setText(showTimerChanges(dt));
                        //notifyDataSetChanged();
                        break;
                    }
                    case "Failed":
                        Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                        break;
                }

                if (k!=0) {
                    Intent intent = new Intent(context, Details.class);
                    intent.putExtra("id", idUpd);
                    intent.putExtra("type", 3);
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

    //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private String showTimerChanges(String currentDateandTime) {
        String mi = "",min = "",hr ="";
        Database db = new Database(context);
        String tot = db.getdaysTotal(currentDateandTime);
        if(!tot.equals("")&& tot!=null) {

            System.out.println("***tot "+tot);
            int m = Math.round(Float.valueOf(tot)) / 60;
            int h = m / 60;

            if (h<10)
                hr = "0"+h;
            else
                hr = ""+h;

            if (m > 60) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, m);
                /*calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, 37540);*/
                min = new SimpleDateFormat("mm").format(calendar.getTime());
            } else {
                if (m<10) {
                    min = "0" + m;
                }
                else
                    min = ""+m;

             //   min = "" + mi;
            }

            return "Total Hours:\n" + hr + ":" + min+" hrs" ;
            //((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Total- " + h + ":" + min);
        }
        else{
            return "Total Hours:\n00 : 00 hrs" ;
            // ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Total- 00:00");
        }
    }

    private OkHttpClient client = new OkHttpClient();
    private String postTask(String url, int type) throws IOException {
        // RequestBody body = RequestBody.create(JSON, json);

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("subdomain", ""+subdomain);
        if(type==0) {
            float stoptime = System.currentTimeMillis()/1000.0f;
            float diffInMillies = 0f;

            diffInMillies = stoptime - Float.valueOf(aTime.getStarttime());
            formBuilder.add("total", "" + diffInMillies)
                    .add("starttime", "" + aTime.getStarttime())
                    .add("stoptime", "" + stoptime)
                    .add("id", "" + aTime.getId());
        }
        else if(type==1){
            float stoptime = System.currentTimeMillis()/1000.0f;
            float diffInMillies = 0f;

            diffInMillies = stoptime - Float.valueOf(aTime.getStarttime());
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

    private String postTask1(String url, HashMap<String, String> hm) throws IOException {
        // RequestBody body = RequestBody.create(JSON, json);
        FormBody.Builder formBuilder = new FormBody.Builder();

        Iterator it = hm.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            formBuilder.add(""+pair.getKey().toString(), ""+pair.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
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
        else {
            return "Failed";
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    private void displayDialog(String message, String title){
        new AlertDialog.Builder(context)
                .setTitle(""+title)
                .setMessage(""+message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private void deleteConfirmDialog(final int position) {
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

    private void removeAt(int position) {
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

    private DatePickerDialog.OnDateSetListener dateListener ;
    private HashMap<String,String> hm ;
    private String dateVal = "";
    private TextView editTotalTime,clientname;
    //private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private void spinnerExpandedEdit(final int id){

        Timer aTime = getDataVal(id);

        laptopCollection = new LinkedHashMap<String, List<String>>();
        createGroupList();
        createCollection();

        final AlertDialog.Builder dialogBuilderMain = new AlertDialog.Builder(context);
        LayoutInflater inflater =((Activity) context).getLayoutInflater();
        final View dialogViewMain = inflater.inflate(R.layout.custom_dia, null);
        dialogBuilderMain.setView(dialogViewMain);
        final AlertDialog alertDialogMain = dialogBuilderMain.create();
        alertDialogMain.setTitle("Edit Time Entry");
        final Spinner spinner = (Spinner) dialogViewMain.findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Database db = new Database(context);
        ArrayList<Task> mTaskArray = db.getTasks();

        List<String> strArrTask = new ArrayList<String>();

        for (int i = 0; i<mTaskArray.size(); i++){
            strArrTask.add(""+mTaskArray.get(i).getTask_name());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, R.layout.spinner_text) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(R.id.textView23)).setText("");
                    ((TextView)v.findViewById(R.id.textView23)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // dataAdapter.setDropDownViewResource(R.layout.spinner_text);
        for (int i =0;i<strArrTask.size();i++ ){
            dataAdapter.add(""+strArrTask.get(i));
        }
        dataAdapter.add("Select task");

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(dataAdapter.getCount());

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.select_state, android.R.layout.simple_spinner_item);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        if (!aTime.getTask().equals(null)) {
            int spinnerPosition = dataAdapter.getPosition(aTime.getTask());
            spinner.setSelection(spinnerPosition);
        }

        clientname = (TextView) dialogViewMain.findViewById(R.id.textView5);
        final TextView projEdit = (TextView) dialogViewMain.findViewById(R.id.editText3);
        projEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dia_, null);
                dialogBuilder.setView(dialogView);

                ExpandableListView expListView = (ExpandableListView) dialogView.findViewById(R.id.laptop_list);
                final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                        ((Activity) context), groupList, laptopCollection);
                expListView.setAdapter(expListAdapter);

                //setGroupIndicatorToRight();

                final AlertDialog alertDialog = dialogBuilder.create();
                expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {
                        final String selected = (String) expListAdapter.getChild(
                                groupPosition, childPosition);
                        final String selectedGrp = (String) expListAdapter.getGroup(
                                groupPosition);

                        projEdit.setText(""+selected);
                        clientname.setText(""+selectedGrp);

                        alertDialog.cancel();
                        return true;
                    }
                });
                alertDialog.show();
            }
        });

        final EditText editDesc = (EditText) dialogViewMain.findViewById(R.id.editText5);
        editTotalTime = (TextView) dialogViewMain.findViewById(R.id.editText7);
        final TextView currentDate = (TextView) dialogViewMain.findViewById(R.id.editText6);

        //String currentDateandTime = sdf.format();
        SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy");

       // String startDateString = "06/27/2007";
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date startDate; String newDateString = "";
        try {
            startDate = df.parse(aTime.getDate().toString());
            newDateString = formatter.format(startDate);
            System.out.println(newDateString );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        currentDate.setText( newDateString );

        String text = "";
        byte[] data = Base64.decode(""+ aTime.getDescp(), Base64.DEFAULT);
        try {
            text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        editDesc.setText(""+text);
        //editDesc.setText(aTime.getDescp());

        editTotalTime.setText(setTimerTotal(aTime));
        clientname.setVisibility(View.VISIBLE);
        clientname.setText(aTime.getClient());
        projEdit.setText(aTime.getProject());
        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateVal = updateLabel();
                currentDate.setText(""+dateVal);
            }
        };

        Button btnSubmit = (Button) dialogViewMain.findViewById(R.id.button3);
        Button btnCancel = (Button) dialogViewMain.findViewById(R.id.button2);

        btnSubmit.setText("UPDATE");
        ImageButton imgCal = (ImageButton) dialogViewMain.findViewById(R.id.imageView4);
        imgCal.setVisibility(View.INVISIBLE);
        imgCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, dateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aMainTimerSub = new Timer();
                String tot_time = editTotalTime.getText().toString();
                double time1 = 0 ;
                if (clientname.getText().length()>0&& !spinner.getSelectedItem().toString().equals("Select task") ) {

                    if (!tot_time.equals("")) {
                        tot_time = tot_time.trim();
                        //double number = Double.parseDouble(tot_time);
                        int decimal = Integer.parseInt(tot_time.split(":")[0]);
                        int fractional = Integer.parseInt(tot_time.split(":")[1]);

                        fractional = fractional * 60000;
                        decimal = decimal * 3600000;
                        time1 = (fractional + decimal) / 1000;
                        Log.d("total: ", "" + time1);
                    }

                    String clName = clientname.getText().toString();
                    String prName = projEdit.getText().toString();
                    //long time = System.currentTimeMillis();
                    if (clName.length() > 0 && email.length() > 0) {
                        /*String start_dt = "" + currentDate.getText().toString();
                        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = null;
                        try {
                            date = (Date) formatter.parse(start_dt);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/
                        //SimpleDateFormat newFormat = new SimpleDateFormat("MM-dd-yyyy");
                        //     String finalString = sdf.format(currentDate);

                        hm = new HashMap<String, String>();

                        hm.put("id", "" + id);
                        hm.put("project", "" + prName);
                        hm.put("email", "" + email);
                        hm.put("client", "" + clName);
                       // hm.put("date", "" + currentDate ); //issue with date always paassing +1day or -1
                        hm.put("desc", "" + editDesc.getText().toString());
                        hm.put("task", "" + spinner.getSelectedItem().toString());
                       // hm.put("time", "" + (time1 / 1000.0f));
                        hm.put("total", "" + time1);
                        hm.put("subdomain", "" + subdomain);

                        if (isNetworkConnected() && hm.size() > 0) {
                            AsyncTaskSubmit runner = new AsyncTaskSubmit();
                            runner.execute("3");
                            alertDialogMain.dismiss();
                        } else if (hm.size() == 0) {
                            displayDialogS("Unavailable", "Server Error", null);
                        } else {
                            displayDialogS("Internet connection unavailable", "Connection Error", null);
                        }
                    }
                }
                else if (clientname.getText().length()==0) {
                    projEdit.setError("Please Select project");
                }
                else{
                    spinner.performClick();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogMain.dismiss();
            }
        });

        editTotalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultTimePickerDialog();
            }
        });

        alertDialogMain.show();
    }

    private Calendar myCalendar = Calendar.getInstance();
    private List<String> groupList,childList;
    private void createGroupList() {
        groupList = new ArrayList<String>();
        Database db = new Database(context);
        groupList = db.getClientNames();
        Log.d("Group", ""+groupList.toString());
    }
    private String updateLabel() {
        SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return sdfd.format(myCalendar.getTime());
    }

    private void createCollection() {

        ArrayList<Client> mClArr = new ArrayList<Client>();
        System.out.println("GrpSize ##**"+groupList.get(0));
        for(int i = 0;i<groupList.size();i++){
            Client mClient = new Client();
            mClient.setClient(groupList.get(i).toString());
            Database db = new Database(context);
            mClient.setmClProjArr(db.getClients(groupList.get(i)));

            mClArr.add(mClient);
        }

        for (String laptop : groupList) {
            for (int j=0;j<mClArr.size();j++) {
                if (laptop.equals(mClArr.get(j).getClient())) {
                    String[] prjModels = new String[ mClArr.get(j).getmClProjArr().size()];
                    for (int k = 0; k < mClArr.get(j).getmClProjArr().size(); k++) {
                        prjModels[k] = mClArr.get(j).getmClProjArr().get(k).getProj_name();
                    }
                    loadChild(prjModels);
                }
                laptopCollection.put(laptop, childList);
            }
        }
    }

    Map<String, List<String>> laptopCollection;
    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels)
            childList.add(model);
    }

    private int mHour, mMinute;String hms = "";
    public String defaultTimePickerDialog() {
        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
       /* android.app.TimePickerDialog dpd = new android.app.TimePickerDialog(getActivity(), this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), false);
        dpd.show();*/
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        //txtTime.setText(hourOfDay + ":" + minute);
                        hms = ""+hourOfDay + ":" + minute;
                        editTotalTime.setText(hms);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();

        return hms;
    }

    public void displayDialogS(String message, final String title,final Timer aTimer){
        new AlertDialog.Builder(context)
                .setTitle(""+title).setCancelable(false)
                .setMessage(""+message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(title.equals("Success"))
                        {
                            Intent intent = new Intent(context, Details.class);
                            intent.putExtra("id",aTimer.getId());
                            context.startActivity(intent);
                            ((Activity) context).finish();
                           // displayNotificationOne(aTimer.getId());
                        }
                        dialog.dismiss();
                    }
                }) .setIcon(R.drawable.ic_cloud_done_black_24dp).show();
    }

    private Timer getDataVal(int id) {
        Database db = new Database(context);
        String text = "";
        Timer aTime = db.getResult(id);
        //SpannableString content = new SpannableString("Project: "+aTime.getProject().toUpperCase());
        //content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        byte[] data = Base64.decode(""+ aTime.getDescp(), Base64.DEFAULT);
        try {
            text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String start_dt = ""+ aTime.getDate();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = (Date) sdf.parse(start_dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //SimpleDateFormat newFormat = new SimpleDateFormat("MM-dd-yyyy");
        String finalString = formatter.format(date);

        return aTime;
    }

    private String setTimerTotal(final Timer mTime){
        String timeDta = "";
        SimpleDateFormat df = new SimpleDateFormat("hh:mm"); // HH for 0-23
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        //String time = df.format(d);
        float diffInMillies = Float.valueOf(mTime.getTotal());
        if (diffInMillies!=0) {
            int m = Math.round(diffInMillies) / 60;
            int h = m / 60;

            String min = "";
            if (m > 59) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, m);
                    /*calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.SECOND, 37540);*/
                min = new SimpleDateFormat("mm").format(calendar.getTime());
            } else if (m < 10) {
                min = "0" + m;
            } else {
                min = "" + m;
            }
            timeDta = " " + h + ":" + min;
        }
        return timeDta;
    }
}