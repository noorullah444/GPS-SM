package com.wonderapps.speedometer.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.redinput.compassview.CompassView;
import com.wonderapps.speedometer.Activities.MainActivity;
import com.wonderapps.speedometer.DigitSpeedView;
import com.wonderapps.speedometer.GPSUtil.GpsUtils;
import com.wonderapps.speedometer.ImageSpeedometer;
import com.wonderapps.speedometer.LocationService.LocationService;
import com.wonderapps.speedometer.MyGPSListener;
import com.wonderapps.speedometer.R;
import com.wonderapps.speedometer.Service.MyService;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;
import static com.wonderapps.speedometer.Activities.MainActivity.myContext;
import static com.wonderapps.speedometer.Activities.MainActivity.restoredNoti;
import static com.wonderapps.speedometer.Activities.MainActivity.resumed;
import static com.wonderapps.speedometer.Activities.MainActivity.tabLayout;
import static com.wonderapps.speedometer.Activities.MainActivity.toolbar1;
import static com.wonderapps.speedometer.Activities.SettingsActivity.ANALOG;
import static com.wonderapps.speedometer.Activities.SettingsActivity.COLOR_KEY;
import static com.wonderapps.speedometer.Activities.SettingsActivity.COLOR_NAME;
import static com.wonderapps.speedometer.Activities.SettingsActivity.DIGITAL;
import static com.wonderapps.speedometer.Activities.SettingsActivity.KNOTS;
import static com.wonderapps.speedometer.Activities.SettingsActivity.KPH;
import static com.wonderapps.speedometer.Activities.SettingsActivity.LIMItSPEED;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MPH;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MPS;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MyPREFERENCES;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwAVgSPEED;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwBATTERySTATUS;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwCOMPASS;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwMAxSPEED;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwTRAVElDISTANCE;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwTRAVElTIME;
import static com.wonderapps.speedometer.Fragments.GraphFragment.gpsStatusGraph;
import static com.wonderapps.speedometer.Fragments.GraphFragment.internetStatusGraph;
import static com.wonderapps.speedometer.Fragments.GraphFragment.statusTxtGraph;
import static com.wonderapps.speedometer.Fragments.MapFragment.avgspeed;
import static com.wonderapps.speedometer.Fragments.MapFragment.gpsStatusMap;
import static com.wonderapps.speedometer.Fragments.MapFragment.internetStatusMap;
import static com.wonderapps.speedometer.Fragments.MapFragment.lastKnownLatLng;
import static com.wonderapps.speedometer.Fragments.MapFragment.maxspeed;
import static com.wonderapps.speedometer.Fragments.MapFragment.statusTxtMap;
import static com.wonderapps.speedometer.LocationService.LocationService.avgSpeedforAnyUnit;
import static com.wonderapps.speedometer.LocationService.LocationService.maxvalue;
import static com.wonderapps.speedometer.LocationService.LocationService.mp;

public class DigitalFragment extends Fragment implements SensorEventListener {
    public static TextView distance_tv_digital,
            avg_speed_tv_digital, max_speed_tv_digital,
            timer_tv_digital, timerTextWhite;
    TextView avgSpeed_km_per_hr, meterSpeedKm_per_hr, maxSpeed_per_hr,
            distance_km, speed_km_per_hr_with_digital, batteryPercentage, degreeTextView;
    ImageView settingsImg, speed_bg;
    public static TextView statusTxt, gpsStatus, internetStatus;

    RelativeLayout avgSpeedBg, maxSpeedBg;
    ImageView kmphbtn, mphbtn;
    Button start, pause, stop;
    public static Button startDigitalButton;
    public static Button stopDigitalButton;
    public static Button HUDDigitalButton;
    private final boolean check = false;
    ConstraintLayout mainDigitalConstraintLayout;
    public static DigitSpeedView digitSpeedView;
    public static ImageSpeedometer speedAnalog;
    public static long startTime, endTime;
    private ProgressDialog progressDialog;
    public static int p = 0;
    public static long MillisecondTime, StartTime, TimeBuff, UpdateTime, currentTime = 0L;
    public static int Seconds, Minutes, MilliSeconds, mysecond, Hours;
    public static int cSeconds, cMinutes, cHours;
    public static NotificationManager mNotificationManager;
    public static NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static LocationService myService;
    public static boolean status;
    public static boolean statusForService = false;
    LocationManager locationManager;
    int mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientationSensor = new float[3];
    private final float[] mLastAccelerometer = new float[3];
    private final float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    public static boolean stopBtnIsVisible = false;
    public static ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (LocationService.LocalBinder) service;
            DigitalFragment.myService = binder.getService();
            DigitalFragment.status = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
            DigitalFragment.status = false;
        }
    };
    public static boolean showToolbar = true;
    public static boolean showTabLayout = true;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int themeColor;
    private CompassView compass;
    private ImageView meterImg;
    private ImageView batterImg;
    private ImageView compassBG;
    public static TextView digital_speed;
    private int orientation;
    private TextView distance_text;
    private TextView avg_speed_white_text;
    private TextView maxSpeedTextWhite;
    private boolean hudbol = false;
    //    private InterstitialAd mInterstitialAd;
    public static String restoredText;
    //    private boolean restoredNoti = false;
    public static String limitSpeed;
    public static boolean animateMapBol = false;
    public static int buttonHideDelay = 9000;
    private static boolean state;
    public static boolean gpsEnabled = false;
    private static AlertDialog alert;
    public static Toast toast;
    private View myView;
    public static boolean rotate = false;
    private static LocationService.LocalBinder binder;
    public static long milies;
    private static LocationService locationService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_digital, container, false);
//        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME_SELECT_UNIT, MODE_PRIVATE);
        PackageManager PM = requireContext().getPackageManager();
        boolean acc = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        boolean acc2 = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
        DecimalFormat df = new DecimalFormat("#.##");
        myView = view;
        //***************************************************************

        // for theme colors
        pref = requireActivity().getSharedPreferences(MyPREFERENCES, 0);
        editor = pref.edit();

        initView(view);

        locationService = new LocationService();

        start();                                                                                                                  // starting sensor for compass

        SensorManager mSensorManager = (SensorManager) requireContext().getSystemService(SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        requireActivity().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {                                                               // compass is in portrait only
            compass = view.findViewById(R.id.compass);
            compass.setBackgroundColor(Color.BLACK);
            compass.setLineColor(Color.WHITE);
            compass.setMarkerColor(Color.RED);
            compass.setTextColor(getResources().getColor(R.color.tab_item_color));
            compass.setShowMarker(false);                                                                           // we are using our own
            compass.setTextSize(28);
            compass.setRangeDegrees(180);
            compass.setOnCompassDragListener(new CompassView.OnCompassDragListener() {
                @Override
                public void onCompassDragListener(float degrees) {
                    // Do what you want with the degrees
                    compass.setDegrees(degrees);
                }
            });
        }

        mainDigitalConstraintLayout.setOnClickListener(new View.OnClickListener() {                             // showing or hiding toolbar and viewpager with tap
            @Override
            public void onClick(View v) {
                if (showToolbar && showTabLayout) {
                    toolbar1.animate().translationY(-toolbar1.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    tabLayout.animate().translationY(tabLayout.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    startDigitalButton.setVisibility(View.GONE);
                    stopDigitalButton.setVisibility(View.GONE);
                    HUDDigitalButton.setVisibility(View.GONE);
                    showToolbar = false;
                } else {
                    toolbar1.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                    tabLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

                    if (stopBtnIsVisible) {
                        stopDigitalButton.setVisibility(View.VISIBLE);
                    } else {
                        startDigitalButton.setVisibility(View.VISIBLE);
                    }

                    HUDDigitalButton.setVisibility(View.VISIBLE);
                    showToolbar = true;
                }


                final Handler handler = new Handler(Looper.getMainLooper());                                                // for hiding view pager or toolbar after 3 secs
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toolbar1.animate().translationY(-toolbar1.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                        tabLayout.animate().translationY(tabLayout.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                        startDigitalButton.setVisibility(View.GONE);
                        stopDigitalButton.setVisibility(View.GONE);
                        HUDDigitalButton.setVisibility(View.GONE);
                        showToolbar = false;
                    }
                }, buttonHideDelay);
            }


        });
        ///handler
        handler = new Handler();
        //handler ends


        max_speed_tv_digital.setText("0");

        startDigitalButton.setOnClickListener(v -> {
            status = false;
            animateMapBol = true;
            checkGps(getActivity());


        });


        stopDigitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateMapBol = false;

                MapFragment mapFragment = new MapFragment();
                mapFragment.showDialog(getActivity());

            }
        });
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        if (savedInstanceState != null) {
            state = savedInstanceState.getBoolean("buttonState");
            if (state) {

//                if (!rotate) {
//                    StartTime = SystemClock.uptimeMillis();
//                }
//                handler.postDelayed(runnable, 0);
                if (stopBtnIsVisible) {
                    stopDigitalButton.setVisibility(View.INVISIBLE);
                    startDigitalButton.setVisibility(View.VISIBLE);
                } else {
                    stopDigitalButton.setVisibility(View.VISIBLE);
                    startDigitalButton.setVisibility(View.INVISIBLE);
                }

                try {
                    MapFragment.startBtn.setVisibility(View.INVISIBLE);
                    MapFragment.stopBtn.setVisibility(View.VISIBLE);
                    GraphFragment.startDigitalBtn.setVisibility(View.INVISIBLE);
                    GraphFragment.stopDigitalButton.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                startTime = savedInstanceState.getLong("timer");
                int mins = savedInstanceState.getInt("minutes");
                int secs = savedInstanceState.getInt("seconds");
                boolean bol = savedInstanceState.getBoolean("buttonState");


                Log.e("savedInsValues", "after");
                Log.d("savedInsValues", String.valueOf(startTime));
                Log.d("savedInsValues", String.valueOf(mins));
                Log.d("savedInsValues", String.valueOf(secs));
                Log.d("savedInsValues", String.valueOf(bol));

                Minutes = mins;
                Seconds = secs;
                Hours = Minutes / 60;
                timer_tv_digital.setText(Hours + ":"
                        + String.format("%02d", Minutes) + ":"
                        + String.format("%02d", Seconds));
            }
        }


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editor.putBoolean("stopButtonState", stopBtnIsVisible);     // saving buttons state when app destroys
        editor.apply();

        rotate = true;      // this is used when we rotate screen
        Log.d("destroyed123", "Digital fragment   " + stopBtnIsVisible);


    }

    @Override
    public void onResume() {
        super.onResume();
        if (gpsEnabled) {                                                                                               // this checks after GPS dialogs and we are setting it true when GPS turns on
            Log.d("exceptionhai", " yes in DigitalFragment");
            initProgress(myContext);
        }

        if (alert != null && alert.isShowing()) {                                                                        // for dismissing GPS dialog
            alert.dismiss();
        }

        if (!isGPSEnabled(myContext.getApplicationContext())) {                                                             // when gps is off
            try {
                if (stopBtnIsVisible) {                                                                                 // when stop button is visible means GPS is running
                    gpsStatus.setText(R.string.gps_off_on);
                    gpsStatusGraph.setText(R.string.gps_off_on);
                    gpsStatusMap.setText(R.string.gps_off_on);
                }
                DecimalFormat avgSpeedFormat;
                if (avgSpeedforAnyUnit <= 0.0) {                                                                     // if average speed is less than 0 then keepin in 1 digit
                    avgSpeedFormat = new DecimalFormat("0");
                } else {
                    avgSpeedFormat = new DecimalFormat("0.0");

                }
                avgspeed.setText(avgSpeedFormat.format(avgSpeedforAnyUnit));
                GraphFragment.avg_speed_graph.setText(avgSpeedFormat.format(avgSpeedforAnyUnit));
                DigitalFragment.avg_speed_tv_digital.setText(avgSpeedFormat.format(avgSpeedforAnyUnit));

                MapFragment.distance_tv_digital.setText(new DecimalFormat("#.##").format(LocationService.distance));        // for distance in all fragments
                DigitalFragment.distance_tv_digital.setText(new DecimalFormat("#.##").format(LocationService.distance));
                GraphFragment.distance_graph.setText(new DecimalFormat("#.##").format(LocationService.distance));

                maxspeed.setText((String.valueOf(maxvalue)));                                                                               // maxmimum speed value in all fragments
                GraphFragment.max_speed_graph.setText(String.valueOf(maxvalue));
                DigitalFragment.max_speed_tv_digital.setText(String.valueOf(maxvalue));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (!isNetworkConnected(myContext.getApplicationContext())) {                                                                       // checking internet
            try {
                internetStatus.setText(R.string.no_internet);
                internetStatusGraph.setText(R.string.no_internet);
                internetStatusMap.setText(R.string.no_internet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        themeColor = pref.getInt(COLOR_KEY, R.color.green);                                                                            // 2131034226 default green color
        limitSpeed = pref.getString(LIMItSPEED, "100");                                                                             // for maximum speed, we are keeping 100 default so


        setThemeColor(themeColor);                                                                                                      // theme color will be applied for all textviews and some other views
        getPrefValuesForMeters();                                                                                                       // for meter color and compass colors, pref values
        setSpeedUnits();                                                                                                                // setting speed units, these values are also coming from preferences
        showOrHideSpeedDistance();                                                                                                      // to show or hide speed distance when user sets it in settings
        showOrHideAvgSpeed();                                                                                                           // same for average speed
        showOrHideMaxSpeed();                                                                                                           // same...
        showOrHideTimer();                                                                                                              // timer (stop watch, time)

        if (orientation == Configuration.ORIENTATION_PORTRAIT)                                                                          // as we are showing compass only in portrait so checking portrait
            showOrHideCompass();

        resumed = pref.getBoolean("isResumed", false);                                                                            // saving buttons state in prefs
        stopBtnIsVisible = resumed;

        if (resumed) {                                                                                                                   // for buttons, like when we clear app from RAM and then keep the buttons state
            stopDigitalButton.setVisibility(View.VISIBLE);
            HUDDigitalButton.setVisibility(View.VISIBLE);
        } else {
            startDigitalButton.setVisibility(View.VISIBLE);
            HUDDigitalButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("buttonState", stopBtnIsVisible);
        outState.putLong("startTime", StartTime);
        outState.putInt("minutes", Minutes);
        outState.putInt("seconds", Seconds);
        outState.putLong("updateTime", UpdateTime);
        outState.putLong("milles", MillisecondTime);
        outState.putBoolean("hudBol", hudbol);
        outState.putLong("crtime", SystemClock.uptimeMillis());

        rotate = true;
        Log.d("savedInsValues", " crtime " + MillisecondTime);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientationSensor)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientationSensor);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientationSensor)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            compass.setDegrees(mAzimuth);
            degreeTextView.setText(mAzimuth + " °");
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            int mins = savedInstanceState.getInt("minutes");
            int secs = savedInstanceState.getInt("seconds");
            long startT = savedInstanceState.getLong("startTime");
            long updTime = savedInstanceState.getLong("updateTime");
            milies = savedInstanceState.getLong("crtime");


            long curTime = savedInstanceState.getLong("crtime");
            boolean bol = savedInstanceState.getBoolean("buttonState");
            Log.d("mins_and_secs", String.valueOf(startTime));
            Log.d("mins_and_secs", String.valueOf(mins));
            Log.d("mins_and_secs", String.valueOf(secs));
            Log.d("mins_and_secs", String.valueOf(bol));


            Log.d("savedInsValues", " Millies " + MillisecondTime);
            Log.d("savedInsValues", " Mins " + Minutes);
            Log.d("savedInsValues", " Seconds " + Seconds);
            Log.d("savedInsValues", " updateTime " + UpdateTime);
            Log.d("savedInsValues", " startTime " + startTime);

            Minutes = mins;
            Seconds = secs;
            UpdateTime = updTime;
            MillisecondTime = milies;
            StartTime = startT;
            currentTime = curTime;

            Log.e("StartTime", "Restored " + startT);
            boolean hudCheck = savedInstanceState.getBoolean("hudBol");
            if (hudCheck) {
                mainDigitalConstraintLayout.setRotationY(180f);
                MainActivity.mainLayout.setRotation(180f);
//                if (orientation == Configuration.ORIENTATION_PORTRAIT)
//                    MainActivity.adContainerView.setVisibility(View.GONE);
            } else {
                mainDigitalConstraintLayout.setRotationY(0f);
                MainActivity.mainLayout.setRotation(0f);
//                if (orientation == Configuration.ORIENTATION_PORTRAIT)
//                    MainActivity.adContainerView.setVisibility(View.VISIBLE);
            }

            hudbol = hudCheck;
        }
    }

    static void bindService(Context context) {
        if (status)
            return;
        Intent i = new Intent(context, LocationService.class);
        context.bindService(i, sc, Context.BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();

    }

    void unbindService(Context context) {
        if (!status)
            return;
        try {
            context.unbindService(sc);
        } catch (Exception e) {
            Toast.makeText(context, getResources().getString(R.string.crashed) + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        status = false;
        rotate = false;

    }

    private void setThemeColor(int color) {
        if (color != 0) {
            distance_tv_digital.setTextColor(getResources().getColor(color));
            avg_speed_tv_digital.setTextColor(getResources().getColor(color));
            max_speed_tv_digital.setTextColor(getResources().getColor(color));
            timer_tv_digital.setTextColor(getResources().getColor(color));
            meterSpeedKm_per_hr.setTextColor(getResources().getColor(color));
            maxSpeed_per_hr.setTextColor(getResources().getColor(color));
            avgSpeed_km_per_hr.setTextColor(getResources().getColor(color));
            distance_km.setTextColor(getResources().getColor(color));
            speed_km_per_hr_with_digital.setTextColor(getResources().getColor(color));

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                degreeTextView.setTextColor(getResources().getColor(color));
                compass.setTextColor(getResources().getColor(color));
            }

            digitSpeedView.setSpeedTextColor(getActivity());
            digital_speed.setTextColor(getResources().getColor(color));
            Log.d("colorCode", String.valueOf(color));


        }
    }

    private void showOrHideBatteryPercentage() {
        boolean showBatteryPercentage = pref.getBoolean(SHOwBATTERySTATUS, true);
        if (showBatteryPercentage) {
            batterImg.setVisibility(View.VISIBLE);
            batteryPercentage.setVisibility(View.VISIBLE);


        } else {
            batterImg.setVisibility(View.GONE);
            batteryPercentage.setVisibility(View.GONE);

        }
    }

    private void showOrHideTimer() {
        boolean showTimer = pref.getBoolean(SHOwTRAVElTIME, true);
        if (showTimer) {
            timer_tv_digital.setVisibility(View.VISIBLE);
            timerTextWhite.setVisibility(View.VISIBLE);
        } else {
            timer_tv_digital.setVisibility(View.GONE);
            timerTextWhite.setVisibility(View.GONE);

        }
    }

    private void showOrHideMaxSpeed() {
        boolean showMaxSpeed = pref.getBoolean(SHOwMAxSPEED, true);
        if (showMaxSpeed) {
            max_speed_tv_digital.setVisibility(View.VISIBLE);
            maxSpeedTextWhite.setVisibility(View.VISIBLE);
            maxSpeed_per_hr.setVisibility(View.VISIBLE);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                maxSpeedBg.setVisibility(View.VISIBLE);
            }

        } else {
            max_speed_tv_digital.setVisibility(View.GONE);
            maxSpeedTextWhite.setVisibility(View.GONE);
            maxSpeed_per_hr.setVisibility(View.GONE);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                maxSpeedBg.setVisibility(View.GONE);
            }

        }
    }

    private void showOrHideAvgSpeed() {
        boolean showAvgSpeed = pref.getBoolean(SHOwAVgSPEED, true);
        if (showAvgSpeed) {
            avg_speed_tv_digital.setVisibility(View.VISIBLE);
            avg_speed_white_text.setVisibility(View.VISIBLE);
            avgSpeed_km_per_hr.setVisibility(View.VISIBLE);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                avgSpeedBg.setVisibility(View.VISIBLE);
            }
        } else {
            avg_speed_tv_digital.setVisibility(View.GONE);
            avg_speed_white_text.setVisibility(View.GONE);
            avgSpeed_km_per_hr.setVisibility(View.GONE);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                avgSpeedBg.setVisibility(View.GONE);
            }

        }
    }

    private void showOrHideSpeedDistance() {
        boolean showDistance = pref.getBoolean(SHOwTRAVElDISTANCE, true);
        if (showDistance) {
            distance_tv_digital.setVisibility(View.VISIBLE);
            distance_km.setVisibility(View.VISIBLE);
            distance_text.setVisibility(View.VISIBLE);
        } else {
            distance_tv_digital.setVisibility(View.GONE);
            distance_km.setVisibility(View.GONE);
            distance_text.setVisibility(View.GONE);
        }
    }

    private void showOrHideCompass() {
        boolean showCompass = pref.getBoolean(SHOwCOMPASS, true);
        if (showCompass) {
            compassBG.setVisibility(View.VISIBLE);
            compass.setVisibility(View.VISIBLE);
            degreeTextView.setVisibility(View.VISIBLE);
        } else {
            compass.setVisibility(View.GONE);
            compassBG.setVisibility(View.GONE);
            degreeTextView.setVisibility(View.GONE);

        }
    }

    private void setSpeedUnits() {
        boolean kmPerhr = pref.getBoolean(KPH, true);
        boolean mph = pref.getBoolean(MPH, false);
        boolean knots = pref.getBoolean(KNOTS, false);
        boolean mps = pref.getBoolean(MPS, false);
        if (kmPerhr) {
            restoredText = "KM/H";
            avgSpeed_km_per_hr.setText(R.string.km_per_hr);
            maxSpeed_per_hr.setText(R.string.km_per_hr);
            meterSpeedKm_per_hr.setText(R.string.km_per_hr);
        } else if (mph) {
            restoredText = "MPH";
            avgSpeed_km_per_hr.setText(R.string.miles_per_hr);
            maxSpeed_per_hr.setText(R.string.miles_per_hr);
            meterSpeedKm_per_hr.setText(R.string.miles_per_hr);
        } else if (knots) {
            restoredText = "knots";
            avgSpeed_km_per_hr.setText(R.string.knots);
            maxSpeed_per_hr.setText(R.string.knots);
            meterSpeedKm_per_hr.setText(R.string.knots);
        } else {
            restoredText = "M/S";
            avgSpeed_km_per_hr.setText(R.string.meter_per_second);
            maxSpeed_per_hr.setText(R.string.meter_per_second);
            meterSpeedKm_per_hr.setText(R.string.meter_per_second);
        }


    }

    private void getPrefValuesForMeters() {
        boolean digital = pref.getBoolean(DIGITAL, false);
        boolean analog = pref.getBoolean(ANALOG, false);
        String stringColor = pref.getString(COLOR_NAME, "green");

        boolean kmPerhr = pref.getBoolean(KPH, true);
        boolean mph = pref.getBoolean(MPH, false);
        boolean knots = pref.getBoolean(KNOTS, false);
        boolean mps = pref.getBoolean(MPS, false);

        // for changing meter color
        switch (stringColor) {
            case "blue":
                Log.d("colorName", stringColor);
                if (kmPerhr) {
                    meterImg.setImageResource(R.drawable.blue_speedometer_kmph);
                } else if (mph){
                    meterImg.setImageResource(R.drawable.blue_speedometer_mph);
                } else if (knots){
                    meterImg.setImageResource(R.drawable.blue_speedometer_knots);
                } /*else if (mps){

                }*/
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    compassBG.setImageResource(R.drawable.blue_compass_make);
                break;

            case "red":
                Log.d("colorName", stringColor);
                if (kmPerhr) {
                    meterImg.setImageResource(R.drawable.red_speedometer_kmph);
                } else if (mph){
                    meterImg.setImageResource(R.drawable.red_speedometer_mph);
                } else if (knots){
                    meterImg.setImageResource(R.drawable.red_speedometer_knots);
                } /*else if (mps){

                }*/
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    compassBG.setImageResource(R.drawable.red_compass);
                break;

            case "green":
                Log.d("colorName", stringColor);
                if (kmPerhr) {
                    meterImg.setImageResource(R.drawable.green_speedometer_kmph);
                } else if (mph){
                    meterImg.setImageResource(R.drawable.green_speedometer_mph);
                } else if (knots){
                    meterImg.setImageResource(R.drawable.green_speedometer_knots);
                } /*else if (mps){

                }*/
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    compassBG.setImageResource(R.drawable.green_compass);
                break;

            case "pink":
                Log.d("colorName", stringColor);
                if (kmPerhr) {
                    meterImg.setImageResource(R.drawable.pink_speedometer_kmph);
                } else if (mph){
                    meterImg.setImageResource(R.drawable.pink_speedometer_mph);
                } else if (knots){
                    meterImg.setImageResource(R.drawable.pink_speedometer_knots);
                } /*else if (mps){

                }*/
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    compassBG.setImageResource(R.drawable.pink_compass);
                break;

            case "yellow":
                Log.d("colorName", stringColor);
                if (kmPerhr) {
                    meterImg.setImageResource(R.drawable.yellow_speedometer_kmph);
                } else if (mph){
                    meterImg.setImageResource(R.drawable.yellow_speedometer_mph);
                } else if (knots){
                    meterImg.setImageResource(R.drawable.yellow_speedometer_knots);
                } /*else if (mps){

                }*/
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    compassBG.setImageResource(R.drawable.yellow_compass);
                break;

            case "orange":
                Log.d("colorName", stringColor);
                if (kmPerhr) {
                    meterImg.setImageResource(R.drawable.orange_speedmeter_kmph);
                } else if (mph){
                    meterImg.setImageResource(R.drawable.orange_speedometer_mph);
                } else if (knots){
                    meterImg.setImageResource(R.drawable.orange_speedometer_knots);
                } /*else if (mps){

                }*/
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    compassBG.setImageResource(R.drawable.orange_compass);
                break;

            case "purple":
                Log.d("colorName", stringColor);
                if (kmPerhr) {
                    meterImg.setImageResource(R.drawable.purple_speedometer_kmph);
                } else if (mph){
                    meterImg.setImageResource(R.drawable.purple_speedometer_mph);
                } else if (knots){
                    meterImg.setImageResource(R.drawable.purple_speedometer_knots);
                } /*else if (mps){

                }*/
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    compassBG.setImageResource(R.drawable.purple_compass);
                break;
        }

        if (digital) {
            Log.d("checking_here", "digital");
            analogMeterViews(View.GONE);
            digitalMeterViews(View.VISIBLE);

        } else if (analog) {
            Log.d("checking_here", "analog");
//            analogMeterViews(View.VISIBLE);
            digitalMeterViews(View.GONE);

            meterImg.setVisibility(View.VISIBLE);
            speedAnalog.setVisibility(View.VISIBLE);
            digitSpeedView.setVisibility(View.GONE);
            meterSpeedKm_per_hr.setVisibility(View.GONE);
            speed_bg.setVisibility(View.GONE);


        } else {
            analogMeterViews(View.VISIBLE);
            digitalMeterViews(View.GONE);
            Log.d("checking_here", "both");
        }


    }

    private void analogMeterViews(int visibility) {
        speedAnalog.setVisibility(visibility);
        meterImg.setVisibility(visibility);
        digitSpeedView.setVisibility(visibility);
        meterSpeedKm_per_hr.setVisibility(visibility);
        speed_bg.setVisibility(visibility);
    }

    private void digitalMeterViews(int visibility) {
        speed_km_per_hr_with_digital.setVisibility(visibility);
        digital_speed.setVisibility(visibility);
    }

    public static Handler handler;  // this handler is with start button, updated in every second
    public static Runnable runnable = new Runnable() {  // so most function checks in each second, like internet state, GPS etc

        public void run() {
            if (lastKnownLatLng == null) {  // this was used for map is getting null location when GPS off, but when we turns it on it shows after some delay, so it gets location when it gets location
                MapFragment mapFragment = new MapFragment();
                mapFragment.animateMap(myContext);
            }

            if (!isNetworkConnected(myContext.getApplicationContext())) {
                Log.d("testgps", "No internet");
                internetStatus.setText(R.string.no_internet);
                internetStatusGraph.setText(R.string.no_internet);
                internetStatusMap.setText(R.string.no_internet);
            } else {
                internetStatus.setText("");
                internetStatusGraph.setText("");
                internetStatusMap.setText("");
                Log.d("testgps", "internt");
            }

            if (isGPSEnabled(myContext.getApplicationContext())) {
                Log.d("testgps", "yes");
                gpsStatus.setText("");
                gpsStatusGraph.setText("");
                gpsStatusMap.setText("");
            } else {
                Log.d("testgps", "no");
                gpsStatus.setText(R.string.gps_off_on);
                gpsStatusGraph.setText(R.string.gps_off_on);
                gpsStatusMap.setText(R.string.gps_off_on);
                if ((alert != null && !alert.isShowing())) {
                    showGPSDisabledAlertToUser(myContext);
                }

                // for calling GPS  Strength class
                MyGPSListener myGPSListener = new MyGPSListener();  // for checking GPS signals strength in each second
                LocationManager locMgr = (LocationManager) myContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(myContext.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                try {
                    locMgr.addGpsStatusListener(myGPSListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            if (rotate) {
//                MillisecondTime += milies;
//                rotate = false;
//            }


            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            Log.d("StartTime", "before: " + StartTime);

            // for calculating all seconds, mins and hours
            UpdateTime = TimeBuff + MillisecondTime;
            mysecond = (int) (UpdateTime / 1000);
            Seconds = (int) (UpdateTime / 1000);
            cSeconds = Seconds % 60;                                                                                //counter
            Minutes = Seconds / 60;
            cMinutes = (Minutes) % 60;      //cMins
            cHours = (Minutes) / 60;                                                                                //cHours
            MilliSeconds = (int) (UpdateTime % 1000);


            timer_tv_digital.setText(cHours + ":"                                                                       // setting timer value on textView
                    + String.format("%02d", cMinutes) + ":"
                    + String.format("%02d", cSeconds));


            Log.d("speedeee", String.valueOf((int) LocationService.speed));                                         // for updating speed after every second
            Log.d("time12", " cMinutes: " + cMinutes + "  counter: " + cSeconds + "  seconds: " + Seconds + "  Minutes: " + Minutes + "  Hours: " + Hours + "  cHours: " + cHours);


            Log.d("speedeee", String.valueOf((int) LocationService.speed));

            digital_speed.setText(String.valueOf((int) LocationService.speed));
            speedAnalog.speedTo((float) LocationService.speed);


            // for updating notification
            if (restoredNoti) {
                try {
                    updateNotification(myContext.getApplicationContext(), LocationService.speed, restoredText, restoredNoti);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            handler.postDelayed(this, 400); // for updating notification value in each 400 millis
        }

    };

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {  // battery percentage level
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

        }
    };

    public void showNotificationSpeedInStatusBar(Context context, String restoredText, boolean restoredNoti) {
        if (restoredNoti) {
            mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(context.getString(R.string.running))
                    .setContentText(context.getString(R.string.noti_speed) + (int) LocationService.speed + restoredText)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent(context, MainActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent intent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent = PendingIntent.getActivity(context, 0,
                        notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            }

            mBuilder.setContentIntent(intent);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert mNotificationManager != null;
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
        }
    }

    public static void updateNotification(Context c, double sp, String restoredText, boolean restoredNoti) {
        if (restoredNoti) {
            mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
        }
    }

    public void initView(View view) {
        mSensorManager = (SensorManager) requireActivity().getSystemService(SENSOR_SERVICE);
        speedAnalog = view.findViewById(R.id.imageSpeedometer);
        mainDigitalConstraintLayout = view.findViewById(R.id.digital_main_constraint_layout_id);
        meterImg = view.findViewById(R.id.meter_img);
        batterImg = view.findViewById(R.id.battery_bg);
        compassBG = view.findViewById(R.id.compassBG);
        digitSpeedView = view.findViewById(R.id.speed_text);
        start = view.findViewById(R.id.start);
        pause = view.findViewById(R.id.pause);
        avgSpeedBg = view.findViewById(R.id.avg_speed_bg);

        digital_speed = view.findViewById(R.id.digitalspeedtext);
        distance_tv_digital = view.findViewById(R.id.distance_tv_digital);
        max_speed_tv_digital = view.findViewById(R.id.max_speed_tv_digital);
        meterSpeedKm_per_hr = view.findViewById(R.id.meter_speed_km_per_hr_txt);
        maxSpeed_per_hr = view.findViewById(R.id.max_speed_per_hr);
        maxSpeedBg = view.findViewById(R.id.max_speed_bg);
        maxSpeedTextWhite = view.findViewById(R.id.tv_max_speed_white);
        avg_speed_tv_digital = view.findViewById(R.id.avg_speed_tv_digital);
        avg_speed_white_text = view.findViewById(R.id.avg_speed_white);
        avgSpeed_km_per_hr = view.findViewById(R.id.avg_speed_km_pe_hr);
        speed_km_per_hr_with_digital = view.findViewById(R.id.speed_km_per_hr_with_digital);
        distance_km = view.findViewById(R.id.distance_km);
        distance_text = view.findViewById(R.id.distance_text);
        speed_bg = view.findViewById(R.id.relativeLayout2);
        batteryPercentage = view.findViewById(R.id.battery_percentage);
        degreeTextView = view.findViewById(R.id.degree_text_view);
        gpsStatus = view.findViewById(R.id.status_off_on);
        statusTxt = view.findViewById(R.id.gps_status);
        internetStatus = view.findViewById(R.id.internet_status);
        stop = view.findViewById(R.id.stop);

        kmphbtn = view.findViewById(R.id.kmphbtn);
        mphbtn = view.findViewById(R.id.mphbtn);
        timer_tv_digital = view.findViewById(R.id.timer_tv_digital);
        timerTextWhite = view.findViewById(R.id.timer_text_white);

        startDigitalButton = view.findViewById(R.id.start_button_id);
        stopDigitalButton = view.findViewById(R.id.stop_button_id);
        HUDDigitalButton = view.findViewById(R.id.hud_digital);

        HUDDigitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hudbol) {
                    hudbol = true;
                    mainDigitalConstraintLayout.setRotationY(180);
                    MainActivity.mainLayout.setRotation(180f);
//                    if (orientation == Configuration.ORIENTATION_PORTRAIT)
//                        MainActivity.adContainerView.setVisibility(View.GONE);

                } else {
                    hudbol = false;
                    mainDigitalConstraintLayout.setRotationY(0);
                    MainActivity.mainLayout.setRotation(0f);
//                    if (orientation == Configuration.ORIENTATION_PORTRAIT)
//                        MainActivity.adContainerView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void stopMeter(Context context) {
        stopDigitalButton.setVisibility(View.INVISIBLE);                                                    // stopping meter and reseting all / some static values/ variables
        startDigitalButton.setVisibility(View.VISIBLE);
        MapFragment.startBtn.setVisibility(View.VISIBLE);
        MapFragment.stopBtn.setVisibility(View.INVISIBLE);
        GraphFragment.stopDigitalButton.setVisibility(View.VISIBLE);
        GraphFragment.stopDigitalButton.setVisibility(View.INVISIBLE);
        stopBtnIsVisible = false;
        resumed = false;
        savePrefValueForButtons(context);
        context.stopService(new Intent(context, MyService.class));

        //set all to zero
        digitSpeedView.updateSpeed(0);
        GraphFragment.speedViewGraph.updateSpeed(0);
        MapFragment.digitSpeedView.updateSpeed(0);
        digital_speed.setText("0");
        LocationService.speed = 0;
        speedAnalog.speedTo(0);
        speedAnalog.stop();
        speedAnalog.setWithTremble(false);
        speedAnalog.setSpeedAt(0f);
        distance_tv_digital.setText("0");
        avg_speed_tv_digital.setText("0");
        max_speed_tv_digital.setText("0");
        timer_tv_digital.setText("00:00:00");

        MapFragment.timer.setText("00:00:00");
        MapFragment.maxspeed.setText("0");
        MapFragment.distance_tv_digital.setText("0");
        MapFragment.avgspeed.setText("0");

        GraphFragment.time_graph.setText("00:00:00");
        GraphFragment.avg_speed_graph.setText("0");
        GraphFragment.distance_graph.setText("0");
        GraphFragment.max_speed_graph.setText("0");
        GraphFragment.line_chart.clear();


        statusTxt.setText("");
        statusTxtGraph.setText("");
        statusTxtMap.setText("");

        internetStatus.setText("");
        internetStatusMap.setText("");
        internetStatusGraph.setText("");

        gpsStatus.setText("");
        gpsStatusMap.setText("");
        gpsStatusGraph.setText("");

        // for cancelling notification from notification bar
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }

        try {
            if (mp != null) {
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        handler.removeCallbacks(runnable);
        MapFragment.handler.removeCallbacks(MapFragment.runnable);
        GraphFragment.handler.removeCallbacks(GraphFragment.runnable);

        LocationService.speedText = 0;
        LocationService.noOfSpeed = 0;
        avgSpeedforAnyUnit = 0;
        LocationService.speed = 0;
        maxvalue = 0;
        LocationService.distance = 0;

        cSeconds = 0;
        Minutes = 0;
        Hours = 0;
        if (status) {
            unbindService(myContext.getApplicationContext());
        }

    }

    public void initProgress(Context c) {
        // For background service
        if (!status) {
            //Here, the Location Service gets bound and the GPS Speedometer gets Active.
            bindService(myContext.getApplicationContext());
        }
        MapFragment.mapOn = true;
        gpsEnabled = false;
        c.startService(new Intent(c, MyService.class));

        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
        MapFragment.handler.postDelayed(MapFragment.runnable, 0);
        GraphFragment.handler.postDelayed(GraphFragment.runnable, 0);

        stopDigitalButton.setVisibility(View.VISIBLE);
        startDigitalButton.setVisibility(View.INVISIBLE);
        MapFragment.startBtn.setVisibility(View.INVISIBLE);
        MapFragment.stopBtn.setVisibility(View.VISIBLE);
        GraphFragment.startDigitalBtn.setVisibility(View.INVISIBLE);
        GraphFragment.stopDigitalButton.setVisibility(View.VISIBLE);
        stopBtnIsVisible = true;
        resumed = true;

        savePrefValueForButtons(c);
        try {
            editor.putBoolean("stopButtonState", stopBtnIsVisible);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        showNotificationSpeedInStatusBar(c, restoredText, restoredNoti);

        MapFragment mapFragment = new MapFragment();                                                        // for zooming in map
        mapFragment.animateMap(c);

    }

    @NotNull
    public void savePrefValueForButtons(Context c) {
        SharedPreferences pref = c.getSharedPreferences(MyPREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isResumed", resumed);
        editor.apply();
    }

    public void checkGps(Context c) {
        Dexter.withContext(c)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if (report.isAnyPermissionPermanentlyDenied()) {
                    new AlertDialog.Builder(c, R.style.DialogeTheme).setTitle(R.string.turn_on_permission)
                            .setMessage(R.string.permanentlyDeniedmessage)
                            .setNegativeButton(c.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(c.getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", c.getPackageName(), null);
                                    intent.setData(uri);
                                    c.startActivity(intent);

                                }
                            })
                            .show();
                } else {
                    if (report.areAllPermissionsGranted()) {
                        if (isNetworkConnected(c)) {
                            locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                                showGPSDisabledAlertToUser(c);
                            } else {
                                initProgress(c);
                            }
                        } else {
                            showNoInternetConnectionDialog(c);
                        }
                    } else {
                        System.exit(0);
                    }
                }


            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();


    }

    public static void showGPSDisabledAlertToUser(Context c) {


        new GpsUtils(myContext).turnGPSOn(new GpsUtils.onGpsListener() {                // used class for GPS listener
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                Log.d("yes123", "yes");
            }
        });

    }

    public void stop() {
        if (haveSensor && haveSensor2) {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        } else {
            if (haveSensor)
                mSensorManager.unregisterListener(this, mRotationV);
        }
    }

    public void start() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) ||
                    (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                //noSensorsAlert();
            } else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        } else {
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public static boolean isNetworkConnected(Context c) {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

            // Check for network connections
            if (connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                    connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                    connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
                return true;

            } else if (
                    connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                            connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private static void showNoInternetConnectionDialog(Context c) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c, R.style.DialogeTheme);
        alertDialogBuilder.setMessage(c.getResources().getString(R.string.not_connected_internet_digital))
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();

                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();

        alert.show();
    }

}
