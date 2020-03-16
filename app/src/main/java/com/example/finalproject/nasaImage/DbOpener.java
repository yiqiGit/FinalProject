package com.example.finalproject.nasaImage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpener extends SQLiteOpenHelper{


    protected final static String DATABASE_NAME = "ImagesDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "MESSAGES";
    public final static String COL_DATE = "date";
    public final static String COL_DESCRIPTION = "description";
    public final static String COL_TITLE = "title";
    public final static String COL_FILE_NAME = "fileName";
    public final static String COL_URL = "url";
    public final static String COL_HD_URL = "hdUrl";
    public final static String COL_ID = "_id";

    public DbOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    //This function gets called if no database file exists.
    //Look on your device in the /data/data/package-name/database directory.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DATE + " text,"
                + COL_DESCRIPTION + " text,"
                + COL_TITLE + " text,"
                + COL_FILE_NAME + " text,"
                + COL_URL + " text,"
                + COL_HD_URL + " text);");   // add or remove columns
    }


    //this function gets called if the database version on your device is lower than VERSION_NUM
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }
}

