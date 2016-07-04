package com.example.ambar.todoapp2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Work extends Fragment {
    private View view;

    public Work() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.general_fragment, container, false);
        general general = new general();
        general.init(view);
        general.clickListener();
        return view;
    }
}
