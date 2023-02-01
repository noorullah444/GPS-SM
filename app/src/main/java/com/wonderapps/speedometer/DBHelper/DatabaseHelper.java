package com.wonderapps.speedometer.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "history.db";
    public static final String PATH_TABLE = "path_table";
    public static final String PATH_DETAILS_TABLE = "path_details_table";
    public static final String PATH_STOPS_TABLE = "stops_table";
    public static final String HISTORY_TABLE = "history_table";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "NAME";
    public static final String COL_2 = "PATH";
    public static final String COL_3 = "TIMESTAMP";

    DatabaseHelper databaseHelper;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
//        databaseHelper = new DatabaseHelper(context);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + PATH_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,TIME TEXT,DISTANCE TEXT,IMAGE BLOB,LAT TEXT,LON TEXT,FINALLAT TEXT,FINALLON TEXT" +
                ",TIMESTAMP TEXT)");
        sqLiteDatabase.execSQL("create table " + PATH_DETAILS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,PATHID TEXT,LAT TEXT,LON TEXT,FINALLAT TEXT,FINALLON TEXT" +
                ")");
        sqLiteDatabase.execSQL("create table " + PATH_STOPS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,PATHID TEXT,LAT TEXT,LON TEXT,TITLE TEXT" +
                ")");
        sqLiteDatabase.execSQL("create table " + HISTORY_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,JSONDATA TEXT, GRAPHVALUESJSON TEXT, TIMESTAMP TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PATH_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PATH_DETAILS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PATH_STOPS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);
        onCreate(sqLiteDatabase);

    }

    public void deletePath(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        /*db.delete(TABLE_NAME,
                COL_1 + "= ?" + new String[]{name} + " and " + COL_2 + "= ?" + new String[]{time} + " and " + COL_3 + "= ?" + new String[]{distance},
                null);*/

        db.delete(PATH_TABLE, "ID" + "=" + id, null);
        /*if you just have key_name to select a row,you can ignore passing rowid(here-row) and use:

         */
    }

    public void updatePath(String name, String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, name);
        db.update(PATH_TABLE, contentValues, "ID = " + id, null);
        /*String updateString = "UPDATE TABLE_NAME SET DESTINATION = "+name+" WHERE TIMETAKEN = "+time;
        db.execSQL(updateString);*/
    }

    public int insertJson(String json, String graphValuesJson, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("JSONDATA", json);
        contentValues.put("GRAPHVALUESJSON", graphValuesJson);
        contentValues.put("TIMESTAMP", timestamp);
        long result = db.insert(HISTORY_TABLE, null, contentValues);
        return (int) result;
    }

    public Cursor getJson() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + HISTORY_TABLE, null);
    }

    public void updateJson(String json, String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("JSONDATA", json);
        db.update(HISTORY_TABLE, contentValues, "ID = " + id, null);
    }

    public int deleteJson(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(HISTORY_TABLE, "ID = ?", new String[]{id});
    }

    public int insertPathData(String name, String time, String distance, String timestamp, byte[] image, double lat, double lon, double finalLat, double finalLon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("TIME", time);
        contentValues.put("TIMESTAMP", timestamp);
        contentValues.put("DISTANCE", distance);
        contentValues.put("IMAGE", image);
//        contentValues.put("IMAGE", image);
        contentValues.put("LAT", lat);
        contentValues.put("LON", lon);
        contentValues.put("FINALLAT", finalLat);
        contentValues.put("FINALLON", finalLon);
        long result = db.insert(PATH_TABLE, null, contentValues);
        //  Log.d("DB Result",String.valueOf(result));
        return (int) result;
    }

    public int insertPathDetails(String initLon, String initLat, String finalLon, String finalLat, int pathId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PATHID", pathId);
        contentValues.put("LON", initLon);
        contentValues.put("LAT", initLat);
        contentValues.put("FINALLON", finalLon);
        contentValues.put("FINALLAT", finalLat);
        long result = db.insert(PATH_DETAILS_TABLE, null, contentValues);
        Log.d("DB Result", String.valueOf(result));
        return (int) result;
    }

    public int insertPathStops(String lon, String lat, int pathId, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PATHID", pathId);
        contentValues.put("LON", lon);
        contentValues.put("LAT", lat);
        contentValues.put("TITLE", title);
        long result = db.insert(PATH_STOPS_TABLE, null, contentValues);
        Log.d("DB Result", String.valueOf(result));
        return (int) result;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + PATH_TABLE, null);
        return res;
    }


    public Cursor getPathData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
//         Cursor res = db.rawQuery("select * from "+ PATH_DETAILS_TABLE +" WHERE PATHID = "+id,null);

        // Cursor res = db.rawQuery("select * from "+ PATH_DETAILS_TABLE,null);
//        Cursor res1 = db.rawQuery("select * from " + PATH_TABLE + " WHERE ID = " + id, null);
        Cursor res1 = db.rawQuery("select * from "+PATH_DETAILS_TABLE +" where PATHID =?",new String[] {id});
        return res1;
    }

    public Cursor getStopsData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Cursor res = db.rawQuery("select * from "+ PATH_DETAILS_TABLE +" WHERE PATHID = "+id,null);

        // Cursor res = db.rawQuery("select * from "+ PATH_DETAILS_TABLE,null);
        Cursor res = db.rawQuery("select * from " + PATH_STOPS_TABLE + " where PATHID =?", new String[]{id});
        return res;
    }
//    public void updatePathName(String name,String path,int id)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_1,name);
//        contentValues.put(COL_2,path);
//        db.update(PATH_TABLE,contentValues,"ID = ?", new String[]{String.valueOf(id)});
//
//    }

//    public Cursor getSingleData(String name){
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor res = db.rawQuery("select * from "+PATH_TABLE+"where NAME = ?",new String[] {name});
//        return res;
//
//    }

    public int deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PATH_TABLE, "ID = ?", new String[]{id});
    }
}
