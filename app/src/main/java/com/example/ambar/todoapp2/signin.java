 package com.example.ambar.todoapp2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import org.json.JSONObject;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class signin extends Activity {
    String etname,etpassword;
    static JSONObject jsonParam;
    static int HttpResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Bundle b = getIntent().getExtras();
        etname = b.getString("username");
        etpassword = b.getString("password");
        jsonParam = new JSONObject();
        try {
            jsonParam.put("username", etname);
            jsonParam.put("password", etpassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Mytask mytask = new Mytask();
        mytask.execute();
    }
    class Mytask extends AsyncTask<Void, Void, Void> {
        ProgressDialog Dialog = new ProgressDialog(signin.this);

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Loading...");
//            Dialog.setTitle("");
            Dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL url;
            HttpURLConnection urlConnection = null;
//            StringBuilder sb = new StringBuilder();
            try {
                url = new URL("https://todoambar.herokuapp.com/authenticate");
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
                System.out.println("Response code" + " " + HttpResult);
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    HttpResult = HttpURLConnection.HTTP_OK;
                } else {
                    System.out.println(urlConnection.getResponseMessage());
                    HttpResult = 400;
                }
            } catch (IOException e) {
                e.printStackTrace();
                HttpResult = 600;
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Dialog.dismiss();
            if (HttpResult == 200) {
                System.out.println("Login Successful");
                new AlertDialog.Builder(signin.this)
                        .setTitle("Status")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(signin.this, todos.class).putExtra("username", etname);
                                signin.this.startActivity(intent);
                                finish();
                            }
                        })
                        .setMessage("Welcome Back!!!")
                        .show();
            } else if (HttpResult == 600) {
                new AlertDialog.Builder(signin.this)
                        .setTitle("Status")
                        .setMessage("Server timeout")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            } else {
                new AlertDialog.Builder(signin.this)
                        .setTitle("Status")
                        .setMessage("Invalid Username or Password")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        }
    }
}
