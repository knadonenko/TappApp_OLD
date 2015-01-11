package com.taptester.tappapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taptester.database.dbConfig;

//Created 15.08.2014 by Constantine


public class Config_Activity extends ActionBarActivity implements View.OnClickListener {

    Button tapOneBtn, tapTwoBtn, tapThreeBtn, tapFourBtn, tapFiveBtn, slideTop, slideBottom, slideLeft, slideRight;
    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9;
    ImageView iv1, iv2, iv3, iv4, iv5;
    RelativeLayout relativeLayout, relativeLayout2, relativeLayout3, relativeLayout4, relativeLayout5,
            relativeLayout6, relativeLayout7, relativeLayout8, relativeLayout9;

    String button, command, text;
    int configExtra;

    private dbConfig eDB;
    private SQLiteDatabase sqlDB;

    int[] textViewID = new int[] {0, R.id.textView1, R.id.textView2,
            R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8, R.id.textView9};
    int[] imageViewId = new int[] {0, R.id.imageView1, R.id.imageView2, R.id.imageView3,
            R.id.imageView4, R.id.imageView5, R.id.imageView6, R.id.imageView7, R.id.imageView8, R.id.imageView9};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        eDB = new dbConfig(this);
        sqlDB = eDB.getWritableDatabase();

        tapOneBtn = (Button) findViewById(R.id.tapOneButton);
        tapTwoBtn = (Button) findViewById(R.id.tapTwoButton);
        tapThreeBtn = (Button) findViewById(R.id.tapThreeButton);
        tapFourBtn = (Button) findViewById(R.id.tapFourButton);
        tapFiveBtn = (Button) findViewById(R.id.tapFiveButton);
        slideTop = (Button) findViewById(R.id.slideTopButton);
        slideBottom = (Button) findViewById(R.id.slideBottom);
        slideLeft = (Button) findViewById(R.id.slideLeft);
        slideRight = (Button) findViewById(R.id.slideRight);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        relativeLayout3 = (RelativeLayout) findViewById(R.id.relativeLayout3);
        relativeLayout4 = (RelativeLayout) findViewById(R.id.relativeLayout4);
        relativeLayout5 = (RelativeLayout) findViewById(R.id.relativeLayout5);
        relativeLayout6 = (RelativeLayout) findViewById(R.id.relativeLayout6);
        relativeLayout7 = (RelativeLayout) findViewById(R.id.relativeLayout7);
        relativeLayout8 = (RelativeLayout) findViewById(R.id.relativeLayout8);
        relativeLayout9 = (RelativeLayout) findViewById(R.id.relativeLayout9);


        tapOneBtn.setOnClickListener(this);
        tapTwoBtn.setOnClickListener(this);
        tapThreeBtn.setOnClickListener(this);
        tapFourBtn.setOnClickListener(this);
        tapFiveBtn.setOnClickListener(this);
        slideTop.setOnClickListener(this);
        slideBottom.setOnClickListener(this);
        slideLeft.setOnClickListener(this);
        slideRight.setOnClickListener(this);

        relativeLayout.setOnClickListener(this);
        relativeLayout2.setOnClickListener(this);
        relativeLayout3.setOnClickListener(this);
        relativeLayout4.setOnClickListener(this);
        relativeLayout5.setOnClickListener(this);
        relativeLayout6.setOnClickListener(this);
        relativeLayout7.setOnClickListener(this);
        relativeLayout8.setOnClickListener(this);
        relativeLayout9.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);
        tv4 = (TextView) findViewById(R.id.textView4);
        tv5 = (TextView) findViewById(R.id.textView5);
        tv6 = (TextView) findViewById(R.id.textView6);
        tv7 = (TextView) findViewById(R.id.textView7);
        tv8 = (TextView) findViewById(R.id.textView8);
        tv9 = (TextView) findViewById(R.id.textView9);

        int i = 1;
        while (i <= 9) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Config_Activity.this);
            button = sp.getString(String.valueOf(i), "NO NUMBER");
            Log.d("BUTTON", button);
            if (!button.equals("NO NUMBER")) {
                CheckDB(button, i); //((TextView)findViewById(textViewID[i]))
            }
            i++;
        }
    }

    public void CheckDB(String buttonNum, int textViewNum) {

        String query = "SELECT " + eDB.ACTION + ", " + eDB.COMMAND_NAME + " ," + eDB.TEXT +
                " FROM " + eDB.TABLE_CONFIGS + " WHERE " + eDB.GESTURE + " = " + button;

        Cursor cursor = sqlDB.rawQuery(query, null);

        while (cursor.moveToNext()) {

            command = cursor.getString(cursor.getColumnIndex(dbConfig.ACTION));
            text = cursor.getString(cursor.getColumnIndex(dbConfig.TEXT));
            String command_id = cursor.getString(cursor.getColumnIndex(dbConfig.COMMAND_NAME));
            if (command != null){
                Log.d("Command is", command);
            }
            Log.d("TV", String.valueOf(textViewNum));
            updateTextView(textViewNum, Integer.parseInt(command_id));
            //textView.setText(command);
        }
    }

    private void updateTextView(int tvNumber, int command_number) {

        Log.d("COMMAND NUM", String.valueOf(command_number));

        if (command_number == 1) {
            ((TextView)findViewById(textViewID[tvNumber])).setText(text);
            ((ImageView)findViewById(imageViewId[tvNumber])).setImageResource(R.drawable.phone);
        } else if (command_number == 2) {
            ((TextView)findViewById(textViewID[tvNumber])).setText(R.string.record);
        } else if (command_number == 4) {
            ((TextView)findViewById(textViewID[tvNumber])).setText(R.string.not_active);
            ((ImageView)findViewById(imageViewId[tvNumber])).setImageDrawable(null);
        } else if (command_number == 3) {
            ((TextView)findViewById(textViewID[tvNumber])).setText(R.string.gpsText);
            ((ImageView)findViewById(imageViewId[tvNumber])).setImageResource(R.drawable.gps);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eDB.close();
        sqlDB.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tapOneButton :
                showMenu(tapOneBtn);
                configExtra = 1;
                break;
            case R.id.tapTwoButton :
                showMenu(tapTwoBtn);
                configExtra = 2;
                break;
            case R.id.tapThreeButton :
                showMenu(tapThreeBtn);
                configExtra = 3;
                break;
            case R.id.tapFourButton :
                showMenu(tapFourBtn);
                configExtra = 4;
                break;
            case R.id.tapFiveButton :
                showMenu(tapFiveBtn);
                configExtra = 5;
                break;
            case R.id.slideTopButton :
                showMenu(slideTop);
                configExtra = 6;
                break;
            case R.id.slideBottom :
                showMenu(slideBottom);
                configExtra = 7;
                break;
            case R.id.slideLeft :
                showMenu(slideLeft);
                configExtra = 8;
                break;
            case R.id.slideRight :
                showMenu(slideRight);
                configExtra = 9;
                break;
            case R.id.relativeLayout :
                showMenu(tapOneBtn);
                configExtra = 1;
                break;
            case R.id.relativeLayout2 :
                showMenu(tapTwoBtn);
                configExtra = 2;
                break;
            case R.id.relativeLayout3 :
                showMenu(tapThreeBtn);
                configExtra = 3;
                break;
            case R.id.relativeLayout4 :
                showMenu(tapFourBtn);
                configExtra = 4;
                break;
            case R.id.relativeLayout5 :
                showMenu(tapFiveBtn);
                configExtra = 5;
                break;
            case R.id.relativeLayout6 :
                showMenu(slideTop);
                configExtra = 6;
                break;
            case R.id.relativeLayout7 :
                showMenu(slideBottom);
                configExtra = 7;
                break;
            case R.id.relativeLayout8 :
                showMenu(slideLeft);
                configExtra = 8;
                break;
            case R.id.relativeLayout9 :
                showMenu(slideRight);
                configExtra = 9;
                break;
            default:
                break;
        }

    }

    public void smsSettings(View v) {
        Intent intent = new Intent(Config_Activity.this, SMS.class);
        startActivity(intent);
    }

    public void showMenu(Button button) {
        PopupMenu popupMenu = new PopupMenu(this, button);
        popupMenu.getMenuInflater().inflate(R.menu.config_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.dial :
                        Log.d("Create new dial item", "DIAL ITEM");
                        Intent configIntent = new Intent(Config_Activity.this, DialActivity.class);
                        configIntent.putExtra("buttonNum", configExtra);
                        startActivity(configIntent);
                        return true;
                    case R.id.Record :
                        Intent recordIntent = new Intent(Config_Activity.this, RecordConfig.class);
                        recordIntent.putExtra("buttonNum", configExtra);
                        startActivity(recordIntent);
                        return true;
                    case R.id.GPS:
                        Intent gpsIntent = new Intent(Config_Activity.this, GPSRecord.class);
                        gpsIntent.putExtra("buttonNum", configExtra);
                        startActivity(gpsIntent);
                        return true;
                    case R.id.notActive :
                        //int buttonNum = configIntent.getIntExtra("buttonNum", 0);
                        Log.d("BUTTON", String.valueOf(configExtra));
                        DeleteNumber(configExtra);
                        updateTextView(configExtra, 4);
                        return true;
                    default:
                        return false;
                }

            }
        });

        popupMenu.show();
    }

    public void DeleteNumber(int number) {
        try {
            sqlDB.delete(eDB.TABLE_CONFIGS, eDB.GESTURE + " = " + number, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.config_, menu);
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
