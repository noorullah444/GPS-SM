package com.wonderapps.speedometer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.wonderapps.speedometer.Adapters.HisPageAdapter;
import com.wonderapps.speedometer.CustomViewPager;
import com.wonderapps.speedometer.Model.GraphModel;
import com.wonderapps.speedometer.R;

import java.util.ArrayList;

public class ResumeMapActivity extends AppCompatActivity {
    private static final String TAG = ResumeMapActivity.class.getSimpleName();
    public static TabLayout tabLayout;
    public static CustomViewPager viewPager;
    HisPageAdapter hisPageAdapter;

    public static String lat, lon;
    public static String nameExtraText, distanceExtraText, timerExtraText, dateExtraText, idExtraText;
    public static ArrayList<GraphModel> coordinatesExtra = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // for screen on,
        View view = getLayoutInflater().inflate(R.layout.activity_resume_map, null);
        view.setKeepScreenOn(true);
        setContentView(view);

        initViews();

        Intent intent = getIntent();
        if (intent != null) {
            lat = intent.getStringExtra("LAT");
            lon = intent.getStringExtra("LON");
            nameExtraText = intent.getStringExtra("name");
            distanceExtraText = intent.getStringExtra("distance");
            timerExtraText = intent.getStringExtra("timer");
            dateExtraText = intent.getStringExtra("date");
            idExtraText = intent.getStringExtra("ID");
            coordinatesExtra = intent.getParcelableArrayListExtra("graphCoordinates");
        }

        viewPager.setOffscreenPageLimit(2);
        hisPageAdapter = new HisPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(hisPageAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                // for disabling swipe
                viewPager.setPagingEnabled(false);

                /*if (viewPager.getCurrentItem() == 2) {
                    viewPager.setPagingEnabled(false);
                } else {
                    viewPager.setPagingEnabled(true);
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void initViews() {
        viewPager = findViewById(R.id.his_viewPager);
        tabLayout = findViewById(R.id.his_tab_layout);
    }
}