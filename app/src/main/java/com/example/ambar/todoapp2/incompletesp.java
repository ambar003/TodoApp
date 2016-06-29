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
public class incompletesp {
    static final String prefs_name = "incomplete_sp";
    static final String incompletetodo = "incomplete_todo";
    static ArrayList<todoinfo>  incompletelist;

    incompletesp(){
       super();
    }

    static void storeincomplete(Context context,ArrayList<todoinfo> incompletelist){
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonincompletetodo = gson.toJson(incompletelist);
        editor.putString(incompletetodo, jsonincompletetodo);
        editor.apply();
    }

    static ArrayList<todoinfo> loadincomplete(Context context){
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE);
        if(settings.contains(incompletetodo)){
             String jsonincompletetodo = settings.getString(incompletetodo,null);
             Gson gson = new Gson();
             Type type = new TypeToken<List<todoinfo>>(){}.getType();
             List<todoinfo> incompletetodos = gson.fromJson(jsonincompletetodo,type);
             assert incompletetodos!=null;
             incompletelist = new ArrayList<>(incompletetodos);
        }
        else{
            return null;
        }
        return incompletelist;
    }

    static void addtodo(Context context,todoinfo todo){
          incompletelist = loadincomplete(context);
          if(incompletelist!=null){
              incompletelist.add(0,todo);
              storeincomplete(context,incompletelist);
          }
    }

    static void removetodo(Context context,int index){
        incompletelist = loadincomplete(context);
        if(incompletelist!=null){
            incompletelist.remove(index);
            storeincomplete(context,incompletelist);
        }
        storeincomplete(context,incompletelist);
    }
}
