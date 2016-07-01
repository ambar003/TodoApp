package com.example.ambar.todoapp2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/*
 * Created by ambar on 22/6/16.
 */
public class customAdapter extends ArrayAdapter {
    viewHolder holder;
    ArrayList<todoinfo> str;
    String deleteurl,updateurl;
    int HttpResult;
    JSONObject jsonObject,jsonObject1;
    ProgressDialog dialog;
    expandListAdapter expandlist;
    int done;
    private Context context;

    public customAdapter(Context applicationContext, int listview, ArrayList<todoinfo> str) {
        super(applicationContext, listview, str);
        this.str = str;
        this.context = applicationContext;
        expandlist = new expandListAdapter();
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent) {
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
            initialize(i);
            checkboxcheck(i,position);
            overflowmenuclick(position);
        }
        return view;
    }

    private void initialize(todoinfo i) {
        holder.task.setText(i.task);
        holder.date.setText(i.date);
        if (i.done == 0)
            holder.done.setChecked(false);
        else
            holder.done.setChecked(true);
    }

    private void checkboxcheck(final todoinfo i , final int position) {
       holder.done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   str.remove(position);
                   notifyDataSetChanged();
                   i.done = 1;
                   todos.completelist.add(i);
                   expandlist.notifyDataSetChanged();
                   jsonObject1 = new JSONObject();
                   System.out.println(todos.username+" "+i.id+" "+i.done);
                   done =1;
                   try {
                       jsonObject1.put("done",done);
                       jsonObject1.put("username",todos.username);
                       jsonObject1.put("taskid",i.id);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
               updatetask updatetask = new updatetask();
               updatetask.execute();
           }
       });
    }

    private void overflowmenuclick(final int position) {
        holder.overflowmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(),v );
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String s = item.getTitle().toString();
                        toast(s);
                        if(s.equals("Delete")){
                            int id = str.get(position).id;
                            jsonObject = new JSONObject();
                            try {
                                jsonObject.put("taskid",id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            str.remove(position);
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
    class updatetask extends AsyncTask<Void,Void,Void>{
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
                toast("Added to Complete List");
                dismissDialog();
            }
            else{
                toast("Error in Adding!!!");
                dismissDialog();
            }
        }
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
