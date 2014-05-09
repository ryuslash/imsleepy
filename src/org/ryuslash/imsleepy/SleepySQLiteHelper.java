package org.ryuslash.imsleepy;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SleepySQLiteHelper extends SQLiteOpenHelper
{
    public static final String COLUMN_ID = "id";

    public static final String TABLE_SLEEP_SESSION = "sleep_session";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";

    public static final String TABLE_INTERRUPTION = "interruption";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_SLEEP_SESSION_ID = "sleep_session_id";

    private static final String DATABASE_NAME = "imsleepy.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_SLEEP_SESSION_CREATE =
        "CREATE TABLE " + TABLE_SLEEP_SESSION + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COLUMN_START + " DATETIME NOT NULL,"
        + COLUMN_END + " DATETIME"
        + ");";

    private static final String TABLE_INTERRUPTION_CREATE =
        "CREATE TABLE " + TABLE_INTERRUPTION + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COLUMN_SLEEP_SESSION_ID + " INTEGER REFERENCES sleep_session(id), "
        + COLUMN_TIME + " DATETIME NOT NULL"
        + ");";

    private SimpleDateFormat date_formatter =
        new SimpleDateFormat("yyyy-MM-dd'T'kk:MM:ssZZZZZ", Locale.US);

    public SimpleDateFormat getDateFormatter()
    {
        return date_formatter;
    }

    public SleepySQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(TABLE_SLEEP_SESSION_CREATE);
        db.execSQL(TABLE_INTERRUPTION_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV)
    {
        Log.w(SleepySQLiteHelper.class.getName(),
              "Upgrading database from version " + oldV + " to " + newV);
    }
}
