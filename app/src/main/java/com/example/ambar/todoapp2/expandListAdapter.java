package com.example.ambar.todoapp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/*
 * Created by ambar on 28/6/16.
 */
public class expandListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<group> groups;
    protected TextView date;
    private CheckBox done;
    private ImageView ofm;
    JSONObject jsonObject, jsonObject1;
    int HttpResult;
    String deleteurl,updateurl;
    ProgressDialog dialog;

    public expandListAdapter() {
    }

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
//        group group = (group) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parent, null);
        }
//        TextView tv = (TextView) convertView.findViewById(R.id.heading);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        todoinfo i = (todoinfo) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listview, null);
        }
        if (i != null) {
            initialize(i, convertView);
            checkboxclick(i, childPosition);
            overflowmenuclick(childPosition);
        }
        return convertView;
    }

    private void overflowmenuclick(final int childPosition) {
        ofm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String s = item.getTitle().toString();
                        toast(s);
                        if(s.equals("Delete")){
                            int id = todos.completelist.get(childPosition).id;
                            jsonObject = new JSONObject();
                            try {
                                jsonObject.put("taskid",id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            todos.completelist.remove(childPosition);
                            notifyDataSetChanged();
                            deletetask deletetask = new deletetask();
                            deletetask.execute();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void checkboxclick(final todoinfo i, final int childPosition) {
        done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    todos.completelist.remove(childPosition);
                    notifyDataSetChanged();
                    i.done = 0;
                    todos.completelist.add(i);
                    todos.str.add(i);
                    jsonObject1 = new JSONObject();
                    System.out.println(todos.username + " " + i.id + " " + i.done);
                    try {
                        jsonObject1.put("done", 0);
                        jsonObject1.put("username", todos.username);
                        jsonObject1.put("taskid", i.id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updatetask updatetask = new updatetask();
                    updatetask.execute();
                }
            }
        });
    }

    void initialize(todoinfo i, View convertView) {
        TextView task = (TextView) convertView.findViewById(R.id.task);
        date = (TextView) convertView.findViewById(R.id.date);
        done = (CheckBox) convertView.findViewById(R.id.done);
        ofm = (ImageView) convertView.findViewById(R.id.overflowmenu);
        task.setText(i.task);
        date.setText(i.date);
        if (i.done == 0)
            done.setChecked(false);
        else
            done.setChecked(true);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class updatetask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            createDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            updateurl = "https://todoambar.herokuapp.com/updatetodo";
            URL url = null;
            System.out.println("Do in background");
            HttpURLConnection urlConnection;
            try {
                url = new URL(updateurl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(jsonObject1.toString());
                out.close();
                System.out.println(urlConnection.getResponseMessage());
                HttpResult = urlConnection.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(HttpResult==HttpURLConnection.HTTP_OK){
                toast("Added to InComplete List");
                dismissDialog();
            }
            else{
                toast("Error in Adding!!!");
                dismissDialog();
            }
        }
    }

    class deletetask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            createDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            deleteurl = "https://todoambar.herokuapp.com/deletetodo";
            URL url = null;
            HttpURLConnection urlConnection;
            try {
                url = new URL(deleteurl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(jsonObject.toString());
                out.close();
                System.out.println(urlConnection.getResponseMessage());
                HttpResult = urlConnection.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                toast("Deleted Successfully");
                dismissDialog();
            } else {
                toast("Some error occured!!!");
                dismissDialog();
            }
        }
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    void createDialog(){
        dialog =new ProgressDialog(context);
        System.out.println("new todos");
        dialog.setMessage("Updating...");
        dialog.show();
    }

    void dismissDialog(){
        dialog.dismiss();
    }
}
