package com.example.ambar.todoapp2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

public class todos extends AppCompatActivity {
    EditText addtask;
    ImageView add;
    static String task, username, date, seturl, completeurl, getallurl;
    static ListView todos;
    static int HttpResult, id;
    static JSONObject jsonParam, completedjson;
    static JSONArray jsonArray, completejsonarray;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    addTask mytask;
    getAllTodo mytask1;
    static ArrayList<todoinfo> str, completelist;
    static StringBuilder sb;
    customAdapter customlist;
    ExpandableListView completetodos;
    ProgressDialog dialog;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        handler = new Handler();
        dialog = new ProgressDialog(todos.this);
        dialog.setMessage("Getting todos...");
        dialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 3000);
        System.out.println("out of handler");
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
        System.out.println("id" + " " + id);
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
            str.add(0, new todoinfo(task, date, 0, id));
//        todos.requestLayout();
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
                        int id = a.getString("taskid").charAt(0) - '0';
                        str.add(new todoinfo(t, da, don, id));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                customlist = new customAdapter(todos.this, R.layout.listview, str);
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
            expandListAdapter expadapter = new expandListAdapter(todos.this, arr);
            completetodos.setAdapter(expadapter);
        }
    }

    public void toast(String text) {
        Toast.makeText(todos.this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout(getApplicationContext());
                return true;
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                if (drawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(Context context) {
        SharedPreferences sharedprefrences = context.getSharedPreferences(MainActivity.prefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = sharedprefrences.edit();
        editor.putBoolean("isFirstTime", true);
        editor.apply();
        dialog = new ProgressDialog(todos.this);
        dialog.setMessage("Logging out...");
        dialog.show();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Intent intent = new Intent(todos.this, MainActivity.class);
                todos.this.startActivity(intent);
                finish();
            }
        }, 3000);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
//                fragmentClass = FirstFragment.class;
                break;
            case R.id.nav_second_fragment:
//                fragmentClass = SecondFragment.class;
                break;
            case R.id.nav_third_fragment:
//                fragmentClass = ThirdFragment.class;
                break;
            default:
//                fragmentClass = FirstFragment.class;
        }

/*        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
