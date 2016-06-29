package com.example.ambar.todoapp2;

import java.util.ArrayList;

/*
 * Created by ambar on 28/6/16.
 */
public class group {
    private String name;
    private ArrayList<todoinfo> items;

    public String getName() {
        return name;
    }

    public ArrayList<todoinfo> getItems() {
        return items;
    }

    public void setItems(ArrayList<todoinfo> items) {
        this.items = items;
    }

    public void setName(String name) {
        this.name = name;
    }
}
