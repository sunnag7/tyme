package com.san.tyme;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    public static TextView meditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tyme);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(Html.fromHtml("<font face='verdana' color='#ffffff'><b>tyme</b></font>" +
                "<font color='#A4D377'>.</b></font><font face='verdana' color='#ffffff'><b>co</b></font>"));

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
        meditText = (TextView)findViewById(R.id.textView24);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerExpanded();
            }
        });*/

        pager = (ViewPager) findViewById(R.id.pager);
        dateList = (DateRecyclerView) findViewById(R.id.date_list);
        snapHelper.attachToRecyclerView(dateList);

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
            if(recievedDate.equals("")) {
                defaultDate = PagerDatePickerDateFormat.DATE_PICKER_DD_MM_YYYY_FORMAT.parse(currentDateandTime);
            }
            else {
               // DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
              //  Date date = null;
                try {
                    DateFormat originalFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

                    Date date = originalFormat.parse(recievedDate);
                    formattedDate = sdf1.format(date);

                    //date = (Date) formatter.parse(recievedDate);
                } catch (ParseException e) {
                    e.printStackTrace();;
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
                //showTimerChanges(currentDate);
                //invalidateOptionsMenu();
                return SimplePageFragment.newInstance(position, date);
            }
        };

        pager.setAdapter(fragmentAdapter);
        dateList.setPager(pager);

        /*dateList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                DateItem dateItem = dateList.getDateAdapter().getItem(datePosition);

                int day = dateItem.getDate().getDay();
                if(day== 0 ){
                    if(datePosition>6)
                        dateList.scrollToPosition( datePosition-6);
                    else
                        dateList.scrollToPosition(1);
                }

                if (day== 1){
                    dateList.scrollToPosition( datePosition+6);
                }

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });*/

        //dateList.setOnFocusChangeListener()

        dateList.setDatePickerListener(new DateRecyclerView.DatePickerListener() {
            @Override
            public void onDatePickerItemClick(DateItem dateItem, int position) {
               /* Toast.makeText(getBaseContext(), sdf.format(dateItem.getDate())
                        + " "+TymeApplication.reportDate, Toast.LENGTH_SHORT)
                        .show();*/
               // showTimerChanges( sdf.format(dateItem.getDate()));
               //  User clicked date item from top date picker
                int day = dateItem.getDate().getDay();
                if (day == 1){
                    if(position<dateList.getDateAdapter().getItemCount()-6)
                        dateList.scrollToPosition( position+7);
                }

                if (day == 2){
                    if(position<dateList.getDateAdapter().getItemCount()-6)
                        dateList.scrollToPosition( position+6);
                }

                if (day == 3){
                    if(position<dateList.getDateAdapter().getItemCount()-6)
                        dateList.scrollToPosition( position+5);
                }

                if (day == 4){
                    if(position<dateList.getDateAdapter().getItemCount()-6)
                        dateList.scrollToPosition( position+4);
                }

                if (day ==5){
                    if(position<dateList.getDateAdapter().getItemCount()-6)
                        dateList.scrollToPosition( position+3);
                }
                if (day ==6){
                    if(position<dateList.getDateAdapter().getItemCount()-6)
                        dateList.scrollToPosition( position+2);
                }
                if (day ==0){
                    if(position<dateList.getDateAdapter().getItemCount()-6)
                        dateList.scrollToPosition( position+1);
                }

            }

            @Override
            public void onDatePickerPageSelected(int position) {
                // User changed date using swipe (left/right)
                // showTimerChanges(currentDate);
                // smoothScrollToPosition(position);
                dateList.getDateAdapter().setSelectedDate(position);
                DateItem dateItem = dateList.getDateAdapter().getItem(position);

                int day = dateItem.getDate().getDay();
                if(day== 0 ){
                    if(position>6)
                        dateList.scrollToPosition(position-6);
                    else
                        dateList.scrollToPosition(0);
                }

                if (day == 1){
                    if(position<dateList.getDateAdapter().getItemCount()-6)
                        dateList.scrollToPosition( position+7);
                }
            }

            @Override
            public void onDatePickerPageStateChanged(int state) {
                //User changed page
            }

            @Override
            public void onDatePickerPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //User changed page
                final DateItem dateItem = dateList.getDateAdapter().getItem(position);

                Database db = new Database(TymeActivity.this);
                final double totrunning = db.getdaysRunningTym(sdf.format(dateItem.getDate()));

               /*  ScheduledExecutorService scheduler =
                        Executors.newSingleThreadScheduledExecutor();

                scheduler.scheduleAtFixedRate
                        (new Runnable() {
                            public void run() {*/
                                showTimerChanges(sdf.format(dateItem.getDate()),totrunning);
                           /* }
                        }, 0, 1, TimeUnit.MINUTES);*/
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

                        currentDate = sdf.format(date);
                        datePosition = position;
                        dateFrg = date;
                        //showTimerChanges();
                        //updateMenuTitles(currentDate);
                        //invalidateOptionsMenu();
                        // dateList.getDateAdapter().setSelectedDate(datePosition);
                        return SimplePageFragment.newInstance(position, date);
                    }
                };

                pager.setAdapter(fragmentAdapter);
                dateList.setPager(pager);
                pager.getAdapter().notifyDataSetChanged();
             //   dateList.scrollToPosition(datePosition+7);
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
        exportDatabase("tyme.sqlite");
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

    public void showTimerChanges(String currentDateandTime, double totrunning) {
        Database db = new Database(TymeActivity.this);
        long tot = db.getdaysTotal(currentDateandTime);
        if(tot!=0) {
            String min,hr;int m = 0;
            if (totrunning!=0) {
                 m = Math.round(tot+getTimeUpdter(totrunning)) / 60;
            }
            else{
                m = Math.round(tot) / 60;
            }
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

               // min = "" + m;
            }

            meditText.setText("Total Hours\n" + hr + ":" +min+" hrs");
            //((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Total- " + h + ":" + min);
        }
        else if(tot==0 && totrunning>0 ){
            String min,hr;int m = 0;
            if (totrunning!=0) {
                m = Math.round(getTimeUpdter(totrunning)) / 60;
            }
            else{
                m = Math.round(tot) / 60;
            }
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

                // min = "" + m;
            }
            meditText.setText("Total Hours\n" + hr + ":" +min+" hrs");
        }
        else{
            meditText.setText("Total Hours\n00:00 hrs");
            // ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Total- 00:00");
        }
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

    public float getTimeUpdter(double timeUpd){
        float uptime = System.currentTimeMillis()/1000.0f;
        float diffInMillies;
        diffInMillies = /*Float.valueOf(totTime) + */(uptime - (float) timeUpd);
      return   diffInMillies;
    }

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
            mClient.setClient(groupList.get(i));
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

    /*@Override
    public void UpdateMyText(String mystr) {
        meditText.setText(mystr);
    }*/

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
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
                if (response.isSuccessful()) {
                    try {
                        mIcon11 = BitmapFactory.decodeStream(response.body().byteStream());
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                bmImage.setImageBitmap(getRoundedCornerBitmap(result,100));
            } catch (Exception e) {
                bmImage.setImageBitmap(result);
            }
            // bmImage.setImageBitmap(result);
        }
    }

    public void exportDatabase(String databaseName) {
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
        } catch (Exception ignored) {

        }
    }

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
            else {
                Toast.makeText(getBaseContext(), "Unable to load data. ", Toast.LENGTH_SHORT)
                        .show();
            }
            exportDatabase("tyme.sqlite");

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



        public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, (float) pixels, (float) pixels, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }

    LinearSnapHelper snapHelper = new LinearSnapHelper() {
        @Override
        public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
            View centerView = findSnapView(layoutManager);
            if (centerView == null) {
                return RecyclerView.NO_POSITION;
            }

            int position = layoutManager.getPosition(centerView);
            int targetPosition = -1;
            if (layoutManager.canScrollHorizontally()) {
                if (velocityX < 0) {
                    targetPosition = position - 1;
                } else {
                    targetPosition = position + 1;
                }
            }

            if (layoutManager.canScrollVertically()) {
                if (velocityY < 0) {
                    targetPosition = position - 1;
                } else {
                    targetPosition = position + 1;
                }
            }

            final int firstItem = 0;
            final int lastItem = layoutManager.getItemCount() - 1;
            targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
            return targetPosition;
        }
    };


    public TextView getTextView() {
       // TextView txtView = (TextView)findViewById(R.id.textView24);
        return meditText;
    }
}

  /*  byte[] data = text.getBytes("UTF-8");
    String base64 = Base64.encodeToString(data, Base64.DEFAULT);

    // Receiving side
    byte[] data = Base64.decode(base64, Base64.DEFAULT);
    String text = new String(data, "UTF-8");*/