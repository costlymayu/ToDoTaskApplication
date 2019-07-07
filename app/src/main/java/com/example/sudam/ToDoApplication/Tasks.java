package com.example.sudam.ToDoApplication;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Sudam Chole on 22/02/19.
 */

@IgnoreExtraProperties
public class Tasks {

    public String taskTittle;
    public String taskDesc;
    public String taskDate;
    public String taskTime;

    // Default constructor required for calls to
    // DataSnapshot.getValue(Tasks.class)
    public Tasks() {

    }
    public Tasks(String taskTittle, String taskDesc, String taskDate, String taskTime) {
        this.taskTittle = taskTittle;
        this.taskDesc = taskDesc;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
    }
}
