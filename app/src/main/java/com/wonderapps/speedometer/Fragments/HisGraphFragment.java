package com.wonderapps.speedometer.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.wonderapps.speedometer.Activities.ResumeMapActivity;
import com.wonderapps.speedometer.LocationService.LocationService;
import com.wonderapps.speedometer.R;

import java.util.ArrayList;

import static com.wonderapps.speedometer.Activities.SettingsActivity.KNOTS;
import static com.wonderapps.speedometer.Activities.SettingsActivity.KPH;
import static com.wonderapps.speedometer.Activities.SettingsActivity.LIMItSPEED;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MPH;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MPS;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MyPREFERENCES;

public class HisGraphFragment extends Fragment implements OnChartValueSelectedListener {
    private static final String TAG = HisGraphFragment.class.getSimpleName();
    TextView distanceText;
    TextView timerText;
    TextView dateText;
    String distanceExtraText, timerExtraText, dateExtraText, id;

    private TextView tvGraphValue;

    public static LineChart line_chart;
    public static YAxis left_Yaxis;
    public static XAxis xAxis;
    public static String limitSpeed;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_his_graph, container, false);
        view.setKeepScreenOn(true);
        initViews(view);
        initLineChart(view);

        // setting values on the graph
        setData();

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

//        Log.d(TAG, "onCreateView: graphValuesList: "+ HistoryActivity.graphValuesList.toString());

        return view;
    }

    private void setData() {
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < ResumeMapActivity.coordinatesExtra.size(); i++) {
            values.add(new Entry(ResumeMapActivity.coordinatesExtra.get(i).getX()/* / 60.0f*/,
                    ResumeMapActivity.coordinatesExtra.get(i).getY()));
        }

        LineDataSet set1;

        if (line_chart.getData() != null &&
                line_chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) line_chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            line_chart.getData().notifyDataChanged();
            line_chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setLineWidth(2f);
            set1.setColor(Color.GREEN);
            set1.setDrawCircles(true);
            //lineDataSet1.setDrawCircleHole(true);
            set1.setCircleColor(Color.RED);
            //lineDataSet1.setCircleHoleColor(Color.GREEN);
            set1.setCircleRadius(3f);
            //lineDataSet1.setCircleHoleRadius(25f);
            set1.setValueTextSize(5f);
            set1.enableDashedLine(2f, 2f, 2f);
            set1.setLabel("");
            set1.setDrawFilled(true);

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.gradient);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set value text color
            data.setValueTextColor(Color.WHITE);

            // set data
            line_chart.setData(data);

//            line_chart.setVisibleXRangeMaximum(60);
            if (GraphFragment.left_Yaxis != null) {
                GraphFragment.left_Yaxis.setAxisMaximum(LocationService.maxvalue + 10f);
            }

            line_chart.getDescription().setText("Time (S)");

            /*if (cMinutes == 1 || cMinutes > 1) {
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
            }*/
        }
    }

    private void initLineChart(View view) {
        line_chart = view.findViewById(R.id.line_chart_his);
        line_chart.setOnChartValueSelectedListener(this);
        //setting upper limit
        LimitLine upper_limit = null;
        try {
            pref = getActivity().getSharedPreferences(MyPREFERENCES, 0);
            limitSpeed = pref.getString(LIMItSPEED, "100");
            upper_limit = new LimitLine((float) Double.parseDouble(limitSpeed), "Speed Limit");
            upper_limit.setLineWidth(2f);
            upper_limit.enableDashedLine(10f, 10f, 0f);
            upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            upper_limit.setTextSize(10f);
            upper_limit.setTextColor(Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //setting lower limit
        LimitLine lower_limit = new LimitLine(0f, "lower limit");
        lower_limit.setLineWidth(2f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);

//        line_chart.getAxisLeft().setAxisMinimum(20);
        line_chart.setBackgroundColor(getResources().getColor(R.color.background_color));
        line_chart.setNoDataText("No data available");
        line_chart.setNoDataTextColor(Color.WHITE);
        line_chart.setDrawGridBackground(true);
        line_chart.setDrawBorders(false);
        line_chart.setBorderColor(Color.BLACK);
        line_chart.setBorderWidth(2f);
        line_chart.setGridBackgroundColor(getResources().getColor(R.color.background_color));
        // enable scaling and dragging
        line_chart.setDragEnabled(true);
        line_chart.setScaleEnabled(true);
        line_chart.setDrawGridBackground(true);
        line_chart.setHighlightPerDragEnabled(true);

//        line_chart.setScaleMinima(10f, 0f);
//        line_chart.zoom(0f, 0f,0f, 0f);

        //new
        line_chart.setTouchEnabled(true);
        line_chart.setPinchZoom(true);

//        line_chart.getDescription().setPo("Time");
//        line_chart.getDescription().setPosition(500,20);
        line_chart.getDescription().setTextColor(Color.WHITE);
        line_chart.notifyDataSetChanged();

        YAxis rightAxis = line_chart.getAxisRight();
        rightAxis.setEnabled(true);
        left_Yaxis = line_chart.getAxisLeft();
        left_Yaxis.removeAllLimitLines();
        left_Yaxis.addLimitLine(upper_limit);
        left_Yaxis.setAxisMinimum(0f);
        left_Yaxis.setTextColor(Color.WHITE);
        xAxis = line_chart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);

        line_chart.getAxisRight().setEnabled(false);       // for hiding values to the right of graph
    }

    private void initViews(View view) {
        distanceText = view.findViewById(R.id.tv_distance);
        timerText = view.findViewById(R.id.timer_text);
        dateText = view.findViewById(R.id.date_text);
        tvGraphValue = view.findViewById(R.id.tv_graph_value);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        float x = e.getX();
        float y = e.getY();

        // get selected speed unit from prefs
        boolean kmPerHr = pref.getBoolean(KPH, true);
        boolean mph = pref.getBoolean(MPH, false);
        boolean knots = pref.getBoolean(KNOTS, false);
        boolean mps = pref.getBoolean(MPS, false);

        StringBuilder builder = new StringBuilder();
        String unit;

        builder.append("Speed: ");
        builder.append(roundUp(y));
        builder.append(" km/h");
        /*if (kmPerHr) {
            builder.append(" km/h");
        } else if (mph) {
            builder.append(" mph");
        } else if (knots) {
            builder.append(" knots");
        } else {
            builder.append(" m/s");
        }*/
        builder.append(" Time: ");
        builder.append(roundUp(x));
        builder.append(" s");

        unit = new String(builder);

        tvGraphValue.setText(unit);
//        tvGraphValue.setText("Speed: " + roundUp(y) +" " + R.string.km_per_hr + " Time: " + roundUp(x));
        Log.d(TAG, "onValueSelected: entry: "+ e.toString());
    }

    public static float roundUp(float value) {
        return (float) (Math.round(value * Math.pow(10, 1)) / Math.pow(10, 1));
    }

    @Override
    public void onNothingSelected() {

    }
}
