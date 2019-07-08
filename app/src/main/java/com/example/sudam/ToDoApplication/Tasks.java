package com.example.sudam.ToDoApplication;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Sudam Chole on 7/05/19.
 */
@IgnoreExtraProperties
public class Tasks {

    public String taskTittle;
    public String taskDesc;
    public String taskDate;
    public String taskTime;
    public String taskComment;

    // Default constructor required for calls to
    // DataSnapshot.getValue(Tasks.class)
    public Tasks() {

    }
    public Tasks(String taskTittle, String taskDesc, String taskDate, String taskTime,String taskComment) {
        this.taskTittle = taskTittle;
        this.taskDesc = taskDesc;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.taskComment=taskComment;
    }
}
