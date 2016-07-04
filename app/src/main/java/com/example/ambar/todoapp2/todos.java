package com.example.ambar.todoapp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

public class todos extends AppCompatActivity {
    static String task, username;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    static ArrayList<todoinfo> str, completelist;
    ProgressDialog dialog;
    Handler handler;
    Fragment fragment;
    MenuItem item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos);
        Bundle b = getIntent().getExtras();
        task = "";
        username = b.get("username").toString();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        Menu m = nvDrawer.getMenu();
        item = m.findItem(R.id.home_fragment);
        item.setChecked(true);
        System.out.println("item");
        setTitle(item.getTitle());
        setupDrawerContent(nvDrawer);
        fragment = new Home();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        mDrawer.closeDrawers();
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout(getApplicationContext());
                return true;
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                if (drawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(Context context) {
        SharedPreferences sharedprefrences = context.getSharedPreferences(MainActivity.prefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = sharedprefrences.edit();
        editor.putBoolean("isFirstTime", true);
        editor.apply();
        dialog = new ProgressDialog(todos.this);
        dialog.setMessage("Logging out...");
        dialog.show();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Intent intent = new Intent(todos.this, MainActivity.class);
                todos.this.startActivity(intent);
                finish();
            }
        }, 3000);
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        fragment = null;
        item = menuItem;
        switch (menuItem.getItemId()) {
            case R.id.home_fragment:
                fragment = new Home();
                break;
            case R.id.nav_first_fragment:
                fragment = new Family();
                break;
            case R.id.nav_second_fragment:
                fragment = new Work();
                break;
            case R.id.nav_third_fragment:
                fragment = new Movies();
                break;
            case R.id.nav_fourth_fragment:
                fragment = new Groceries();
                break;
            case R.id.nav_fifth_fragment:
                fragment = new Travelling();
                break;
            default:
                break;
        }

        System.out.println("Title" + " " + menuItem.getTitle());
        if (fragment != null) {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
            // Close the navigation drawer
            mDrawer.closeDrawers();
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
