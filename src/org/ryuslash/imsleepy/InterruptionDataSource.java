package org.ryuslash.imsleepy;

import java.util.Date;
import java.text.ParseException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class InterruptionDataSource
{
    private SQLiteDatabase database;
    private SleepySQLiteHelper dbHelper;
    private String[] allColumns = {
        SleepySQLiteHelper.COLUMN_ID,
        SleepySQLiteHelper.COLUMN_SLEEP_SESSION_ID,
        SleepySQLiteHelper.COLUMN_TIME
    };

    public InterruptionDataSource(Context context)
    {
        dbHelper = new SleepySQLiteHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public Interruption createInterruption(long sleep_session_id)
    {
        ContentValues values = new ContentValues();

        values.put(SleepySQLiteHelper.COLUMN_SLEEP_SESSION_ID,
                   sleep_session_id);
        values.put(SleepySQLiteHelper.COLUMN_TIME,
                   dbHelper.getDateFormatter().format(new Date()));

        long insertId = database.insert(
            SleepySQLiteHelper.TABLE_INTERRUPTION, null, values
        );
        String selectionArgs[] = { "" + insertId };
        Cursor cursor  = database.query(
            SleepySQLiteHelper.TABLE_INTERRUPTION, allColumns,
            SleepySQLiteHelper.COLUMN_ID + " = ?", selectionArgs, null,
            null, null
        );

        cursor.moveToFirst();
        Interruption interruption = cursorToInterruption(cursor);
        cursor.close();

        return interruption;
    }

    public Interruption latestInterruption(long sleep_session_id)
    {
        String[] whereArgs = {"" + sleep_session_id};
        Cursor cursor = database.query(
            SleepySQLiteHelper.TABLE_INTERRUPTION, allColumns,
            "sleep_session_id = ?", whereArgs, null, null,
            "time desc", "1"
        );
        Interruption interruption = null;

        if (cursor.moveToFirst())
            interruption = cursorToInterruption(cursor);

        cursor.close();

        return interruption;
    }

    private Interruption cursorToInterruption(Cursor cursor)
    {
        Interruption interruption = new Interruption();

        interruption.setId(cursor.getLong(0));
        interruption.setSessionId(cursor.getLong(1));

        try {
            interruption.setTime(dbHelper.getDateFormatter().parse(cursor.getString(2)));
        }
        catch (ParseException ex) {
            return null;
        }

        return interruption;
    }
}
