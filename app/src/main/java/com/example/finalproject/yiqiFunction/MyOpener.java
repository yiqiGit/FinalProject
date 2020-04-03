package com.example.finalproject.yiqiFunction;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "ImageDB1";
    protected final static int VERSION_NUM = 2;
    public final static String TABLE_NAME = "Image";
    public final static String COL_LON= "Lon";
    public final static String COL_LAT= "Lat";
    public final static String COL_DATE= "Date";
    public final static String COL_ID = "_id";
    public final static String COL_IMAGEID = "ImageId";
    public final static String COL_RESOURCE = "Resource";
    public final static String COL_VERSION = "Version";
    public final static String COL_IMAGEURL = "Url";
    public final static String COL_PIC = "Picture";

    public MyOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    //This function gets called if no database file exists.
    //Look on your device in the /data/data/package-name/database directory.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LON + " text,"
                + COL_LAT + " text,"
                + COL_DATE + " text,"
                + COL_RESOURCE + " text,"
                + COL_VERSION + " text,"
                + COL_IMAGEID + " text,"
                + COL_IMAGEURL + " text,"
                + COL_PIC  + " text);"); // add or remove columns
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