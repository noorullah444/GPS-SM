package com.wonderapps.speedometer.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wonderapps.speedometer.DigitSpeedView;
import com.wonderapps.speedometer.ImageSpeedometer;
import com.wonderapps.speedometer.LocationService.LocationService;
import com.wonderapps.speedometer.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class    AnalogFragment extends Fragment {

    LocationService myService;
    static boolean status;
    LocationManager locationManager;
    static TextView dist, time;
    public static ImageSpeedometer speed;
    static DigitSpeedView speeds;
    Button start, pause, stop;
    Button hudAnalog;
    public  static long startTime, endTime;
    ImageView image;
    static ProgressDialog locate;

    RelativeLayout mainRelativeLayoutAnalog;

  public   static int p = 0;
    ImageView kmphbtn,mphbtn;
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

    void bindService() {
        if (status)
            return;
        Intent i = new Intent(getContext(), LocationService.class);
        requireActivity().bindService(i, sc, Context.BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (!status)
            return;
        Intent i = new Intent(getContext(), LocationService.class);
        requireActivity().unbindService(sc);
        status = false;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (status)
            unbindService();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_analog, container, false);

        speed=(ImageSpeedometer)view.findViewById(R.id.imageSpeedometer);
        speed.setImageSpeedometer(R.drawable.needle);
        speeds = (DigitSpeedView) view.findViewById(R.id.speed_text);
        start = (Button)view.findViewById(R.id.start);
        pause = (Button) view.findViewById(R.id.pause);

        mainRelativeLayoutAnalog = view.findViewById(R.id.main_Relative_layout_analog);

        hudAnalog = view.findViewById(R.id.hud_analog);
        hudAnalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainRelativeLayoutAnalog.setRotationX(180f);
                //Toast.makeText(getActivity(), "Button pressed", Toast.LENGTH_SHORT).show();
            }
        });

        hudAnalog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mainRelativeLayoutAnalog.setRotationX(360f);
                //Toast.makeText(getActivity(), "Long pressed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        stop = (Button) view.findViewById(R.id.stop);
        kmphbtn=(ImageView)view.findViewById(R.id.kmphbtn);
        mphbtn=(ImageView)view.findViewById(R.id.mphbtn);
        kmphbtn.setSelected(true);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //The method below checks if Location is enabled on device or not. If not, then an alert dialog box appears with option
                //to enable gps.
                checkGps();
                locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    return;
                }
                if (!status)
                    //Here, the Location Service gets bound and the GPS Speedometer gets Active.
                    bindService();
                locate = new ProgressDialog(getContext());
                locate.setIndeterminate(true);
                locate.setCancelable(false);
                locate.setMessage("Getting Location...");
                locate.show();
                start.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                pause.setText("Pause");
                stop.setVisibility(View.VISIBLE);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pause.getText().toString().equalsIgnoreCase("pause")) {
                    pause.setText("Resume");
                    p = 1;

                } else if (pause.getText().toString().equalsIgnoreCase("Resume")) {
                    checkGps();
                    locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pause.setText("Pause");
                    p = 0;

                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status)
                    unbindService();
                start.setText("Restart");
                start.setVisibility(View.VISIBLE);

                pause.setText("Pause");
                pause.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                p = 0;
            }
        });

        kmphbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mphbtn.setSelected(false);
                kmphbtn.setSelected(true);
            }
        });

        mphbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                kmphbtn.setSelected(false);
                mphbtn.setSelected(true);
            }
        });
        return view;
    }
    void checkGps() {
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            showGPSDisabledAlertToUser();
        }
    }
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}
