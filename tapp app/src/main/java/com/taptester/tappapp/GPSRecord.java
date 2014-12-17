package com.taptester.tappapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.taptester.database.dbConfig;
import com.taptester.json.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GPSRecord extends ActionBarActivity {

    private static final String TAG_RESULTS = "results";
    private static final String TAG_GEOMETRY = "geometry";
    private static final String TAG_VIEWPORT = "viewport";
    private static final String TAG_NORTHEAST = "northeast";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LNG = "lng";
    String url = "";

    private int configButton;

    EditText editText;
    Button saveButton;

    JSONArray results = null;

    Intent configIntent;

    private dbConfig eDB;
    private SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        editText = (EditText) findViewById(R.id.editText);

        eDB = new dbConfig(this);
        sqlDB = eDB.getWritableDatabase();

        saveButton = (Button) findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetGeo().execute();
            }
        });

        configIntent = getIntent();
        configButton = configIntent.getIntExtra("buttonNum", 0);

        String query = "SELECT " + eDB.ACTION + ", " + eDB.COMMAND_NAME + " ," + eDB.TEXT +
                " FROM " + eDB.TABLE_CONFIGS + " WHERE " + eDB.GESTURE + " = " + configButton;

        Cursor cursor = sqlDB.rawQuery(query, null);

        if (cursor.getCount()<=0) {
            return;
        } else {
            sqlDB.delete(eDB.TABLE_CONFIGS, eDB.GESTURE + "=" + configButton, null);
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(String.valueOf(configButton));
        editor.commit();

        Log.d("EXTRA", String.valueOf(configButton));
    }

    public void goBack(View v) {
        onBackPressed();
        finish();
    }

    private class GetGeo extends AsyncTask<String, String, String> {

        String lat, lng;

        @Override
        protected String doInBackground(String... strings) {

            JSONParser jsonParser = new JSONParser();

            String address = editText.getText().toString();

            address = address.replaceAll(" ", "%20");
            String url = "http://maps.googleapis.com/maps/api/geocode/json?address="
                   + address + "&sensor=true";

            //String url = "http://geocode-maps.yandex.ru/1.x/?format=json&geocode=" + address;
            //String url = "http://geocode-maps.yandex.ru/1.x/";

            //List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("format", "json"));
            //params.add(new BasicNameValuePair("geocode", address));

            Log.d("URL", url);

            JSONObject json = jsonParser.getJSONFromUrl(url);//jsonParser.makeHttpRequest(url, "GET", params); //
            //Log.d("JSON", String.valueOf(json));

            try {
                JSONArray jsonResponse = json.getJSONArray("results");

                //JSONObject jsonGeoObjects = jsonResponse.getJSONObject("GeoObjectCollection");

                //results = jsonGeoObjects.getJSONArray("featureMember");

                for(int i = 0; i< jsonResponse.length(); i++) {
                    JSONObject r = jsonResponse.getJSONObject(i);

                    JSONObject geometry = r.getJSONObject("geometry");

                    JSONObject point = geometry.getJSONObject("location");

                    //JSONObject viewport = geometry.getJSONObject(TAG_VIEWPORT);

                    //JSONObject northeast = viewport.getJSONObject(TAG_NORTHEAST);

                    //String latlong = point.getString("pos");
                    //String parts[] = new String[2];
                    //parts = latlong.split(" ");

                    //lat = parts[1];
                    //lng = parts[0];
                    lat = point.getString(TAG_LAT);
                    lng = point.getString(TAG_LNG);

                    Log.d("LAT", lat);
                    Log.d("LONG", lng);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            url = "waze://?ll=" + lat + "," + lng + "&navigate=yes";
            saveGPS();
        }
    }

    public void saveGPS() {

        ContentValues contVal = new ContentValues();
        contVal.put(eDB.COMMAND_NAME, "3");
        contVal.put(eDB.GESTURE, configButton);
        contVal.put(eDB.ACTION, url);

        sqlDB.insert(dbConfig.TABLE_CONFIGS, null, contVal);
        SaveConfig(configButton);
        finish();

    }

    public void SaveConfig(int buttonNum) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GPSRecord.this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(String.valueOf(buttonNum), String.valueOf(buttonNum));
        edit.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gpsrecord, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
