package com.example.finalproject.GuardianNews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This is the database opener class
 *
 *@author Pei Lun Zou
 *@version 1.0
 */

public class GuardianOpener extends SQLiteOpenHelper {

    /**
     * The final static string that stores database name to avoid hardcoding.
     */
    protected final static String DATABASE_NAME = "GuardianDB";
    /**
     * The final static string that stores version number.
     */
    protected final static int VERSION_NUM = 1;
    /**
     * The final static string that stores table name for the database to avoid hardcoding.
     */
    public final static String TABLE_NAME = "GuardianFav";
    /**
     * The final static string that stores the name of the column for news' title in the database to avoid hardcoding.
     */
    public final static String COL_TITLE = "Title";
    /**
     * The final static string that stores the name of the column for news' url in the database to avoid hardcoding.
     */
    public final static String COL_URL = "Url";
    /**
     * The final static string that stores the name of the column for news' section name in the database to avoid hardcoding.
     */
    public final static String COL_SECTIONNAME = "SectionName";
    /**
     * The final static string that stores the id for each row.
     */
    public final static String COL_ID = "_id";

    /**
     * The constructor with one Context parameter.
     */
    public GuardianOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    /**
     * Method to create table.
     */
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
    /**
     * Method to upgrade an existing table. Called when database version on the device is lower than VERSION_NUM.
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    /**
     * Method to downgrade an existing table. Called when database version on the device is higher than VERSION_NUM.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }


}
