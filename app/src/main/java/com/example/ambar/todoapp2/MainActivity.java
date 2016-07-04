package com.example.ambar.todoapp2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.w3c.dom.Text;


public class MainActivity extends Activity {
    static String etname, etpassword;
    boolean isFirstTime;
    static String prefs = "My_prefs";
    static String username = "username";
    public static SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checklogin()) {
            final EditText name = (EditText) findViewById(R.id.email);
            final EditText password = (EditText) findViewById(R.id.password);
            final Button signin = (Button) findViewById(R.id.signin);
            final TextView signup = (TextView) findViewById(R.id.signup);
            sharedpreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            assert signin != null;
            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etname = name.getText().toString();
                    etpassword = password.getText().toString();
//                    System.out.println("Boolean"+" "+sharedpreferences.getBoolean("isFirstTime",true));
                    Intent intent = new Intent(MainActivity.this, signin.class).putExtra("username", etname).putExtra("password", etpassword);
                    startActivity(intent);
                }
            });
            assert signup != null;
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, signup.class);
                    MainActivity.this.startActivity(intent);
                }
            });
            System.out.println("checklogin" + " " + isFirstTime);
        } else {
            etname = sharedpreferences.getString(username, "");
            System.out.println("etname" + " " + etname);
            Intent intent = new Intent(MainActivity.this, todos.class).putExtra("username", etname);
            MainActivity.this.startActivity(intent);
        }
    }

    private boolean checklogin() {
        sharedpreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        isFirstTime = sharedpreferences.getBoolean("isFirstTime", true);
        System.out.println("isFirstTime" + " " + isFirstTime);
        editor.apply();
        return isFirstTime;
    }
}
