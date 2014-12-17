package com.taptester.tappapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//Created 06.08.2014 by Constantine

public class SMS extends ActionBarActivity {

    EditText editText;
    String smsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        SharedPreferences smsPreferences = PreferenceManager.getDefaultSharedPreferences(SMS.this);
        smsText = smsPreferences.getString("smsTXT", "I'll call you later");

        editText = (EditText) findViewById(R.id.editSMS);
        editText.setText(smsText);
    }

    public void saveSMS(View v) {

        if (editText.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter new text please!", Toast.LENGTH_LONG).show();
        } else {
            smsText = editText.getText().toString();
            SharedPreferences sharedPreferences;
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SMS.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("smsTXT", smsText);
            editor.commit();
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sm, menu);
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
