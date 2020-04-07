package com.example.finalproject.BBCNews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * This class is the database handler class.
 *
 *@author Xiaoting Kong
 *@version 1.0
 */
public class DbHandler extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "NewsTable";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "BBC_NEWS";
    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "title";
    public final static String COL_DESCRIPTION = "description";
    public final static String COL_DATE = "date";
    public final static String COL_LINK = "link";
    public final static String COL_FAVOURITE = "isFavourite";

    public DbHandler(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " text," + COL_DESCRIPTION + " text," + COL_DATE + " text,"
                + COL_LINK + " text," + COL_FAVOURITE + " text);");  // add or remove columns
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create the new table
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create the new table
        onCreate(db);
    }
}
