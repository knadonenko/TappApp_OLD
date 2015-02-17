package com.taptester.tappapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by air on 03.02.15.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences smsPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String smsText = smsPreferences.getString("smsTXT", "I'll call you later");

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                str = msgs[i].getOriginatingAddress();
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(str, null, smsText, null, null);
//                str += " :";
//                str += msgs[i].getMessageBody().toString();
//                str += "\n";
            }
            //---display the new SMS message---
            Toast.makeText(context, "Incoming SMS", Toast.LENGTH_SHORT).show();
        }

    }
}
