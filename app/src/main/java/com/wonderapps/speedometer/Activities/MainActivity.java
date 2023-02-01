package com.wonderapps.speedometer.Activities;

import static com.wonderapps.speedometer.Activities.SettingsActivity.COLOR_KEY;
import static com.wonderapps.speedometer.Activities.SettingsActivity.COLOR_NAME;
import static com.wonderapps.speedometer.Activities.SettingsActivity.LOCkSCREEnROTATION;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MyPREFERENCES;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwBATTERySTATUS;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SHOwSPEEdInNOTIFICATION;
import static com.wonderapps.speedometer.Activities.SettingsActivity.SPEEdLIMItALARM;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.Minutes;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.Seconds;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.startDigitalButton;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.startTime;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.stopBtnIsVisible;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.stopDigitalButton;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wonderapps.speedometer.Adapters.PageAdapter;
import com.wonderapps.speedometer.CustomViewPager;
import com.wonderapps.speedometer.Fragments.DigitalFragment;
import com.wonderapps.speedometer.Fragments.GraphFragment;
import com.wonderapps.speedometer.Fragments.MapFragment;
import com.wonderapps.speedometer.R;

public class MainActivity extends AppCompatActivity {
    private static final int IN_APP_UPDATE_MY_REQUEST_CODE = 123;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MY_PREFS_NAME_SELECT_SOUND = "SelectSound";
    public static final String MY_PREFS_NAME_SELECT_SOUNDONOF = "soundOnOf";
    public static TabLayout tabLayout;
    public static CustomViewPager viewPager;
    PageAdapter pageAdapter;
    TabItem tabChats;
    TabItem tabCalls;
    TabItem tabGraph;
    public static Toolbar toolbar1;
    public static LinearLayout mainLayout;
    DigitalFragment digitalFragment;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int themeColor;
    private int orientation;
    private boolean lockScreenRotation;
    private static FirebaseAnalytics firebaseAnalytics;
    public static boolean restoredNoti = false;
    private CardView privacyLayout;
    private LinearLayout privacyLayoutTitle;
    private static WebView mainWebview;
    private static ImageView closeDialog;
    public static boolean isSpeedLimitOn = false;
    private ImageView batterImg;
    private ImageView chargingIC;
    private TextView batteryPercentage;
    public static Context myContext;
    public static Activity mActivity;
    public static final int GPS_DIALOG_REQUEST_CODE = 1414;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryPercentage.setText(level + "%");
            if (isConnected(MainActivity.this)) {
                chargingIC.setVisibility(View.VISIBLE);

            } else {
                chargingIC.setVisibility(View.GONE);
            }
            myBatteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        }
    };

    private BatteryManager myBatteryManager;
    public static boolean resumed = false;

    AppUpdateManager appUpdateManager;
    private InterstitialAd mInterstitialAd;
    private AdView adView;
    private int adValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//      for screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FrameLayout bannerAdContainer = findViewById(R.id.ad_view_container);
        adView = new AdView(this);
        adView.setAdUnitId(getResources().getString(R.string.admob_banner_ad_id));
        if (bannerAdContainer != null)
            bannerAdContainer.addView(adView);

        if (isOnline(MainActivity.this)) {
            loadBanner();
            loadInterstitialAd();
        } else {
            Log.d(TAG, "onCreate: no internet!");
        }

        initInAppUpdate();
        myContext = MainActivity.this;
        mActivity = MainActivity.this;
        privacyLayout = findViewById(R.id.dialog_layout);
        privacyLayoutTitle = findViewById(R.id.linearLayout_title);
        mainWebview = findViewById(R.id.webview_main);
        closeDialog = findViewById(R.id.dialog_close);
        batterImg = findViewById(R.id.battery_bg);
        chargingIC = findViewById(R.id.charging_icon);
        batteryPercentage = findViewById(R.id.battery_percentage);
        registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        showPrivacyPolicyDialog();//just load the privacy dialog, will show later

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);    // for firebase analytics

        Intent intent = getIntent();
        digitalFragment = new DigitalFragment();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    // for full screen activity
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tabLayout = findViewById(R.id.tablayout);
        tabChats = findViewById(R.id.tabChats);
        tabCalls = findViewById(R.id.tabCalls);
        mainLayout = findViewById(R.id.mainLayout);
        orientation = getResources().getConfiguration().orientation;
        toolbar1 = findViewById(R.id.toolbar1);
        toolbar1.setTitle("");
        setSupportActionBar(toolbar1);

        tabGraph = findViewById(R.id.tabGraph);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        if (intent.getStringExtra("Activity") != null && intent.getStringExtra("Activity").equals("map")) {
            viewPager.setCurrentItem(1, true);
        }

        viewPager.setAdapter(pageAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                // for disabling swipe in graph fragment
                if (viewPager.getCurrentItem() == 2) {
                    viewPager.setPagingEnabled(false);
                } else {
                    viewPager.setPagingEnabled(true);
                }
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

    private void loadBanner() {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize0();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);
//        bannerLinearLayout0.setVisibility(View.VISIBLE);
        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize0() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
        //return AdSize.getPortraitAnchoredAdaptiveBannerAdSize(this,adWidth);
        //return AdSize.getLandscapeAnchoredAdaptiveBannerAdSize(this,adWidth);
    }

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager connec =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // Check for network connections
            if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                    connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                    connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                    connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
                return true;
            } else if (
                    connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                            connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getResources().getString(R.string.admob_interstitial_ad_id_splash), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");

                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d(TAG, "The ad was dismissed.");
                                mInterstitialAd = null;
                                loadInterstitialAd();

                                if (adValue == 1) {
                                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                                } else if (adValue == 2) {
                                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                                } else if (adValue == 3) {
                                    startActivity(new Intent(MainActivity.this, ExitActivity.class));
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d(TAG, "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d(TAG, "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

    }

    private void initInAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // This example applies an immediate update. To apply a flexible update
                        // instead, pass in AppUpdateType.FLEXIBLE
                        && result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.FLEXIBLE, MainActivity.this, IN_APP_UPDATE_MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    private final InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showCompleteUpdate();
            }
        }
    };

    @Override
    protected void onStop() {
        if (appUpdateManager != null)
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onStop();
    }

    private void showCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "New update is ready!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("buttonState", stopBtnIsVisible);
        outState.putLong("timer", startTime);
        outState.putInt("minutes", Minutes);
        outState.putInt("seconds", Seconds);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        if (savedInstanceState != null) {
            boolean btnState = savedInstanceState.getBoolean("buttonState");

            long Timer = savedInstanceState.getLong("timer");
            int mins = savedInstanceState.getInt("minutes");
            int secs = savedInstanceState.getInt("seconds");

            boolean state = savedInstanceState.getBoolean("buttonState");
            if (state) {

//                StartTime = SystemClock.uptimeMillis();
//                handler.postDelayed(runnable, 0);

                if (stopBtnIsVisible) {
                    stopDigitalButton.setVisibility(View.INVISIBLE);
                    startDigitalButton.setVisibility(View.VISIBLE);
                } else {
                    stopDigitalButton.setVisibility(View.VISIBLE);
                    startDigitalButton.setVisibility(View.INVISIBLE);
                }

                MapFragment.startBtn.setVisibility(View.INVISIBLE);
                MapFragment.stopBtn.setVisibility(View.VISIBLE);
                GraphFragment.startDigitalBtn.setVisibility(View.INVISIBLE);
                GraphFragment.stopDigitalButton.setVisibility(View.VISIBLE);

                startTime = savedInstanceState.getLong("timer");
                Log.d("mins_and_secs", String.valueOf(startTime));

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        pref = getApplicationContext().getSharedPreferences(MyPREFERENCES, 0);
        String stringColor = pref.getString(COLOR_NAME, "green");
//        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        changeBatteryIcon(stringColor);
        showOrHideBatteryPercentage();
//        }


        restoredNoti = pref.getBoolean(SHOwSPEEdInNOTIFICATION, true);
        editor = pref.edit();
        themeColor = pref.getInt(COLOR_KEY, R.color.green);    // 2131034226 is the default app green color .........
        // for lock screen
        lockScreenRotation = pref.getBoolean(LOCkSCREEnROTATION, false);
        if (lockScreenRotation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // for speed limit on/off option
        isSpeedLimitOn = pref.getBoolean(SPEEdLIMItALARM, false);
        Log.d("mainActivity", "isSpeedOn " + isSpeedLimitOn);
        // theme color will be applied for all textviews and some other views
        setThemeColor(themeColor);


    }

    private void changeBatteryIcon(String color) {
        switch (color) {
            case "blue":
                Log.d("colorName", color);
                batterImg.setImageResource(R.drawable.blue_battery);
                break;
            case "red":
                batterImg.setImageResource(R.drawable.red_battery);
                break;
            case "green":
                batterImg.setImageResource(R.drawable.green_battery);
                break;
            case "pink":
                batterImg.setImageResource(R.drawable.pink_battery);
                break;
            case "yellow":
                batterImg.setImageResource(R.drawable.yellow_battery);
                break;
            case "orange":
                batterImg.setImageResource(R.drawable.orange_battery);
                break;
            case "purple":
                batterImg.setImageResource(R.drawable.purple_battery);
                break;

        }
    }

    private void setThemeColor(int color) {
        if (color != 0) {
            privacyLayoutTitle.setBackgroundColor(color);
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(color));
            tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(color));
//            if (orientation == Configuration.ORIENTATION_PORTRAIT)
            toolbar1.getOverflowIcon().setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainActivity.this, ExitActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_setting:
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    adValue = 1;
                } else {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.action_history:
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    adValue = 2;
                } else {
                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                }
                return true;
            /*case R.id.action_rate_us:
                Intent intent2 = new Intent(MainActivity.this, RatingsActivity.class);
                startActivity(intent2);
                finish();
                return true;*/
            case R.id.action_more_apps:
                gotoMoreApps();
                return true;
            case R.id.action_privacy_policy:
                viewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                privacyLayout.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_exit:
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    adValue = 3;
                } else {
                    startActivity(new Intent(MainActivity.this, ExitActivity.class));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void gotoMoreApps() {
        Uri uri2 = Uri.parse("https://play.google.com/store/apps/dev?id=5611819854980639847");
        Intent goToMarket2 = new Intent(Intent.ACTION_VIEW, uri2);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket2);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/dev?id=5611819854980639847")));
        }
    }

    public void showPrivacyPolicyDialog() {
        mainWebview.loadUrl("https://sites.google.com/view/privacy-policy-speedometer/home");
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privacyLayout.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
            }
        });

        mainWebview.clearCache(true);
        mainWebview.destroyDrawingCache();
    }

    private void unregisterReceiver() {
        int apiLevel = Build.VERSION.SDK_INT;

        if (apiLevel >= 7) {
            try {
                getApplicationContext().unregisterReceiver(mBatInfoReceiver);
            } catch (IllegalArgumentException e) {
                mBatInfoReceiver = null;
            }
        } else {
            getApplicationContext().unregisterReceiver(mBatInfoReceiver);
            mBatInfoReceiver = null;
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

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isUSBCharging() {
        return myBatteryManager.isCharging();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resumed = stopBtnIsVisible;
        editor.putBoolean("isResumed", resumed);
        editor.apply();
        Log.d("destroyed", "main Activity destroyed");

        unregisterReceiver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MainActivity.GPS_DIALOG_REQUEST_CODE) {
                DigitalFragment.gpsEnabled = true;  // this callBack method calls when user enable GPS
                Toast.makeText(MainActivity.this, getResources().getString(R.string.gps_turned_on), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == IN_APP_UPDATE_MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.d(TAG, "onActivityResult: Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }

    }
}
