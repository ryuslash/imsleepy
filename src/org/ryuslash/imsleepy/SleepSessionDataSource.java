package org.ryuslash.imsleepy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SleepSessionDataSource
{
    private SQLiteDatabase database;
    private SleepySQLiteHelper dbHelper;
    private String[] allColumns = { SleepySQLiteHelper.COLUMN_ID,
                                    SleepySQLiteHelper.COLUMN_START,
                                    SleepySQLiteHelper.COLUMN_END };

    public SleepSessionDataSource(Context context)
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

    public SleepSession startSleepSession()
    {
        ContentValues values = new ContentValues();
        String startDate = dbHelper.getDateFormatter().format(new Date());

        values.put(SleepySQLiteHelper.COLUMN_START, startDate);
        Log.w(SleepSessionDataSource.class.getName(),
              "Starting session at: " + startDate);

        long insertId = database.insert(
            SleepySQLiteHelper.TABLE_SLEEP_SESSION, null, values
        );
        String selectionArgs[] = { "" + insertId };
        Cursor cursor  = database.query(
            SleepySQLiteHelper.TABLE_SLEEP_SESSION, allColumns,
            SleepySQLiteHelper.COLUMN_ID + " = ?", selectionArgs, null,
            null, null
        );

        cursor.moveToFirst();
        SleepSession session = cursorToSleepSession(cursor);
        cursor.close();

        return session;
    }

    public void stopSleepSession(SleepSession session)
    {
        ContentValues values = new ContentValues();
        String[] whereArgs = { "" + session.getId() };
        Date endDate = new Date();
        String endStr = dbHelper.getDateFormatter().format(endDate);

        values.put(SleepySQLiteHelper.COLUMN_END, endStr);
        Log.w(SleepSessionDataSource.class.getName(),
              "Stopping session at: " + endStr);

        database.update(SleepySQLiteHelper.TABLE_SLEEP_SESSION, values,
                        SleepySQLiteHelper.COLUMN_ID + " = ?",
                        whereArgs);
        session.setEnd(endDate);
    }

    public SleepSession getCurrent()
    {
        Cursor cursor = database.query(
            SleepySQLiteHelper.TABLE_SLEEP_SESSION, allColumns,
            SleepySQLiteHelper.COLUMN_START + " IS NOT NULL AND "
            + SleepySQLiteHelper.COLUMN_END + " IS NULL", null, null,
            null, null
        );
        SleepSession session = null;

        if (cursor.moveToFirst())
            session = cursorToSleepSession(cursor);

        cursor.close();

        return session;
    }

    private SleepSession cursorToSleepSession(Cursor cursor)
    {
        SleepSession session = new SleepSession();
        String endStr = null;

        session.setId(cursor.getLong(0));

        try {
            session.setStart(dbHelper.getDateFormatter().parse(cursor.getString(1)));
        }
        catch (ParseException ex) {
            return null;
        }

        endStr = cursor.getString(2);
        if (endStr != null)
            try {
                session.setEnd(dbHelper.getDateFormatter().parse(endStr));
            }
            catch (ParseException ex) {
                ;               // It's OK not to have an end time.
            }

        return session;
    }
}
