package com.wonderapps.speedometer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.wonderapps.speedometer.Fragments.DigitalFragment;
import com.wonderapps.speedometer.Fragments.GraphFragment;
import com.wonderapps.speedometer.LocationService.LocationService;
import com.wonderapps.speedometer.Model.GraphModel;


import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.Minutes;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.cHours;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.cMinutes;
import static com.wonderapps.speedometer.Fragments.DigitalFragment.cSeconds;
import static com.wonderapps.speedometer.Fragments.GraphFragment.Minutesg;
import static com.wonderapps.speedometer.Fragments.GraphFragment.line_chart;


public class GraphTask extends AsyncTask<Double, Void, Void> {
    private static final String TAG = GraphTask.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private final Context context;

    public GraphTask(Context context) {
        this.context = context;
    }

    GraphFragment graphFragment = new GraphFragment();

    //    public ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    public static ArrayList<Entry> values = new ArrayList<>();

    /*@SuppressLint("UseCompatLoadingForDrawables")
    public void kaka(Activity activity){
        Handler handler = new Handler();
        handler.post(() -> new Thread(() -> {
            // do background stuff here
            //Background work here
            Collections.sort(graphFragment.dataValues1(), new EntryXComparator());
            LineDataSet lineDataSet1 = new LineDataSet(graphFragment.dataValues1(), "Data Set 1");
            values = graphFragment.dataValues1();

            Log.d(TAG, "graphTask: values: size= "+ values.size()+ "---" + values.toString());

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();

            lineDataSet1.setLineWidth(2f);
            lineDataSet1.setColor(Color.GREEN);
            lineDataSet1.setDrawCircles(true);
            lineDataSet1.setCircleColor(Color.RED);
            lineDataSet1.setCircleRadius(3f);
            lineDataSet1.setValueTextSize(5f);
            lineDataSet1.enableDashedLine(2f, 2f, 2f);
            lineDataSet1.setLabel("");
            lineDataSet1.setDrawFilled(true);
            lineDataSet1.setFillDrawable(context.getResources().getDrawable(R.drawable.gradient));
            dataSets.add(lineDataSet1);

            LineData data = new LineData(dataSets);
            data.setValueTextColor(Color.WHITE);
            data.notifyDataChanged();
            line_chart.setData(data);

            GraphFragment.left_Yaxis.setAxisMaximum(LocationService.maxvalue + 10f);
            line_chart.getDescription().setText("Time (S)");

            if (cMinutes == 1 || cMinutes > 1) {
                line_chart.getDescription().setText("Time (M)");
            } else if (cHours == 1 || cHours > 1) {
                line_chart.getDescription().setText("Time (H)");
            } else {
                line_chart.getDescription().setText("Time (S)");
            }
            line_chart.invalidate();

            activity.runOnUiThread(()->{
                // OnPostExecute stuff here
                Log.d(TAG, "graphTask: on pre execute method called");
                Log.d(TAG, "graphTask: dataValues: " + graphFragment.dataValues1().toString());
            });
        }).start());
    }*/

    /*@SuppressLint("UseCompatLoadingForDrawables")
    public void graphTask() {
        GraphFragment graphFragment = new GraphFragment();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            Collections.sort(graphFragment.dataValues1(), new EntryXComparator());
            LineDataSet lineDataSet1 = new LineDataSet(graphFragment.dataValues1(), "Data Set 1");
            values = graphFragment.dataValues1();

            Log.d(TAG, "graphTask: values: size= "+ values.size()+ "---" + values.toString());

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();

            lineDataSet1.setLineWidth(2f);
            lineDataSet1.setColor(Color.GREEN);
            lineDataSet1.setDrawCircles(true);
            lineDataSet1.setCircleColor(Color.RED);
            lineDataSet1.setCircleRadius(3f);
            lineDataSet1.setValueTextSize(5f);
            lineDataSet1.enableDashedLine(2f, 2f, 2f);
            lineDataSet1.setLabel("");
            lineDataSet1.setDrawFilled(true);
            lineDataSet1.setFillDrawable(context.getResources().getDrawable(R.drawable.gradient));
            dataSets.add(lineDataSet1);

            LineData data = new LineData(dataSets);
            data.setValueTextColor(Color.WHITE);
            data.notifyDataChanged();
            line_chart.setData(data);

            GraphFragment.left_Yaxis.setAxisMaximum(LocationService.maxvalue + 10f);
            line_chart.getDescription().setText("Time (S)");

            if (cMinutes == 1 || cMinutes > 1) {
                line_chart.getDescription().setText("Time (M)");
            } else if (cHours == 1 || cHours > 1) {
                line_chart.getDescription().setText("Time (H)");
            } else {
                line_chart.getDescription().setText("Time (S)");
            }
            line_chart.invalidate();

            handler.post(() -> {
                //UI Thread work here
                Log.d(TAG, "graphTask: on pre execute method called");
                Log.d(TAG, "graphTask: dataValues: " + graphFragment.dataValues1().toString());
            });
        });
    }*/

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public Void doInBackground(Double... dbl) {
        Log.d("doInBackground", "do in Background called");

        //graphFragment.setData();
        Collections.sort(graphFragment.dataValues1(), new EntryXComparator());
        LineDataSet lineDataSet1 = new LineDataSet(graphFragment.dataValues1(), "Data Set 1");
        values = graphFragment.dataValues1();

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        lineDataSet1.setLineWidth(2f);
        lineDataSet1.setColor(Color.GREEN);
        lineDataSet1.setDrawCircles(true);
        //lineDataSet1.setDrawCircleHole(true);
        lineDataSet1.setCircleColor(Color.RED);
        //lineDataSet1.setCircleHoleColor(Color.GREEN);
        lineDataSet1.setCircleRadius(3f);
        //lineDataSet1.setCircleHoleRadius(25f);
        lineDataSet1.setValueTextSize(5f);
        lineDataSet1.enableDashedLine(2f, 2f, 2f);
        lineDataSet1.setLabel("");
        lineDataSet1.setDrawFilled(true);
        //Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
        //R.drawable.gradient, null);
        lineDataSet1.setFillDrawable(context.getResources().getDrawable(R.drawable.gradient));
        dataSets.add(lineDataSet1);
//        Log.d(TAG, "doInBackground: data set: " + dataSets.toString());

        LineData data = new LineData(dataSets);
        data.setValueTextColor(Color.WHITE);
        data.notifyDataChanged();
        line_chart.setData(data);

//        line_chart.setVisibleXRangeMaximum(60);
        GraphFragment.left_Yaxis.setAxisMaximum(LocationService.maxvalue + 10f);
        line_chart.getDescription().setText("Time (S)");

        if (cMinutes == 1 || cMinutes > 1) {
            line_chart.getDescription().setText("Time (M)");
//            line_chart.moveViewToX(Minutesg);
//            xAxis.setAxisMaximum(mysecondg);
        } else if (cHours == 1 || cHours > 1) {
            line_chart.getDescription().setText("Time (H)");
//            line_chart.moveViewToX(DigitalFragment.cHours);
//            xAxis.setAxisMaximum(mysecondg);
        } else {
            line_chart.getDescription().setText("Time (S)");
//            line_chart.moveViewToX(mysecondg);
//            xAxis.setAxisMaximum(mysecondg);
        }

        line_chart.invalidate();


        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        //locationService = new LocationService();
        //locationService.updateGraph();

        Log.d("onProgress update", "on progress update called");

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.d("onPreExecute", " on pre execute method called");
        //lineChart.moveViewToX(data.getEntryCount());
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("onPostExecute", " on pre execute method called");
        Log.d(TAG, "onPostExecute: dataValues: " + graphFragment.dataValues1().toString());
    }
}


