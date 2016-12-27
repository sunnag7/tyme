package com.san.tyme.model;

/**
 * Created by Sanny on 10/17/2016.
 */

public class Task {

    public String task_name = "";
    public String task_id = "";
    public int isBillable = 0;

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public int getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(int isBillable) {
        this.isBillable = isBillable;
    }

}
