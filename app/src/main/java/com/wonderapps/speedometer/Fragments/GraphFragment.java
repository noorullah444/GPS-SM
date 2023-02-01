
package com.wonderapps.speedometer.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.wonderapps.speedometer.DigitSpeedView;
import com.wonderapps.speedometer.LocationService.LocationService;
import com.wonderapps.speedometer.R;


import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.wonderapps.speedometer.Activities.MainActivity.resumed;

import static com.wonderapps.speedometer.Activities.SettingsActivity.LIMItSPEED;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.animateMapBol;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.buttonHideDelay;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.cHours;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.cMinutes;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.cSeconds;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.limitSpeed;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.showTabLayout;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.showToolbar;
import static com.wonderapps.speedometer.Activities.MainActivity.tabLayout;
import static com.wonderapps.speedometer.Activities.MainActivity.toolbar1;
import static com.wonderapps.speedometer.Activities.SettingsActivity.COLOR_KEY;
import static com.wonderapps.speedometer.Activities.SettingsActivity.KNOTS;
import static com.wonderapps.speedometer.Activities.SettingsActivity.KPH;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MPH;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MyPREFERENCES;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwAVgSPEED;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwMAxSPEED;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwTRAVElDISTANCE;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwTRAVElTIME;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.stopBtnIsVisible;


public class GraphFragment extends Fragment implements SensorEventListener {

    public static TextView time_graph, timerWhite, distance_graph, max_speed_graph, avg_speed_graph;
    private TextView max_speed_per_hr, avg_speed_per_hr, distance_km, digital_speed_km_per_hr, avg_white_text, speedWhiteText;
    public static DigitSpeedView speedViewGraph;
    public static int Secondsg, Minutesg, MilliSecondsg, mysecondg;
    private static long MillisecondTimeg, StartTimeg, TimeBuffg, UpdateTimeg = 0L;
    public static Handler handler;
    public static Button startDigitalBtn, HUDDigitalBtn, stopDigitalButton;
    ImageView settingsImg, avgBg, speedBg;
    ConstraintLayout mainGraphConstraintLayout;
    LocationService myService;
    static boolean status;
    private ImageView compassView;

    // for compass
    int mAzimuth;
    String currentDateAndTime = null;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientationSensor = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    public static TextView statusTxtGraph, gpsStatusGraph, internetStatusGraph;

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            status = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;
        }
    };

    public static Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTimeg = SystemClock.uptimeMillis() - StartTimeg;

            UpdateTimeg = TimeBuffg + MillisecondTimeg;

            mysecondg = (int) (UpdateTimeg / 1000);
            Secondsg = (int) (UpdateTimeg / 1000);
            Minutesg = Secondsg / 60;
            Secondsg = Secondsg % 60;
            MilliSecondsg = (int) (UpdateTimeg % 1000);


            time_graph.setText(DigitalFragment.cHours + ":"
                    + String.format("%02d", DigitalFragment.cMinutes) + ":"
                    + String.format("%02d", DigitalFragment.cSeconds));

            Log.d(TAG, "run: seconds: " + cSeconds);
            Log.d(TAG, "run: minutes: " + cMinutes);
            Log.d(TAG, "run: hours: " + cHours);
            handler.postDelayed(this, 0);
        }

    };


    public static LineChart line_chart;
    double speed;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int themeColor;
    private TextView distance_white_text;
    private int orientation;
    //    private InterstitialAd mInterstitialAd;
    private ConstraintLayout test;
    public static YAxis left_Yaxis;
    public static XAxis xAxis;

    ArrayList<Entry> dataValues = new ArrayList<>();

    public GraphFragment() {
    }

    public GraphFragment(double speed) {
        this.speed = speed;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_graph, container, false);
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        initViews(view);
        start();
//        pref = getContext().getSharedPreferences(MyPREFERENCES, 0);


        startDigitalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DigitalFragment digitalFragment = new DigitalFragment();
                digitalFragment.checkGps(getActivity());
                animateMapBol = true;
                status = false;

            }
        });

        stopDigitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateMapBol = false;
                stopBtnIsVisible = false;
                MapFragment mapFragment = new MapFragment();
                mapFragment.showDialog(getActivity());
            }
        });


        mainGraphConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showToolbar && showTabLayout) {
                    toolbar1.animate().translationY(-toolbar1.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    tabLayout.animate().translationY(tabLayout.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    startDigitalBtn.setVisibility(View.GONE);
                    stopDigitalButton.setVisibility(View.GONE);
//                    HUDDigitalBtn.setVisibility(View.GONE);
                    showToolbar = false;
                } else {
                    //Toast.makeText(getActivity(), "not showing", Toast.LENGTH_SHORT).show();
                    toolbar1.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                    tabLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                    if (DigitalFragment.stopBtnIsVisible) {
                        stopDigitalButton.setVisibility(View.VISIBLE);
                    } else {
                        startDigitalBtn.setVisibility(View.VISIBLE);
                    }
//                    HUDDigitalBtn.setVisibility(View.VISIBLE);
                    showToolbar = true;
                }
            }


        });

        // for hiding buttons after 3 seconds
        final Handler handler4 = new Handler(Looper.getMainLooper());
        handler4.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (showToolbar && showTabLayout) {
                    toolbar1.animate().translationY(-toolbar1.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    tabLayout.animate().translationY(tabLayout.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    startDigitalBtn.setVisibility(View.GONE);
                    stopDigitalButton.setVisibility(View.GONE);
                    showToolbar = false;
                }
            }
        }, buttonHideDelay);

        handler = new Handler();
        StartTimeg = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

        distance_graph.setText(new DecimalFormat("#.###").format(LocationService.distanceGraph));
        Log.d("distanceGraph", "" + LocationService.distanceGraph);

        line_chart.setBackgroundColor(getResources().getColor(R.color.background_color));
        line_chart.setNoDataText(getString(R.string.no_graph_data_available));
        line_chart.setNoDataTextColor(Color.WHITE);
        line_chart.setDrawGridBackground(true);
        line_chart.setDrawBorders(false);
        line_chart.setBorderColor(Color.BLACK);
        line_chart.setBorderWidth(2f);
        line_chart.setGridBackgroundColor(getResources().getColor(R.color.background_color));
        //change here

        //setting upper limit
        LimitLine upper_limit = null;
        try {
            pref = getActivity().getSharedPreferences(MyPREFERENCES, 0);
            limitSpeed = pref.getString(LIMItSPEED, "100");
            upper_limit = new LimitLine((float) Double.parseDouble(limitSpeed), "Speed Limit");
            upper_limit.setLineWidth(2f);
            upper_limit.enableDashedLine(10f, 10f, 0f);
            upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            upper_limit.setTextSize(10f);
            upper_limit.setTextColor(Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //setting lower limit
        LimitLine lower_limit = new LimitLine(0f, "lower limit");
        lower_limit.setLineWidth(2f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);

        // enable scaling and dragging
        line_chart.setDragEnabled(true);
        line_chart.setScaleEnabled(true);
        line_chart.setDrawGridBackground(true);
        line_chart.setHighlightPerDragEnabled(true);

        //new
        line_chart.setTouchEnabled(true);
        line_chart.setPinchZoom(true);


//        line_chart.getDescription().setPo("Time");
//        line_chart.getDescription().setPosition(500,20);
        line_chart.getDescription().setTextColor(Color.WHITE);
        line_chart.notifyDataSetChanged();

        YAxis rightAxis = line_chart.getAxisRight();
        rightAxis.setEnabled(true);
        left_Yaxis = line_chart.getAxisLeft();
        left_Yaxis.removeAllLimitLines();
        left_Yaxis.addLimitLine(upper_limit);
        left_Yaxis.setAxisMinimum(0f);
        left_Yaxis.setTextColor(Color.WHITE);
        xAxis = line_chart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);

        line_chart.getAxisRight().setEnabled(false);       // for hiding values to the right of graph
        orientation = getResources().getConfiguration().orientation;

        if (savedInstanceState != null) {
            boolean state = savedInstanceState.getBoolean("buttonState");
            if (state) {
                DigitalFragment.stopDigitalButton.setVisibility(View.VISIBLE);
                DigitalFragment.startDigitalButton.setVisibility(View.INVISIBLE);
                MapFragment.startBtn.setVisibility(View.INVISIBLE);
                MapFragment.stopBtn.setVisibility(View.VISIBLE);
                GraphFragment.startDigitalBtn.setVisibility(View.INVISIBLE);
                GraphFragment.stopDigitalButton.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    public ArrayList<Entry> dataValues1() {
        dataValues.add(new Entry((DigitalFragment.mysecond)/* / 60.0f*/, (float) LocationService.speed));
        return dataValues;
    }

    public void initViews(View view) {
        time_graph = view.findViewById(R.id.time_tv_graph);
        timerWhite = view.findViewById(R.id.timer_text_white);
        distance_graph = view.findViewById(R.id.distance_tv_graph);
        max_speed_graph = view.findViewById(R.id.max_speed_tv_graph);
        avg_speed_graph = view.findViewById(R.id.avg_speed_tv_graph);
        avg_white_text = view.findViewById(R.id.avg_speed_white);
        avgBg = view.findViewById(R.id.avg_bg);
        speedViewGraph = view.findViewById(R.id.speed_text);
        speedWhiteText = view.findViewById(R.id.max_speed_white);
        speedBg = view.findViewById(R.id.speed_bg);
        max_speed_per_hr = view.findViewById(R.id.max_speed_per_hr);
        avg_speed_per_hr = view.findViewById(R.id.maximum_km_per_hr);
        distance_km = view.findViewById(R.id.distance_km);
        distance_white_text = view.findViewById(R.id.distance_white_text);
        digital_speed_km_per_hr = view.findViewById(R.id.digital_speed_km_per_hr);
        statusTxtGraph = view.findViewById(R.id.gps_status_graph);
        gpsStatusGraph = view.findViewById(R.id.gps_on_off_graph);
        internetStatusGraph = view.findViewById(R.id.internet_status_graph);

        mainGraphConstraintLayout = view.findViewById(R.id.main_graph_constraint_layout_id);
        line_chart = view.findViewById(R.id.line_chart);

        startDigitalBtn = view.findViewById(R.id.start_button_id);
        stopDigitalButton = view.findViewById(R.id.stop_button_id);
        HUDDigitalBtn = view.findViewById(R.id.hud_digital);
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        compassView = view.findViewById(R.id.img_compass2);


    }

    @Override
    public void onResume() {
        super.onResume();

        pref = getActivity().getSharedPreferences(MyPREFERENCES, 0);
        editor = pref.edit();
        themeColor = pref.getInt(COLOR_KEY, R.color.green);
        // theme color will be applied for all textviews and some other views
        setThemeColor(themeColor);
        setSpeedUnits();
        showOrHideSpeedDistance();
        showOrHideAvgSpeed();
        showOrHideMaxSpeed();
        showOrHideTimer();

        if (resumed) {
            stopDigitalButton.setVisibility(View.VISIBLE);
            startDigitalBtn.setVisibility(View.GONE);
        } else {
            startDigitalBtn.setVisibility(View.VISIBLE);
            stopDigitalButton.setVisibility(View.GONE);
        }


    }

    private void showOrHideTimer() {
        boolean showTimer = pref.getBoolean(SHOwTRAVElTIME, true);
        if (showTimer) {
            time_graph.setVisibility(View.VISIBLE);
            timerWhite.setVisibility(View.VISIBLE);
        } else {
            time_graph.setVisibility(View.GONE);
            timerWhite.setVisibility(View.GONE);
        }
    }

    private void showOrHideMaxSpeed() {
        boolean showMaxSpeed = pref.getBoolean(SHOwMAxSPEED, true);
        if (showMaxSpeed) {
            max_speed_graph.setVisibility(View.VISIBLE);
            speedWhiteText.setVisibility(View.VISIBLE);
            max_speed_per_hr.setVisibility(View.VISIBLE);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                speedBg.setVisibility(View.VISIBLE);
            }
        } else {
            max_speed_graph.setVisibility(View.GONE);
            speedWhiteText.setVisibility(View.GONE);
            max_speed_per_hr.setVisibility(View.GONE);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                speedBg.setVisibility(View.GONE);
            }
        }
    }

    private void showOrHideAvgSpeed() {
        boolean showAvgSpeed = pref.getBoolean(SHOwAVgSPEED, true);
        if (showAvgSpeed) {
            avg_speed_graph.setVisibility(View.VISIBLE);
            avg_white_text.setVisibility(View.VISIBLE);
            avg_speed_per_hr.setVisibility(View.VISIBLE);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                avgBg.setVisibility(View.VISIBLE);
            }
        } else {
            avg_speed_graph.setVisibility(View.GONE);
            avg_white_text.setVisibility(View.GONE);
            avg_speed_per_hr.setVisibility(View.GONE);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                avgBg.setVisibility(View.GONE);
            }
        }
    }

    private void setSpeedUnits() {
        boolean kmPerhr = pref.getBoolean(KPH, true);
        boolean mph = pref.getBoolean(MPH, false);
        boolean knots = pref.getBoolean(KNOTS, false);
        if (kmPerhr) {
            avg_speed_per_hr.setText(R.string.km_per_hr);
            max_speed_per_hr.setText(R.string.km_per_hr);
            digital_speed_km_per_hr.setText(R.string.km_per_hr);


        } else if (mph) {
            avg_speed_per_hr.setText(R.string.miles_per_hr);
            max_speed_per_hr.setText(R.string.miles_per_hr);
            digital_speed_km_per_hr.setText(R.string.miles_per_hr);
        } else if (knots) {
            avg_speed_per_hr.setText(R.string.knots);
            max_speed_per_hr.setText(R.string.knots);
            digital_speed_km_per_hr.setText(R.string.knots);
        } else {
            avg_speed_per_hr.setText(R.string.meter_per_second);
            max_speed_per_hr.setText(R.string.meter_per_second);
            digital_speed_km_per_hr.setText(R.string.meter_per_second);
        }


    }

    private void showOrHideSpeedDistance() {
        boolean showDistance = pref.getBoolean(SHOwTRAVElDISTANCE, true);
        if (showDistance) {
            distance_graph.setVisibility(View.VISIBLE);
            distance_km.setVisibility(View.VISIBLE);
            distance_white_text.setVisibility(View.VISIBLE);
        } else {
            distance_graph.setVisibility(View.GONE);
            distance_km.setVisibility(View.GONE);
            distance_white_text.setVisibility(View.GONE);
        }
    }

    private void setThemeColor(int color) {
        if (color != 0) {
            time_graph.setTextColor(getResources().getColor(color));
            distance_graph.setTextColor(getResources().getColor(color));
            max_speed_graph.setTextColor(getResources().getColor(color));
            avg_speed_graph.setTextColor(getResources().getColor(color));
            max_speed_per_hr.setTextColor(getResources().getColor(color));
            avg_speed_per_hr.setTextColor(getResources().getColor(color));
            distance_km.setTextColor(getResources().getColor(color));
            digital_speed_km_per_hr.setTextColor(getResources().getColor(color));
            speedViewGraph.setSpeedTextColor(getActivity());
            Log.d("colorCode", String.valueOf(color));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("buttonState", DigitalFragment.stopBtnIsVisible);
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
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            compassView.setRotation(-mAzimuth);


        String where = "NW";

        if (mAzimuth >= 350 || mAzimuth <= 10)
            where = "N";
        if (mAzimuth < 350 && mAzimuth > 280)
            where = "NW";
        if (mAzimuth <= 280 && mAzimuth > 260)
            where = "W";
        if (mAzimuth <= 260 && mAzimuth > 190)
            where = "SW";
        if (mAzimuth <= 190 && mAzimuth > 170)
            where = "S";
        if (mAzimuth <= 170 && mAzimuth > 100)
            where = "SE";
        if (mAzimuth <= 100 && mAzimuth > 80)
            where = "E";
        if (mAzimuth <= 80 && mAzimuth > 10)
            where = "NE";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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

    public void stop() {
        if (haveSensor && haveSensor2) {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        } else {
            if (haveSensor)
                mSensorManager.unregisterListener(this, mRotationV);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //  stopLocationUpdates();
        stop();
    }
}
