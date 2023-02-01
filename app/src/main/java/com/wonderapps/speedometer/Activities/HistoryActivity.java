package com.wonderapps.speedometer.Activities;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.wonderapps.speedometer.Adapters.HistoryAdapter1;
import com.wonderapps.speedometer.DBHelper.DatabaseHelper;
import com.wonderapps.speedometer.Model.HisModel;
import com.wonderapps.speedometer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = HistoryActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private HistoryAdapter1 historyAdapter1;
    public static ArrayList<String> ids;
    public static List<byte[]> bytesar;
    DatabaseHelper databaseHelper;
    HistoryActivity historyActivity;

    private ArrayList<HisModel> historyModelsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // for screen on
        View view = getLayoutInflater().inflate(R.layout.activity_history, null);
        view.setKeepScreenOn(true);
        setContentView(view);

        historyActivity = this;
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ids = new ArrayList<>();
        historyModelsList = new ArrayList<>();

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getJsonFromDb(this);

        historyAdapter1 = new HistoryAdapter1(this, historyModelsList, null, historyActivity);
        mRecyclerView.setAdapter(historyAdapter1);
    }

    public void getJsonFromDb(Context context) {
        clearAllLists();
        databaseHelper = new DatabaseHelper(context);
        Cursor data = databaseHelper.getJson();

        if (data != null) {
            if (data.getCount() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getResources().getString(R.string.no_record_found), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (data.moveToFirst()) {
                    for (int i = 0; i < data.getCount(); i++) {
                        try {
                            String json = data.getString(data.getColumnIndexOrThrow("JSONDATA"));
                            Log.d(TAG, "getJsonFromDb: json string= " + json);

                            Gson gson = new Gson(); // Or use new GsonBuilder().create();
                            HisModel model = gson.fromJson(json, HisModel.class);
                            historyModelsList.add(model);

                            ids.add(data.getString(data.getColumnIndexOrThrow("ID")));

                            data.moveToNext();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    data.close();
                    Collections.reverse(historyModelsList);
                    Collections.reverse(ids);
                }
            }
        }
    }

    public void clearAllLists() {
        ids.clear();
        historyModelsList.clear();
    }

}