package com.taptester.tappapp;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.taptester.database.dbConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

//Created 06.08.2014 by Constantine

public class MainActivity extends ActionBarActivity implements GPSCallback {

    //Taps counter
    int tapCounter;
    String tapString;
    String typeRecording;
    String email;

    private static final String LOG_TAG = "AudioCaptureDemo";
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;

    private MediaPlayer   mPlayer = null;

    //UI
    ImageView image1, image2, image3, image4;
    RelativeLayout textLayout;
    int imageWidth = 50;

    //Media player
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private String outputFile = null;

    //Swipes
    private static final int SWIPE_MIN_DISTANCE = 80;
    private static final int SWIPE_THRESHOLD_VELOCITY = 50;

    //Database connectivity
    private dbConfig eDB;
    private SQLiteDatabase sqlDB;

    SharedPreferences smsPreferences;
    String smsText;

    TelephonyManager telephonymanager;

    private Handler sizeHandler = new Handler();

    private GPSManager gpsManager = null;
    private double speed = 0.0;
    Boolean isDriving = false;
    private AbsoluteSizeSpan sizeSpanLarge = null;
    private AbsoluteSizeSpan sizeSpanSmall = null;

    Button buttonInfo, shareBtn, buttonConfig;

    TextView gpsInfo;
    Boolean isRunning = false;

    public MainActivity() {
        //mFileName = Environment.getExternalStorageDirectory() + File.separator
        //        + Environment.DIRECTORY_DCIM + File.separator + "MyMemo.3gp";
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/MyMemo-" + hour + "-" + minute + ".3gp";
    }

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //outputFile = Environment.getExternalStorageDirectory().
        //        getAbsolutePath() + "/recordMemo.3gp";

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                tapString = String.valueOf(tapCounter);

                getDB();

                tapCounter = 0;
            }
        };

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.taptester.tappapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        buttonInfo = (Button) findViewById(R.id.buttonInfo);
        shareBtn = (Button) findViewById(R.id.shareBtn);
        buttonConfig = (Button) findViewById(R.id.buttonConfig);


        eDB = new dbConfig(this);
        sqlDB = eDB.getWritableDatabase();

        smsPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        smsText = smsPreferences.getString("smsTXT", "I'll call you later");
        //Log.d("TEXT", smsText);

        gpsManager = new GPSManager();

        gpsManager.startListening(getApplicationContext());
        gpsManager.setGPSCallback((GPSCallback) this);


        StateListener phoneStateListener = new StateListener();
        telephonymanager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        telephonymanager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.DEFAULT);
        myRecorder.setOutputFile(outputFile);

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/

        //gpsInfo = (TextView) findViewById(R.id.gps_info);
        //gpsInfo.setText(getResources().getString(R.string.gpsSPEED));

        //

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this).setMessage("Tap and hold!").
                        setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });


        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this).setMessage("Tap and hold!").
                        setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });

        buttonConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this).setMessage("Tap and hold!").
                        setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });

        shareBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent intent = new Intent(MainActivity.this, ShareActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });

        buttonInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent intent = new Intent(MainActivity.this, InfoAcitivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });

        buttonConfig.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent intent = new Intent(MainActivity.this, Config_Activity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });

    }

    /*public void shareApp(View v) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");

        //String imagePath = String.valueOf(getResources().getDrawable(R.drawable.info));

        //File imageFileToShare = new File(imagePath);
        ArrayList<Uri> uris = new ArrayList<Uri>();
        uris.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.info));

        share.putExtra(Intent.EXTRA_STREAM, uris);
        //share.putExtra(Intent.EXTRA_TEXT, "Dialing and navigating have never been easier: Use Drive App!");

        startActivity(Intent.createChooser(share, "Share Image!"));
    }*/

    private void startRecording() {

        if (mRecorder != null) {
            mRecorder.release();
        }

        mRecorder = new MediaRecorder();
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        image4.setEnabled(false);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.getMessage();
            //Log.e(LOG_TAG, "prepare() failed");
        }


    }

    private void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            ArrayList<String> uris = new ArrayList<String>();
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += "/MyMemo.3gp";
            uris.add(mFileName);
            image4.setEnabled(true);
            if (typeRecording.equals("1")) {
                //email(MainActivity.this, email, "test@test.com", "My record!", "My record!", uris);
                new SendMail().execute();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    public static void email(Context context, String emailTo, String emailCC,
                             String subject, String emailText, List<String> filePaths)
    {

        ArrayList<String> extra_subject = new ArrayList<String>();
        extra_subject.add(subject);
        ArrayList<String> extra_text = new ArrayList<String>();
        extra_text.add(emailText);
        //need to "send multiple" to get more than one attachment
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{emailTo});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, extra_subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, extra_text);
        //emailIntent.putExtra(android.content.Intent.EXTRA_CC,
        //       new String[]{emailCC});
        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        //convert from paths to Android friendly Parcelable Uri's
        for (String file : filePaths)
        {
            File fileIn = new File(file);
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    @Override
    public void onGPSUpdate(Location location) {
        String speedTXT;
        location.getLatitude();
        location.getLongitude();
        speed = location.getSpeed();

        String speedString = String.valueOf(roundDecimal(convertSpeed(speed), 1));
        String unitString = "km/h";


        speedTXT = String.format("%.0f", speed);

        if (Integer.parseInt(speedTXT) > 15) {
            isDriving = true;
        } else {
            isDriving = false;
        }

        //setSpeedText(R.id.gps_info, speedString + " " + unitString);
    }

    private void setSpeedText(int textid, String text) {
        Spannable span = new SpannableString(text);
        int firstPos = text.indexOf(32);

        span.setSpan(sizeSpanLarge, 0, firstPos,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(sizeSpanSmall, firstPos + 1, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView tv = ((TextView) findViewById(textid));

        tv.setText(span);
    }

    private double convertSpeed(double speed) {
        return ((speed * Constants.HOUR_MULTIPLIER) * Constants.UNIT_MULTIPLIERS);
    }

    private double roundDecimal(double value, final int decimalPlace) {
        BigDecimal bd = new BigDecimal(value);

        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        value = bd.doubleValue();

        return value;
    }

    @Override
    protected void onDestroy() {
        gpsManager.stopListening();
        gpsManager.setGPSCallback(null);

        gpsManager = null;

        super.onDestroy();
    }

    class StateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                    //Disconnect the call here...
                    if (isDriving) {
                        new AlertDialog.Builder(MainActivity.this).setTitle("Keep your eyes on the road!")
                                .setMessage("Keep your eyes on the road!")
                                .setPositiveButton("I'm not driving", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                }).show();

                        String phoneNumber = incomingNumber;
                        Log.d("INCOMING", phoneNumber);

                        try {
                            Class clazz = Class.forName(telephonymanager.getClass().getName());
                            Method method = clazz.getDeclaredMethod("getITelephony");
                            method.setAccessible(true);
                            ITelephony iTelephony = (ITelephony) method.invoke(telephonymanager);
                            iTelephony.endCall();
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(phoneNumber, null, smsText, null, null);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    } else {
                        new AlertDialog.Builder(MainActivity.this).setTitle("Keep your eyes on the road!")
                                .setMessage("Keep your eyes on the road!")
                                .setPositiveButton("I'm not driving", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                }).show();
                        String phoneNumber = incomingNumber;
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(phoneNumber, null, smsText, null, null);
                    }
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        ((TextView) findViewById(R.id.gps_info))
                .setText(getString(R.string.gpsSPEED));

        //tapImage = (ImageView) findViewById(R.id.tapObject);
        image1 = (ImageView) findViewById(R.id.imageView1);
        image2 = (ImageView) findViewById(R.id.imageView2);
        image3 = (ImageView) findViewById(R.id.imageView3);
        image4 = (ImageView) findViewById(R.id.imageView4);
        textLayout = (RelativeLayout) findViewById(R.id.textLayout);
        image2.requestLayout();
        image2.getLayoutParams().width = 0;
        image3.requestLayout();
        image3.getLayoutParams().width = 0;
        image4.requestLayout();
        image4.getLayoutParams().width = 0;
        ChangeSizeTime(1);

    }

    /*public void changeSize(int widthSize) {
        image1.requestLayout();
        image1.getLayoutParams().width = widthSize;
    }*/

    public void ChangeSizeTime(int imageNumber) {

        if (imageNumber == 1) {


            CountDownTimer start = new CountDownTimer(800, 20) {



                @Override
                public void onTick(long l) {
                    imageWidth = imageWidth + 50;
                    //changeSize(imageWidth);
                    image1.requestLayout();
                    image1.getLayoutParams().width = imageWidth;
                    //int imageStats = image1.getWidth();
                    //tapImage.postInvalidate();
                    //Log.d("IMAGE1", String.valueOf(imageStats));

                }

                @Override
                public void onFinish() {
                    //Log.d("done", "done");
                    ChangeSizeTime(2);

                }
            }.start();
        }

        if (imageNumber == 2) {
            image2.setVisibility(View.VISIBLE);
            imageWidth = 50;
            CountDownTimer start = new CountDownTimer(400, 20) {

                @Override
                public void onTick(long l) {
                    imageWidth = imageWidth + 50;
                    image2.requestLayout();
                    image2.getLayoutParams().width = imageWidth;
                    //changeSize(imageWidth);
                    //int imageStats = image2.getWidth();
                    //tapImage.postInvalidate();
                    //Log.d("IMAGE2", String.valueOf(imageStats));

                }

                @Override
                public void onFinish() {
                    //Log.d("done", "done");
                    ChangeSizeTime(3);
                }
            }.start();
        }

        if (imageNumber == 3) {
            image3.setVisibility(View.VISIBLE);
            imageWidth = 50;
            CountDownTimer start = new CountDownTimer(230, 5) {

                @Override
                public void onTick(long l) {
                    imageWidth = imageWidth + 50;
                    image3.requestLayout();
                    image3.getLayoutParams().width = imageWidth;
                    //changeSize(imageWidth);
                    //int imageStats = image2.getWidth();
                    //tapImage.postInvalidate();
                    //Log.d("IMAGE2", String.valueOf(imageStats));

                }

                @Override
                public void onFinish() {
                    //Log.d("done", "done");
                    ChangeSizeTime(4);
                }
            }.start();
        }
        if (imageNumber == 4) {
            image4.setVisibility(View.VISIBLE);
            imageWidth = 50;
            CountDownTimer start = new CountDownTimer(100, 4) {

                @Override
                public void onTick(long l) {
                    imageWidth = imageWidth + 80;
                    image4.requestLayout();
                    image4.getLayoutParams().width = imageWidth;
                    //changeSize(imageWidth);
                    //int imageStats = image4.getWidth();
                    //tapImage.postInvalidate();
                    //Log.d("IMAGE2", String.valueOf(imageStats));

                }

                @Override
                public void onFinish() {
                    textLayout.setVisibility(View.VISIBLE);
                    //Log.d("done", "done");
                }
            }.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        image1.setVisibility(View.GONE);
//        image2.setVisibility(View.GONE);
        //image3.setVisibility(View.GONE);
        //image4.setVisibility(View.GONE);
        image1.getLayoutParams().width = 0;
        image2.getLayoutParams().width = 0;
        image3.getLayoutParams().width = 0;
        image4.getLayoutParams().width = 0;



    }

    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    GestureDetector.SimpleOnGestureListener simpleongesturelistener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
                Log.d("SWIPE", "right to left");
                String tapSwipe = "8";
                String query = "SELECT " + eDB.COMMAND_NAME + ", " + eDB.ACTION + ", " + eDB.EMAIL + ", " + eDB.TEXT + " FROM " + eDB.TABLE_CONFIGS + " WHERE " + eDB.GESTURE + " = " + tapSwipe;

                Cursor cursor = sqlDB.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    String command = cursor.getString(cursor.getColumnIndex(dbConfig.COMMAND_NAME));
                    String telephone = cursor.getString(cursor.getColumnIndex(dbConfig.ACTION));
                    String mYemail = cursor.getString(cursor.getColumnIndex(dbConfig.EMAIL));
                    String text = cursor.getString(cursor.getColumnIndex(dbConfig.TEXT));
                    Log.d("Command is", command);
                    Log.d("Telephone is", telephone);

                    if (command.equals("1")) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + telephone));
                        startActivity(intent);
                    } else if(command.equals("2")) {

                        email = mYemail;
                        typeRecording = telephone;


                        Long timer = Long.parseLong(text);

                        timer *= 1000;

                        startRecording();

                        Toast.makeText(getApplicationContext(), "Start recording...",
                                Toast.LENGTH_SHORT).show();

                        CountDownTimer start = new CountDownTimer(timer, 1000) {

                            @Override
                            public void onTick(long l) {
                                Toast.makeText(getApplicationContext(), "Recording!!!",
                                        Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFinish() {
                                stopRecording();

                            }
                        }.start();


                    } else if (command.equals("3")) {
                        try
                        {
                            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(telephone) );
                            startActivity( intent );
                        }
                        catch ( ActivityNotFoundException ex  )
                        {
                            Intent intent =
                                    new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                            startActivity(intent);
                        }
                    }

                }
                return true; //Right to left
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
                Log.d("SWIPE", "left to right");
                String tapSwipe = "9";
                String query = "SELECT " + eDB.COMMAND_NAME + ", " + eDB.ACTION + ", " + eDB.EMAIL + ", " + eDB.TEXT + " FROM " + eDB.TABLE_CONFIGS + " WHERE " + eDB.GESTURE + " = " + tapSwipe;


                Cursor cursor = sqlDB.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    String command = cursor.getString(cursor.getColumnIndex(dbConfig.COMMAND_NAME));
                    String telephone = cursor.getString(cursor.getColumnIndex(dbConfig.ACTION));
                    String mYemail = cursor.getString(cursor.getColumnIndex(dbConfig.EMAIL));
                    String text = cursor.getString(cursor.getColumnIndex(dbConfig.TEXT));
                    Log.d("Command is", command);
                    Log.d("Telephone is", telephone);

                    if (command.equals("1")) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + telephone));
                        startActivity(intent);
                    } else if(command.equals("2")) {
                        email = mYemail;
                        typeRecording = telephone;


                        Long timer = Long.parseLong(text);

                        timer *= 1000;

                        startRecording();

                        Toast.makeText(getApplicationContext(), "Start recording...",
                                Toast.LENGTH_SHORT).show();

                        CountDownTimer start = new CountDownTimer(timer, 1000) {

                            @Override
                            public void onTick(long l) {
                                Toast.makeText(getApplicationContext(), "Recording!!!",
                                        Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFinish() {
                                stopRecording();

                            }
                        }.start();
                    } else if (command.equals("3")) {
                        try
                        {
                            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(telephone) );
                            startActivity( intent );
                        }
                        catch ( ActivityNotFoundException ex  )
                        {
                            Intent intent =
                                    new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                            startActivity(intent);
                        }
                    }

                }
                return true; //Left to right
            }

            //This will test for up and down movement.
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
                Log.d("SWIPE", "To top");
                String tapSwipe = "6";
                String query = "SELECT " + eDB.COMMAND_NAME + ", " + eDB.ACTION + ", " + eDB.EMAIL + ", " + eDB.TEXT + " FROM " + eDB.TABLE_CONFIGS + " WHERE " + eDB.GESTURE + " = " + tapSwipe;

                Cursor cursor = sqlDB.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    String command = cursor.getString(cursor.getColumnIndex(dbConfig.COMMAND_NAME));
                    String telephone = cursor.getString(cursor.getColumnIndex(dbConfig.ACTION));
                    String mYemail = cursor.getString(cursor.getColumnIndex(dbConfig.EMAIL));
                    String text = cursor.getString(cursor.getColumnIndex(dbConfig.TEXT));
                    Log.d("Command is", command);
                    Log.d("Telephone is", telephone);

                    if (command.equals("1")) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + telephone));
                        startActivity(intent);
                    } else if(command.equals("2")) {
                        email = mYemail;
                        typeRecording = telephone;


                        Long timer = Long.parseLong(text);

                        timer *= 1000;

                        startRecording();

                        Toast.makeText(getApplicationContext(), "Start recording...",
                                Toast.LENGTH_SHORT).show();

                        CountDownTimer start = new CountDownTimer(timer, 1000) {

                            @Override
                            public void onTick(long l) {
                                Toast.makeText(getApplicationContext(), "Recording!!!",
                                        Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFinish() {
                                stopRecording();

                            }
                        }.start();
                    } else if (command.equals("3")) {
                        try
                        {
                            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(telephone) );
                            startActivity( intent );
                        }
                        catch ( ActivityNotFoundException ex  )
                        {
                            Intent intent =
                                    new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                            startActivity(intent);
                        }
                    }

                }
                return false; //Bottom to top
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
                Log.d("SWIPE", "To bottom");
                String tapSwipe = "7";
                String query = "SELECT " + eDB.COMMAND_NAME + ", " + eDB.ACTION + ", " + eDB.EMAIL + ", " + eDB.TEXT + " FROM " + eDB.TABLE_CONFIGS + " WHERE " + eDB.GESTURE + " = " + tapSwipe;

                Cursor cursor = sqlDB.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    String command = cursor.getString(cursor.getColumnIndex(dbConfig.COMMAND_NAME));
                    String telephone = cursor.getString(cursor.getColumnIndex(dbConfig.ACTION));
                    String mYemail = cursor.getString(cursor.getColumnIndex(dbConfig.EMAIL));
                    String text = cursor.getString(cursor.getColumnIndex(dbConfig.TEXT));
                    Log.d("Command is", command);
                    Log.d("Telephone is", telephone);

                    if (command.equals("1")) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + telephone));
                        startActivity(intent);
                    } else if(command.equals("2")) {
                        email = mYemail;
                        typeRecording = telephone;


                        Long timer = Long.parseLong(text);

                        timer *= 1000;

                        startRecording();

                        Toast.makeText(getApplicationContext(), "Start recording...",
                                Toast.LENGTH_SHORT).show();

                        CountDownTimer start = new CountDownTimer(timer, 1000) {

                            @Override
                            public void onTick(long l) {
                                Toast.makeText(getApplicationContext(), "Recording!!!",
                                        Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFinish() {
                                stopRecording();

                            }
                        }.start();
                    } else if (command.equals("3")) {
                        try
                        {
                            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(telephone) );
                            startActivity( intent );
                        }
                        catch ( ActivityNotFoundException ex  )
                        {
                            Intent intent =
                                    new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                            startActivity(intent);
                        }
                    }

                }
                return false; //Top to bottom
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

    GestureDetector gestureDetector = new GestureDetector(getBaseContext(),
            simpleongesturelistener);


    public void checkTap(View v) {
        tapCounter++;
        //Log.d("Число нажатий: ", String.valueOf(tapCounter));

        if(isRunning == false) {

            handler.postDelayed(runnable, 2500);
            isRunning = true;

            /*Handler handler = new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 2500);*/
        } else {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 2500);
        }
        /*new CountDownTimer(1500, 500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                tapString = String.valueOf(tapCounter);

                getDB();

                //dbConfig.COMMAND_NAME = tapString;

            }
        }.start();*/

    }

    class SendMail extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            String to=email;//"kos891@gmail.com";//change accordingly
            //from address
            final String user="driveappdriveapp@gmail.com";//change accordingly
            final String password="DriveappdriveapP2014";//change accordingly
            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mc);
            //1) get the session object
            Properties properties = System.getProperties();
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "465");

            Session session = Session.getDefaultInstance(properties,
                    new javax.mail.Authenticator() {
                        protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user,password);
                        }
                    });

            //2) compose message
            try{
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(user));
                message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
                message.setSubject("Record");
                //3) create MimeBodyPart object and set your message content
                BodyPart messageBodyPart1 = new MimeBodyPart();
                messageBodyPart1.setText("Memo Record");
                //4) create new MimeBodyPart object and set DataHandler object to this object
                MimeBodyPart messageBodyPart2 = new MimeBodyPart();
                //Location of file to be attached
                String filename = mFileName;//Environment.getExternalStorageDirectory().getPath()+"/MyMemo-4-53.3gp";//change accordingly
                DataSource source = new FileDataSource(filename);
                messageBodyPart2.setDataHandler(new DataHandler(source));
                messageBodyPart2.setFileName("MyMemo.3gp");
                //5) create Multipart object and add MimeBodyPart objects to this object
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart1);
                multipart.addBodyPart(messageBodyPart2);
                //6) set the multiplart object to the message object
                message.setContent(multipart );
                //7) send message
                Transport.send(message);
                System.out.println("MESSAGE SENT....");
            }catch (MessagingException ex) {ex.printStackTrace();}

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Toast.makeText(MainActivity.this, "SEND", Toast.LENGTH_LONG).show();
        }
    }

    public void getDB() {
        String query = "SELECT " + eDB.COMMAND_NAME + ", " + eDB.ACTION + ", " + eDB.EMAIL + ", " + eDB.TEXT + " FROM " + eDB.TABLE_CONFIGS + " WHERE " + eDB.GESTURE + " = " + tapString;

        Cursor cursor = sqlDB.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String command = cursor.getString(cursor.getColumnIndex(dbConfig.COMMAND_NAME));
            String telephone = cursor.getString(cursor.getColumnIndex(dbConfig.ACTION));
            final String mYemail = cursor.getString(cursor.getColumnIndex(dbConfig.EMAIL));
            String text = cursor.getString(cursor.getColumnIndex(dbConfig.TEXT));
            Log.d("Command is", command);
            Log.d("Telephone is", telephone);

            if (command.equals("1")) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + telephone));
                startActivity(intent);
            } else if(command.equals("2")) {
                email = mYemail;
                typeRecording = telephone;

                Long timer = Long.parseLong(text);

                timer *= 1000;

                Recordings(timer);

            } else if (command.equals("3")) {
                try
                {
                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(telephone) );
                    startActivity( intent );
                }
                catch ( ActivityNotFoundException ex  )
                {
                    Intent intent =
                            new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                    startActivity(intent);
                }
            }

        }


        tapCounter = 0;
    }

    public void Recordings(Long time) {


        startRecording();

        Toast.makeText(getApplicationContext(), "Start recording...",
                Toast.LENGTH_SHORT).show();

        CountDownTimer start = new CountDownTimer(time, 2000) {

            @Override
            public void onTick(long l) {
                Toast.makeText(getApplicationContext(), "Recording!!!",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFinish() {
                stopRecording();
                Toast.makeText(getApplicationContext(), "Record stopped!",
                        Toast.LENGTH_SHORT).show();

            }
        }.start();
    }

    public void OpenSettings(View v) {

        //finish();
    }

    public void OpenInfo(View v) {

        //finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SMS.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;

        }
    }


}
