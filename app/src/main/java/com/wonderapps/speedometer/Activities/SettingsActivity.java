package com.wonderapps.speedometer.Activities;

import static com.wonderapps.speedometer.Activities.MainActivity.isOnline;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.wonderapps.speedometer.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    TextView speed_limit_tv_settings;
    String selectedItem;

    public static final String MyPREFERENCES = "SpeedMeterSettings";
    public static final String SPEEdLIMItALARM = "speedLimitAlarmS";
    public static final String LOCkSCREEnROTATION = "lockScreenRotationS";
    public static final String KPH = "kmperhrS";
    public static final String MPH = "milesperhrS";
    public static final String KNOTS = "knotsS";
    public static final String MPS = "mPerSs";
    public static final String ANALOG = "analogS";
    public static final String DIGITAL = "digitalS";
    public static final String BOTH = "both";
    public static final String SHOwCLOCK = "showClockS";
    public static final String SHOwCOMPASS = "showCompass";
    public static final String SHOwTRAVElDISTANCE = "showTravelDistance";
    public static final String SHOwTRAVElTIME = "showTravelTime";
    public static final String SHOwAVgSPEED = "showAvgSpeed";
    public static final String SHOwMAxSPEED = "showMaxSpeed";
    public static final String SHOwSPEEdInNOTIFICATION = "onnotification";
    public static final String SHOwBATTERySTATUS = "showBatteryStatusS";
    public static final String LIMItSPEED = "limitSpeed";
    public static final String SELECTEdITEM = "selectedItem";
    public static final String NORMAlMAP = "normalMap";
    public static final String SATELLITeMAP = "satelliteMap";
    public static final String TERRAInMAP = "terrainMap";
    public static final String HYBRIdMAP = "hybridMap";


    SwitchCompat speedLimitAlarmS, lockScreenRotationS;
    FrameLayout nativeAdContainer;
    RadioButton kmPerHrS, milesPerHrS, knotsS, mPerSs;
    RadioButton analogS, digitalS, bothS;
    RadioButton normalMap, satelliteMap, terrainMap, hybridMap;
    CheckBox showClockS, showCompassS, showTravelDistanceS, showTravelTimeS,
            showAvgSpeedS, showMaxSpeedS, showSpeedInNotificationS, showBatteryStatusS;

    private SharedPreferences pref;
    private ImageView purple, pink, yellow, red, blue, green, darkYellow;
    private SharedPreferences.Editor editor;
    public static String COLOR_KEY = "bg_color";
    public static String COLOR_NAME = "meter_color";
    private TextView speedAlarmTxt, lockScreenTxt, setUnitsTxt, showSpeedMeter, moreOptionsTxt, displaySettingsTxt, themeColorTxt;
    private int themeColor;
    private boolean lockScreen = false;
    private ImageView backButton;
    private SeekBar seekBar;
    private NativeAd nativeAd;
    private NativeAdView nativeAdView;

    @Override
    protected void onResume() {
        super.onResume();
        lockScreen = pref.getBoolean(LOCkSCREEnROTATION, false);
        if (lockScreen)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 0 - for private mode
        pref = getApplicationContext().getSharedPreferences(MyPREFERENCES, 0);
        editor = pref.edit();
        themeColor = pref.getInt(COLOR_KEY, R.color.green);

        //initialization
        initializeAllViews();

        // theme color will be applied for all text views and some other views
        setThemeColor(themeColor);
        setThemeColorForAllCheckBoxesAndRadioButtons(themeColor);

        initColors();
        initListenerForColors();
        setUpClickListeners();
        getRefSavedValues();

        if (isOnline(SettingsActivity.this)) {
            loadNativeAdSmall();
        }
    }

    private void setUpClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        speedLimitAlarmS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(SPEEdLIMItALARM, true);

                    // for checking if theme color is not equal to 0, means if app is installed first time then default color will be applied
                    if (themeColor != 0)
                        speedLimitAlarmS.getThumbDrawable().setColorFilter(ContextCompat.getColor(SettingsActivity.this, themeColor), PorterDuff.Mode.MULTIPLY);
                    else
                        speedLimitAlarmS.getThumbDrawable().setColorFilter(ContextCompat.getColor(SettingsActivity.this, R.color.texts_color), PorterDuff.Mode.MULTIPLY);
                } else {
                    editor.putBoolean(SPEEdLIMItALARM, false);
                    speedLimitAlarmS.getThumbDrawable().setColorFilter(ContextCompat.getColor(SettingsActivity.this, R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                editor.apply();
            }
        });

        lockScreenRotationS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(LOCkSCREEnROTATION, true);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    // for checking if theme color is not equal to 0, means if app is installed first time then default color will be applied
                    if (themeColor != 0)
                        lockScreenRotationS.getThumbDrawable().setColorFilter(ContextCompat.getColor(SettingsActivity.this, themeColor), PorterDuff.Mode.MULTIPLY);
                    else
                        lockScreenRotationS.getThumbDrawable().setColorFilter(ContextCompat.getColor(SettingsActivity.this, R.color.texts_color), PorterDuff.Mode.MULTIPLY);
                } else {
                    editor.putBoolean(LOCkSCREEnROTATION, false);
                    lockScreenRotationS.getThumbDrawable().setColorFilter(ContextCompat.getColor(SettingsActivity.this, R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                editor.apply();
            }
        });

        kmPerHrS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(KPH, true);
                    milesPerHrS.setChecked(false);
                    knotsS.setChecked(false);
                    mPerSs.setChecked(false);


                } else {
                    editor.putBoolean(KPH, false);

                }
                editor.apply();
            }
        });

        milesPerHrS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(MPH, true);

                    kmPerHrS.setChecked(false);
                    knotsS.setChecked(false);
                    mPerSs.setChecked(false);

                } else {
                    editor.putBoolean(MPH, false);
                }
                editor.apply();
            }
        });

        knotsS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(KNOTS, true);


                    kmPerHrS.setChecked(false);
                    milesPerHrS.setChecked(false);
                    mPerSs.setChecked(false);

                } else {
                    editor.putBoolean(KNOTS, false);
                }
                editor.apply();
            }
        });

        mPerSs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(MPS, true);

                    kmPerHrS.setChecked(false);
                    knotsS.setChecked(false);
                    milesPerHrS.setChecked(false);

                } else {
                    editor.putBoolean(MPS, false);
                }
                editor.apply();
            }
        });

        analogS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(ANALOG, true);

                    digitalS.setChecked(false);
                    bothS.setChecked(false);

                } else {
                    editor.putBoolean(ANALOG, false);
                }
                editor.apply();
            }
        });

        digitalS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(DIGITAL, true);

                    analogS.setChecked(false);
                    bothS.setChecked(false);

                } else {
                    editor.putBoolean(DIGITAL, false);
                }
                editor.apply();
            }
        });

        bothS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(BOTH, true);

                    digitalS.setChecked(false);
                    analogS.setChecked(false);

                } else {
                    editor.putBoolean(BOTH, false);
                }
                editor.apply();
            }
        });

        normalMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(NORMAlMAP, true);

                    satelliteMap.setChecked(false);
                    terrainMap.setChecked(false);
                    hybridMap.setChecked(false);

                } else {
                    editor.putBoolean(NORMAlMAP, false);
                }
                editor.apply();
            }
        });

        satelliteMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(SATELLITeMAP, true);

                    normalMap.setChecked(false);
                    terrainMap.setChecked(false);
                    hybridMap.setChecked(false);

                } else {
                    editor.putBoolean(SATELLITeMAP, false);
                }
                editor.apply();
            }
        });

        terrainMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(TERRAInMAP, true);

                    normalMap.setChecked(false);
                    satelliteMap.setChecked(false);
                    hybridMap.setChecked(false);

                } else {
                    editor.putBoolean(TERRAInMAP, false);
                }
                editor.apply();
            }
        });

        hybridMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(HYBRIdMAP, true);

                    normalMap.setChecked(false);
                    satelliteMap.setChecked(false);
                    terrainMap.setChecked(false);

                } else {
                    editor.putBoolean(HYBRIdMAP, false);
                }
                editor.apply();
            }
        });

        showClockS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(SHOwCLOCK, true);

                } else {
                    editor.putBoolean(SHOwCLOCK, false);
                }
                editor.apply();
            }
        });

        showCompassS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(SHOwCOMPASS, true);

                } else {
                    editor.putBoolean(SHOwCOMPASS, false);
                }
                editor.apply();
            }
        });

        showTravelDistanceS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(SHOwTRAVElDISTANCE, true);

                } else {
                    editor.putBoolean(SHOwTRAVElDISTANCE, false);
                }
                editor.apply();
            }
        });

        showTravelTimeS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(SHOwTRAVElTIME, true);

                } else {
                    editor.putBoolean(SHOwTRAVElTIME, false);
                }
                editor.apply();
            }
        });

        showAvgSpeedS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(SHOwAVgSPEED, true);

                } else {
                    editor.putBoolean(SHOwAVgSPEED, false);
                }
                editor.apply();
            }
        });

        showMaxSpeedS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(SHOwMAxSPEED, true);

                } else {
                    editor.putBoolean(SHOwMAxSPEED, false);
                }
                editor.apply();
            }
        });

        showSpeedInNotificationS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(SHOwSPEEdInNOTIFICATION, true);
//                    editor.putString("onnotification", "notification");

                } else {
                    editor.putBoolean(SHOwSPEEdInNOTIFICATION, false);
//                    editor.putString("onnotification", "notification");
                }
                editor.apply();
            }
        });

        showBatteryStatusS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(SHOwBATTERySTATUS, true);

                } else {
                    editor.putBoolean(SHOwBATTERySTATUS, false);
                }
                editor.apply();
            }
        });

        speed_limit_tv_settings = findViewById(R.id.speed_limit_tv_settings);
        speed_limit_tv_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*final Dialog dialog = new Dialog(SettingsActivity.this);
                dialog.setContentView(R.layout.custom);
                dialog.setCancelable(false);

                //initialization
                custom_speed_limit_et_settings = dialog.findViewById(R.id.custom_speed_limit_et_settings);
                Spinner custom_spinner_settings = dialog.findViewById(R.id.custom_spinner_settings);
                Button custom_ok_button_settings = dialog.findViewById(R.id.custom_ok_button_settings);
                Button custom_cancel_button_settings = dialog.findViewById(R.id.custom_cancel_button_settings);


                String limitSpeed = pref.getString(LIMItSPEED, null);
                custom_speed_limit_et_settings.setText(limitSpeed);


                dialog.show();


                custom_cancel_button_settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                try {
                    Field popup = Spinner.class.getDeclaredField("mPopup");
                    popup.setAccessible(true);

                    // Get private mPopup member variable and try cast to ListPopupWindow
                    android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(custom_spinner_settings);

                    // Set popupWindow height to 500px
                    popupWindow.setHeight(800);
                } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
                    // silently fail...
                }
                // Initializing a String Array
                final String[] plants = new String[]{
                        "km/hr", "m/s", "miles/hr", "knots"
                };

                final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));
                // Initializing an ArrayAdapter
                final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                        (SettingsActivity.this, R.layout.spinner_item, plantsList) {
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        return view;

                    }
                };
                spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
                custom_spinner_settings.setAdapter(spinnerArrayAdapter);

                custom_spinner_settings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        selectedItem = custom_spinner_settings.getItemAtPosition(position).toString();
                        // Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_LONG).show();


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                custom_ok_button_settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String limitSpeed = custom_speed_limit_et_settings.getText().toString();

                        if (limitSpeed.isEmpty()) {
                            Toast.makeText(SettingsActivity.this, getResources().getString(R.string.enter_speed_limit_settings), Toast.LENGTH_SHORT).show();
                        } else if (limitSpeed.length() > 3) {
                            Toast.makeText(SettingsActivity.this, getResources().getString(R.string.speed_is_too_long_settings), Toast.LENGTH_SHORT).show();
                        } else {
                            speed_limit_tv_settings.setText(limitSpeed + " " + selectedItem);

                            editor.putString(LIMItSPEED, limitSpeed);
                            editor.putString(SELECTEdITEM, selectedItem);
                            editor.apply();
                            dialog.dismiss();
                        }

                    }
                });*/
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String limitSpeed = String.valueOf(progress);

                if (limitSpeed.isEmpty()) {
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.enter_speed_limit_settings), Toast.LENGTH_SHORT).show();
                } else if (limitSpeed.length() > 3) {
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.speed_is_too_long_settings), Toast.LENGTH_SHORT).show();
                } else {
                    speed_limit_tv_settings.setText(limitSpeed);

                    editor.putString(LIMItSPEED, limitSpeed);
                    editor.putString(SELECTEdITEM, selectedItem);
                    editor.apply();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void loadNativeAdSmall() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native_ad_id));
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd1) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = nativeAd1;

                nativeAdContainer = findViewById(R.id.ad_view_container_small);
                nativeAdView = (NativeAdView) getLayoutInflater()
                        .inflate(R.layout.ad_unified_small, null);
                populateNativeAdView(nativeAd1, nativeAdView);
                nativeAdContainer.removeAllViews();
                nativeAdContainer.addView(nativeAdView);
                nativeAdContainer.setVisibility(View.VISIBLE);
            }
        });

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        //adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        ConstraintLayout constraint = adView.findViewById(R.id.constraint);
        TextView ad_body = adView.findViewById(R.id.ad_body);
        TextView ad_headline = adView.findViewById(R.id.ad_headline);


        //adView.setStoreView(adView.findViewById(R.id.ad_store));
        //adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.GONE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.GONE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraint);
            constraintSet.connect(R.id.ad_headline, ConstraintSet.TOP, constraint.getId(), ConstraintSet.TOP, 24);
            constraintSet.connect(R.id.ad_headline, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 32);
            constraintSet.connect(R.id.ad_body, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 32);
            constraintSet.connect(R.id.ad_body, ConstraintSet.TOP, R.id.ad_headline, ConstraintSet.BOTTOM, 3);
            constraintSet.applyTo(constraint);

            ad_headline.setTextSize(16);
            ad_body.setTextSize(14);

        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }

    private void setThemeColorForAllCheckBoxesAndRadioButtons(int color) {
        if (color != 0) {
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(showClockS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(showCompassS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(showTravelDistanceS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(showTravelTimeS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(showAvgSpeedS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(showMaxSpeedS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(showSpeedInNotificationS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(showBatteryStatusS, ColorStateList.valueOf(getResources().getColor(color)));

                // radio boxes
                CompoundButtonCompat.setButtonTintList(kmPerHrS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(milesPerHrS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(knotsS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(mPerSs, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(analogS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(digitalS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(bothS, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(normalMap, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(satelliteMap, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(terrainMap, ColorStateList.valueOf(getResources().getColor(color)));
                CompoundButtonCompat.setButtonTintList(hybridMap, ColorStateList.valueOf(getResources().getColor(color)));
            } else {
                showClockS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));     //setButtonTintList is accessible directly on API>19
                showCompassS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));     //setButtonTintList is accessible directly on API>19
                showTravelDistanceS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));     //setButtonTintList is accessible directly on API>19
                showTravelTimeS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));     //setButtonTintList is accessible directly on API>19
                showAvgSpeedS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));     //setButtonTintList is accessible directly on API>19
                showMaxSpeedS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));     //setButtonTintList is accessible directly on API>19
                showSpeedInNotificationS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));     //setButtonTintList is accessible directly on API>19
                showBatteryStatusS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));

                // radio boxes
                kmPerHrS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));
                milesPerHrS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));
                knotsS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));
                mPerSs.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));
                analogS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));
                digitalS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));
                bothS.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));
                normalMap.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));
                satelliteMap.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));
                terrainMap.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));
                hybridMap.setButtonTintList(ColorStateList.valueOf(getResources().getColor(color)));

                themeColor = color;         // is just to assigning color to themeColor to avoid restarting phone, because sharedpref value get at start

                // we want to change activated/ on switch color so that's why its checking before .......
                if (speedLimitAlarmS.isChecked()) {
                    speedLimitAlarmS.getThumbDrawable().setColorFilter(ContextCompat.getColor(SettingsActivity.this, color), PorterDuff.Mode.MULTIPLY);
                }
                if (lockScreenRotationS.isChecked()) {
                    lockScreenRotationS.getThumbDrawable().setColorFilter(ContextCompat.getColor(SettingsActivity.this, color), PorterDuff.Mode.MULTIPLY);
                }
            }
        }
    }

    private void setThemeColor(int color) {
        if (color != 0) {
            speedAlarmTxt.setTextColor(getResources().getColor(color));
            lockScreenTxt.setTextColor(getResources().getColor(color));
            setUnitsTxt.setTextColor(getResources().getColor(color));
            showSpeedMeter.setTextColor(getResources().getColor(color));
            moreOptionsTxt.setTextColor(getResources().getColor(color));
            displaySettingsTxt.setTextColor(getResources().getColor(color));
            themeColorTxt.setTextColor(getResources().getColor(color));
            seekBar.getProgressDrawable().setTint(getResources().getColor(color));
            setThemeColorForAllCheckBoxesAndRadioButtons(color);  // also calling this method to change all colors with click with using any call back method or getting prefs value again  again

            Log.d("colorCode", String.valueOf(color));
        }
    }

    private void initializeAllViews() {
        //switch
        speedLimitAlarmS = findViewById(R.id.speed_limit_alarm_switch);
        lockScreenRotationS = findViewById(R.id.lock_screen_rotation_switch);

        //radio button
        kmPerHrS = findViewById(R.id.km_per_hr_radio_button);
        milesPerHrS = findViewById(R.id.miles_per_hr_radio_button);
        knotsS = findViewById(R.id.knots_radio_button);
        mPerSs = findViewById(R.id.m_per_second_radio_button);
        analogS = findViewById(R.id.analog_radio_button);
        digitalS = findViewById(R.id.digital_radio_button);
        bothS = findViewById(R.id.both_radio_button);
        normalMap = findViewById(R.id.normal_map_type);
        satelliteMap = findViewById(R.id.satellite_map_type);
        terrainMap = findViewById(R.id.terrain_map_type);
        hybridMap = findViewById(R.id.hybrid_map_type);

        //check box
        showClockS = findViewById(R.id.show_clock_check_box);
        showCompassS = findViewById(R.id.show_compass_check_box);
        showTravelDistanceS = findViewById(R.id.show_travel_distance_check_box);
        showTravelTimeS = findViewById(R.id.show_travel_time_check_box);
        showAvgSpeedS = findViewById(R.id.show_avg_speed_check_box);
        showMaxSpeedS = findViewById(R.id.show_max_speed_check_box);
        showSpeedInNotificationS = findViewById(R.id.show_speed_in_notification_check_box);
        showBatteryStatusS = findViewById(R.id.show_battery_status_check_box);


        // all textViews
        speedAlarmTxt = findViewById(R.id.speedAlarm);
        lockScreenTxt = findViewById(R.id.lock_screen_txt);
        setUnitsTxt = findViewById(R.id.set_units_txt);
        showSpeedMeter = findViewById(R.id.show_speed_meter);
        moreOptionsTxt = findViewById(R.id.more_options_txt);
        displaySettingsTxt = findViewById(R.id.display_settings_txt);
        themeColorTxt = findViewById(R.id.theme_color_txt);

        // Button
        backButton = findViewById(R.id.back_btn);

        // seek bar
        seekBar = findViewById(R.id.seek_bar_speed_limit);
    }

    private void initListenerForColors() {
        purple.setOnClickListener(this);
        pink.setOnClickListener(this);
        yellow.setOnClickListener(this);
        darkYellow.setOnClickListener(this);
        green.setOnClickListener(this);
        red.setOnClickListener(this);
        blue.setOnClickListener(this);
    }

    private void initColors() {
        purple = findViewById(R.id.purple);
        pink = findViewById(R.id.pink);
        yellow = findViewById(R.id.yellow);
        green = findViewById(R.id.green);
        blue = findViewById(R.id.blue);
        red = findViewById(R.id.red);
        darkYellow = findViewById(R.id.dark_yellow);
    }

    public void getRefSavedValues() {
        boolean speedAlarm = pref.getBoolean(SPEEdLIMItALARM, false);
        boolean lockScreenRotation = pref.getBoolean(LOCkSCREEnROTATION, false);

        boolean kph = pref.getBoolean(KPH, true);
        boolean mph = pref.getBoolean(MPH, false);
        boolean knots = pref.getBoolean(KNOTS, false);
        boolean mps = pref.getBoolean(MPS, false);

        boolean analog = pref.getBoolean(ANALOG, false);
        boolean digital = pref.getBoolean(DIGITAL, false);
        boolean both = pref.getBoolean(BOTH, true);

        boolean normal = pref.getBoolean(NORMAlMAP, true);
        boolean satellite = pref.getBoolean(SATELLITeMAP, false);
        boolean terrain = pref.getBoolean(TERRAInMAP, false);
        boolean hybrid = pref.getBoolean(HYBRIdMAP, false);

        boolean showClock = pref.getBoolean(SHOwCLOCK, true);
        boolean showCompass = pref.getBoolean(SHOwCOMPASS, true);
        boolean showTravelDistance = pref.getBoolean(SHOwTRAVElDISTANCE, true);
        boolean showTravelTime = pref.getBoolean(SHOwTRAVElTIME, true);
        boolean showAvgSpeed = pref.getBoolean(SHOwAVgSPEED, true);
        boolean showMaxSpeed = pref.getBoolean(SHOwMAxSPEED, true);
        boolean showSpeedInNotification = pref.getBoolean(SHOwSPEEdInNOTIFICATION, true);
        boolean showBatteryStatus = pref.getBoolean(SHOwBATTERySTATUS, true);

        String limitSpeed = pref.getString(LIMItSPEED, "100");
        String selectedItem = pref.getString(SELECTEdITEM, null);


        speedLimitAlarmS.setChecked(speedAlarm);
        lockScreenRotationS.setChecked(lockScreenRotation);

        kmPerHrS.setChecked(kph);
        milesPerHrS.setChecked(mph);
        knotsS.setChecked(knots);
        mPerSs.setChecked(mps);

        analogS.setChecked(analog);
        digitalS.setChecked(digital);
        bothS.setChecked(both);

        normalMap.setChecked(normal);
        satelliteMap.setChecked(satellite);
        terrainMap.setChecked(terrain);
        hybridMap.setChecked(hybrid);

        showClockS.setChecked(showClock);
        showCompassS.setChecked(showCompass);
        showTravelDistanceS.setChecked(showTravelDistance);
        showTravelTimeS.setChecked(showTravelTime);
        showAvgSpeedS.setChecked(showAvgSpeed);
        showMaxSpeedS.setChecked(showMaxSpeed);
        showSpeedInNotificationS.setChecked(showSpeedInNotification);
        showBatteryStatusS.setChecked(showBatteryStatus);

        //custom_speed_limit_et_settings.setText(limitSpeed+" "+selectedItem);
        speed_limit_tv_settings.setText(limitSpeed);
        seekBar.setProgress(Integer.parseInt(limitSpeed));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.purple:
                purple.setImageResource(R.drawable.selected_purple);
                pink.setImageResource(R.drawable.theme_circle_dark_pink);
                yellow.setImageResource(R.drawable.theme_circle_yellow);
                darkYellow.setImageResource(R.drawable.theme_circle_dark_yellow);
                red.setImageResource(R.drawable.theme_circle_red);
                blue.setImageResource(R.drawable.theme_circle_blue);
                green.setImageResource(R.drawable.theme_circle_green);
                editor.putInt(COLOR_KEY, R.color.purple);
                editor.putString(COLOR_NAME, "purple");
                setThemeColor(R.color.purple);
                editor.commit();
                break;
            case R.id.pink:
                pink.setImageResource(R.drawable.selected_pink);
                purple.setImageResource(R.drawable.theme_circle_purple);
                yellow.setImageResource(R.drawable.theme_circle_yellow);
                darkYellow.setImageResource(R.drawable.theme_circle_dark_yellow);
                red.setImageResource(R.drawable.theme_circle_red);
                blue.setImageResource(R.drawable.theme_circle_blue);
                green.setImageResource(R.drawable.theme_circle_green);
                editor.putInt(COLOR_KEY, R.color.dark_pink);
                editor.putString(COLOR_NAME, "pink");
                setThemeColor(R.color.dark_pink);
                editor.commit();
                break;
            case R.id.yellow:
                yellow.setImageResource(R.drawable.selected_yellow);
                pink.setImageResource(R.drawable.theme_circle_dark_pink);
                purple.setImageResource(R.drawable.theme_circle_purple);
                darkYellow.setImageResource(R.drawable.theme_circle_dark_yellow);
                red.setImageResource(R.drawable.theme_circle_red);
                blue.setImageResource(R.drawable.theme_circle_blue);
                green.setImageResource(R.drawable.theme_circle_green);
                editor.putInt(COLOR_KEY, R.color.yellow);
                editor.putString(COLOR_NAME, "yellow");
                setThemeColor(R.color.yellow);
                editor.commit();
                break;
            case R.id.red:
                red.setImageResource(R.drawable.selected_red);
                pink.setImageResource(R.drawable.theme_circle_dark_pink);
                yellow.setImageResource(R.drawable.theme_circle_yellow);
                darkYellow.setImageResource(R.drawable.theme_circle_dark_yellow);
                purple.setImageResource(R.drawable.theme_circle_purple);
                blue.setImageResource(R.drawable.theme_circle_blue);
                green.setImageResource(R.drawable.theme_circle_green);
                editor.putInt(COLOR_KEY, R.color.red);
                editor.putString(COLOR_NAME, "red");
                setThemeColor(R.color.red);
                editor.commit();
                break;
            case R.id.blue:
                blue.setImageResource(R.drawable.selected_blue);
                pink.setImageResource(R.drawable.theme_circle_dark_pink);
                yellow.setImageResource(R.drawable.theme_circle_yellow);
                darkYellow.setImageResource(R.drawable.theme_circle_dark_yellow);
                red.setImageResource(R.drawable.theme_circle_red);
                purple.setImageResource(R.drawable.theme_circle_purple);
                green.setImageResource(R.drawable.theme_circle_green);
                editor.putInt(COLOR_KEY, R.color.blue);
                editor.putString(COLOR_NAME, "blue");
                setThemeColor(R.color.blue);
                editor.commit();
                break;
            case R.id.dark_yellow:
                darkYellow.setImageResource(R.drawable.selected_dark_yellow);
                pink.setImageResource(R.drawable.theme_circle_dark_pink);
                yellow.setImageResource(R.drawable.theme_circle_yellow);
                purple.setImageResource(R.drawable.theme_circle_purple);
                red.setImageResource(R.drawable.theme_circle_red);
                blue.setImageResource(R.drawable.theme_circle_blue);
                green.setImageResource(R.drawable.theme_circle_green);
                editor.putInt(COLOR_KEY, R.color.dark_yellow);
                setThemeColor(R.color.dark_yellow);
                editor.putString(COLOR_NAME, "orange");
                editor.commit();
                break;
            case R.id.green:
                green.setImageResource(R.drawable.selected_green);
                pink.setImageResource(R.drawable.theme_circle_dark_pink);
                yellow.setImageResource(R.drawable.theme_circle_yellow);
                darkYellow.setImageResource(R.drawable.theme_circle_dark_yellow);
                red.setImageResource(R.drawable.theme_circle_red);
                blue.setImageResource(R.drawable.theme_circle_blue);
                purple.setImageResource(R.drawable.theme_circle_purple);
                editor.putInt(COLOR_KEY, R.color.green);
                editor.putString(COLOR_NAME, "green");
                setThemeColor(R.color.green);
                editor.commit();
                break;

        }
    }
}
