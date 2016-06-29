package com.example.ambar.todoapp2;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class todos extends AppCompatActivity {
    EditText addtask;
    ImageView add;
    static String task, username, date, seturl, completeurl, getallurl;
    static ListView todos;
    static int HttpResult, id;
    static JSONObject jsonParam, completedjson;
    static JSONArray jsonArray, completejsonarray;
    addTask mytask;
    getAllTodo mytask1;
    static ArrayList<todoinfo> str, completelist;
    static StringBuilder sb;
    customAdapter customlist;
    ExpandableListView completetodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos);
        init();
        setCompletedtodos();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });
    }

    private void createTask() {
        task = addtask.getText().toString();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        date = df.format(c.getTime());
        id = sharedpreferences.getId(getApplicationContext());
        System.out.println("id"+" "+id);
        jsonParam = new JSONObject();
        customlist = new customAdapter(getApplicationContext(), R.layout.listview, str);
        try {
            jsonParam.put("taskId", id);
            jsonParam.put("task", task);
            jsonParam.put("username", username);
            jsonParam.put("date", date);
            jsonParam.put("done", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!task.equals(""))
            str.add(0, new todoinfo(task, date, 0));
        todos.requestLayout();
        customlist.notifyDataSetChanged();
        addtask.setText("");
        mytask = new addTask();
        mytask.execute();
    }

    private void init() {
        sb = new StringBuilder();
        addtask = (EditText) findViewById(R.id.addtask);
        add = (ImageView) findViewById(R.id.addbutton);
        todos = (ListView) findViewById(R.id.todoitem);
        completetodos = (ExpandableListView) findViewById(R.id.completetodos);
        Bundle b = getIntent().getExtras();
        task = "";
        username = b.get("username").toString();
        jsonParam = new JSONObject();
        try {
            jsonParam.put("username", username);
            jsonParam.put("done", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mytask1 = new getAllTodo();
        mytask1.execute();
    }

    private void setCompletedtodos() {
        completedjson = new JSONObject();
        try {
            completedjson.put("username", username);
            completedjson.put("done", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        completeTodo com = new completeTodo();
        com.execute();
    }

    class addTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (task.equals("")) {
                HttpResult = 1000;
            } else {
                URL url;
                HttpURLConnection urlConnection;
                seturl = "https://todoambar.herokuapp.com/addtodo";
                try {
                    url = new URL(seturl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setUseCaches(false);
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.connect();
                    OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                    out.write(jsonParam.toString());
                    out.close();
                    HttpResult = urlConnection.getResponseCode();
                    if (HttpResult != 300) {
                        System.out.println("urlConnectionResponse"+" "+urlConnection.getResponseMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (HttpResult == 300) {
                toast("Added Successfully!!!");
                sharedpreferences.updatedid(getApplicationContext());
            } else if (HttpResult == 1000) {
                new AlertDialog.Builder(todos.this).setTitle("Alert").setMessage("Nothing added").show();
            } else {
                toast("Some error occured. try agin!!!");
            }
        }
    }

    class getAllTodo extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            URL url;
            HttpURLConnection urlConnection;
            getallurl = "https://todoambar.herokuapp.com/gettodo";
            try {
                url = new URL(getallurl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(jsonParam.toString());
                out.close();
                HttpResult = urlConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    jsonArray = new JSONArray(sb.toString());
                } else {
                    System.out.println(urlConnection.getResponseMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (jsonArray != null) {
                int len = jsonArray.length();
                str = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    try {
                        JSONObject a = jsonArray.getJSONObject(i);
                        String t = a.getString("task");
                        String da = a.getString("date");
                        int don = a.getInt("done");
                        str.add(new todoinfo(t, da, don));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                customlist = new customAdapter(getApplicationContext(), R.layout.listview, str);
                todos.setAdapter(customlist);
                todos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        toast("Hi");
                    }
                });
            } else {
                toast("Error connecting to server...");
            }
        }
    }

    class completeTodo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            completeurl = "https://todoambar.herokuapp.com/completetodo";
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(completeurl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(completedjson.toString());
                out.close();
                sb = new StringBuilder();
                HttpResult = urlConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    completejsonarray = new JSONArray(sb.toString());
                } else {
                    System.out.println(urlConnection.getResponseMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            completelist = new ArrayList<>();
            if (completejsonarray != null) {
                int len = completejsonarray.length();
                for (int i = 0; i < len; i++) {
                    try {
                        JSONObject a = completejsonarray.getJSONObject(i);
                        String t = a.getString("task");
                        String da = a.getString("date");
                        int don = a.getInt("done");
                        completelist.add(new todoinfo(t, da, don));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            group gru = new group();
            gru.setItems(completelist);
            ArrayList<group> arr = new ArrayList<>();
            arr.add(gru);
            expandListAdapter expadapter = new expandListAdapter(getApplicationContext(), arr);
            completetodos.setAdapter(expadapter);
        }
    }

    public void toast(String text) {
        Toast.makeText(todos.this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

    }
}
