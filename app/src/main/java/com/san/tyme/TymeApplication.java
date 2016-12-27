package com.san.tyme;

/**
 * Created by Sanny on 10/24/2016.
 */

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Application;

@SuppressWarnings("serial")
public class TymeApplication extends Application implements Serializable{

    //public static ArrayList<Details> mReportArr = null;

    public static String reportDate = "";
    public static String reportPath = "";

    public static int isSynced = 0;
    public static String reportAddressCountry = "";
}