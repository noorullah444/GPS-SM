package com.wonderapps.speedometer;

import android.app.ProgressDialog;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.wonderapps.speedometer.Activities.MainActivity;
import com.wonderapps.speedometer.Fragments.DigitalFragment;

import static com.wonderapps.speedometer.Fragments.DigitalFragment.statusTxt;
import static com.wonderapps.speedometer.Fragments.GraphFragment.statusTxtGraph;
import static com.wonderapps.speedometer.Fragments.MapFragment.statusTxtMap;
import static com.wonderapps.speedometer.LocationService.LocationService.mLastLoc;
import static com.wonderapps.speedometer.LocationService.LocationService.mLastLocationMillis;

public class MyGPSListener implements GpsStatus.Listener {
    private boolean isGPSFix;
    private ProgressDialog progressDialog;

    public void onGpsStatusChanged(int event) {
        progressDialog = new ProgressDialog(MainActivity.myContext, R.style.DialogeTheme);

        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                if ((SystemClock.elapsedRealtime() - mLastLocationMillis) < 20000) {
                    if (!isGPSFix) {
                        statusTxt.setText("");
                        statusTxtGraph.setText("");
                        statusTxtMap.setText("");
                        Log.i("GPS1234", "Fix Acquired");

                    }

                    isGPSFix = true;
                } else {
                    if (isGPSFix) {
                        statusTxt.setText(R.string.gps_signals_lost);
                        statusTxtGraph.setText(R.string.gps_signals_lost);
                        statusTxtMap.setText(R.string
                                .gps_signals_lost);
                        Log.i("GPS1234", "Fix Lost (expired)");
//                            .requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
                    }
                    isGPSFix = false;
                }

                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Log.i("GPS1234", "First Fix/ Refix");
                statusTxt.setText("");
                statusTxtGraph.setText("");
                statusTxtMap.setText("");
                isGPSFix = true;
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                Log.i("GPS1234", "Started!");
                statusTxt.setText("");
                statusTxtGraph.setText("");
                statusTxtMap.setText("");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.i("GPS1234", "Stopped");
                statusTxt.setText(R.string.gps_signals_lost);
                statusTxtGraph.setText(R.string.gps_signals_lost);
                statusTxtMap.setText(R.string.gps_signals_lost);
                break;
        }
    }

//    private void showProgressDialogWithTitle(String substring) {
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
////Without this user can hide loader by tapping outside screen
//        progressDialog.setCancelable(true);
//        progressDialog.setMessage(substring);
//        progressDialog.show();
//
//    }
}