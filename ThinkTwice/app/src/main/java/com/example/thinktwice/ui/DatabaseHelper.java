package com.example.thinktwice.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.SurfaceControl;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "ThinkTwice.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Transactions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_FROM = "from_category";
    public static final String COLUMN_TO = "to_category";
    public static final String COLUMN_DETAILS = "details";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_PLANNED = "planned";




    public static final String CATEGORY_TABLE_NAME = "Categories";
    public static final String CATEGORY_COLUMN_ID = "_id";
    public static final String CATEGORY_COLUMN_TITLE = "title";
    static final String CATEGORY_COLUMN_ISGENERAL = "IsGeneral";
    public static final String CATEGORY_COLUMN_PERCENTAGEAMOUNT = "PercentageAmount";
    public static final String CATEGORY_COLUMN_TYPE = "type";


    private static final String CREATE_TABLE1 =
            "CREATE TABLE " + TABLE_NAME +" ("+ COLUMN_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TITLE +
                    " TEXT, " +
                    COLUMN_DETAILS + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_AMOUNT + " INTEGER, " +
                    COLUMN_PLANNED + " INTEGER, " +
                    COLUMN_FROM + " INTEGER, " +
                    COLUMN_TO + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_FROM + ") REFERENCES " + CATEGORY_TABLE_NAME + "(" + CATEGORY_COLUMN_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_TO + ") REFERENCES " + CATEGORY_TABLE_NAME + "(" + CATEGORY_COLUMN_ID + "));";

    private static final String CREATE_TABLE2 =
            "CREATE TABLE " + CATEGORY_TABLE_NAME +" ("+ CATEGORY_COLUMN_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " + CATEGORY_COLUMN_TITLE +
                    " TEXT, " +
                    CATEGORY_COLUMN_ISGENERAL + " INTEGER, " +
                    CATEGORY_COLUMN_PERCENTAGEAMOUNT + " DECIMAL(10, 5), " +
                    CATEGORY_COLUMN_TYPE + " TEXT);";



    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE2);
        db.execSQL(CREATE_TABLE1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the second table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);

        onCreate(db);
    }

    public void addTransaction (String title, String details, String date, int amount, int planned, int to_category, int from_category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DETAILS, details);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_PLANNED, planned);
        cv.put(COLUMN_TO, to_category);
        cv.put(COLUMN_FROM, from_category);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Added successfuly!", Toast.LENGTH_SHORT).show();
        }
    }


    public void addCategory (String title, Integer isGeneral, Double PercentageAmount, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(CATEGORY_COLUMN_TITLE, title);
        cv.put(CATEGORY_COLUMN_ISGENERAL, isGeneral);
        cv.put(CATEGORY_COLUMN_PERCENTAGEAMOUNT, PercentageAmount);
        cv.put(CATEGORY_COLUMN_TYPE, type);

        long result = db.insert(CATEGORY_TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Added successfuly!", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor getAllTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public String getCategoryType(String categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String type = null;
        Cursor cursor = db.rawQuery("SELECT " + CATEGORY_COLUMN_TYPE + " FROM " + CATEGORY_TABLE_NAME + " WHERE " + CATEGORY_COLUMN_ID + " = ?", new String[]{categoryId});
        if (cursor.moveToFirst()) {
            type = cursor.getString(0);
        }
        cursor.close();
        return type;
    }

    public String getCategoryName(String categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = null;
        Cursor cursor = db.rawQuery("SELECT " + CATEGORY_COLUMN_TITLE + " FROM " + CATEGORY_TABLE_NAME + " WHERE " + CATEGORY_COLUMN_ID + " = ?", new String[]{categoryId});
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }



}
