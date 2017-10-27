package com.corral.firebase.shailshah.silentme.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shailshah on 10/26/17.
 */

public class DbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "silentme.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SILENT_TABLE = "CREATE TABLE " + SilentContract.SilentEntry.TABLE_NAME + " (" +
                SilentContract.SilentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SilentContract.SilentEntry.COLUMN_PLACE_ID + " TEXT NOT NULL, " +
                "UNIQUE (" + SilentContract.SilentEntry.COLUMN_PLACE_ID + ") ON CONFLICT REPLACE" +
                "); ";

        db.execSQL(SQL_CREATE_SILENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + SilentContract.SilentEntry.TABLE_NAME);
        onCreate(db);

    }
}
