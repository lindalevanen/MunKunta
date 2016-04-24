package com.vincit.munkunta;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vincit.munkunta.HelperMethods.changeStatusBarColor;
import static com.vincit.munkunta.HelperMethods.toPx;

public class MainViewActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {

    public String host2 = "http://vincit-mun-kunta-katiska-node.herokuapp.com";
    public static Municipality activeMunicipality;
    MunicipalityService muniPrefs;
    MunicipalityService boolPrefs;
    MunicipalityService allMunis;
    NavigationView naviView;
    public static int muniAmount = 0;
    public int allMuniAmount;
    private int screenWidth;

    //TODO: tyylittele drawer (vaikeahko)

    /**
     * Creates the Main activity
     * @param savedInstanceState the saved state of the activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //To prevent infinite loop when reloading the activity
        getIntent().setAction("Already created");

        boolPrefs = new MunicipalityService(getApplicationContext(), MunicipalityService.booleanPrefsName);
        muniPrefs = new MunicipalityService(getApplicationContext(), MunicipalityService.muniPrefsName);
        allMunis = new MunicipalityService(getApplicationContext(), MunicipalityService.allmunisName);

        //Save all municipalities to SharedPreferences
        saveAllMunisToSP();

        //get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main_view);

        //Check whether the user has opened the app before, and
        //open the municipalityselection if the user hasn't opened the app before
        if(!boolPrefs.appOpenedBefore()) {
            Intent intent = new Intent(getApplicationContext(), MunicipalitySelection.class);
            startActivity(intent);
            finish();
        } else {
            Button newsBtn = (Button) findViewById(R.id.newsButton);
            setButtonListenerToActivity(newsBtn, NewsListActivity.class);

            activeMunicipality = muniPrefs.getActiveMuni(allMunis.getMunicipalityList());
            reloadStyleAttributes();

            ImageButton drawerIcon = (ImageButton) findViewById(R.id.drawer_icon);

            drawerIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });

            reloadStyleAttributes();
            createDrawer();
        }
    }

    /**
     * Saves all municipalities from katiska to SharedPreferences as json string
     */

    public void saveAllMunisToSP() {

        KatiskaInterface katiska = ((MunKunta) getApplication()).getKatiska();
        Call<List<Municipality>> call = katiska.getMunicipalityList();
        call.enqueue(new Callback<List<Municipality>>() {
            @Override
            public void onResponse(Response<List<Municipality>> response) {
                if (response.isSuccess()) {
                    List<Municipality> municipalities = response.body();
                    allMuniAmount = municipalities.size();
                    for(Municipality muni: municipalities) {
                        callMunicipality(muni.getId().toString());
                    }
                } else {
                    KatiskaError error = ((MunKunta) getApplication())
                            .parseKError(response.errorBody());
                    Log.e("KatiskaError", error.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public int munisAdded = 0;
    List<Municipality> newMunis = new ArrayList<>();

    public void callMunicipality(String id) {
        KatiskaInterface katiska = ((MunKunta) getApplication()).getKatiska();
        Call<Municipality> call = katiska.getMunicipality(id);
        call.enqueue(new Callback<Municipality>() {
            @Override
            public void onResponse(Response<Municipality> response) {
                if (response.isSuccess()) {
                    Municipality muni = response.body();
                    newMunis.add(muni);
                    munisAdded++;
                    if (munisAdded == allMuniAmount) {
                        allMunis.saveMunisAsJson(newMunis);
                    }
                } else {
                    KatiskaError error = ((MunKunta) getApplication())
                            .parseKError(response.errorBody());
                    Log.e("KatiskaError", error.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Creates the navigation drawer
     */

    public void createDrawer()  {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        naviView = (NavigationView) findViewById(R.id.nav_view);
        naviView.setNavigationItemSelectedListener(this);

        Menu menu = naviView.getMenu();
        menu.clear();
        addMunisToDrawer();
        menu.add(R.id.muni_selec, 0, 200, "Kuntavalinta");
    }

    /**
     * Adds the followed municipalities to navigation drawer
     */

    public void addMunisToDrawer() {
        List<Municipality> municipalities = allMunis.getMunicipalityList();
        Menu menu = naviView.getMenu();

        for(Municipality muni: municipalities){
            if(muniPrefs.muniIsFollowed(muni.getId().toString())) {
                menu.add(R.id.muni_group, muni.getId(), 200, muni.getName());
                muniAmount++;
            }
        }
    }

    /**
     * Sets a button listener that takes the user to the activity that's given as the parameter
     * @param button the going to be listened button
     * @param activity the activity the user is taken to
     */

    public void setButtonListenerToActivity(Button button, final Class activity) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), activity);
                startActivity(intent);
            }
        });
    }

    /**
     * Reloads the style attributes so that they're the active municipality's own
     */

    public void reloadStyleAttributes() {
        editBanner();
        editButton();
        editText();
        changeStatusBarColor(this, activeMunicipality);
    }

    /**
     * Edits the municipality main view banner so that it fills the entire top of the screen
     */

    //TODO: croppaa kuva, tällä hetkellä litistyy leveyssuunnassa

    public void editBanner() {
        ImageView banner = (ImageView) findViewById(R.id.banner);

        String contentURL = host2 + activeMunicipality.getImg();

        Picasso.with(this)
                .load(contentURL)
                .fit()
                .centerCrop()
                .into(banner);

        banner.setScaleType(ImageView.ScaleType.FIT_XY);
        //banner.setAdjustViewBounds(true);
        /*
        if(activeMunicipality.getId() == 2) {
            banner.setBackgroundResource(R.drawable.imatra_banner);
        } else {
            banner.setBackgroundResource(R.drawable.pirkkala_back);
        }*/

    }

    public void editButton() {
        Button newsButton = (Button) findViewById(R.id.newsButton);
        newsButton.setBackgroundResource(R.drawable.news_button_bg);

        GradientDrawable drawable = (GradientDrawable) newsButton.getBackground();
        drawable.setColor(Color.parseColor(activeMunicipality.getColor()));
    }

    public void editText() {
        TextView muniText = (TextView) findViewById(R.id.kuntaTeksti);
        muniText.setText(activeMunicipality.getName().toUpperCase());
    }

    /**
     * Defines what happenes when the navigation drawer-menu items are pressed
     * @param item the menuitem object
     * @return boolean according to whether the click has been a success
     */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Integer id = item.getItemId();

        //Check if the user has pressed the Kuntavalinta-button
        if(id == 0) {
            Intent intent = new Intent(getApplicationContext(), MunicipalitySelection.class);
            startActivity(intent);
        }

        //Check whether the item id is municipality item's id
        for(Integer i = 1; i <= muniAmount; i++){
            if(id == i) {
                muniPrefs.setNewActiveMuni(id.toString());
                activeMunicipality = muniPrefs.getActiveMuni(allMunis.getMunicipalityList());
                reloadStyleAttributes();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Closes the navigation drawer if it is open
     */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Reloads the navigation drawer when it resumes
     */

    @Override
    public void onResume() {
        String action = getIntent().getAction();

        if(action == null || !action.equals("Already created")) {
            createDrawer();
            activeMunicipality = muniPrefs.getActiveMuni(allMunis.getMunicipalityList());
            reloadStyleAttributes();
        } else {
            getIntent().setAction(null);
        }

        super.onResume();
    }
}
