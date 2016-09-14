package com.rendall.martyn.lightup.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.rendall.martyn.lightup.R;
import com.rendall.martyn.lightup.fragments.ControlFragment;
import com.rendall.martyn.lightup.fragments.LogsFragment;
import com.rendall.martyn.lightup.fragments.ManageFragment;
import com.rendall.martyn.lightup.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout navigationDrawerLayout;
    private ListView navigationDrawerListView;

    public static final String NAV_CONTROL = "Control";
    public static final String NAV_SETTINGS = "Settings";
    public static final String NAV_MANAGE = "Manage";
    public static final String NAV_LOGS = "Logs";
    public static final String[] navigationEntries = {NAV_CONTROL, NAV_MANAGE, NAV_SETTINGS, NAV_LOGS};

    private ActionBarDrawerToggle drawerToggle;

    private String activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationDrawerListView = (ListView)findViewById(R.id.nav_list);

        //TODO only show the nav drawer expanded on first ever load of app then save a sharedpref to prevent doing this again?
        addNavigationItems();
        setupNavigationDrawer();

        if (savedInstanceState == null) {
            // TODO: read http://stackoverflow.com/questions/7951730/viewpager-and-fragments-whats-the-right-way-to-store-fragments-state/9646622#9646622

            // Set default fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new ControlFragment())
                    .commit();
            setTitle(NAV_CONTROL);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        } else {

            //TODO make this work so the app handles orientation changes
            Log.e("MainActivity", "reload instance state!");
        }
    }

    private void setupNavigationDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this
                , navigationDrawerLayout
                , R.string.drawer_open
                , R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(activityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        navigationDrawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title) {
        activityTitle = title.toString();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(activityTitle);
        }
    }

    private void addNavigationItems() {

        ArrayAdapter<String> navigationDrawerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navigationEntries);
        navigationDrawerListView.setAdapter(navigationDrawerAdapter);

        // TODO prevent duplicate clicks on the same navigation drawer item. i.e. when its selected once then
        // prevent it being clicked again
        navigationDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Fragment fragment = null;

                // TODO resuse the fragments if they already exist instead of always creating them, this can be done by attempting to get them via their
                //  tag from the fragment manager
                switch (navigationEntries[position]) {
                    case NAV_CONTROL:
                        fragment = new ControlFragment();
                        break;
                    case NAV_MANAGE:
                        fragment = new ManageFragment();
                        break;
                    case NAV_SETTINGS:
                        fragment = new SettingsFragment();
                        break;
                    case NAV_LOGS:
                        fragment = new LogsFragment();
                        break;
                }

                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();

                    // Highlight the selected item, update the title and close the drawer
                    navigationDrawerListView.setItemChecked(position, true);
                    setTitle(navigationEntries[position]);
                    navigationDrawerLayout.closeDrawer(navigationDrawerListView);
                }
            }
        });
    }
}
