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
                Timespan timespan =
                    Timespan.diff(startTime, System.currentTimeMillis());
                Resources res = display.getResources();

                timespan.setFormat(res.getString(R.string.last_wakeup));
                display.setText(timespan.toString());
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
