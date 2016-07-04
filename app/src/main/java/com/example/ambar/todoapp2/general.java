package com.example.ambar.todoapp2;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/*
 * Created by ambar on 2/7/16.
 */
public class general {

    private EditText addtask;
    private ExpandableListView completetodos;
    private String username;
    private todos todo;
    private String task;
    private ListView todos;
    private ImageView add;
    private Context context;
    private View view;

    public void init(View view) {
        this.view = view;
        context = view.getContext();
        addtask = (EditText) view.findViewById(R.id.addtask);
        add = (ImageView) view.findViewById(R.id.addbutton);
        todos = (ListView) view.findViewById(R.id.todoitem);
        completetodos = (ExpandableListView) view.findViewById(R.id.completetodos);
        username = todo.username;
        task = "";
    }

    public void clickListener(){
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   toast("Hi");
            }
        });
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
