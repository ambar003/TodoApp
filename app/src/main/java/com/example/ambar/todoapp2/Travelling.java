package com.example.ambar.todoapp2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Travelling extends Fragment {

    private View view;

    public Travelling() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.general_fragment, container, false);
        general general = new general();
        general.init(view);
        general.clickListener();
        return view;
    }
}
