package com.san.tyme.model;

import java.util.ArrayList;

/**
 * Created by Sanny on 10/17/2016.
 */

public class Client {

    public String client= "";

    public ArrayList<Project> getmClProjArr() {
        return mClProjArr;
    }

    public void setmClProjArr(ArrayList<Project> mClProjArr) {
        this.mClProjArr = mClProjArr;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    private ArrayList<Project> mClProjArr;
}
