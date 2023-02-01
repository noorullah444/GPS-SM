package com.wonderapps.speedometer.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.wonderapps.speedometer.R;

public class ExitActivity extends AppCompatActivity {
    private static final String TAG = ExitActivity.class.getSimpleName();
    private ReviewManager reviewManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        initInAppReview();
        setUpClickListeners();
    }

    private void initInAppReview() {
        Log.d(TAG, "initInAppReview: called!");
        reviewManager = ReviewManagerFactory.create(this);
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
                Task<Void> flow = reviewManager.launchReviewFlow((Activity) this, reviewInfo);
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

    private void setUpClickListeners() {
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ExitActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        findViewById(R.id.btn_rate_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateApp();
            }
        });

        findViewById(R.id.btn_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        findViewById(R.id.findlostphone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.wonderapps.org.findphone"));
                startActivity(intent);
            }
        });

        findViewById(R.id.textonpic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.wonderApps.TextOnPicture"));
                startActivity(intent);
            }
        });

        findViewById(R.id.photoeditor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.wonderapps.photo.mixer.editor"));
                startActivity(intent);
            }
        });

        findViewById(R.id.greetingcard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.greetings.cards"));
                startActivity(intent);
            }
        });

        findViewById(R.id.animal_tones).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.skytek.animals.ringtone"));
                startActivity(intent);
            }
        });

        findViewById(R.id.photo_hider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.wonderapps.imagevault.videohider"));
                startActivity(intent);
            }
        });
    }

    private void rateApp() {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.wonderapps.speedometergps");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.wonderapps.speedometergps")));
        }
    }

    public static boolean checkInternet(Context context) {
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ExitActivity.this, MainActivity.class));
        finish();
    }
}
