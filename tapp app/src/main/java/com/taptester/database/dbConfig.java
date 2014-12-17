package com.taptester.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Const on 04.08.2014.
 */
public class dbConfig extends SQLiteOpenHelper implements BaseColumns {

    private static final String DATABASE_NAME = "config.db";
    private static final int DATABASE_VERSION = 4;
    public static final String TABLE_CONFIGS = "CONFIGS";

    //fields for TABLE_CONFIGS
    public static final String GESTURE = "category_name";
    public static String COMMAND_NAME = "command";
    public static String ACTION = "action";
    public static String TEXT = "text";
    public static String EMAIL = "email";

    private static final String SQL_CATEGORY = "CREATE TABLE " + TABLE_CONFIGS
            + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + GESTURE + " INTEGER," + COMMAND_NAME
            + " VARCHAR(255)," + ACTION + " VARCHAR(255)," + TEXT + " VARCHAR(255)," + EMAIL + " VARCHAR(255));";

    private static final String SQL_DELETE_CATEGORY = "DROP TABLE IF EXISTS "
            + TABLE_CONFIGS;

    public dbConfig(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CATEGORY);

        Log.d("database", "database created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL(SQL_DELETE_CATEGORY);

        onCreate(sqLiteDatabase);

    }

}
