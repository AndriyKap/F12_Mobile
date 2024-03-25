package com.example.thinktwice.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "ThinkTwice.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Transactions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    static final String COLUMN_FROM = "from_category";
    public static final String COLUMN_TO = "to_category";
    public static final String COLUMN_DETAILS = "details";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_PLANNED = "planned";
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +" ("+ COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TITLE +
                " TEXT, " +
                COLUMN_DETAILS + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_AMOUNT + " INTEGER, " +
                COLUMN_PLANNED + " INTEGER, " +
                COLUMN_TO + " TEXT, " +
                COLUMN_FROM + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTransaction (String title, String details, String date, int amount, int planned, String to, String from) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DETAILS, details);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_PLANNED, planned);
        cv.put(COLUMN_TO, to);
        cv.put(COLUMN_FROM, from);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Added successfuly!", Toast.LENGTH_SHORT).show();
        }
    }
}
