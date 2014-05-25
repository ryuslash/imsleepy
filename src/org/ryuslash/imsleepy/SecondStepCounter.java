package org.ryuslash.imsleepy;

import android.content.res.Resources;
import android.os.Handler;
import android.widget.TextView;

public class SecondStepCounter
{
    private TextView display;
    private Handler tickHandler = new Handler();
    private long startTime = 0;

    private Runnable updateTime = new Runnable() {
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int)(millis / 1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;
                minutes = minutes % 60;
                seconds = seconds % 60;
                Resources res = display.getResources();
                String message = String.format(
                    res.getString(R.string.last_wakeup),
                    hours, minutes, seconds
                );

                display.setText(message);
                tickHandler.postDelayed(this, 200);
            }
        };

    public SecondStepCounter(TextView textView)
    {
        display = textView;
    }

    public void start()
    {
        tickHandler.removeCallbacks(updateTime);
        tickHandler.postDelayed(updateTime, 100);
    }

    public void setStartValue(long value)
    {
        startTime = value;
    }

    public void stop()
    {
        display.setText("");
        setStartValue(0);
        tickHandler.removeCallbacks(updateTime);
    }
}
