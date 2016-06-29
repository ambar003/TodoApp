package com.example.ambar.todoapp2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class signup extends Activity {
    static JSONObject jsonParam;
    static String etname, etemail, etpassword;
    static int HttpResult;
    static boolean status;
    static EditText name, email, password;
    static Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);
        assert submit != null;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
                createJson();
                if (etname.equals("") || etemail.equals("") || etpassword.equals("")) {
                    toast("Invalid Entry");
                } else {
                    System.out.println("Onclick");
                    Mytask mytask = new Mytask();
                    mytask.execute();
                }
            }
        });
    }

    static void getdata() {
        assert name != null;
        etname = name.getText().toString();
        assert email != null;
        etemail = email.getText().toString();
        assert password != null;
        etpassword = password.getText().toString();
    }

    static void createJson(){
        jsonParam = new JSONObject();
        try {
            jsonParam.put("username", etname);
            jsonParam.put("email", etemail);
            jsonParam.put("password", etpassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Mytask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder sb = new StringBuilder();
            try {
                url = new URL("https://todoambar.herokuapp.com/adduser");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
//                System.out.println(jsonParam.toString());
                out.write(jsonParam.toString());
                out.close();
                HttpResult = urlConnection.getResponseCode();
//                System.out.println("Response code" + " " + HttpResult);
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
//                    System.out.println("" + sb.toString());
                    status = true;
                } else {
                    System.out.println(urlConnection.getResponseMessage());
                    status = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AlertDialog.Builder alert = new AlertDialog.Builder(signup.this);
            String s1 = "status", s2;
            if (status) {
                s2 = "Registration Successful!!!";
                alert.setTitle(s1)
                        .setMessage(s2)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                toast("Login Now");
                                finish();
                            }
                        })
                        .setIcon(R.drawable.success)
                        .show();
            } else {
                s2 = "Registration Failed!!!";
                alert
                        .setTitle(s1)
                        .setMessage(s2)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                toast("Try Again");
                            }
                        })
                        .setIcon(R.drawable.failed)
                        .show();
            }
        }
    }

    public void toast(String text) {
        Toast.makeText(signup.this, text, Toast.LENGTH_SHORT).show();
    }
}