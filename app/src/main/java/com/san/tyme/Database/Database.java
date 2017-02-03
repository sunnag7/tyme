package com.san.tyme.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.san.tyme.model.Project;
import com.san.tyme.model.Task;
import com.san.tyme.model.Timer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sanny on 10/17/2016.
 */

public class Database extends SQLiteOpenHelper {

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tyme.sqlite";


    //Table names
    private static final String TABLE_PROJECT = "Project";
    private static final String TABLE_TASKS = "task";
    private static final String TABLE_RESULTS = "results";

    private static final String KEY_ID = "id";

    private static final String PROJ_NAME  = "proj_name";
    private static final String PROJ_CLIENT = "client_name"; //
    private static final String PROJ_CODE = "proj_code";
    private static final String PROJ_STARTDATE = "start_date";
    private static final String PROJ_ENDDATE  = "end_date";
    private static final String PROJ_NOTES  = "notes";
    private static final String PROJ_BUDGET  = "budget_value";
    private static final String PROJ_ISARCHIVED  = "archived";
    private static final String PROJ_ISBILLABLE  = "project_billable";
    private static final String PROJ_SLACK_ID  = "slack_id";
    private static final String PROJ_TRELLO_ID  = "trelloboard_id";
    private static final String PROJ_TASK_LIST  = "taskList";
    private static final String PROJ_NET_ID  = "net_id";

    private static final String TASK_NAME  = "task_name";
    private static final String TASK_ID = "task_id"; //
    private static final String TASK_ISBILLABLE = "isBillable";

    private static final String RESULT_NET_ID  = "net_id";
    private static final String RESULT_EMAIL = "email"; //
    private static final String RESULT_CLIENT = "client";
    private static final String RESULT_PROJECT  = "project";
    private static final String RESULT_TASK = "task"; //
    private static final String RESULT_DATE = "date";
    private static final String RESULT_START  = "starttime";
    private static final String RESULT_STOP = "stoptime"; //
    private static final String RESULT_TOTAL_TIME= "total";
    private static final String RESULT_DESCP = "descp";
    private static final String RESULT_ISSYNCED = "isSynced";

    private static final String CREATE_TABLE_PROJECT= "CREATE TABLE IF NOT EXISTS "
            + TABLE_PROJECT
            + "("
            + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PROJ_NET_ID
            + " INTEGER,"
            + PROJ_NAME
            + " TEXT, "
            + PROJ_CLIENT
            + " TEXT, "
            + PROJ_CODE
            + " TEXT, "
            + PROJ_STARTDATE
            + " TEXT, "
            + PROJ_ENDDATE
            + " TEXT, "
            + PROJ_NOTES
            + " TEXT, "
            +PROJ_BUDGET
            + " TEXT, "
            +PROJ_ISBILLABLE
            + " TEXT, "
            +PROJ_SLACK_ID
            + " TEXT, "
            +PROJ_TRELLO_ID
            + " TEXT, "
            +PROJ_TASK_LIST
            + " TEXT, "
            + PROJ_ISARCHIVED
            + " TEXT"
            +")";

    private static final String CREATE_TABLE_TASK= "CREATE TABLE IF NOT EXISTS "
            + TABLE_TASKS
            + "("
            + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TASK_ID
            + " TEXT,"
            + TASK_NAME
            + " TEXT, "
            + TASK_ISBILLABLE
            + " INTEGER"
            +")";


    private static final String CREATE_TABLE_RESULTS= "CREATE TABLE IF NOT EXISTS "
            + TABLE_RESULTS
            + "("
            + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RESULT_NET_ID
            + " INTEGER,"
            + RESULT_EMAIL
            + " TEXT, "
            + RESULT_CLIENT
            + " TEXT, "
            + RESULT_PROJECT
            + " TEXT, "
            + RESULT_TASK
            + " TEXT, "
            + RESULT_DATE
            + " TEXT, "
            + RESULT_START
            + " TEXT, "
            +RESULT_STOP
            + " TEXT, "
            +RESULT_TOTAL_TIME
            + " TEXT, "
            +RESULT_DESCP
            + " TEXT, "
            +RESULT_ISSYNCED
            + " TEXT "
            +")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PROJECT);
        db.execSQL(CREATE_TABLE_TASK);
        db.execSQL(CREATE_TABLE_RESULTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public long createProject(ArrayList<Project> mProjArr)
    {
        long row_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for(int i = 0;i<mProjArr.size() ;i++) {
            values.put(PROJ_NET_ID, mProjArr.get(i).getId());
            values.put(PROJ_NAME, mProjArr.get(i).getProj_name());
            values.put(PROJ_BUDGET, mProjArr.get(i).getBudget_value());
            values.put(PROJ_CLIENT, mProjArr.get(i).getClient_name());
            values.put(PROJ_CODE, mProjArr.get(i).getProj_code());
            values.put(PROJ_ENDDATE, mProjArr.get(i).getEnd_date());
            values.put(PROJ_ISARCHIVED, mProjArr.get(i).getArchived());
            values.put(PROJ_ISBILLABLE, mProjArr.get(i).getProject_billable());
            values.put(PROJ_NOTES, mProjArr.get(i).getNotes());
            values.put(PROJ_SLACK_ID, mProjArr.get(i).getSlack_id());
            values.put(PROJ_STARTDATE, mProjArr.get(i).getStart_date());
            values.put(PROJ_TASK_LIST, mProjArr.get(i).getTaskList());
            values.put(PROJ_TRELLO_ID, mProjArr.get(i).getTrelloboard_id());

            row_id = db.insert(TABLE_PROJECT, null, values);
        }

        db.close();
        Log.d("Database.class", "Parent class object values are added to DB" + values);

        return row_id;
    }

    public long createResults(ArrayList<Timer> mTimArr)
    {
        long row_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for(int i = 0;i<mTimArr.size() ;i++) {

            values.put(RESULT_NET_ID, mTimArr.get(i).getId());
            values.put(RESULT_EMAIL, mTimArr.get(i).getEmail());
            values.put(RESULT_CLIENT, mTimArr.get(i).getClient());
            values.put(RESULT_PROJECT, mTimArr.get(i).getProject());
            values.put(RESULT_TASK, mTimArr.get(i).getTask());
            values.put(RESULT_DATE, mTimArr.get(i).getDate());
            values.put(RESULT_START, mTimArr.get(i).getStarttime());
            values.put(RESULT_STOP, mTimArr.get(i).getStoptime());
            values.put(RESULT_TOTAL_TIME, mTimArr.get(i).getTotal());
            values.put(RESULT_DESCP, mTimArr.get(i).getDescp());
            values.put(RESULT_ISSYNCED, 1);
           /* values.put(PROJ_TASK_LIST, mTimArr.get(i).getTaskList());
            values.put(PROJ_TRELLO_ID, mTimArr.get(i).getTrelloboard_id());*/

            row_id = db.insert(TABLE_RESULTS, null, values);
        }

        db.close();
        Log.d("Database.class", "Parent class object values are added to DB" + values);

        return row_id;
    }

    public long createResultSingle(Timer mTimer) {
        long row_id;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(RESULT_NET_ID, mTimer.getId());
        values.put(RESULT_EMAIL, mTimer.getEmail());
        values.put(RESULT_CLIENT, mTimer.getClient());
        values.put(RESULT_PROJECT, mTimer.getProject());
        values.put(RESULT_TASK, mTimer.getTask());
        values.put(RESULT_DATE, mTimer.getDate());
        values.put(RESULT_START, mTimer.getStarttime());
        values.put(RESULT_STOP, mTimer.getStoptime());
        values.put(RESULT_TOTAL_TIME, mTimer.getTotal());
        values.put(RESULT_DESCP, mTimer.getDescp());
        values.put(RESULT_ISSYNCED, 1);

        row_id = db.insert(TABLE_RESULTS, null, values);
        db.close();
        Log.d("Database.class", "Parent class object values are added to DB" + values);

        return row_id;
    }

    public long createTask(ArrayList<Task> mTaskArr) {
        long row_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for(int i = 0;i<mTaskArr.size() ;i++) {
            values.put(TASK_ID, mTaskArr.get(i).getTask_id());
            values.put(TASK_NAME, mTaskArr.get(i).getTask_name());
            values.put(TASK_ISBILLABLE, mTaskArr.get(i).getIsBillable());

            row_id = db.insert(TABLE_TASKS, null, values);
        }
        db.close();
        //Log.d("Database.class", "Parent class object values are added to DB" + values);

        return row_id;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> ar = new ArrayList<Task>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS;
        //Log.d("QUERY",""+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Task r = new Task();
                r.setTask_id(c.getString(c.getColumnIndex(TASK_ID)));
                r.setIsBillable( c.getInt(c.getColumnIndex(TASK_ISBILLABLE)));
                r.setTask_name(c.getString(c.getColumnIndex(TASK_NAME)));

                ar.add(r);

            } while (c.moveToNext());
        }
        db.close();

        return ar;
    }

    public ArrayList<Project> getClients(String clName) {

        ArrayList<Project> ar = new ArrayList<Project>();

        String selectQuery = "SELECT * FROM " + TABLE_PROJECT+" where "+PROJ_CLIENT+" = '"+clName.replace("'","")+"'";
        //Log.d("QUERY",""+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Project r = new Project();
                r.setClient_name(c.getString(c.getColumnIndex(PROJ_CLIENT)));
                r.setProj_name(c.getString(c.getColumnIndex(PROJ_NAME)));
                r.setId(c.getString(c.getColumnIndex(PROJ_NET_ID)));
                r.setStart_date(c.getString(c.getColumnIndex(PROJ_STARTDATE)));
                r.setProj_code(c.getString(c.getColumnIndex(PROJ_CODE)));
                r.setBudget_value(c.getString(c.getColumnIndex(PROJ_BUDGET)));
                r.setEnd_date(c.getString(c.getColumnIndex(PROJ_ENDDATE)));
                ar.add(r);

            } while (c.moveToNext());
        }
        db.close();

        return ar;
    }

    public ArrayList<String> getClientNames() {

        ArrayList<String> ar = new ArrayList<String>();

        String selectQuery = "SELECT "+PROJ_CLIENT+" FROM "
                +TABLE_PROJECT+" group by "+PROJ_CLIENT+" order by id DESC";
        //Log.d("QUERY",""+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                /*Project r = new Project();
                r.setClient_name(c.getString(c.getColumnIndex(PROJ_CLIENT)));
                r.setProj_name(c.getString(c.getColumnIndex(PROJ_NAME)));
                r.setId(c.getString(c.getColumnIndex(PROJ_NET_ID)));
                r.setStart_date(c.getString(c.getColumnIndex(PROJ_STARTDATE)));
                r.setProj_code(c.getString(c.getColumnIndex(PROJ_CODE)));
                r.setBudget_value(c.getString(c.getColumnIndex(PROJ_BUDGET)));
                r.setEnd_date(c.getString(c.getColumnIndex(PROJ_ENDDATE)));*/
                ar.add(c.getString(c.getColumnIndex(PROJ_CLIENT)));

            } while (c.moveToNext());
        }
        db.close();

        return ar;
    }

    public long getdaysTotal(String date_req) {

        long tot = 0;
        String selectQuery = "SELECT date, SUM(total) AS total FROM "+TABLE_RESULTS+" WHERE date = '"+date_req+"' GROUP BY date";
        //Log.d("QUERY",""+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                tot = Long.parseLong(c.getString(c.getColumnIndex(RESULT_TOTAL_TIME)));
            } while (c.moveToNext());
        }
        db.close();

        return tot;
    }

    public double getdaysRunningTym(String date_req) {

        double tot = 0;
        String selectQuery = "SELECT "+RESULT_START+" FROM "+TABLE_RESULTS+" WHERE date = '"+date_req+"' AND "+RESULT_START+ "!= '0'";
        //Log.d("QUERY",""+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                tot = Double.parseDouble(c.getString(c.getColumnIndex(RESULT_START) ));

            } while (c.moveToNext());
        }
        db.close();

        return tot;
    }

    public int getResultCount() {
        String countQuery = "SELECT * FROM " + TABLE_RESULTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();

        return cnt;
    }

    public int getCount() {
        String countQuery = "SELECT * FROM " + TABLE_PROJECT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public ArrayList<Timer> getResults(String date) {

        ArrayList<Timer> ar = new ArrayList<Timer>();
        String selectQuery = "SELECT * FROM " + TABLE_RESULTS+" where "+RESULT_DATE+" = '"+date+"'";
        //Log.d("QUERY",""+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Timer r = new Timer();
                r.setId(c.getInt(c.getColumnIndex(RESULT_NET_ID)));
                r.setDate(c.getString(c.getColumnIndex(RESULT_DATE)));
                r.setEmail(c.getString(c.getColumnIndex(RESULT_EMAIL)));
                r.setClient(c.getString(c.getColumnIndex(RESULT_CLIENT)));
                r.setStarttime(c.getString(c.getColumnIndex(RESULT_START)));
                r.setDescp(c.getString(c.getColumnIndex(RESULT_DESCP)));
                r.setStoptime(c.getString(c.getColumnIndex(RESULT_STOP)));
                r.setProject(c.getString(c.getColumnIndex(RESULT_PROJECT)));
                r.setTask(c.getString(c.getColumnIndex(RESULT_TASK)));
                r.setTotal(c.getString(c.getColumnIndex(RESULT_TOTAL_TIME)));
                r.setIsSynced(c.getInt(c.getColumnIndex(RESULT_ISSYNCED)));
                ar.add(r);

            } while (c.moveToNext());
        }
        db.close();

        return ar;
    }


    public Timer getResult(int id) {
        Timer r = new Timer();

        String selectQuery = "SELECT * FROM " + TABLE_RESULTS+" where "+RESULT_NET_ID+" = "+id;
        //Log.d("QUERY",""+selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {

                r.setId(c.getInt(c.getColumnIndex(RESULT_NET_ID)));
                r.setDate(c.getString(c.getColumnIndex(RESULT_DATE)));
                r.setEmail(c.getString(c.getColumnIndex(RESULT_EMAIL)));
                r.setClient(c.getString(c.getColumnIndex(RESULT_CLIENT)));
                r.setStarttime(c.getString(c.getColumnIndex(RESULT_START)));
                r.setDescp(c.getString(c.getColumnIndex(RESULT_DESCP)));
                r.setStoptime(c.getString(c.getColumnIndex(RESULT_STOP)));
                r.setProject(c.getString(c.getColumnIndex(RESULT_PROJECT)));
                r.setTask(c.getString(c.getColumnIndex(RESULT_TASK)));
                r.setTotal(c.getString(c.getColumnIndex(RESULT_TOTAL_TIME)));
                r.setIsSynced(c.getInt(c.getColumnIndex(RESULT_ISSYNCED)));

            } while (c.moveToNext());
        }
        db.close();

        return r;
    }

    public void deleteMedia() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESULTS, null,null );

        System.out.println(" deleted at : ");
        db.close();
    }

    public int updateTime(int rID, String total,String startTime) {

        SQLiteDatabase db = this.getWritableDatabase();
        //System.out.println("R_ISSYNC "+rID);
        ContentValues values = new ContentValues();

        if(!total.equals("0"))
            values.put(RESULT_TOTAL_TIME, total);

        values.put(RESULT_STOP, "0");
        values.put(RESULT_START, ""+startTime);
        //values.put(R_ID, aRepID);
        //updating row

        int update = db.update(TABLE_RESULTS, values, RESULT_NET_ID + " = ?",
                new String[] { String.valueOf(rID)});

        db.close();
        return update;
    }

    public int updateTimerWhole(int rID, Timer mTimer) {

        SQLiteDatabase db = this.getWritableDatabase();
        //System.out.println("R_ISSYNC "+rID);
        ContentValues values = new ContentValues();

        values.put(RESULT_CLIENT, mTimer.getClient());
        values.put(RESULT_PROJECT, mTimer.getProject());
        values.put(RESULT_TASK, mTimer.getTask());
       // values.put(RESULT_DATE, mTimer.getDate());
        values.put(RESULT_DESCP, mTimer.getDescp());
        values.put(RESULT_ISSYNCED, 1);
        //values.put(R_ID, aRepID);
        //updating row

        int update = db.update(TABLE_RESULTS, values, RESULT_NET_ID + " = ?",
                new String[] { String.valueOf(rID)});

        db.close();
        return update;
    }

    public void removeAll()
    {
        SQLiteDatabase dbs = this.getWritableDatabase();
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        dbs.delete(TABLE_RESULTS, null, null);
        dbs.delete(TABLE_TASKS , null, null);
        dbs.delete(TABLE_PROJECT , null, null);

        dbs.close();
    }

    public long removeReports(){
        SQLiteDatabase dbs = this.getWritableDatabase();
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        long isRemvd = dbs.delete(TABLE_RESULTS, null, null);
        System.out.println(" deleted reports: ");
        dbs.close();

        return isRemvd;
    }

    public void deleteUser(String netID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try  {
            db.delete(TABLE_RESULTS, RESULT_NET_ID +" = ?", new String[] { netID });
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally  {
            db.close();
        }
    }
  //  "SELECT client FROM projects group by client order by id DESC";
}