package com.example.ambar.todoapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/*
 * Created by ambar on 28/6/16.
 */
public class expandListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<group> groups;

    public expandListAdapter(Context context, ArrayList<group> groups) {
        this.context = context;
        this.groups = groups;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<todoinfo> chList = groups.get(groupPosition).getItems();
        return chList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<todoinfo> chList = groups.get(groupPosition).getItems();
        return chList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        group group = (group) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parent, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.heading);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        todoinfo i = (todoinfo) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listview, null);
        }
        if (i != null) {
            TextView task = (TextView) convertView.findViewById(R.id.task);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            CheckBox done = (CheckBox) convertView.findViewById(R.id.done);
            ImageView ofm = (ImageView) convertView.findViewById(R.id.overflowmenu);
            task.setText(i.task);
            date.setText(i.date);
            ofm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, v);
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
            if (i.done == 0)
                done.setChecked(false);
            else
                done.setChecked(true);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
