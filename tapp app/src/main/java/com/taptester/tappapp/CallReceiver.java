package com.taptester.tappapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by air on 13.09.14.
 */
public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).
                equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            /*alertDialog.setTitle("Keep your eyes on the road!")
                    .setMessage("Keep your eyes on the road!")
                    .setPositiveButton("I'm not driving", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();*/
            Toast.makeText(context, "Keep your eyes on the road", Toast.LENGTH_LONG).show();
        }
    }
}
