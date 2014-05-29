package org.ryuslash.imsleepy;

import java.util.Date;

class Timespan
{
    private long millis;
    private String format;

    private Timespan(long millis)
    {
        this.millis = millis;
    }

    public static Timespan diff(Date date1, Date date2)
    {
        long millis1 = date1.getTime();
        long millis2 = date2.getTime();

        return new Timespan(millis2 - millis1);
    }

    public static Timespan diff(long millis1, long millis2)
    {
        return new Timespan(millis2 - millis1);
    }

    @Override
    public String toString()
    {
        int seconds = (int)(millis / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        minutes = minutes % 60;
        seconds = seconds % 60;

        return String.format(format, hours, minutes, seconds);
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public String getFormat()
    {
        return format;
    }
}
