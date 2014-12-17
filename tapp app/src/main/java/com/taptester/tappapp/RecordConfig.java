package com.taptester.tappapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.taptester.database.dbConfig;

//Created 06.08.2014 by Constantine


public class RecordConfig extends ActionBarActivity {

    ToggleButton sendEmail, storeOnDevice;
    EditText EmailAddress, timeTXT;

    Intent configIntent;
    private int configButton;
    String type = "1";

    private dbConfig eDB;
    private SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_config);

        eDB = new dbConfig(this);
        sqlDB = eDB.getWritableDatabase();

        sendEmail = (ToggleButton) findViewById(R.id.sendEmail);
        storeOnDevice = (ToggleButton) findViewById(R.id.storeDevice);
        EmailAddress = (EditText) findViewById(R.id.EmailAddress);
        timeTXT = (EditText) findViewById(R.id.timeText);

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

    }



    public void onSendEmail(View arg0) {
        // Is the toggle on?

        sendEmail.setChecked(true);
        type = "1";

        if (type.equals("1")) {
            // Enable vibrate
            storeOnDevice.setChecked(false);
            sendEmail.setChecked(true);
            EmailAddress.setVisibility(View.VISIBLE);
        }
    }

    public void onStoreDevice(View arg0) {
        // Is the toggle on?

        storeOnDevice.setChecked(true);
        type = "2";

        if (type.equals("2")) {
            // Enable vibrate
            storeOnDevice.setChecked(true);
            sendEmail.setChecked(false);
            EmailAddress.setVisibility(View.GONE);

        }
    }

    public void saveRecord(View v) {

        ContentValues contVal = new ContentValues();
        contVal.put(eDB.COMMAND_NAME, "2");
        contVal.put(eDB.GESTURE, configButton);
        contVal.put(eDB.ACTION, type);
        contVal.put(eDB.TEXT, timeTXT.getText().toString());

        if (type.equals("1")) {
            contVal.put(eDB.EMAIL, EmailAddress.getText().toString());
        }
        sqlDB.insert(dbConfig.TABLE_CONFIGS, null, contVal);
        SaveConfig(configButton);
        finish();

    }

    public void SaveConfig(int buttonNum) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RecordConfig.this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(String.valueOf(buttonNum), String.valueOf(buttonNum));
        edit.commit();
    }

}
