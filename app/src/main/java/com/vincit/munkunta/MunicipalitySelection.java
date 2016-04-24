package com.vincit.munkunta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

import static com.vincit.munkunta.HelperMethods.changeStatusBarColor;
import static com.vincit.munkunta.HelperMethods.toPx;

public class MunicipalitySelection extends AppCompatActivity {

    /*
    TO*DO: Mitä appin pitää muistaa:
    TO*DO:  - onko käyttäjä nähnyt tervetuloa-näkymän
    TO*DO:  - mikä on viimeisin aktiivinen näkymä (tämän taskin voi tehdä kun kuntien vaihtaminen onnistuu) (helppo)
    TO*DO:  - käyttäjän seuraamat kunnat
     */

    //TODO: laita latausympyrä (timo)

    private HashMap<Integer, Boolean> municipalityMap = new HashMap<>();
    MunicipalityService muniPrefs;
    MunicipalityService boolPrefs;
    LinearLayout done;
    private Boolean doneActive = false;

    /**
     * Creates the MunicipalitySelection-layout and calls the municipalities
     * @param savedInstanceState the saved state of the activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality_selection);

        boolPrefs = new MunicipalityService(getApplicationContext(), MunicipalityService.booleanPrefsName);
        muniPrefs = new MunicipalityService(getApplicationContext(), MunicipalityService.muniPrefsName);

        if(boolPrefs.appOpenedBefore()) {
            Municipality activeMuni = MainViewActivity.activeMunicipality;
            changeStatusBarColor(this, activeMuni);
        }

        KatiskaInterface katiska = ((MunKunta) getApplication()).getKatiska();
        Call<List<Municipality>> call = katiska.getMunicipalityList();
        call.enqueue(new Callback<List<Municipality>>() {
            @Override
            public void onResponse(Response<List<Municipality>> response) {
                if (response.isSuccess()) {
                    List<Municipality> municipalities = response.body();
                    draw(municipalities);
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
     * Draws all the municipality buttons
     * @param municipalities the municipalities as a list
     */

    private void draw(List<Municipality> municipalities) {

        LinearLayout firstLO = (LinearLayout) findViewById(R.id.firstColumn);
        LinearLayout secondLO = (LinearLayout) findViewById(R.id.secondColumn);

        done = (LinearLayout) findViewById(R.id.done);
        done.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.doneInactive));
        setDoneListener(done);

        // Add backend's municipalities to columns as buttons
        int i = 0;
        for(Municipality x: municipalities) {
            municipalityMap.put(x.getId(), false);
            if(i%2 == 0) {
                drawButton(firstLO, x);
            } else {
                drawButton(secondLO, x);
            }
            i++;
        }
    }

    /**
     * Draws a single button to a layout given as a parameter
     * @param layout the layout the button is put to
     * @param municipality The municipality that is attached to the button
     */

    private void drawButton(LinearLayout layout, Municipality municipality) {
        LinearLayout.LayoutParams bParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              toPx(39.6));
        bParams.setMargins(toPx(10.0), toPx(13.5), toPx(10.0), toPx(13.5));

        Button muniButton = new Button(this);
        styleButton(muniButton, municipality);
        setButtonListener(muniButton);

        layout.addView(muniButton, bParams);
    }

    /**
     * Styles the button
     * @param button the button to be styled
     * @param municipality the municipality that is attached to the button
     */

    private void styleButton(Button button, Municipality municipality) {
        button.setText(municipality.getName().toUpperCase());
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.button_text_size));
        button.setTag(municipality.getId());

        // Set button background to dark or light according to whether it's followed or not
        if (muniPrefs.muniIsFollowed(municipality.getId().toString())) {
            municipalityMap.put(municipality.getId(), true);
            button.setBackgroundResource(R.drawable.muni_button_bg);

            button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.lightTextColor));
            GradientDrawable drawable = (GradientDrawable) button.getBackground();
            drawable.setColor(ContextCompat.getColor(getApplicationContext(), R.color.buttonBGactive));

            //id even one municipality is chosen, change the done button's color to active (red)
            done.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            doneActive = true;
        } else {
            button.setBackgroundResource(R.drawable.muni_button_bg);

            button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textColor));
            GradientDrawable drawable = (GradientDrawable) button.getBackground();
            drawable.setColor(ContextCompat.getColor(getApplicationContext(), R.color.buttonBGinactive));
        }
    }

    /**
     * Listens the Municipality buttons and saves their new values in municipalityMap
     * and changes their background color when pressed
     * @param button the button listened
     */

    public void setButtonListener(Button button) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button b = (Button) v;

                if (!municipalityMap.get(b.getTag())) {
                    municipalityMap.put((int) b.getTag(), true);
                    b.setBackgroundResource(R.drawable.muni_button_bg);

                    b.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.lightTextColor));
                    GradientDrawable drawable = (GradientDrawable) b.getBackground();
                    drawable.setColor(ContextCompat.getColor(getApplicationContext(), R.color.buttonBGactive));
                } else {
                    municipalityMap.put((int) b.getTag(), false);
                    b.setBackgroundResource(R.drawable.muni_button_bg);

                    b.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.doneInactive));
                    GradientDrawable drawable = (GradientDrawable) b.getBackground();
                    drawable.setColor(ContextCompat.getColor(getApplicationContext(), R.color.buttonBGinactive));
                }

                LinearLayout done = (LinearLayout) findViewById(R.id.done);
                if (!municipalityMap.containsValue(true)) {
                    done.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.doneInactive));
                    doneActive = false;
                } else {
                    done.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    doneActive = true;
                }
            }
        });
    }

    /**
     * Listens the Done LinearLayout and sends the user to MainView if the LinearLayout is pressed
     * @param lo the Done LinearLayout
     */

    public void setDoneListener(LinearLayout lo) {
        lo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If at least one municipality is chosen, it saves all the selections to Shared Preferences
                // and finishes the app (and opens MainViewActivity if this is the first time using the app)
                if (doneActive) {
                    Boolean primaryMuniSet = false;
                    for (int i = 0; i < municipalityMap.size(); i++) {
                        Object key = municipalityMap.keySet().toArray()[i];
                        if (municipalityMap.get(key)) {
                            if (!primaryMuniSet) {
                                muniPrefs.setMuniActivity(key.toString(), true);
                                primaryMuniSet = true;
                            } else {
                                muniPrefs.setMuniActivity(key.toString(), false);
                            }
                        } else {
                            muniPrefs.unfollowMuni(key.toString());
                        }
                    }

                    if (!boolPrefs.appOpenedBefore()) {
                        boolPrefs.setAppAsOpened();
                        Intent intent = new Intent(getApplicationContext(), MainViewActivity.class);
                        startActivity(intent);
                    }
                    Toast.makeText(MunicipalitySelection.this, R.string.munisUpdated, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MunicipalitySelection.this, R.string.pickOneMuni, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
