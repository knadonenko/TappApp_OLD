package com.taptester.tappapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.taptester.database.dbConfig;

import java.io.IOException;


public class RecordActivity extends ActionBarActivity {

    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private String outputFile = null;
    private int configButton;

    Intent configIntent;

    RelativeLayout layoutSave, layoutStart;

    Button stopButton, playButton, recordButton;

    private dbConfig eDB;
    private SQLiteDatabase sqlDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        eDB = new dbConfig(this);
        sqlDB = eDB.getWritableDatabase();

        outputFile = Environment.getExternalStorageDirectory().
        getAbsolutePath() + "/recordMemo"+ configButton +".3gpp";

        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myRecorder.setOutputFile(outputFile);

        recordButton = (Button) findViewById(R.id.recordBtn);
        stopButton = (Button) findViewById(R.id.stopButton);
        playButton = (Button) findViewById(R.id.playButton);
        layoutStart = (RelativeLayout) findViewById(R.id.recordStartLayout);
        layoutSave = (RelativeLayout) findViewById(R.id.recordSaveLayout);

        configIntent = getIntent();
        configButton = configIntent.getIntExtra("buttonNum", 0);

        Log.d("EXTRA", String.valueOf(configButton));


    }

    public void startRecord(View v) {
        try {
            myRecorder.prepare();
            myRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        recordButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);


        Toast.makeText(getApplicationContext(), "Start recording...",
                Toast.LENGTH_SHORT).show();

    }

    public void stopRecord(View v) {
        try {
            myRecorder.stop();
            myRecorder.release();
            myRecorder = null;

            layoutStart.setVisibility(View.GONE);
            layoutSave.setVisibility(View.VISIBLE);

            Log.d("STOP", "STOP");

            Toast.makeText(getApplicationContext(), "Stop recording...",
                    Toast.LENGTH_SHORT).show();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    public void playRecord(View v) {
        try {
            myPlayer = new MediaPlayer();
            myPlayer.setDataSource(outputFile);
            myPlayer.prepare();
            myPlayer.start();

            Toast.makeText(getApplicationContext(), "Start play the recording...",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void saveRecord(View v) {

        ContentValues contVal = new ContentValues();
        contVal.put(eDB.COMMAND_NAME, "2");
        contVal.put(eDB.GESTURE, configButton);
        contVal.put(eDB.ACTION, outputFile);

        sqlDB.insert(dbConfig.TABLE_CONFIGS, null, contVal);
        SaveConfig(configButton);

    }

    public void SaveConfig(int buttonNum) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RecordActivity.this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(String.valueOf(buttonNum), String.valueOf(buttonNum));
        edit.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void CloseActivity(View v) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record, menu);
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
    }*/
}
