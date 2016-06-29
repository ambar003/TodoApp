package com.example.ambar.todoapp2;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.w3c.dom.Text;


public class MainActivity extends Activity {
    static String etname, etpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText name = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);
        final Button signin = (Button) findViewById(R.id.signin);
        final TextView signup = (TextView) findViewById(R.id.signup);
        assert signin != null;
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etname = name.getText().toString();
                etpassword = password.getText().toString();
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
    }
}
