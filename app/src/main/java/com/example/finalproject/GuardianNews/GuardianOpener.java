package com.example.finalproject.GuardianNews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GuardianOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "GuardianDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "GuardianFav";
    public final static String COL_TITLE = "Title";
    public final static String COL_URL = "Url";
    public final static String COL_SECTIONNAME = "SectionName";
    public final static String COL_ID = "_id";


    public GuardianOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " text,"
                + COL_URL + " text,"
                + COL_SECTIONNAME + " text);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }


}
