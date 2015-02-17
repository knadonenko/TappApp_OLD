package com.taptester.tappapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.taptester.database.dbConfig;


/*
Created somewhere by me
 */


public class DialActivity extends ActionBarActivity {

    //private static final int PICK_CONTACT = 3;
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;

    String phoneNo;
    private Uri uriContact;
    private String contactID;
    private String contactName;
    private String contactNumber = "";
    private int configButton;

    EditText editPhone, editName;
    Intent configIntent;

    private dbConfig eDB;
    private SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);

        eDB = new dbConfig(this);
        sqlDB = eDB.getWritableDatabase();

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

    @Override
    protected void onResume() {
        super.onResume();
        editName = (EditText) findViewById(R.id.editText);
        editPhone = (EditText) findViewById(R.id.editText2);
    }

    public void openAddress (View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_CONTACTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d("Response: ", data.toString());
            uriContact = data.getData();

            retrieveContactName();
            retrieveContactNumber();
            updateFields();

        }
    }

    private void retrieveContactNumber() {

        contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        //Log.d("Contact ID: ", contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        } else {
            cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME,

                    new String[]{contactID},
                    null);

            if (cursorPhone.moveToFirst()) {
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            } else {
                cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK,

                        new String[]{contactID},
                        null);

                if (cursorPhone.moveToFirst()) {
                    contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                } else {
                    cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE_OTHER,

                            new String[]{contactID},
                            null);

                    if (cursorPhone.moveToFirst()) {
                        contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                }
            }
        }



                /*if (contactNumber.length() == 0) {
                    Cursor cursorWorkPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK,

                            new String[]{contactID},
                            null);

                    if (cursorPhone.moveToFirst()) {
                        contactNumber = cursorWorkPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                }*/

//        Log.d("CONTACT", contactNumber);


        cursorPhone.close();

        //Log.d("Contact Phone Number: ", contactNumber);
    }

    private void retrieveContactName() {

        contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        Log.d("Contact Name: ", contactName);

    }

    public void addConfig(View v) {

        contactNumber = editPhone.getText().toString();
        contactName = editName.getText().toString();

        if (editName.getText().length() == 0 || editPhone.getText().length() == 0 || contactNumber == "") {
            Toast.makeText(DialActivity.this,
                    "Check contacts name and phone number!", Toast.LENGTH_LONG)
                    .show();
        } else {

            ContentValues contVal = new ContentValues();
            contVal.put(eDB.COMMAND_NAME, "1");
            contVal.put(eDB.GESTURE, configButton);
            contVal.put(eDB.ACTION, contactNumber);
            contVal.put(eDB.TEXT, contactName);

            sqlDB.insert(dbConfig.TABLE_CONFIGS, null, contVal);
            SaveConfig(configButton);

            Log.d("NUMBER", String.valueOf(contactNumber.length()));

            finish();
        }


    }

    public void SaveConfig(int buttonNum) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DialActivity.this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(String.valueOf(buttonNum), String.valueOf(buttonNum));
        edit.commit();
    }

    private void updateFields(){
        editName.setText(contactName);
        editPhone.setText(contactNumber);
    }

    public void goBack(View v) {
        onBackPressed();
        eDB.close();
        sqlDB.close();
        finish();
    }

}
