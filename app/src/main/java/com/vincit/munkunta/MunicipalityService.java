package com.vincit.munkunta;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by Linda on 11/03/16.
 */

/**
 * A helper class for all shared preferences calls.
 */

public class MunicipalityService extends AppCompatActivity {
    public static final String booleanPrefsName = "MunKuntaBooleans";
    public static final String muniPrefsName = "Kuntavalinnat";
    public static final String allmunisName = "Kunnat";
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public MunicipalityService(Context context, String sharedPrefsName) {
        this.sharedPrefs = context.getSharedPreferences(sharedPrefsName, 0);
        this.prefsEditor = sharedPrefs.edit();
    }

    public void saveMunisAsJson(List municipalities) {
        Gson gson = new Gson();
        String json = gson.toJson(municipalities);
        prefsEditor.putString("municipalityList", json);
        prefsEditor.commit();
    }

    public List<Municipality> getMunicipalityList() {
        Gson gson = new Gson();
        String jsonPreferences = sharedPrefs.getString("municipalityList", "");
        Type type = new TypeToken<List<Municipality>>() {}.getType();
        List<Municipality> productFromShared = gson.fromJson(jsonPreferences, type);
        return productFromShared;
    }

    public Municipality getMunicipality(String id, List<Municipality> allMunis) {
        if(allMunis.isEmpty()) {
            return null;
        } else {
            Municipality chosenMuni = allMunis.get(0);
            for (Municipality m: allMunis) {
                if(m.getId().toString().equals(id)) {
                    chosenMuni = m;
                }
            }
            return chosenMuni;
        }
    }

    public Map getAllItems() {
        return sharedPrefs.getAll();
    }

    public Municipality getActiveMuni(List<Municipality> allMunis) {
        Municipality chosenMuni = null;
        Map<String, ?> followesMunis = sharedPrefs.getAll();
        for(Map.Entry<String,?> entry : followesMunis.entrySet()){
            if(entry.getValue().equals(true)) {
                chosenMuni = getMunicipality(entry.getKey(), allMunis);
            }
        }
        //if not set, change the first municipality's acivity to true
        //setMuniActivity(chosenMuni.getId().toString(), true);
        return chosenMuni;
    }

    public boolean appOpenedBefore() {
        return sharedPrefs.getBoolean("hasOpened", false);
    }

    public void setAppAsOpened() {
        prefsEditor.putBoolean("hasOpened", true);
        prefsEditor.commit();
    }

    public boolean muniIsFollowed(String id) {
        return sharedPrefs.contains(id);
    }

    public void setMuniActivity(String id, Boolean active) {
        prefsEditor.putBoolean(id, active);
        prefsEditor.commit();
    }

    /**
     * Sets all other municipalitys inactive and sets the wanted municipality active.
     * @param id the id of the municipality that is to be set active
     */

    public void setNewActiveMuni(String id) {
        Map<String, ?> followesMunis = sharedPrefs.getAll();
        for(Map.Entry<String,?> entry : followesMunis.entrySet()){
            setMuniActivity(entry.getKey(), false);
        }
        setMuniActivity(id, true);
    }

    public void unfollowMuni(String id) {
        prefsEditor.remove(id);
        prefsEditor.commit();
    }

}