package com.example.aidsappdetection;
/*
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String databaseName = "Signup.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "Signup.db" , null, 2);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table allusers(email TEXT primary key,password TEXT)");
        db.execSQL("CREATE TABLE report(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, filepath TEXT, datetime TEXT,FOREIGN KEY(email) REFERENCES allusers(email))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists allusers");
        db.execSQL("drop Table if exists report");
        onCreate(db);
    }

    //----------------------------------------------------------------------------------------------
    //alluser Table Methods
    public Boolean insertData(String email,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = db.insert("allusers", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean checkEmail(String email){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from allusers where email =?", new String[]{email});

        if(cursor.getCount()>0){
            return true;
        }
        else{
            return false;
        }
    }
    public Boolean checkEmailPassword(String email,String password){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from allusers where email =? and password =?", new String[]{email,password});

        if(cursor.getCount()>0){
            return true;
        }
        else{
            return false;
        }
    }

    public void deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("allusers", "email=?", new String[]{email});
    }
    //--------------------------------------------------------------------------------------------

    //Report Table Method

    public boolean insertReport(String email, String filepath, String datetime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("filepath", filepath);
        contentValues.put("datetime", datetime);
        long result = db.insert("report", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllReports() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM report", null);
    }
     public void deleteReport(String email, String filepath){
        SQLiteDatabase db= this.getWritableDatabase();
        db.delete("report","email=? and filepath=?",new String[]{email,filepath});
     }

    public boolean isFileExist(String filepath) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from report where filepath =?", new String[]{filepath});

        if(cursor.getCount()>0){
            return true;
        }
        else{
            return false;
        }

    }
    public String imagePaths(int position) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT filepath FROM files LIMIT 1 OFFSET ?", new String[]{String.valueOf(position)});

        String path = null;
        if (cursor.moveToFirst()) {
            path = cursor.getString(1);
            Log.d("FilePath", "Path at position 5: " + path);
        }
        cursor.close();
        return path;
    }
}*/

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "user_database";
    private static final int DATABASE_VERSION = 2;

    // Table for all users (no changes allowed)
    public static final class UserEntry {
        public static final String TABLE_NAME = "allusers";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

    // Table for reports
    public static final class ReportEntry {
        public static final String TABLE_NAME = "report";
        public static final String _ID = "id";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_FILEPATH = "filepath";
        public static final String COLUMN_NAME_DATETIME = "datetime";
        public static final String COLUMN_NAME_FILENAME = "filename";
        public static final String FOREIGN_KEY_USER_EMAIL = "fk_user_email";
    }

    private static final String SQL_CREATE_USERS_TABLE =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry.COLUMN_NAME_EMAIL + " TEXT PRIMARY KEY," +
                    UserEntry.COLUMN_NAME_PASSWORD + " TEXT)";

    private static final String SQL_CREATE_REPORTS_TABLE =
            "CREATE TABLE " + ReportEntry.TABLE_NAME + " (" +
                    ReportEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ReportEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    ReportEntry.COLUMN_NAME_FILEPATH + " TEXT UNIQUE," +
                    ReportEntry.COLUMN_NAME_DATETIME + " TEXT," +
                    ReportEntry.COLUMN_NAME_FILENAME + " TEXT, " +
                    "FOREIGN KEY(" + ReportEntry.COLUMN_NAME_EMAIL + ") REFERENCES " +
                    UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_EMAIL + ") ON DELETE CASCADE)";

    private static final String SQL_DELETE_USERS_TABLE =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;

    private static final String SQL_DELETE_REPORTS_TABLE =
            "DROP TABLE IF EXISTS " + ReportEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_REPORTS_TABLE);
        Log.i(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL(SQL_DELETE_USERS_TABLE);
        db.execSQL(SQL_DELETE_REPORTS_TABLE);
        onCreate(db);
    }

    //----------------------------------------------------------------------------------------------
    // User table methods
    public Boolean insertData(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserEntry.COLUMN_NAME_EMAIL, email);
        contentValues.put(UserEntry.COLUMN_NAME_PASSWORD, password);
        long result = db.insert(UserEntry.TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    public Boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_EMAIL + " =?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_EMAIL + " =? AND " + UserEntry.COLUMN_NAME_PASSWORD + " =?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public void deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(UserEntry.TABLE_NAME, UserEntry.COLUMN_NAME_EMAIL + "=?", new String[]{email});
        db.close();
    }

    //--------------------------------------------------------------------------------------------
    // Report table methods

    public boolean insertReport(String email, String filepath, String datetime, String filename) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReportEntry.COLUMN_NAME_EMAIL, email);
        contentValues.put(ReportEntry.COLUMN_NAME_FILEPATH, filepath);
        contentValues.put(ReportEntry.COLUMN_NAME_DATETIME, datetime);
        contentValues.put(ReportEntry.COLUMN_NAME_FILENAME, filename);
        long result = db.insert(ReportEntry.TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    public Cursor getAllReports() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + ReportEntry.TABLE_NAME, null);
    }

    public Cursor getAllReportsByUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                ReportEntry._ID,
                ReportEntry.COLUMN_NAME_FILENAME,
                ReportEntry.COLUMN_NAME_DATETIME,
                ReportEntry.COLUMN_NAME_FILEPATH
        };
        String selection = ReportEntry.COLUMN_NAME_EMAIL + " = ?";
        String[] selectionArgs = {email};
        String sortOrder = ReportEntry.COLUMN_NAME_DATETIME + " DESC";
        return db.query(ReportEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public boolean deleteReport(String email, String filename) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(ReportEntry.TABLE_NAME, ReportEntry.COLUMN_NAME_EMAIL + "=? AND " + ReportEntry.COLUMN_NAME_FILENAME + "=?", new String[]{email, filename});
        db.close();
        return rowsDeleted > 0;
    }


    public boolean isFileExist(String filepath) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ReportEntry.TABLE_NAME + " WHERE " + ReportEntry.COLUMN_NAME_FILEPATH + " =?", new String[]{filepath});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public String getImagePath(long reportId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {ReportEntry.COLUMN_NAME_FILEPATH};
        String selection = ReportEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(reportId)};
        Cursor cursor = db.query(ReportEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        String path = null;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(ReportEntry.COLUMN_NAME_FILEPATH);
            if (columnIndex != -1) {
                path = cursor.getString(columnIndex);
            }
        }
        cursor.close();
        db.close();
        return path;
    }
}