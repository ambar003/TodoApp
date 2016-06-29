package com.example.ambar.todoapp2;

/*
 * Created by ambar on 28/6/16.
 */
public class todoinfo {
    String task;
    String date;
    int done;

    todoinfo(String task, String date, int done) {
        this.task = task;
        this.date = date;
        this.done = done;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }
}
