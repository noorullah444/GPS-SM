package com.wonderapps.speedometer.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.wonderapps.speedometer.Activities.ResumeMapActivity;
import com.wonderapps.speedometer.DBHelper.DatabaseHelper;
import com.wonderapps.speedometer.R;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HisMapFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = HisMapFragment.class.getSimpleName();
    private GoogleMap mMap;
    Polyline polyline;
    String lat, lon;
    String distanceExtraText, timerExtraText;
    LatLngBounds.Builder boundBuilder;
    public Marker initialMarker = null;
    private Marker currentMarker = null;
    TextView distanceText;
    TextView timerText;
    TextView dateText;
    private String dateExtraText;
    private String id;
    DatabaseHelper databaseHelper;
    ArrayList<LatLng> myPointsList;
    private LatLng initLatLng;
    private LatLng sydney;
    private ProgressBar progressResume;
    private Context mContext;
    SupportMapFragment supportMapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_his_map, container, false);
        view.setKeepScreenOn(true);

        databaseHelper = new DatabaseHelper(getContext());
        myPointsList = new ArrayList<>();
        progressResume = view.findViewById(R.id.progress_resume);
        distanceText = view.findViewById(R.id.tv_distance);
        timerText = view.findViewById(R.id.timer_text);
        dateText = view.findViewById(R.id.date_text);

        try {
            id = ResumeMapActivity.idExtraText;
            distanceExtraText = ResumeMapActivity.distanceExtraText;
            timerExtraText = ResumeMapActivity.timerExtraText;
            dateExtraText = ResumeMapActivity.dateExtraText;

            if (distanceExtraText != null)
                distanceText.setText(distanceExtraText);
            if (timerExtraText != null)
                timerText.setText(timerExtraText);
            if (dateExtraText != null)
                dateText.setText(dateExtraText);
        } catch (Exception e) {
            Log.d(TAG, "onCreateView: Exception= "+ e.getMessage());
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        FragmentManager fm = getActivity().getSupportFragmentManager();/// getChildFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_view);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_view, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //Your code where exception occurs goes here...
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                boundBuilder = new LatLngBounds.Builder();
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(getContext().getResources().getColor(R.color.polylinecolor));
                polylineOptions.width(5);
                polyline = mMap.addPolyline(polylineOptions);


                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                if (id != null) {
                    Cursor data = databaseHelper.getPathData(id);
                    if (data != null) {
                        if (data.getCount() == 0) {
                            Toast.makeText(getContext(), getResources().getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                        } else {
                            data.moveToFirst();
                            for (int i = 0; i < data.getCount(); i++) {
                                double initLat = Double.parseDouble(data.getString(data.getColumnIndexOrThrow("LAT")));
                                double initLon = Double.parseDouble(data.getString(data.getColumnIndexOrThrow("LON")));

                                Log.d(TAG, "onMapLoaded: lat= "+ initLat);
                                Log.d(TAG, "onMapLoaded: lon= "+ initLon);

                                initLatLng = new LatLng(initLat, initLon);
                                myPointsList.add(initLatLng);
                                data.moveToNext();
                            }
                        }
                    }

                    for (int x = 0; x < myPointsList.size(); x++) {
                        Log.d("latlan", "in for");

//                        LatLng myLoc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
//                        LatLng finalLatLon = new LatLng(finalLat, finalLon);
                        sydney = myPointsList.get(x);
//                        LatLng sydney = new LatLng(34.1241, 72.4613);

                        if (x == 0) {
                            initialMarker = mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));
                        } else {

                            if (currentMarker != null)
                                currentMarker.remove();
                            Log.d("threadCheck", "outside executor");
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            Handler handler = new Handler(Looper.getMainLooper());

                            executor.execute(() -> {
                                //Background work here
                                handler.post(() -> {
                                    Log.e("threadCheck", "inside executor");

                                    currentMarker = mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                });
                            });
                        }
                    }
                    progressResume.setVisibility(View.GONE);
                    try {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    polyline.setPoints(myPointsList);
                }
            }
        });
    }
}
