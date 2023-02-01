package com.wonderapps.speedometer.LocationService;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.wonderapps.speedometer.Fragments.DigitalFragment;
import com.wonderapps.speedometer.Fragments.GraphFragment;
import com.wonderapps.speedometer.Fragments.MapFragment;
import com.wonderapps.speedometer.GraphTask;
import com.wonderapps.speedometer.R;

import java.text.DecimalFormat;

import static com.wonderapps.speedometer.Activities.MainActivity.MY_PREFS_NAME_SELECT_SOUND;
import static com.wonderapps.speedometer.Activities.MainActivity.MY_PREFS_NAME_SELECT_SOUNDONOF;
import static com.wonderapps.speedometer.Activities.MainActivity.isSpeedLimitOn;
import static com.wonderapps.speedometer.Activities.MainActivity.myContext;
import static com.wonderapps.speedometer.Activities.MainActivity.tabLayout;
import static com.wonderapps.speedometer.Activities.MainActivity.toolbar1;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.limitSpeed;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.mBuilder;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.restoredText;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.showTabLayout;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.showToolbar;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.status;
import static com.wonderapps.speedometer.Fragments.MapFragment.avgspeed;
import static com.wonderapps.speedometer.Fragments.MapFragment.maxspeed;


public class LocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    int restoredSound;
    SharedPreferences sound;
    public static double distanceGraph;
    GraphTask graphTask1 = new GraphTask(myContext);
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location lStart, lEnd;
    public static Location mCurrentLocation;
    public static Location mLastLoc;

    public static double distance = 0;
    static double distanceInMeter = 0;

    public static double speed;
    double speedInMeterPerSecond;
    public static long maxvalue;
    long MaximumSpeed = 0;

    public static MediaPlayer mediaPlayer;

    private final IBinder mBinder = new LocalBinder();

    DigitalFragment digitalFragment;
    public static MediaPlayer mp;
    public static double speedText = 0;
    public static double noOfSpeed = 0;
    public static double avgSpeedforAnyUnit;
    public static long mLastLocationMillis;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

//        DigitalFragment.locate.dismiss();
        return mBinder;
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //priority high power accuracy
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
        }
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }

    protected void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

            distance = 0;
            distanceInMeter = 0;
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this, "Connection Suspended", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocationMillis = SystemClock.elapsedRealtime();
        MapFragment.lat = location.getLatitude();
        MapFragment.lon = location.getLongitude();
//        DigitalFragment.locate.dismiss();

        // for saving to db, saving lat and long
        MapFragment.currentLat = location.getLatitude();
        MapFragment.currentLon = location.getLongitude();
        mCurrentLocation = location;

        if (lStart == null) {
            lStart = mCurrentLocation;
            lEnd = mCurrentLocation;
        } else {
            lEnd = mCurrentLocation;
            mLastLoc = lEnd;
        }

        // updating map polylines/ data in background service
        MapFragment.updateTrack();

// for getting speed for specified unit
        if (restoredText != null) {
            if (restoredText.equals("KM/H")) {                                                          // checking for user has set speed unit and then converting
                speed = location.getSpeed() * 3.6;
            } else if (restoredText.equals("M/S")) {
                speed = location.getSpeed();
            } else if (restoredText.equals("MPH")) {
                speed = location.getSpeed() * 1.609;
            } else if (restoredText.equals("Knots")) {
                speed = location.getSpeed() * 1.852;
            }
        }

        if (speed < 1) {    // if speed is less than 1 then set to 0, we want speed greater than 1
            speed = 0;
        }

        speedText += speed; // total speed
        noOfSpeed++;    // no of times


        updateUI();
        //updateUIAna();
        updateUImap();


        speedInMeterPerSecond = location.getSpeed(); // now its return speed in meter per second (m/s)


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (showToolbar && showTabLayout) {
                    toolbar1.animate().translationY(-toolbar1.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    tabLayout.animate().translationY(tabLayout.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    showToolbar = false;
                }
            }
        }, 5000);

        if (speed > maxvalue) {
            maxvalue = (long) speed;

        }
        Log.d("average" + " max speed ", String.valueOf(maxvalue));
        maxspeed.setText((String.valueOf(maxvalue)));
        GraphFragment.max_speed_graph.setText(String.valueOf(maxvalue));
        DigitalFragment.max_speed_tv_digital.setText(String.valueOf(maxvalue));

       /* GraphTask task = new GraphTask(getBaseContext());
        task.execute();*/
        //graph calls here
        graphTask1.doInBackground();
        // calling graph task when location changes
//        graphTask1.graphTask();
//        graphTask1.kaka((Activity) myContext);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public class LocalBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }


    }

    //The live feed of Distance and Speed are being set in the method below .
    private void updateUImap() {
        sound = getApplicationContext().getSharedPreferences(MY_PREFS_NAME_SELECT_SOUND, MODE_PRIVATE);
        restoredSound = sound.getInt("sound", 500);

        Log.d("valuesUpdate", "value update");
        distance = distance + (lStart.distanceTo(lEnd) / 1000.00);
        distanceInMeter = distanceInMeter + (lStart.distanceTo(lEnd));
        GraphFragment.distance_graph.setText(new DecimalFormat("#.##").format(distance));
        MapFragment.endTime = System.currentTimeMillis();
        DigitalFragment.digitSpeedView.updateSpeed((int) speed);
        MapFragment.digitSpeedView.updateSpeed((int) speed);
        GraphFragment.speedViewGraph.setVisibility(View.VISIBLE);
        GraphFragment.speedViewGraph.updateSpeed((int) speed);
        MapFragment.distance_tv_digital.setText(new DecimalFormat("#.##").format(distance));
        DigitalFragment.distance_tv_digital.setText(new DecimalFormat("#.##").format(distance));

        //with speed
        avgSpeedforAnyUnit = 0;
        DecimalFormat avgSpeedFormat = new DecimalFormat("0.0");
        try {
            avgSpeedforAnyUnit = speedText / noOfSpeed;
            Log.d("tTime", "t time  is : " + noOfSpeed);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //*******************************

        avgspeed.setText(String.format(avgSpeedFormat.format(avgSpeedforAnyUnit)));
        GraphFragment.avg_speed_graph.setText(avgSpeedFormat.format(avgSpeedforAnyUnit));
        DigitalFragment.avg_speed_tv_digital.setText(avgSpeedFormat.format(avgSpeedforAnyUnit));
        lStart = lEnd;

    }

    private void updateUI() {
        SharedPreferences soundonof = getApplicationContext().getSharedPreferences(MY_PREFS_NAME_SELECT_SOUNDONOF, MODE_PRIVATE);
        digitalFragment = new DigitalFragment();
        sound = getApplicationContext().getSharedPreferences(MY_PREFS_NAME_SELECT_SOUND, MODE_PRIVATE);
        String restoredSoundonof = soundonof.getString("SOUNDONOF", null);
        restoredSound = sound.getInt("sound", 500);
        if (DigitalFragment.p == 0) {
            distance = distance + (lStart.distanceTo(lEnd) / 1000.00);                                                      // distance in KM
            distanceInMeter = distanceInMeter + (lStart.distanceTo(lEnd));                                                  // distance in Meter
            DigitalFragment.endTime = System.currentTimeMillis();
            try {
                mBuilder.setContentText("Speed: " + (int) speed + " " + restoredText);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // init media player
            if (mp == null) {
                mp = MediaPlayer.create(LocationService.this, R.raw.alarm_sound1);
            }
            if (speed > 0.0) {                                                                                              // if speed is > 1 0.0

                if (isSpeedLimitOn) {                                                                                        // for checking speed limit option is on or not .......
                    if (speed >= Double.parseDouble(limitSpeed)) {
                        try {
                            if (mp.isPlaying())
                                return;
                            mp.start();
                            Log.d("paly_tone", "played ");
                        } catch (Exception e) {
                            Log.d("paly_tone", " exception " + e.getMessage());
                            e.printStackTrace();
                        }
                        Toast.makeText(this, getResources().getString(R.string.maximum_speed_limit), Toast.LENGTH_SHORT).show();
                    } else {
                        if (mp != null) {
                            if (mp.isPlaying()) {
                                Log.d("paly_tone", "main else played");
                                mp.stop();
                                mp.release();
                                mp = null;
                            }
                        }

                    }
                }
                if (restoredSoundonof != null) {
                    if (speed >= restoredSound && restoredSoundonof.equals("ON")) {
                    } else {

                    }
                }
            }

        } else {
            DigitalFragment.digitSpeedView.updateSpeed(0);
            DigitalFragment.digital_speed.setText("0");
            DigitalFragment.speedAnalog.speedTo(0);
        }
        lStart = lEnd;

    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (!status) {
            stopLocationUpdates();
            if (mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
            lStart = null;
            lEnd = null;
            distance = 0;
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.setLooping(false);
                }
            }
        }
        return super.onUnbind(intent);

    }


}

