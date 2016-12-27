package com.san.tyme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.san.tyme.Database.Database;
import com.san.tyme.R;
import com.san.tyme.TymeActivity;
import com.san.tyme.model.Timer;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Details extends AppCompatActivity {
    TextView textPro, textDesc, textTask, textDate, textClient, textMsg, textTot;
    Timer aTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("View Task");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                //do something you want
            }
        });

        Bundle extras = getIntent().getExtras();
        int id =0, kType = 0;

        if (extras != null) {
            id = extras.getInt("id",0);
            kType = extras.getInt("type",0);//
        }

        initViews();
        getDataVal(id);

        if(kType==1){
            textMsg.setText("Task timer stopped.");
            SimpleDateFormat df = new SimpleDateFormat("hh:mm"); // HH for 0-23
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            //String time = df.format(d);
            float diffInMillies = Float.valueOf(aTime.getTotal()).floatValue();
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

                textTot.setVisibility(View.VISIBLE);
                textTot.setText(" " + h + ":" + min);
            }
        }
        else if(kType== 2){
            textMsg.setText("Task timer started.");
        }
        else if(kType== 3){
            textMsg.setText("Task timer started.");
        }
        else if(kType== 4){

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed() ;
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    private void getDataVal(int id) {
        Database db = new Database(Details.this);
        String text = "";
        aTime = db.getResult(id);
        //SpannableString content = new SpannableString("Project: "+aTime.getProject().toUpperCase());
        //content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textPro.setText(aTime.getProject().toUpperCase());

        textClient.setText("Client: "+aTime.getClient());
        byte[] data = Base64.decode(""+aTime.getDescp().toString(), Base64.DEFAULT);
        try {
            text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        textDesc.setText("Description:\n"+text);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String start_dt = ""+aTime.getDate().toString();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = (Date) sdf.parse(start_dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //SimpleDateFormat newFormat = new SimpleDateFormat("MM-dd-yyyy");
        String finalString = formatter.format(date);

        textDate.setText(" "+finalString);
        textTask.setText("Task: "+aTime.getTask());
    }

    private void initViews() {
        textPro = (TextView) findViewById(R.id.textView16);
        textClient = (TextView) findViewById(R.id.textView15);
        textDate = (TextView) findViewById(R.id.textView17);
        textDesc = (TextView) findViewById(R.id.textView11);
        textTask = (TextView) findViewById(R.id.textView14);
        textMsg = (TextView) findViewById(R.id.textView20);
        textTot = (TextView) findViewById(R.id.textView21);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TymeActivity.class);
        intent.putExtra("dateRec",aTime.getDate());
        startActivity(intent);
        this.finish();
    }
}
