package com.example.ambar.todoapp2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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

public class Home extends Fragment {
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
    ProgressDialog dialog;
    Handler handler;
    View view;
    todos todo;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            handler = new Handler();
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Getting todos...");
            dialog.show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 5000);
            todo = new todos();
            init(view);
            setCompletedtodos();
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createTask();
                }
            });
        }
        return view;
    }

    private void init(View view) {
        sb = new StringBuilder();
        addtask = (EditText) view.findViewById(R.id.addtask);
        add = (ImageView) view.findViewById(R.id.addbutton);
        todos = (ListView) view.findViewById(R.id.todoitem);
        completetodos = (ExpandableListView) view.findViewById(R.id.completetodos);
        username = todo.username;
        task = "";
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

    private void createTask() {
        task = addtask.getText().toString();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        date = df.format(c.getTime());
        id = sharedpreferences.getId(getContext());
        System.out.println("id" + " " + id);
        jsonParam = new JSONObject();
        customlist = new customAdapter(getContext(), R.layout.listview, str);
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
            str.add(0, new todoinfo(task, date, 0, id));
//        todos.requestLayout();
        customlist.notifyDataSetChanged();
        addtask.setText("");
        mytask = new addTask();
        mytask.execute();
    }

    class addTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            createDialog();
        }

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
                        System.out.println("urlConnectionResponse" + " " + urlConnection.getResponseMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissDialog();
            if (HttpResult == 300) {
                toast("Added Successfully!!!");
                sharedpreferences.updatedid(getContext());
            } else if (HttpResult == 1000) {
                new AlertDialog.Builder(getContext()).setTitle("Alert").setMessage("Nothing added").show();
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
                        int id = a.getString("taskid").charAt(0) - '0';
                        str.add(new todoinfo(t, da, don, id));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                customlist = new customAdapter(getContext(), R.layout.listview, str);
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
                        int id = a.getString("taskid").charAt(0) - '0';
                        completelist.add(new todoinfo(t, da, don, id));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            group gru = new group();
            gru.setItems(completelist);
            ArrayList<group> arr = new ArrayList<>();
            arr.add(gru);
            expandListAdapter expadapter = new expandListAdapter(getContext(), arr);
            completetodos.setAdapter(expadapter);
        }
    }

    public void toast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    void createDialog() {
        dialog = new ProgressDialog(getContext());
        System.out.println("new todos");
        dialog.setMessage("Updating...");
        dialog.show();
    }

    void dismissDialog() {
        dialog.dismiss();
    }
}
