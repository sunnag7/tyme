package com.san.tyme;

/**
 * Created by Sanny on 10/24/2016.
 */

import java.io.Serializable;

import android.app.Application;

import com.san.tyme.utils.TypefaceUtil;

@SuppressWarnings("serial")
public class TymeApplication extends Application implements Serializable{

    //public static ArrayList<Details> mReportArr = null;

    public static String reportDate = "";

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "OpenSans-Regular.ttf");
    }
}