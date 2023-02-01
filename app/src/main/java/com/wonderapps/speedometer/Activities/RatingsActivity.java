package com.wonderapps.speedometer.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wonderapps.speedometer.R;

public class RatingsActivity extends AppCompatActivity {
    int[] emojis = new int[]{R.drawable.terrible, R.drawable.bad, R.drawable.ok, R.drawable.good, R.drawable.great};
    String[] emojiText;
    float rate = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);

        emojiText = new String[]{"Terrible",
                "Bad",
                "Ok",
                "Good",
                "Great"};

        final ImageView emoji = (ImageView) findViewById(R.id.smileyImage);
        final TextView smileyText = (TextView) findViewById(R.id.smileyText);
        final RatingBar simpleRatingBar = (RatingBar) findViewById(R.id.rating);
        simpleRatingBar.setProgress(5);
        simpleRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                rate = rating;
                int pos = (int) rating - 1;
                if (pos != -1) {
                    emoji.setImageResource(emojis[pos]);

                    smileyText.setText(emojiText[pos]);
                } else {
                    ratingBar.setRating(1);
                    emoji.setImageResource(emojis[0]);

                    smileyText.setText(emojiText[0]);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendFeedback(View view) {
        if (rate != 0) {
            if (rate < 4) {
                String to = "wonder.apps.studio@gmail.com";
                String subject = "Speedometer App Rating: " + rate;
                String message = "Hi developer,\n" +
                        "I just rate your App Speedometer: " + rate;
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, message);
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://play.google.com/store/apps/details?id=com.wonderapps.speedometergps"));
                try {
                    startActivity(new Intent(intent)
                            .setPackage("com.wonderapps.speedometer"));
                } catch (android.content.ActivityNotFoundException exception) {
                    startActivity(intent);
                }
            }
        } else {
            Toast.makeText(this, "Nothing selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void exitActivity(View view) {
        Intent intent = new Intent(RatingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RatingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}