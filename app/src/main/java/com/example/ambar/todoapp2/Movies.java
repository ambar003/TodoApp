package com.example.ambar.todoapp2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;

public class Movies extends Fragment {

    private View view;
    private EditText addtask;
    private ExpandableListView completetodos;
    private String username;
    private todos todo;
    private String task;
    private ListView todos;
    private ImageView add;

    public Movies() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.general_fragment, container, false);
        addtask = (EditText) view.findViewById(R.id.addtask);
        add = (ImageView) view.findViewById(R.id.addbutton);
        todos = (ListView) view.findViewById(R.id.todoitem);
        completetodos = (ExpandableListView) view.findViewById(R.id.completetodos);
        username = todo.username;
        task = "";
        return view;
    }

}
