package com.vincit.munkunta;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.apache.commons.collections.iterators.EntrySetMapIterator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
An unfinished helper-activity DrawerActivity, that implements navigation drawer to the activities extending this activity
 */

public class DrawerActivity extends AppCompatActivity {

    protected ActionBarDrawerToggle mDrawerToggle;
    MunicipalityService allMunis;
    MunicipalityService muniPrefs;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private Municipality[] municipalities;

    private void collectMunicipalities() {
        List<String> munis = new ArrayList<>();

        List<Municipality> allMunicipalities = allMunis.getMunicipalityList();

        for(Municipality muni: allMunicipalities){
            if(muniPrefs.muniIsFollowed(muni.getId().toString())) {
                munis.add(muni.getName());
                System.out.println(allMunicipalities);
            }
        }
        Municipality[] muns = new Municipality[munis.size()];
        municipalities = munis.toArray(muns);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        allMunis = new MunicipalityService(getApplicationContext(), MunicipalityService.allmunisName);
        muniPrefs = new MunicipalityService(getApplicationContext(), MunicipalityService.muniPrefsName);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(final int layoutResID) {

        DrawerLayout fullLayout = (DrawerLayout) getLayoutInflater()
                .inflate(R.layout.activity_drawer, null);
        LinearLayout actContent = (LinearLayout) fullLayout.findViewById(R.id.content);

        collectMunicipalities();

        mDrawerLayout = (DrawerLayout) fullLayout.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) fullLayout.findViewById(R.id.nav_drawer);

        //String[] followedMunis = muniPrefs.getAllItems().entrySet().toArray();

        // Set the adapter for the list view
        mDrawerList.setAdapter(new NavDrawerAdapter(this,
                R.layout.drawer_list_item, municipalities));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(parent, position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(AdapterView parent, int position) {

        Municipality mun = (Municipality) parent.getItemAtPosition(position);

        //Reload correct municipality with its style attributes

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        //setTitle(navOptions[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

}

