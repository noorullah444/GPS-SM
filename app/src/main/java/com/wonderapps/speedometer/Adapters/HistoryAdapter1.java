package com.wonderapps.speedometer.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.wonderapps.speedometer.Activities.HistoryActivity;
import com.wonderapps.speedometer.Activities.ResumeMapActivity;
import com.wonderapps.speedometer.DBHelper.DatabaseHelper;
import com.wonderapps.speedometer.Model.GraphModel;
import com.wonderapps.speedometer.Model.HisModel;
import com.wonderapps.speedometer.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wonderapps.speedometer.Activities.HistoryActivity.ids;
import static com.wonderapps.speedometer.Activities.SettingsActivity.COLOR_KEY;
import static com.wonderapps.speedometer.Activities.SettingsActivity.MyPREFERENCES;

public class HistoryAdapter1 extends RecyclerView.Adapter<HistoryAdapter1.HistoryViewHolder1> {
    private static final String TAG = HistoryAdapter1.class.getSimpleName();
    private ArrayList<HisModel> hisModels;
//    private ArrayList<GraphModel> graphDataList;
    private Context mContext;
    private HistoryActivity historyActivity;

    private SharedPreferences pref;
    private int themeColor;

    public HistoryAdapter1(Context mContext, ArrayList<HisModel> list, ArrayList<GraphModel> graphDataList, HistoryActivity historyActivity) {
        this.mContext = mContext;
        this.hisModels = list;
//        this.graphDataList = graphDataList;
        this.historyActivity = historyActivity;

        Log.d(TAG, "HistoryAdapter1: history list size: " + hisModels.size());
    }

    @NonNull
    @Override
    public HistoryViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater2 = LayoutInflater.from(parent.getContext());
        View v = layoutInflater2.inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder1(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder1 holder, int position) {
        HisModel hisModel = hisModels.get(position);
//        GraphModel graphModel = graphDataList.get(position);
//        Log.d(TAG, "onBindViewHolder: graphModel: " + graphModel.toString());
        String id = ids.get(position);
        Log.d(TAG, "onBindViewHolder: model = " + hisModels.toString());
        Log.d(TAG, "onBindViewHolder: ids = " + id);

        holder.historyName.setText(hisModel.getName());
        holder.historyDate.setText(hisModel.getTimeStamp());
        holder.historyTime.setText(hisModel.getTime());
        holder.historyDistance.setText(hisModel.getDistance());

        Bitmap bitmap = byteToBitmap(hisModel.getImage());
        holder.historyScreenShot.setImageBitmap(bitmap);

        // get theme color
        pref = mContext.getSharedPreferences(MyPREFERENCES, 0);
        themeColor = pref.getInt(COLOR_KEY, 2131034226);
        Log.d(TAG, "onBindViewHolder: theme color: "+ themeColor);

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
                databaseHelper.deleteJson(String.valueOf(ids.get(position)));
                historyActivity.getJsonFromDb(mContext);
                notifyDataSetChanged();
                Toast.makeText(mContext, mContext.getResources().getString(R.string.record_deleted_adapter), Toast.LENGTH_SHORT).show();*/
                deletionDialog(mContext, position);
            }
        });

        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(mContext, ids.get(position), hisModel.getName(), position);
            }
        });

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "Values: " + graphModel.toString(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, ResumeMapActivity.class);

                Log.d(TAG, "onClick: Latitude= "+ hisModel.getLatitude());
                Log.d(TAG, "onClick: Longitude= "+ hisModel.getLongitude());

                intent.putExtra("LAT", hisModel.getLatitude());
                intent.putExtra("LON", hisModel.getLongitude());
                intent.putExtra("name", hisModel.getName());
                intent.putExtra("distance", hisModel.getDistance());
                intent.putExtra("timer", hisModel.getTime());
                intent.putExtra("date", hisModel.getTimeStamp());
                intent.putExtra("ID", ids.get(position));
                intent.putParcelableArrayListExtra("graphCoordinates", hisModel.getCoordinatesList());
                mContext.startActivity(intent);
            }
        });
    }

    private Bitmap setUpImage(byte[] bytes) {
//        Log.d(TAG, "Decoding bytes");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    private Bitmap byteToBitmap(byte[] bytes) {
        Log.e(TAG, "Decoding bytes");
        return (bytes == null || bytes.length == 0) ? null : BitmapFactory
                .decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public int getItemCount() {
        return hisModels.size();
    }

    public class HistoryViewHolder1 extends RecyclerView.ViewHolder {
        public ImageView historyScreenShot, deleteIcon, editIcon;
        public TextView historyName, historyDate, historyTime, historyDistance;
        LinearLayout linearLayout, itemLayout;

        public HistoryViewHolder1(View itemView) {
            super(itemView);
            this.historyScreenShot = itemView.findViewById(R.id.history_screenshot);
            this.deleteIcon = itemView.findViewById(R.id.delete_icon);
            this.editIcon = itemView.findViewById(R.id.edit_icon);
            this.historyName = itemView.findViewById(R.id.history_name);
            this.historyTime = itemView.findViewById(R.id.history_time);
            this.historyDate = itemView.findViewById(R.id.history_date);
            this.historyDistance = itemView.findViewById(R.id.history_distance);
            this.linearLayout = itemView.findViewById(R.id.linear_layout);
            this.itemLayout = itemView.findViewById(R.id.item_layout);

        }
    }

    public synchronized void showDialog(final Context context, String id, String label, int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.update_record_dailog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button save = (Button) dialog.findViewById(R.id.save);
        EditText tripNameEd = (EditText) dialog.findViewById(R.id.trip_name_ed);
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        tripNameEd.setText(label);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tripNameEd.getText().toString().equals("")) {
                    Toast.makeText(context, mContext.getResources().getString(R.string.add_name), Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
                    HisModel hisModel = hisModels.get(position);

                    HisModel model = new HisModel(tripNameEd.getText().toString(),
                            hisModel.getTime(),
                            hisModel.getDistance(),
                            hisModel.getTimeStamp(),
                            hisModel.getImage(),
                            hisModel.getLongitude(),
                            hisModel.getLatitude(),
                            hisModel.getCurrentLat(),
                            hisModel.getCurrentLon(),
                            hisModel.getCoordinatesList());


                    Gson gson = new Gson();
                    String json = gson.toJson(model);
                    Log.d(TAG, "onClick: model json = " + json);

                    databaseHelper.updateJson(json, id);
//                    historyActivity.getData(mContext);
                    historyActivity.getJsonFromDb(mContext);
                    notifyDataSetChanged();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.name_updated), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        Button cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public synchronized void deletionDialog(final Context context, int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.deletion_dailog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // setting dialog title background color
        /*LinearLayout titleLayout = dialog.findViewById(R.id.linearLayout_title);
        titleLayout.setBackgroundColor(themeColor);
        Log.d(TAG, "deletionDialog: theme color: "+ themeColor);*/

        dialog.findViewById(R.id.button_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    //Background work here
                    DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
                    databaseHelper.deleteJson(String.valueOf(ids.get(position)));
                    historyActivity.getJsonFromDb(mContext);

                    handler.post(() -> {
                        //UI Thread work here
                        notifyDataSetChanged();
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.record_deleted_adapter), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                });


            }
        });

        dialog.findViewById(R.id.button_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
