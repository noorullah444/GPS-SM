package com.wonderapps.speedometer.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wonderapps.speedometer.Activities.MainActivity;
import com.wonderapps.speedometer.DBHelper.DatabaseHelper;
import com.wonderapps.speedometer.DigitSpeedView;
import com.wonderapps.speedometer.GraphTask;
import com.wonderapps.speedometer.LocationService.LocationService;
import com.wonderapps.speedometer.Model.GraphModel;
import com.wonderapps.speedometer.Model.HisModel;
import com.wonderapps.speedometer.R;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;
import static com.wonderapps.speedometer.Activities.MainActivity.myContext;
import static com.wonderapps.speedometer.Activities.MainActivity.resumed;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.animateMapBol;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.showTabLayout;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.showToolbar;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.speedAnalog;
import static com.wonderapps.speedometer.Activities.MainActivity.tabLayout;
import static com.wonderapps.speedometer.Activities.MainActivity.toolbar1;
import static com.wonderapps.speedometer.Activities.SettingsActivity.COLOR_KEY;
import static com.wonderapps.speedometer.Activities.SettingsActivity.HYBRIdMAP;
import static com.wonderapps.speedometer.Activities.SettingsActivity.KNOTS;
import static com.wonderapps.speedometer.Activities.SettingsActivity.KPH;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MPH;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MyPREFERENCES;
import static com.wonderapps.speedometer.Activities.SettingsActivity.NORMAlMAP;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SATELLITeMAP;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwAVgSPEED;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwMAxSPEED;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwTRAVElDISTANCE;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwTRAVElTIME;
import static com.wonderapps.speedometer.Activities.SettingsActivity.TERRAInMAP;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.stopBtnIsVisible;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener {

    private static final String TAG = MapFragment.class.getSimpleName();
    LocationService myService;
    //compass
    ImageView compass_needle, avgSpeedBg;
    TextView txt_compass;
    int mAzimuth;
    String currentDateAndTime = null;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    //ends
    ConstraintLayout mapMainConstraintLayout;
    static boolean status;
    public static TextView distance_tv_digital, timer, maxspeed, avgspeed, maxSpeedKmPerHr, avgKmPerHr, timerText, timerTextWhite, digital_speed_km_per_hr, maxSpeedWhiteText;
    private int themeColor;
    public static DigitSpeedView digitSpeedView;
    public static Button startBtn, pause, stopBtn, HUD;
    public static long startTime, endTime;
    public static int p = 0;
    public static int Seconds, Minutes, MilliSeconds, mysecond;
    private ImageView maxSpeedBg, settingsImg;
    public static boolean btnStatus;
    LocationManager locationManager;
    //    private InterstitialAd mInterstitialAd;
    public static double currentLat, currentLon;
    public static TextView statusTxtMap, gpsStatusMap, internetStatusMap;

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
    private SharedPreferences.Editor editor;
    private TextView distance_km, distance_text;
    private TextView avg_white_text_km;
    private int scrOrientation;
    private DigitalFragment digitalFragment;
    private static Location myLocation;

    public static List<String> myStopsTitles = new ArrayList<>();
    public static List<LatLng> myPointsList = new ArrayList<>();
    public static List<LatLng> myStopsList = new ArrayList<>();
    public static List<LatLng> points = new ArrayList<>();
    ;

    LatLngBounds.Builder boundBuilder;
    String imageName;
    private Uri contentUri;
    private Uri imageUri;
    DatabaseHelper databaseHelper;
    private byte[] imageByteArray;
    private ByteArrayOutputStream baos;
    public Marker currentMarker = null;
    public Marker initialMarker = null;
    private int id;
    private double initialLat;
    private double initialLng;
    public static GoogleMap.SnapshotReadyCallback callback;

    public static double lat, lon;
    public static PolylineOptions polylineOptions;
    public static boolean mapOn = false;
    private LatLng sydney;
    private double initLong;
    private double initLat;
    private File mediaDir;

    public static GoogleMap mMap;
    public static Polyline polyline;
    private GoogleApiClient googleApiClient;
    public static LatLng lastKnownLatLng;
    private static long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    private SharedPreferences pref;
    public static Handler handler;

    public static Runnable runnable = new Runnable() {
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            mysecond = (int) (UpdateTime / 1000);
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            timer.setText(DigitalFragment.cHours + ":"
                    + String.format("%02d", DigitalFragment.cMinutes) + ":"
                    + String.format("%02d", DigitalFragment.cSeconds));
            handler.postDelayed(this, 0);

        }

    };

    private ReviewManager reviewManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference("History");
//        mStorage = FirebaseStorage.getInstance().getReference();
        databaseHelper = new DatabaseHelper(getContext());

        initViews(view);
        digitalFragment = new DigitalFragment();
        pref = getContext().getSharedPreferences(MyPREFERENCES, 0);
        start();

        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DigitalFragment digitalFragment = new DigitalFragment();                                // calling digital fragment checkGPS method
                digitalFragment.checkGps(getContext());
                animateMapBol = true;
                status = false;
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateMapBol = false;
                stopBtnIsVisible = false;
                showDialog(getActivity());
            }
        });
        mapMainConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showToolbar && showTabLayout) {
                    toolbar1.animate().translationY(-toolbar1.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    tabLayout.animate().translationY(tabLayout.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    startBtn.setVisibility(View.GONE);
                    stopBtn.setVisibility(View.GONE);
                    showToolbar = false;
                } else {
                    toolbar1.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                    tabLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                    if (stopBtnIsVisible) {
                        stopBtn.setVisibility(View.VISIBLE);
                    } else {
                        startBtn.setVisibility(View.VISIBLE);
                    }
                    showToolbar = true;
                }


                final Handler handler = new Handler(Looper.getMainLooper());                                // for hiding buttons and toolbar after 3 seconds
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (showToolbar && showTabLayout) {
                            toolbar1.animate().translationY(-toolbar1.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                            tabLayout.animate().translationY(tabLayout.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                            startBtn.setVisibility(View.GONE);
                            stopBtn.setVisibility(View.GONE);
                            showToolbar = false;
                        }
                    }
                }, DigitalFragment.buttonHideDelay);

            }
        });

        handler = new Handler();
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        scrOrientation = getResources().getConfiguration().orientation;

        if (savedInstanceState != null) {
            boolean state = savedInstanceState.getBoolean("buttonState");
            if (state) {

                try {
                    DigitalFragment.stopDigitalButton.setVisibility(View.VISIBLE);
                    DigitalFragment.startDigitalButton.setVisibility(View.INVISIBLE);
                    MapFragment.startBtn.setVisibility(View.INVISIBLE);
                    MapFragment.stopBtn.setVisibility(View.VISIBLE);
                    GraphFragment.startDigitalBtn.setVisibility(View.INVISIBLE);
                    GraphFragment.stopDigitalButton.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return view;
    }

    public void animateMap(Context context) {
        try {
            polylineOptions = new PolylineOptions();
            polylineOptions.color(context.getResources().getColor(R.color.polylinecolor));
            polylineOptions.width(5);
            polyline = mMap.addPolyline(polylineOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lastKnownLatLng != null)                                                                    // if lastKnownLat found null then will use getLastKnowLocation Method to get last location
        {
            try {
                initialMarker = mMap.addMarker(new MarkerOptions().position(lastKnownLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            myLocation = getLastKnownLocation(context);
            try {
                lastKnownLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                initialMarker = mMap.addMarker(new MarkerOptions().position(lastKnownLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            } catch (Exception e) {
                Log.d("exceptionhai", e.getMessage());
                e.printStackTrace();
            }


        }
        try {
            points = polyline.getPoints();
            for (int x = 0; x < points.size(); x++) {
                sydney = points.get(x);

                if (x == 0) {
                    Log.d("lat123", "in if");

                    initialMarker = mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    // for storing initial location to Database
                    initialLat = sydney.latitude;
                    initialLng = sydney.longitude;
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));

                } else {
                    Log.d("lat123", "in else");
                    if (currentMarker != null)
                        currentMarker.remove();
                    currentMarker = mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));
                    polyline.setPoints(points);

                }
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastKnownLatLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, 18.0f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void showDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.save_track_dailog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button save = dialog.findViewById(R.id.save);
        EditText tripNameEd = dialog.findViewById(R.id.trip_name_ed);
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        Date currentTime = localCalendar.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aaa");
        String timestamp = df.format(currentTime);
        tripNameEd.setText(timestamp);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(activity)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                String time = timer.getText().toString();
                                String distance = distance_tv_digital.getText().toString();
                                DateFormat dat = new SimpleDateFormat("MMMM dd,yyyy");
                                Log.d("Timestamp", timestamp);
                                try {
                                    snapShot(timestamp, tripNameEd.getText().toString(), distance, time, "", activity);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                resetAllValues(activity);   // reset all variables
                                if (dialog != null) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }

//                                initInAppReview();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    new android.app.AlertDialog.Builder(activity, R.style.DialogeTheme).setTitle(R.string.turn_on_permission)
                                            .setMessage(R.string.permanentlyDeniedmessage)
                                            .setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setPositiveButton(activity.getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                                    intent.setData(uri);
                                                    activity.startActivity(intent);

                                                }
                                            })
                                            .show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        Button cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mMap.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resetAllValues(activity);
                dialog.dismiss();
            }
        });


        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initInAppReview() {
        reviewManager = ReviewManagerFactory.create(myContext);
//        findViewById(R.id.btn_rate_app).setOnClickListener(view -> showRateApp());
        showRateApp();
    }

    /**
     * Shows rate app bottom sheet using In-App review API
     * The bottom sheet might or might not shown depending on the Quotas and limitations
     * https://developer.android.com/guide/playcore/in-app-review#quotas
     * We show fallback dialog if there is any error
     */
    public void showRateApp() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "showRateApp: task successful");
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                // Launch the in-app review flow
                Task<Void> flow = reviewManager.launchReviewFlow((Activity) myContext, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
//                showRateAppFallbackDialog();
                Log.d(TAG, "showRateApp: task not successful");
            }
        });
    }

    /**
     * Showing native dialog with three buttons to review the app
     * Redirect user to playstore to review the app
     */
    private void showRateAppFallbackDialog() {
        new MaterialAlertDialogBuilder(myContext)
                .setTitle(R.string.app_name)
                .setMessage(R.string.rate_app_message)
                .setPositiveButton(R.string.rate_btn_pos, (dialog, which) -> {

                })
                .setNegativeButton(R.string.rate_btn_neg,
                        (dialog, which) -> {
                        })
                .setNeutralButton(R.string.rate_btn_nut,
                        (dialog, which) -> {
                        })
                .setOnDismissListener(dialog -> {
                })
                .show();
    }

    public void resetAllValues(Context c) {
        DigitalFragment digitalFragment = new DigitalFragment();
        digitalFragment.stopMeter(c);
        digitSpeedView.updateSpeed(0);
        speedAnalog.speedTo(0);
        handler.removeCallbacks(runnable);
        distance_tv_digital.setText("0");
        avgspeed.setText("0");
        maxspeed.setText("0");
        timer.setText("00:00:00");
        mapOn = false;

        if (DigitalFragment.mNotificationManager != null)
            DigitalFragment.mNotificationManager.cancel(0);

    }

    public void snapShot(String timeStamp, String name, String distance, String time, String date, Context ctx) {
        databaseHelper = new DatabaseHelper(ctx);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("ddMMMyyyyhhmmss");
        String datetime = dateformat.format(c.getTime());
        imageName = datetime + "_Track.png";
        callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //Background work here
                        SDCARD(snapshot, ctx, timeStamp);

                        try {
                            ArrayList<GraphModel> graphModelList = new ArrayList<>();

                            for (int i = 0; i < GraphTask.values.size(); i++) {
                                GraphModel graphModel = new GraphModel(GraphTask.values.get(i).getX(), GraphTask.values.get(i).getY());
                                graphModelList.add(graphModel);
                            }
                            Log.d(TAG, "onSnapshotReady: graphModelList: size= " + graphModelList.size() + "---" + graphModelList.toString());

                            // converting array list to string
                            Gson gson1 = new Gson();
                            String coordinates = gson1.toJson(graphModelList);
                            Log.d(TAG, "onSnapshotReady: coordinates: " + coordinates);

                            HisModel model = new HisModel(name,
                                    time,
                                    distance + " KM",
                                    timeStamp,
                                    baos.toByteArray(),
                                    lastKnownLatLng.longitude,
                                    lastKnownLatLng.latitude,
                                    currentLat,
                                    currentLon,
                                    graphModelList);

                            Gson gson = new Gson();
                            String json = gson.toJson(model);
                            Log.d(TAG, "onSnapshotReady: model json = " + json);

                            if (baos != null) {
                                id = databaseHelper.insertJson(json, null, name);

                                for (int x = 0; x < points.size(); x++) {
                                    databaseHelper.insertPathDetails(String.valueOf(points.get(x).longitude), String.valueOf(points.get(x).latitude), String.valueOf(currentLon), String.valueOf(currentLat), id);  // and this will save only path data like routes/polyline, lat lon etc
                                }


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //UI Thread work here
                                mMap.clear();
                                resetAllValues(ctx);    // resetting map and all values after saving to db
                            }
                        });
                    }
                });
            }
        };
        mMap.snapshot(callback);
    }

    private void SDCARD(Bitmap bitmapForShoot, Context c, String title) {

        File sdcard = Environment.getExternalStorageDirectory();    // for avoiding crash if device doesn't has this directory
        if (sdcard != null) {
            mediaDir = new File(sdcard.getAbsolutePath() + "/SpeedoMeter Tracks");
            if (!mediaDir.exists()) {
                mediaDir.mkdirs();
            }
        }

        File dest = new File(mediaDir, title + ".png");
        baos = new ByteArrayOutputStream();
        bitmapForShoot.compress(Bitmap.CompressFormat.PNG, 100, baos);
        FileOutputStream fi = null;
        try {
            fi = new FileOutputStream(dest);

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        if (fi != null) {
            try {

                fi.write(baos.toByteArray());

            } catch (IOException e) {

                e.printStackTrace();
            }

            try {
                fi.flush();
            } catch (IOException e) {

                e.printStackTrace();
            }

            try {
                fi.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            //out is your output file
            contentUri = Uri.fromFile(dest);
            mediaScanIntent.setData(contentUri);

            c.sendBroadcast(mediaScanIntent);
        } else {
            c.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
    }

    public void initViews(View view) {
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        compass_needle = (ImageView) view.findViewById(R.id.img_compass);
        mapMainConstraintLayout = view.findViewById(R.id.main_map_constraint_layout_id);
        MediaRecorder myAudioRecorder;


        distance_tv_digital = view.findViewById(R.id.tv_distance);
        distance_km = view.findViewById(R.id.distance_km_text);
        distance_text = view.findViewById(R.id.distance_white_text);
        timer = view.findViewById(R.id.timer_text);
        maxspeed = view.findViewById(R.id.maxspeed);
        maxSpeedBg = view.findViewById(R.id.max_speed_bg);
        maxSpeedWhiteText = view.findViewById(R.id.max_speed_text_white);
        maxspeed.setText("0");
        //long MaximumSpeed = 0;
        avgspeed = view.findViewById(R.id.avgspeed);
        avgSpeedBg = view.findViewById(R.id.avg_speed_bg);
        digitSpeedView = view.findViewById(R.id.speed_text);
        avgKmPerHr = view.findViewById(R.id.maximum_km_per_hr);
        avg_white_text_km = view.findViewById(R.id.date_text_white);
        maxSpeedKmPerHr = view.findViewById(R.id.max_speed_km_per_hr);
        digital_speed_km_per_hr = view.findViewById(R.id.digital_speed_km_per_hr);


        timerTextWhite = view.findViewById(R.id.timer_white_text);

        startBtn = view.findViewById(R.id.start_button_id);
        stopBtn = view.findViewById(R.id.stop_button_id);


        statusTxtMap = view.findViewById(R.id.gps_status_map);
        gpsStatusMap = view.findViewById(R.id.gps_on_off_map);
        internetStatusMap = view.findViewById(R.id.internet_status_map);
    }

    private void setSpeedUnits() {
        boolean kmPerhr = pref.getBoolean(KPH, true);
        boolean mph = pref.getBoolean(MPH, false);
        boolean knots = pref.getBoolean(KNOTS, false);
        if (kmPerhr) {
            avgKmPerHr.setText(R.string.km_per_hr);
            maxSpeedKmPerHr.setText(R.string.km_per_hr);
        } else if (mph) {
            avgKmPerHr.setText(R.string.miles_per_hr);
            maxSpeedKmPerHr.setText(R.string.miles_per_hr);
        } else if (knots) {
            avgKmPerHr.setText(R.string.knots);
            maxSpeedKmPerHr.setText(R.string.knots);
        } else {
            avgKmPerHr.setText(R.string.meter_per_second);
            maxSpeedKmPerHr.setText(R.string.meter_per_second);
        }


    }

    private void setThemeColor(int color) {
        if (color != 0) {
            timer.setTextColor(getResources().getColor(color));
            distance_tv_digital.setTextColor(getResources().getColor(color));
            maxspeed.setTextColor(getResources().getColor(color));
            avgspeed.setTextColor(getResources().getColor(color));
            maxSpeedKmPerHr.setTextColor(getResources().getColor(color));
            avgKmPerHr.setTextColor(getResources().getColor(color));
            distance_km.setTextColor(getResources().getColor(color));
            digital_speed_km_per_hr.setTextColor(getResources().getColor(color));
            digitSpeedView.setSpeedTextColor(getActivity());

            Log.d("colorCode", String.valueOf(color));
        }
    }

    public static void updateTrack() {
        try {
            /*ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                //Background work here


                handler.post(() -> {
                    //UI Thread work here


                });
            });*/

            // updating polyline on map is UI thread work, can't be done on background thread
            if (polyline != null) {
                points = polyline.getPoints();  // updating / points when location changes
                points.add(lastKnownLatLng);
                Log.d("polylinesize", String.valueOf(points.size()));
                polyline.setPoints(points);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startLocationUpdates(Context context, GoogleApiClient gApiClient) {
        boundBuilder = new LatLngBounds.Builder();
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(0);
        locationRequest.setSmallestDisplacement(5);
        locationRequest.setFastestInterval(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(gApiClient, locationRequest, this);
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

    public void noSensorsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage(getResources().getString(R.string.compass_not_support))
                .setCancelable(false)
                .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showOrHideTimer() {
        boolean showTimer = pref.getBoolean(SHOwTRAVElTIME, true);
        if (showTimer) {
            timer.setVisibility(View.VISIBLE);
            timerTextWhite.setVisibility(View.VISIBLE);
        } else {
            timer.setVisibility(View.GONE);
            timerTextWhite.setVisibility(View.GONE);
        }
    }

    private void showOrHideMaxSpeed() {
        boolean showMaxSpeed = pref.getBoolean(SHOwMAxSPEED, true);
        if (showMaxSpeed) {
            maxspeed.setVisibility(View.VISIBLE);
            maxSpeedWhiteText.setVisibility(View.VISIBLE);
            maxSpeedKmPerHr.setVisibility(View.VISIBLE);
            if (scrOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                maxSpeedBg.setVisibility(View.VISIBLE);
            }
        } else {
            maxspeed.setVisibility(View.GONE);
            maxSpeedWhiteText.setVisibility(View.GONE);
            maxSpeedKmPerHr.setVisibility(View.GONE);
            if (scrOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                maxSpeedBg.setVisibility(View.GONE);
            }
        }
    }

    private void showOrHideAvgSpeed() {
        boolean showAvgSpeed = pref.getBoolean(SHOwAVgSPEED, true);
        if (showAvgSpeed) {
            avgspeed.setVisibility(View.VISIBLE);
            avg_white_text_km.setVisibility(View.VISIBLE);
            avgKmPerHr.setVisibility(View.VISIBLE);
            if (scrOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                avgSpeedBg.setVisibility(View.VISIBLE);
            }
        } else {
            avgspeed.setVisibility(View.GONE);
            avg_white_text_km.setVisibility(View.GONE);
            avgKmPerHr.setVisibility(View.GONE);
            if (scrOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                avgSpeedBg.setVisibility(View.GONE);
            }
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

    private void getPrefValuesMap() {
        boolean normal = pref.getBoolean(NORMAlMAP, false);
        boolean satellite = pref.getBoolean(SATELLITeMAP, false);
        boolean terrain = pref.getBoolean(TERRAInMAP, false);
        boolean hybrid = pref.getBoolean(HYBRIdMAP, false);

        if (normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }

    public Location getLastKnownLocation(Context c) {
        LocationManager locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void bindService(Context c) {
//        if (status)
//            return;
        Intent i = new Intent(c, LocationService.class);
        c.bindService(i, sc, Context.BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (!status)
            return;
        getActivity().unbindService(sc);
        status = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("buttonState", stopBtnIsVisible);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(MainActivity.myContext), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d("exceptionhai", "onMapReady");
        mMap.setMyLocationEnabled(true);

        try {
            polylineOptions = new PolylineOptions();
            polylineOptions.color(getContext().getResources().getColor(R.color.polylinecolor));
            polylineOptions.width(5);
            polyline = mMap.addPolyline(polylineOptions);
        } catch (Exception e) {
            Log.d("exceptionhai", "onMapReady " + e.getMessage());
            e.printStackTrace();
        }

        if (mapOn) {                                                                                        // mapOn is true when we start map and clear app from RAM, this will resume / re draw all polyline and markers
            try {
                if (currentMarker != null)
                    currentMarker.remove();

                LatLng latLng = new LatLng(points.get(points.size() - 1).latitude,
                        points.get(points.size() - 1).longitude);

                /*mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(points.get(points.size() - 1).latitude,
                                points.get(points.size() - 1).longitude), 12.0f));*/

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));

                currentMarker = googleMap.addMarker(new MarkerOptions().
                        position(new LatLng(points.get(points.size() - 1).latitude,
                                points.get(points.size() - 1).longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                initialMarker = googleMap.addMarker(new MarkerOptions().
                        position(new LatLng(points.get(0).latitude,
                                points.get(0).longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                polyline.setPoints(points);
            } catch (Exception e) {
                Log.d("exceptionhai", "onMapReady mapOn " + e.getMessage());
                e.printStackTrace();
            }


        }
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(MainActivity.myContext),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.myContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onMapLoaded() {

            }


        });


        try {
            getPrefValuesMap();  // get map modes, like normal, satellite etc. It also sets from settings activity
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);


    }

    @Override
    public void onStart() {
        Log.d("exceptionhai", "map started!");
        googleApiClient.connect();
        super.onStart();

    }

    @Override
    public void onStop() {
        Log.d("exceptionhai", "map stopped!");
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        //  stopLocationUpdates();
        stop();
    }

    @Override
    public void onResume() {
        super.onResume();
//        resumed = false;

        if (mMap != null) {
            onMapReady(mMap);

        }
        // getting saved pref values in onResume

        pref = getActivity().getSharedPreferences(MyPREFERENCES, 0);
        editor = pref.edit();
        themeColor = pref.getInt(COLOR_KEY, R.color.green);  //2131034226 default color for app
        setThemeColor(themeColor);
        setSpeedUnits();
        showOrHideSpeedDistance();
        showOrHideAvgSpeed();
        showOrHideMaxSpeed();
        showOrHideTimer();


        if (googleApiClient.isConnected()) {
            start();
        }

        if (resumed) {
            stopBtn.setVisibility(View.VISIBLE);
            startBtn.setVisibility(View.GONE);
        } else {
            startBtn.setVisibility(View.VISIBLE);
            stopBtn.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        resumed = true;
//        if (status)
//            unbindService();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
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
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
        compass_needle.setRotation(-mAzimuth);

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
//        txt_compass.setText(mAzimuth + "° " + where);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("exceptionhai", "onConnected");
        startLocationUpdates(getContext(), googleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //mmap.clear();
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("lastLoc", lastKnownLatLng.latitude + " " + lastKnownLatLng.longitude);
        Log.d("exceptionhai", "onLocationChanged in Map");
        myLocation = location;


        // adding markerr to current position
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("locationis", "location is " + location.getLatitude());

        if (currentMarker != null)
            currentMarker.remove();

        try {
            currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        } catch (Exception e) {


        }
    }
}
