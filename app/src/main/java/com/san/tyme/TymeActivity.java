package com.san.tyme;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.DatePicker;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.san.tyme.Database.Database;
import com.san.tyme.Fragments.SimplePageFragment;
import com.san.tyme.activity.DomainActivity;
import com.san.tyme.model.Client;
import com.san.tyme.model.Timer;
import com.san.tyme.utils.Constants;
import com.san.tyme.utils.CustomDateAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.rspective.pagerdatepicker.PagerDatePickerDateFormat;
import pl.rspective.pagerdatepicker.adapter.DatePagerFragmentAdapter;
import pl.rspective.pagerdatepicker.model.DateItem;
import pl.rspective.pagerdatepicker.view.DateRecyclerView;
import pl.rspective.pagerdatepicker.view.RecyclerViewInsetDecoration;

public class TymeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFS_NAME = "TymePref";
    String email = "",subdomain = "",titleName = "", imgPath = "",selectedItemText = "";
    //private WeekDatePicker datePicker;
    List<String> groupList,childList;
    Map<String, List<String>> laptopCollection;
    //ExpandableListView expListView;
    //HashMap<String,String> hm ;
    int datePosition = 0;
    ImageView mDrawerHeaderImg;
    //private CoordinatorLayout coordinatorLayout;
    public ProgressBar mPrBar;
    String currentDate = "", recievedDate ="";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    ArrayList<Timer> mTotTimerArr ;
    //private Menu menu;
    ViewPager pager ;
    DatePickerDialog.OnDateSetListener dateListenerPick ;
    DateRecyclerView dateList;
    long dateFrg;
    Date start = null,end = null,defaultDate = null;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tyme);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("id", null);
        if (restoredText != null) {
             email = prefs.getString("email", "");//"No name defined" is the default value.
             subdomain = prefs.getString("subdomain", ""); //0 is the default value.
             titleName = prefs.getString("Name", "");
             imgPath = prefs.getString("image", "");
         }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recievedDate = extras.getString("dateRec","");
        }

        mPrBar = (ProgressBar) findViewById(R.id.progressBar);
        mTotTimerArr = new ArrayList<Timer>();

        if (isNetworkConnected() ){
            AsyncTaskFetch runner = new AsyncTaskFetch();
            runner.execute();
        }
        else{
            displayNoNetworkDialog();
        }

        View mHeaderView;
        TextView mDrawerHeaderTitle, mDrawerHeaderName;
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerExpanded();
            }
        });*/

        pager = (ViewPager) findViewById(R.id.pager);
        dateList = (DateRecyclerView) findViewById(R.id.date_list);

        dateList.addItemDecoration(new RecyclerViewInsetDecoration(this, R.dimen.date_card_insets));
        //dateList.addItemDecoration(new RecyclerViewInsetDecoration(TymeActivity.this));
        final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf1.format(new Date());
        currentDate = sdf.format(new Date());
        //TymeApplication.reportDate = currentDate;

        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -365);
            Calendar cal2 = Calendar.getInstance();
            cal2.add(Calendar.DATE, 365);

            String formatted = sdf1.format(cal.getTime());
            String formatted1 = sdf1.format(cal2.getTime());

            start = PagerDatePickerDateFormat.DATE_PICKER_DD_MM_YYYY_FORMAT.parse(""+formatted);
            end = PagerDatePickerDateFormat.DATE_PICKER_DD_MM_YYYY_FORMAT.parse(""+formatted1);
            String formattedDate = "";
            if(recievedDate.equals(""))
            {
                defaultDate = PagerDatePickerDateFormat.DATE_PICKER_DD_MM_YYYY_FORMAT.parse(currentDateandTime);
            }
            else{
               // DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
              //  Date date = null;
                try {
                    DateFormat originalFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
                    DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = originalFormat.parse(recievedDate);
                    formattedDate = targetFormat.format(date);

                    //date = (Date) formatter.parse(recievedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                defaultDate = PagerDatePickerDateFormat.DATE_PICKER_DD_MM_YYYY_FORMAT.parse(formattedDate);
            }

            //updateMenuTitles(sdf.format(new Date()));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //dateList.setAdapter(new DefaultDateAdapter(start, end,defaultDate));
        dateList.setAdapter(new CustomDateAdapter(start,end,defaultDate));

        DatePagerFragmentAdapter fragmentAdapter = new DatePagerFragmentAdapter(getSupportFragmentManager(),
                dateList.getDateAdapter()) {
            @Override
            protected Fragment getFragment(int position, long date) {
               /* Toast.makeText(getBaseContext(), "in frag "+TymeApplication.reportDate, Toast.LENGTH_LONG)
                        .show();*/
                currentDate = sdf.format(date);
                datePosition = position;
                dateFrg = date;
                //updateMenuTitles(currentDate);
                //invalidateOptionsMenu();
                return SimplePageFragment.newInstance(position, date);
            }
        };

        pager.setAdapter(fragmentAdapter);
        dateList.setPager(pager);

        dateList.setDatePickerListener(new DateRecyclerView.DatePickerListener() {
            @Override
            public void onDatePickerItemClick(DateItem dateItem, int position) {
               /* Toast.makeText(getBaseContext(), sdf.format(dateItem.getDate())
                        + " "+TymeApplication.reportDate, Toast.LENGTH_SHORT)
                        .show();*/
                //User clicked date item from top date picker
            }

            @Override
            public void onDatePickerPageSelected(int position) {
                //User changed date using swipe (left/right)
            }

            @Override
            public void onDatePickerPageStateChanged(int state) {
                //User changed page
            }

            @Override
            public void onDatePickerPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //User changed page
            }
        });

        dateListenerPick = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String dateDef = sdf1.format(myCalendar.getTime());

                try {
                    dateList.setAdapter(new CustomDateAdapter(start,end,
                            PagerDatePickerDateFormat.DATE_PICKER_DD_MM_YYYY_FORMAT.parse(dateDef)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                DatePagerFragmentAdapter fragmentAdapter = new DatePagerFragmentAdapter(getSupportFragmentManager(),
                        dateList.getDateAdapter()) {
                    @Override
                    protected Fragment getFragment(int position, long date) {
                        /* Toast.makeText(getBaseContext(), "in frag "+TymeApplication.reportDate, Toast.LENGTH_LONG)
                        .show();*/
                        currentDate = sdf.format(date);
                        datePosition = position;
                        dateFrg = date;
                        //updateMenuTitles(currentDate);
                        //invalidateOptionsMenu();
                        return SimplePageFragment.newInstance(position, date);
                    }
                };

                pager.setAdapter(fragmentAdapter);
                dateList.setPager(pager);
                pager.getAdapter().notifyDataSetChanged();
            }
        };

        //coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        mHeaderView = navigationView.getHeaderView(0);

        mDrawerHeaderTitle = (TextView) mHeaderView.findViewById(R.id.textView);
        mDrawerHeaderName = (TextView) mHeaderView.findViewById(R.id.txtName);
        mDrawerHeaderImg = (ImageView) mHeaderView.findViewById(R.id.imageView);
        mDrawerHeaderTitle.setText(""+email);
        mDrawerHeaderName.setText(""+titleName);
        if(!imgPath.equals("") && isNetworkConnected())
            new DownloadImageTask(mDrawerHeaderImg)
                    .execute("http://"+subdomain.trim()+".tyme.co/"+imgPath.replace("\\",""));

        navigationView.setNavigationItemSelectedListener(this);

        laptopCollection = new LinkedHashMap<String, List<String>>();
        createGroupList();
        createCollection();
        exportDatabse("tyme.sqlite");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateViewpager();
    }

    public void updateViewpager(){
        DatePagerFragmentAdapter fragmentAdapter = new DatePagerFragmentAdapter(getSupportFragmentManager(),
                dateList.getDateAdapter()) {
            @Override
            protected Fragment getFragment(int position, long date) {
               /* Toast.makeText(getBaseContext(), "in frag "+TymeApplication.reportDate, Toast.LENGTH_LONG)
                        .show();*/
                currentDate = sdf.format(date);
                datePosition = position;
                dateFrg = date;
                //updateMenuTitles(currentDate);
                //invalidateOptionsMenu();
                return SimplePageFragment.newInstance(position, date);
            }
        };

        pager.setAdapter(fragmentAdapter);
        dateList.setPager(pager);
        pager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.pencil).setVisible(false);
        //this.menu = menu;

        /*for (int i=0; i<mTotTimerArr.size();i++){
            String min = "";
            if(mTotTimerArr.get(i).getDate().equals(currentDate)){
                int m =  Math.round(Float.valueOf(mTotTimerArr.get(i).getTotal()).floatValue())/60;
                int h = m/60;
                if(m>60){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, h);
                    calendar.set(Calendar.MINUTE, m);*//*
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.SECOND, 37540);*//*
                    min = new SimpleDateFormat("mm").format(calendar.getTime());
                }
                else{
                    min = ""+m;
                }
                menu.findItem(R.id.action_settings).setTitle(h+":"+min);
            }
        }*/

        //updateMenuTitles(sdf.format(new Date()));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.tyme, menu);

        //this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            new DatePickerDialog(TymeActivity.this, dateListenerPick, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*private void updateMenuTitles(String strDate) {
        MenuItem mMenuItem = menu.findItem(R.id.action_settings);
        for (int i=0; i<mTotTimerArr.size();i++){
            String min = "";
            if(mTotTimerArr.get(i).getDate().equals(strDate)){

                int m =  Math.round(Long.valueOf(mTotTimerArr.get(i).getTotal()).longValue())/60;

                int h = m/60;

                if(m>60){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, h);
                    calendar.set(Calendar.MINUTE, m);/*
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.SECOND, 37540);
                    min = new SimpleDateFormat("mm").format(calendar.getTime());
                }
                else{
                    min = ""+m;
                }
                mMenuItem.setTitle(h+":"+min);
            }
        }
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        }else if (id == R.id.nav_manage) {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Log out!")
                    .setMessage("Do you really want to logout from current session?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Database db = new Database(TymeActivity.this);
                            db.removeAll();
                            SharedPreferences preferences = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();
                            editor.commit();
                            Intent intent = new Intent(TymeActivity.this, DomainActivity.class);
                            //intent.putExtra("dateRec",aTime.getDate());
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*public void spinnerExpanded(){

        final AlertDialog.Builder dialogBuilderMain = new AlertDialog.Builder(TymeActivity.this);
        LayoutInflater inflater = TymeActivity.this.getLayoutInflater();
        final View dialogViewMain = inflater.inflate(R.layout.custom_dia, null);
        dialogBuilderMain.setView(dialogViewMain);

        final AlertDialog alertDialogMain = dialogBuilderMain.create();

        Spinner spinner = (Spinner) dialogViewMain.findViewById(R.id.spinner);
        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 selectedItemText = ""+ parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedItemText = ""+ parent.getItemAtPosition(0);
            }
        });
        // Spinner Drop down elements

        Database adb = new Database(this);
        ArrayList<Task> mTaskArray = adb.getTasks();

        List<String> strArrTask = new ArrayList<String>();

        for (int i = 0; i<mTaskArray.size(); i++){
            strArrTask.add(""+mTaskArray.get(i).getTask_name());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, strArrTask);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        final TextView clientname = (TextView) dialogViewMain.findViewById(R.id.textView5);

        final TextView projEdit = (TextView) dialogViewMain.findViewById(R.id.editText3);
        projEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TymeActivity.this);
                LayoutInflater inflater = TymeActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dia_, null);
                dialogBuilder.setView(dialogView);

                expListView = (ExpandableListView) dialogView.findViewById(R.id.laptop_list);
                final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                        TymeActivity.this, groupList, laptopCollection);
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

                        Toast.makeText(getBaseContext(), selected+" "+selectedGrp, Toast.LENGTH_SHORT)
                                .show();
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
        final EditText editTotalTime = (EditText) dialogViewMain.findViewById(R.id.editText7);
        final EditText currentDate = (EditText) dialogViewMain.findViewById(R.id.editText6);

        //String currentDateandTime = sdf.format();
        SimpleDateFormat sdfTemp = new SimpleDateFormat( "dd/MM/yyyy", Locale.US);
        currentDate.setText(""+sdfTemp.format(new Date()));

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
                new DatePickerDialog(TymeActivity.this, dateListener, myCalendar
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
                if(clName.length()>0&& email.length()>0){

                    String start_dt = ""+currentDate.getText().toString();
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

                    hm.put("project",""+prName);
                    hm.put("email",""+email);
                    hm.put("client",""+clName);
                    hm.put("date",""+finalString); //issue with date always paassing +1day or -1
                    hm.put("desc",""+editDesc.getText().toString());
                    hm.put("task",""+selectedItemText);
                    hm.put("time",""+(time/1000.0f));
                    hm.put("total",""+time1);
                    hm.put("subdomain",""+subdomain);

                    if (isNetworkConnected()&& hm.size()>0){
                        AsyncTaskSubmit runner = new AsyncTaskSubmit();
                        runner.execute();
                        alertDialogMain.dismiss();
                    }
                    else if(hm.size()==0){
                        showToast("Failed to upload",0);
                    }
                    else {
                        displayNoNetworkDialog();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogMain.dismiss();
            }
        });

        alertDialogMain.show();
    }*/

    //String dateVal = "";

    //DatePickerDialog.OnDateSetListener dateListener ;

    /*private String updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return sdf.format(myCalendar.getTime());
    }*/

    private void createGroupList() {
        groupList = new ArrayList<String>();
        Database db = new Database(this);
        groupList = db.getClientNames();
        Log.d("Group", ""+groupList.toString());
    }

    private void createCollection() {

        ArrayList<Client> mClArr = new ArrayList<Client>();
        //System.out.println("GrpSize ##**"+groupList.get(0));
        for(int i = 0;i<groupList.size();i++){
            Client mClient = new Client();
            mClient.setClient(groupList.get(i).toString());
            Database db = new Database(this);
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

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    public OkHttpClient client = new OkHttpClient();

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

        return response.body().string();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            Response response = null;
            Bitmap mIcon11 = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()) {
                try {
                    mIcon11 = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void exportDatabse(String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+getPackageName()+"//databases//"+databaseName+"";
                String backupDBPath = "backupname.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }



   /* String postTask(String url, HashMap<String,String> hm) throws IOException {
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
        else
        {
            return "Failed";
        }
    }*/

    public void displayNoNetworkDialog(){
        new AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("It looks like your internet connection is off. Please turn it " +
                        "on and try again")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    static public boolean isServerReachable(Context context) {
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(Constants.mainUrl);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                urlConn.connect();
                if (urlConn.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
    /*public void showToast(String msg, int type){

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, ""+msg, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
        // Changing message text color
        // snackbar.setActionTextColor(Color.RED);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        if (type==1)
            textView.setTextColor(Color.GREEN);
        else
            textView.setTextColor(Color.RED);

        snackbar.show();
    }*/

    private class AsyncTaskFetch extends AsyncTask<String, String, String> {

        private String status;

        @Override
        protected String doInBackground(String... params) {

            try {
                if ( isServerReachable(TymeActivity.this)) {
                    String currentDateandTime = sdf.format(new Date());
                    status = postResults(Constants.mainUrl + Constants.timerTasks, currentDateandTime);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return status;
        }

        @Override
        protected void onPostExecute(String result) {
            mPrBar.setVisibility(View.INVISIBLE);
            ArrayList<Timer> mTimerArr = new ArrayList<Timer>();

            //Log.d("resrlt",""+result);
            if(result!=null){
            JSONObject mJsonObj;
            try {
                mJsonObj = new JSONObject(result);
                String stat = mJsonObj.getString("status");
                if(stat.equals("success")) {
                    JSONArray mJsonTimArr = mJsonObj.getJSONArray("timers");
                    for (int i = 0; i < mJsonTimArr.length(); i++) {
                        JSONObject jsonobject = mJsonTimArr.getJSONObject(i);

                        Timer aTimer = new Timer();
                        aTimer.setClient(jsonobject.getString("client"));
                        aTimer.setId(Integer.parseInt(jsonobject.getString("id")));
                        aTimer.setEmail(jsonobject.getString("email"));
                        aTimer.setDate(jsonobject.getString("date"));
                        aTimer.setStarttime(jsonobject.getString("starttime"));
                        aTimer.setTask(jsonobject.getString("task"));
                        aTimer.setProject(jsonobject.getString("project"));
                        aTimer.setStoptime(jsonobject.getString("stoptime"));
                        aTimer.setTotal(jsonobject.getString("total"));
                        aTimer.setDescp(jsonobject.getString("descp"));
                        aTimer.setIsSynced(1);

                        mTimerArr.add(aTimer);
                    }

                    JSONArray mJsontotArr = mJsonObj.getJSONArray("total");
                    for (int i = 0; i < mJsontotArr.length(); i++) {
                        JSONObject jsonobject = mJsontotArr.getJSONObject(i);

                        Timer aTimer = new Timer();
                        aTimer.setDate(jsonobject.getString("DATE"));
                        aTimer.setStarttime(jsonobject.getString("starttime"));
                        aTimer.setTotal(jsonobject.getString("total"));

                        mTotTimerArr.add(aTimer);
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(TymeActivity.this,
                        "Oopss! Something went wrong.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            Database db = new Database(TymeActivity.this);

            if(mTimerArr.size()>0) {
                //if(db.getResultCount() < mTimerArr.size())
                //{
                db.removeReports();
                db.createResults(mTimerArr);
                // }
                updateViewpager();
            }
            else
            {
                Toast.makeText(getBaseContext(), "Unable to load data. ", Toast.LENGTH_SHORT)
                        .show();
            }
            exportDatabse("tyme.sqlite");

            //SimplePageFragment.newInstance(datePosition, dateFrg);
            }
        }

        @Override
        protected void onPreExecute() {
            mPrBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
        }
    }

    public String postResults(String url, String date) throws IOException {
        // RequestBody body = RequestBody.create(JSON, json);
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("date", ""+date)
                .add("email",""+email)
                .add("subdomain",""+subdomain);

        RequestBody formBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful())
            return response.body().string();
        else
            return null;
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private FragmentRefreshListener fragmentRefreshListener;

    public interface FragmentRefreshListener{
        void onRefresh();
    }
}

  /*  byte[] data = text.getBytes("UTF-8");
    String base64 = Base64.encodeToString(data, Base64.DEFAULT);

    // Receiving side
    byte[] data = Base64.decode(base64, Base64.DEFAULT);
    String text = new String(data, "UTF-8");*/