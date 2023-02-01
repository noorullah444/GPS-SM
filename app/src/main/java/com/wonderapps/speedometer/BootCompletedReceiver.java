package com.wonderapps.speedometer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import static com.wonderapps.speedometer.Activities.MainActivity.resumed;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MyPREFERENCES;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.stopBtnIsVisible;

public class BootCompletedReceiver extends BroadcastReceiver {
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    @Override
    public void onReceive(Context context, Intent arg1) {
        // TODO Auto-generated method stub
        Log.d("boot_broadcast_poc", "starting service...");
//        Toast.makeText(context, "service started", Toast.LENGTH_SHORT).show();


        pref = context.getSharedPreferences(MyPREFERENCES, 0);
        editor = pref.edit();
        if (stopBtnIsVisible) {
            resumed = true;
            editor.putBoolean("isResumed", resumed);
            editor.apply();
        } else {
            resumed = false;
            editor.putBoolean("isResumed", resumed);
            editor.apply();
        }


        Log.d("destroyed", "main Activity destroyed");
    }
//        context.startService(new Intent(context, NotifyingDailyService.class));
    }

