package com.example.ambar.todoapp2;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * Created by ambar on 22/6/16.
 */
public class sharedpreferences {
    static final String todo_id = "id";
    static final String todo_str = "todoid";
    static int id;

    sharedpreferences() {

    }

    public void storeid(Context context, int id) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(todo_id, Context.CONTEXT_IGNORE_SECURITY);
        editor = sharedPreferences.edit();
        editor.putInt(todo_str, id);
        editor.apply();
    }

    static int getId(Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(todo_id, Context.CONTEXT_IGNORE_SECURITY);
        id = sharedPreferences.getInt(todo_str, 0);
        return id;
    }

    static void updatedid(Context context) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(todo_id, Context.CONTEXT_IGNORE_SECURITY);
        editor = sharedPreferences.edit();
        id = sharedPreferences.getInt(todo_str, 0) + 1;
        editor.putInt(todo_str, id);
        editor.apply();
    }
}
