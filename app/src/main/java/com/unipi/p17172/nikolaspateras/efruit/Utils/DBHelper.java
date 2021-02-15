package com.unipi.p17172.nikolaspateras.efruit.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SMS13033DB.db";  /* Database name */
    public static final String SMS_TABLE_NAME = "sms_types"; /* table name */
    public static final String SMS_COLUMN_ID = "sms_id";
    public static final String SMS_NUMBER = "sms_number";
    public static final String SMS_REASON = "sms_reason";
    private static final int DATABASE_VERSION = 4; /* For debugging and table updating purposes */
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + SMS_TABLE_NAME +
                        "("+SMS_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SMS_NUMBER+" INTEGER UNIQUE, " +
                        SMS_REASON+" TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SMS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertRow(int smsNumber, String smsReason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SMS_NUMBER, smsNumber);
        contentValues.put(SMS_REASON, smsReason);
        db.insert(SMS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor readLastWeekData() {
        String query = "SELECT timestamp, " +
                "speed_limit, current_speed, longitude, latitude " +
                "FROM " + SMS_TABLE_NAME + " " +
                "WHERE datetime(timestamp, 'unixepoch') BETWEEN datetime('now', '-6 days') AND datetime('now', 'localtime') " +
                "ORDER BY timestamp DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor  = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, SMS_TABLE_NAME);
    }

    public void deleteAllRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SMS_TABLE_NAME, null, null);
    }

    public Cursor readAllData() {
        String query = "SELECT * " +
                       "FROM " + SMS_TABLE_NAME + " ORDER BY sms_number ASC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor  = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readSmsReason(int smsNumber) {
        String query = "SELECT sms_reason " +
                       "FROM " + SMS_TABLE_NAME + " WHERE sms_number=" + smsNumber;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor  = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public boolean deleteRow(int smsNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SMS_TABLE_NAME, SMS_NUMBER + "=" + smsNumber, null) > 0;
    }
}

