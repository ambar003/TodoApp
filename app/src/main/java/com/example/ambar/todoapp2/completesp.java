package com.example.ambar.todoapp2;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by ambar on 27/6/16.
 */
public class completesp {
    static final String prefs_name = "complete_sp";
    static final String completetodo = "complete_todo";
    static ArrayList<todoinfo> completelist;

    completesp(){
        super();
    }

    static void storeincomplete(Context context, ArrayList<todoinfo> incompletelist){
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsoncompletetodo = gson.toJson(incompletelist);
        editor.putString(completetodo, jsoncompletetodo);
        editor.apply();
    }

    static ArrayList<todoinfo> loadincomplete(Context context){
        SharedPreferences settings;
        settings = context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE);
        if(settings.contains(completetodo)){
            String jsoncompletetodo = settings.getString(completetodo,null);
            Gson gson = new Gson();
            Type type = new TypeToken<List<todoinfo>>(){}.getType();
            List<todoinfo> completetodos = gson.fromJson(jsoncompletetodo,type);
            assert completetodos!=null;
            completelist = new ArrayList<>(completetodos);
        }
        else{
            return null;
        }
        return completelist;
    }

    static void addtodo(Context context,todoinfo todo){
        completelist = loadincomplete(context);
        if(completelist!=null){
            completelist.add(0,todo);
            storeincomplete(context,completelist);
        }
    }

    static void removetodo(Context context,int index){
        completelist = loadincomplete(context);
        if(completelist!=null){
            completelist.remove(index);
            storeincomplete(context,completelist);
        }
        storeincomplete(context,completelist);
    }
}
