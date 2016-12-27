package com.san.tyme.model;

/**
 * Created by Sanny on 10/15/2016.
 */

public class Project {
    public String id = "";
    public String proj_name = "";
    public String client_name = "";
    public String proj_code = "";
    public String start_date = "";
    public String end_date = "";
    public String notes = "";
    public String budget_value = "";
    public String archived = "";
    public String project_billable = "";
    public String slack_id = "";
    public String trelloboard_id = "";
    public String taskList = "";

    public String getTaskList() {
        return taskList;
    }

    public void setTaskList(String taskList) {
        this.taskList = taskList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProj_name() {
        return proj_name;
    }

    public void setProj_name(String proj_name) {
        this.proj_name = proj_name;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getProj_code() {
        return proj_code;
    }

    public void setProj_code(String proj_code) {
        this.proj_code = proj_code;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getBudget_value() {
        return budget_value;
    }

    public void setBudget_value(String budget_value) {
        this.budget_value = budget_value;
    }

    public String getArchived() {
        return archived;
    }

    public void setArchived(String archived) {
        this.archived = archived;
    }

    public String getProject_billable() {
        return project_billable;
    }

    public void setProject_billable(String project_billable) {
        this.project_billable = project_billable;
    }

    public String getSlack_id() {
        return slack_id;
    }

    public void setSlack_id(String slack_id) {
        this.slack_id = slack_id;
    }

    public String getTrelloboard_id() {
        return trelloboard_id;
    }

    public void setTrelloboard_id(String trelloboard_id) {
        this.trelloboard_id = trelloboard_id;
    }



}
