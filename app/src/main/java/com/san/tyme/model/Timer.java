package com.san.tyme.model;

/**
 * Created by Sanny on 10/19/2016.
 */

 public class Timer {

    private int prmKey = 0;
    private int id = 0;
    private String email = "";
    private String client = "";
    private String project = "";
    private String task = "";
    private String date = "";
    private String starttime = "";
    private String stoptime = "";
    private String total = "";
    private String descp = "";
    private int isSynced = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The client
     */
    public String getClient() {
        return client;
    }

    /**
     *
     * @param client
     * The client
     */
    public void setClient(String client) {
        this.client = client;
    }


    public int getPrmKey() {
        return prmKey;
    }

    public void setPrmKey(int prmKey) {
        this.prmKey = prmKey;
    }

    /**
     *
     * @return
     * The project
     */
    public String getProject() {
        return project;
    }

    /**
     *
     * @param project
     * The project
     */
    public void setProject(String project) {
        this.project = project;
    }

    /**
     *
     * @return
     * The task
     */
    public String getTask() {
        return task;
    }

    /**
     *
     * @param task
     * The task
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     *
     * @return
     * The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return
     * The starttime
     */
    public String getStarttime() {
        return starttime;
    }

    /**
     *
     * @param starttime
     * The starttime
     */
    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    /**
     *
     * @return
     * The stoptime
     */
    public String getStoptime() {
        return stoptime;
    }

    /**
     *
     * @param stoptime
     * The stoptime
     */
    public void setStoptime(String stoptime) {
        this.stoptime = stoptime;
    }

    /**
     *
     * @return
     * The total
     */
    public String getTotal() {
        return total;
    }

    /**
     *
     * @param total
     * The total
     */
    public void setTotal(String total) {
        this.total = total;
    }

    /**
     *
     * @return
     * The descp
     */
    public String getDescp() {
        return descp;
    }

    /**
     *
     * @param descp
     * The descp
     */
    public void setDescp(String descp) {
        this.descp = descp;
    }

    public int getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(int isSynced) {
        this.isSynced = isSynced;
    }

}