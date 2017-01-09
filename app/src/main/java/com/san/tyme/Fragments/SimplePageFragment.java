package com.san.tyme.Fragments;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.san.tyme.Database.Database;
import com.san.tyme.R;
import com.san.tyme.TymeActivity;
import com.san.tyme.TymeApplication;
import com.san.tyme.activity.Details;
import com.san.tyme.activity.NotificationOne;
import com.san.tyme.model.Client;
import com.san.tyme.model.Task;
import com.san.tyme.model.Timer;
import com.san.tyme.utils.Constants;
import com.san.tyme.utils.ExpandableListAdapter;
import com.san.tyme.utils.RecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SimplePageFragment extends Fragment  implements TimePickerDialog.OnTimeSetListener{

   // private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");
    private static final String DATE_PICKER_DATE_KEY = "date_picker_date_key";
    private static final String DATE_PICKER_POSITION_KEY = "date_picker_position_key";
    public static final String PREFS_NAME = "TymePref";

    private TextView tvHours,tvPosition;
    //private int position;
    private long date;
    private Context context;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    RecyclerViewAdapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;
    TymeActivity tymeData;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String selectedItemText = "",dateVal = "",email = "",subdomain = "";
    ExpandableListView expListView;
    Map<String, List<String>> laptopCollection;
    List<String> groupList,childList;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dateListener ;
    HashMap<String,String> hm ;
    Database db ;
    public static SimplePageFragment newInstance(int position, long date) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATE_PICKER_POSITION_KEY, position);
        bundle.putLong(DATE_PICKER_DATE_KEY, date);

        SimplePageFragment simplePageFragment = new SimplePageFragment();
        simplePageFragment.setArguments(bundle);

        return simplePageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        //position = getArguments().getInt(DATE_PICKER_POSITION_KEY, -1);
        date = getArguments().getLong(DATE_PICKER_DATE_KEY, -1);

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        String restoredText = prefs.getString("id", null);
        if (restoredText != null) {
            email = prefs.getString("email", "");//"No name defined" is the default value.
            subdomain = prefs.getString("subdomain", ""); //0 is the default value.
            /*titleName = prefs.getString("Name", "");
            imgPath = prefs.getString("image", "");*/
        }

        /* ((TymeActivity)getActivity()).setFragmentRefreshListener(new TymeActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecycler();
            }
        });
        */
        tymeData = new TymeActivity();
        tvHours = tymeData.meditText;
        //showTimerChanges(sdf.format(date));
        // IntentFilter mTime = new IntentFilter(Intent.ACTION_TIME_TICK);
        //register broadcast receiver
        //context.registerReceiver(mtimeInfoReceiver, mTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_page_simple, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearLayout = (LinearLayout) view.findViewById(R.id.linLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview1);
        //tvHours = (TextView) view.findViewById(R.id.textView18);

        recylerViewLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        db = new Database(getActivity());
        String currentDateandTime = sdf.format(date);
        TymeApplication.reportDate = currentDateandTime;

        laptopCollection = new LinkedHashMap<String, List<String>>();
        createGroupList();
        createCollection();

        ArrayList<Timer> mTimerArr = db.getResults(""+currentDateandTime);
        /**have to pass the data from server to the recycler item view */
        recyclerViewAdapter = new RecyclerViewAdapter(context, currentDateandTime, mTimerArr);

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        //Animation animation = AnimationUtils.loadAnimation(context, R.anim.fab1_show);
        //Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerExpanded();
                /*Toast.makeText(getActivity(), SIMPLE_DATE_FORMAT.format(date)+" "
                        +TymeApplication.reportDate, Toast.LENGTH_LONG)
                        .show();*/
            }
        });

        //fab.startAnimation(animation);
        //tvHours. startAnimation(animation1);

        /*recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch(newState) {
                    case 2: // SCROLL_STATE_FLING

                        Animation animation = new TranslateAnimation(0,0,0,1000);
                        animation.setDuration(1000);
                        tvHours.startAnimation(animation);
                        tvHours.setVisibility(View.GONE);

                        break;

                    case 1: // SCROLL_STATE_TOUCH_SCROLL
                        //hide button here
                        Animation animation1 = new TranslateAnimation(0,0,0,1000);
                        animation1.setDuration(1000);
                        tvHours.startAnimation(animation1);
                        tvHours.setVisibility(View.GONE);
                        break;

                    case 0: // SCROLL_STATE_IDLE
                        //show button here
                        Animation animation2 = new TranslateAnimation(0,0,0,1000);
                        animation2.setDuration(1000);
                        tvHours.startAnimation(animation2);
                        tvHours.setVisibility(View.VISIBLE);
                        break;

                    default:
                        //show button here
                        Animation animation3 = new TranslateAnimation(0,0,0,1000);
                        animation3.setDuration(1000);
                        tvHours.startAnimation(animation3);
                        tvHours.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });*/
    }

    private void showTimerChanges(String currentDateandTime) {

        db = new Database(getActivity());
        String tot = db.getdaysTotal(currentDateandTime);
        if(!tot.equals("")&& tot!=null) {
            String min = "";
            System.out.println("***tot "+tot);
            int m = Math.round(Float.valueOf(tot).floatValue()) / 60;
            int h = m / 60;

            if (m > 60) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, m);
                /*calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, 37540);*/
                min = new SimpleDateFormat("mm").format(calendar.getTime());
            } else {
                min = "" + m;
            }

            tvHours.setText("Total Hours\n" + h + ":" + min);
            //((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Total- " + h + ":" + min);
        }
        else{
            tvHours.setText("Total Hours\n00 : 00");
            // ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Total- 00:00");
        }
    }

    TextView editTotalTime,clientname;
    public void spinnerExpanded(){

        final AlertDialog.Builder dialogBuilderMain = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogViewMain = inflater.inflate(R.layout.custom_dia, null);
        dialogBuilderMain.setView(dialogViewMain);

        final AlertDialog alertDialogMain = dialogBuilderMain.create();

        final Spinner spinner = (Spinner) dialogViewMain.findViewById(R.id.spinner);
        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = ""+ parent.getItemAtPosition(position);
                clientname.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedItemText = ""+ parent.getItemAtPosition(0);
            }
        });

        db = new Database(context);
        ArrayList<Task> mTaskArray = db.getTasks();

        List<String> strArrTask = new ArrayList<String>();

        for (int i = 0; i<mTaskArray.size(); i++){
            strArrTask.add(""+mTaskArray.get(i).getTask_name());
        }
        // Creating adapter for spinner

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text) {
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

        /* ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                 R.layout.spinner_text, strArrTask);*/

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // dataAdapter.setDropDownViewResource(R.layout.spinner_text);
        for (int i =0;i<strArrTask.size();i++ ){
            dataAdapter.add(""+strArrTask.get(i));
        }
        dataAdapter.add("Select task");

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(dataAdapter.getCount());

        clientname = (TextView) dialogViewMain.findViewById(R.id.textView5);
        final TextView projEdit = (TextView) dialogViewMain.findViewById(R.id.editText3);
        projEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dia_, null);
                dialogBuilder.setView(dialogView);

                expListView = (ExpandableListView) dialogView.findViewById(R.id.laptop_list);
                final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                        getActivity(), groupList, laptopCollection);
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
        SimpleDateFormat sdfTemp = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        currentDate.setText(""+sdfTemp.format(date));
        //currentDate.setEnabled(false);

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

        ImageButton imgCal = (ImageButton) dialogViewMain.findViewById(R.id.imageView4);
        imgCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), dateListener, myCalendar
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
                    long time = System.currentTimeMillis();
                    if (clName.length() > 0 && email.length() > 0) {
                        String start_dt = "" + currentDate.getText().toString();
                        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = null;
                        try {
                            date = (Date) formatter.parse(start_dt);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //SimpleDateFormat newFormat = new SimpleDateFormat("MM-dd-yyyy");
                        String finalString = sdf.format(date);

                        hm = new HashMap<String, String>();

                        hm.put("project", "" + prName);
                        hm.put("email", "" + email);
                        hm.put("client", "" + clName);
                        hm.put("date", "" + finalString); //issue with date always paassing +1day or -1
                        hm.put("desc", "" + editDesc.getText().toString());
                        hm.put("task", "" + selectedItemText);
                        hm.put("time", "" + (time / 1000.0f));
                        hm.put("total", "" + time1);
                        hm.put("subdomain", "" + subdomain);

                        if (isNetworkConnected() && hm.size() > 0) {
                            AsyncTaskSubmit runner = new AsyncTaskSubmit();
                            runner.execute();
                            alertDialogMain.dismiss();
                        } else if (hm.size() == 0) {
                            displayDialog("Unavailable", "Server Error", null);
                        } else {
                            displayDialog("Internet connection unavailable", "Connection Error", null);
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

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    private String updateLabel() {
        SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return sdfd.format(myCalendar.getTime());
    }

    private void createGroupList() {
        groupList = new ArrayList<String>();
        Database db = new Database(getActivity());
        groupList = db.getClientNames();
        Log.d("Group", ""+groupList.toString());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    private int mHour, mMinute;String hms = "";
    public String defaultTimePickerDialog() {
        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
       /* android.app.TimePickerDialog dpd = new android.app.TimePickerDialog(getActivity(), this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), false);
        dpd.show();*/
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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

    private class AsyncTaskSubmit extends AsyncTask<String, String, String> {

        private String status;
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            try {
                if(hm.size()>0)
                    status = postTask(Constants.mainUrl + Constants.newReport, hm);
                else{
                    displayDialog("Data submit failed.","Failed",null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            // {"status":"success","new_id":[{"id":"1226"}]}
            System.out.println("res** "+result);
            JSONObject mJsonObj;
            try {
                mJsonObj = new JSONObject(result);
                if (mJsonObj.getString("status").equals("success")){

                    JSONArray mJsonTimArr = mJsonObj.getJSONArray("new_id");
                    JSONObject jsonobject = mJsonTimArr.getJSONObject(0);
                    Timer aTimer = new Timer();
                    aTimer.setDescp(jsonobject.getString("descp"));
                    aTimer.setTotal(jsonobject.getString("total"));
                    aTimer.setProject(jsonobject.getString("project"));
                    aTimer.setEmail(jsonobject.getString("email"));
                    aTimer.setClient(jsonobject.getString("client"));
                    aTimer.setDate(jsonobject.getString("date"));
                    aTimer.setTask(jsonobject.getString("task"));
                    aTimer.setStarttime(jsonobject.getString("starttime"));
                    aTimer.setId(jsonobject.getInt("id"));
                    aTimer.setIsSynced(1);

                    Database db = new Database(context);
                    long count = db.createResultSingle(aTimer);

                    if (count>0)
                    {
                        /*if(getFragmentRefreshListener()!=null){
                            SimplePageFragment.newInstance(datePosition,dateFrg);
                            getFragmentRefreshListener().onRefresh();
                        }*/
                        displayDialog("Data submitted successfully.","Success",aTimer);
                    }
                    else{
                        displayDialog("Data submit Failed.","Failed",null);
                    }
                }
                else if(mJsonObj.getString("status").equals("timer_exist")){

                    int id =  Integer.parseInt(mJsonObj.getString("id"));
                    String tot = ""+mJsonObj.getString("total");
                    Database db = new Database(context);
                    db.updateTime(id,tot,"0");

                    JSONArray mJsonTimArr = mJsonObj.getJSONArray("new_id");
                    JSONObject jsonobject = mJsonTimArr.getJSONObject(0);
                    Timer aTimer = new Timer();
                    aTimer.setDescp(jsonobject.getString("descp"));
                    aTimer.setTotal(jsonobject.getString("total"));
                    aTimer.setProject(jsonobject.getString("project"));
                    aTimer.setEmail(jsonobject.getString("email"));
                    aTimer.setClient(jsonobject.getString("client"));
                    aTimer.setDate(jsonobject.getString("date"));
                    aTimer.setTask(jsonobject.getString("task"));
                    aTimer.setStarttime(jsonobject.getString("starttime"));
                    aTimer.setId(jsonobject.getInt("id"));
                    aTimer.setIsSynced(1);

                    long count = db.createResultSingle(aTimer);
                    if (count>0) {
                        displayDialog("Data submitted successfully.","Success",aTimer);
                        //displayNotificationOne();
                    }
                    //refreshRecycler();
                }
                else if(mJsonObj.getString("status").equals("Failure")){
                    displayDialog("Data submit Failed.","Failed",null);
                }
            } catch (JSONException e) {
                displayDialog("Data corrupted.","Failed",null);
                e.printStackTrace();
            }
            //SimplePageFragment.newInstance(datePosition,dateFrg);
            //finalResult.setText(result);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Please wait...");
            progressDialog.setMessage("Task submit in progress");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
        }
    }

    OkHttpClient client = new OkHttpClient();
    String postTask(String url, HashMap<String,String> hm) throws IOException {
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

    public void displayDialog(String message, final String title,final Timer aTimer){
        new AlertDialog.Builder(context)
                .setTitle(""+title).setCancelable(false)
                .setMessage(""+message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(title.equals("Success"))
                        {
                            Intent intent = new Intent(getActivity(), Details.class);
                            intent.putExtra("id",aTimer.getId());
                            startActivity(intent);
                            getActivity().finish();
                            displayNotificationOne(aTimer.getId());
                        }
                        dialog.dismiss();
                    }
                }) .setIcon(R.drawable.ic_cloud_done_black_24dp).show();
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

    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels)
            childList.add(model);
    }

    public void refreshRecycler(){
        Database db = new Database(getActivity());
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDateandTime = sdf.format(date);
        //TymeApplication.reportDate = currentDateandTime;
        ArrayList<Timer> mTimerArr = db.getResults(""+TymeApplication.reportDate);
        /**have to pass the data from server to the recycler item view */
        recyclerViewAdapter = new RecyclerViewAdapter(context, currentDateandTime, mTimerArr);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.invalidate();
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private int numMessagesOne = 0;
    protected void displayNotificationOne(int id) {

        // Invoking the default notification service
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(getActivity());

        mBuilder.setContentTitle("Tyme.co");
        mBuilder.setContentText("Task Added successfully");
        mBuilder.setTicker("Timer Started");
        mBuilder.setSmallIcon(R.drawable.tyme_);

        // Increase notification number every time a new notification arrives
        mBuilder.setNumber(++numMessagesOne);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getActivity(), Details.class);
        //resultIntent.putExtra("notificationId", notificationIdOne);
        resultIntent.putExtra("id", id);
        resultIntent.putExtra("type", 0);

        //This ensures that navigating backward from the Activity leads out of the app to Home page
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

        // Adds the back stack for the Intent
        stackBuilder.addParentStack(NotificationOne.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT //can only be used once
                );
        // start the activity when the user clicks the notification text
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager myNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        // pass the Notification object to the system
        int notificationIdOne = 111;
        myNotificationManager.notify(notificationIdOne, mBuilder.build());
    }

    @Override
    public void onResume() {
        super.onResume();
        //showTimerChanges(TymeApplication.reportDate );
    }



    /*  @Override
    public void onResume() {
        super.onResume();
        Database db = new Database(getActivity());
        String tot = db.getdaysTotal(TymeApplication.reportDate);
        if(!tot.equals("")) {
            String min = "";

            System.out.println("***tot "+tot);
            int m = Math.round(Float.valueOf(tot).floatValue()) / 60;

            int h = m / 60;

            if (m > 60) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, m);

                min = new SimpleDateFormat("mm").format(calendar.getTime());
            } else {
                min = "" + m;
            }

            tvHours.setText("Total Hours\n" + h + ":" + min);

            //((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Total- " + h + ":" + min);
        }
        else{
            tvHours.setText("Total Hours\n00 : 00");
            // ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Total- 00:00");
        }

        //tvHours. startAnimation(animation1);
    }*/
}
