package com.example.ambar.todoapp2;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/*
 * Created by ambar on 22/6/16.
 */
public class customAdapter extends ArrayAdapter {
    viewHolder holder;
    ArrayList<todoinfo> str;

    public customAdapter(Context applicationContext, int listview, ArrayList<todoinfo> str) {
        super(applicationContext, listview, str);
        this.str = str;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview, null);
            holder = new viewHolder(view);
            view.setTag(holder);
        } else {
            holder = (viewHolder) view.getTag();
        }
        todoinfo i = str.get(position);
        if (i != null) {
//            System.out.println(i.task + " " + i.date);
            holder.task.setText(i.task);
            holder.date.setText(i.date);
            if (i.done == 0)
                holder.done.setChecked(false);
            else
                holder.done.setChecked(true);
            holder.overflowmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getContext(),v );
                    popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            toast(item.getTitle().toString());
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
        return view;
    }

    public void toast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public int getCount() {
        return str.size();
    }

    static class viewHolder {
        public CheckBox done;
        public TextView task;
        public TextView date;
        public ImageView overflowmenu;

        viewHolder(View v) {
            done = (CheckBox) v.findViewById(R.id.done);
            task = (TextView) v.findViewById(R.id.task);
            date = (TextView) v.findViewById(R.id.date);
            overflowmenu = (ImageView) v.findViewById(R.id.overflowmenu);
        }
    }
}
