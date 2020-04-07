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

    /**
     * Value representing "database name".
     * <p>
     * {@value #DATABASE_NAME}
     */
    protected final static String DATABASE_NAME = "NewsTable";
    /**
     * Value representing "version number".
     * <p>
     * {@value #VERSION_NUM}
     */
    protected final static int VERSION_NUM = 1;
    /**
     * Value representing "table name".
     * <p>
     * {@value #TABLE_NAME}
     */
    public final static String TABLE_NAME = "BBC_NEWS";
    /**
     * Value representing "column ID".
     * <p>
     * {@value #COL_ID}
     */
    public final static String COL_ID = "_id";
    /**
     * Value representing "column title".
     * <p>
     * {@value #COL_TITLE}
     */
    public final static String COL_TITLE = "title";
    /**
     * Value representing "column description".
     * <p>
     * {@value #COL_DESCRIPTION}
     */
    public final static String COL_DESCRIPTION = "description";
    /**
     * Value representing "column date".
     * <p>
     * {@value #COL_DATE}
     */
    public final static String COL_DATE = "date";
    /**
     * Value representing "column link".
     * <p>
     * {@value #COL_LINK}
     */
    public final static String COL_LINK = "link";
    /**
     * Value representing "column favourite".
     * <p>
     * {@value #COL_FAVOURITE}
     */
    public final static String COL_FAVOURITE = "isFavourite";

    /**
     * Constructor of this DbHandler class.
     *
     * @param ctx  a Context representing context
     */
    public DbHandler(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * onCreate method of this DbHandler class.
     *
     * @param db  SQLiteDatabase representing database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " text," + COL_DESCRIPTION + " text," + COL_DATE + " text,"
                + COL_LINK + " text," + COL_FAVOURITE + " text);");  // add or remove columns
    }

    /**
     * onUpgrade method of this DbHandler class.
     *
     * @param db          SQLiteDatabase representing database
     * @param oldVersion   old version of database
     * @param newVersion   new version of database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create the new table
        onCreate(db);
    }

    /**
     * onDowngrade method of this DbHandler class.
     *
     * @param db          SQLiteDatabase representing database
     * @param oldVersion   old version of database
     * @param newVersion   new version of database
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create the new table
        onCreate(db);
    }
}
