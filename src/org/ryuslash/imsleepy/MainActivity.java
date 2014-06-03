package org.ryuslash.imsleepy;

import java.util.Date;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity
{
    private boolean isSleeping = false;
    private SleepSessionDataSource session_datasource;
    private InterruptionDataSource interruption_datasource;
    private SleepSession current_session;
    private SleepSession last_session;
    private SecondStepCounter seconds;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        session_datasource = new SleepSessionDataSource(this);
        session_datasource.open();

        current_session = session_datasource.getCurrent();

        interruption_datasource = new InterruptionDataSource(this);
        interruption_datasource.open();

        TextView view = (TextView)findViewById(R.id.timespan_view);
        seconds = new SecondStepCounter(view);

        if (current_session != null)
            startCounting(
                interruption_datasource.getLatest(
                    current_session.getId()
                )
            );

        last_session = session_datasource.getLatest();
        if (last_session != null) setSleepLengthText();
    }

    private void setSleepLengthText()
    {
        TextView view = (TextView)findViewById(R.id.sleeplength);
        Timespan span = Timespan.diff(last_session.getStart(),
                                      last_session.getEnd());
        Resources res = view.getResources();

        span.setFormat(res.getString(R.string.sleep_length));
        view.setText(span.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);

        if (current_session != null) {
            isSleeping = true;
            updateUISleeping(menu.findItem(R.id.main_menu_toggle_sleep));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
        case R.id.main_menu_toggle_sleep:
            isSleeping = !isSleeping;
            updateUISleeping(item);
            createSleepSession();
            countSeconds();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause()
    {
        session_datasource.close();
        interruption_datasource.close();
        seconds.stop();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        session_datasource.open();
        interruption_datasource.open();
        if (isSleeping) seconds.start();
        super.onResume();
    }

    private void createSleepSession()
    {
        if (isSleeping)
            current_session = session_datasource.startSleepSession();
        else {
            session_datasource.stopSleepSession(current_session);
            last_session = current_session;
            current_session = null;
            setSleepLengthText();
        }
    }

    private void updateUISleeping(MenuItem item)
    {
        View button = findViewById(R.id.interrupt_button);

        button.setClickable(isSleeping);
        if (isSleeping) {
            item.setIcon(R.drawable.ic_action_brightness_high);
            item.setTitle(R.string.main_menu_toggle_wakey);
            button.setAlpha(1.0f);
        }
        else {
            item.setIcon(R.drawable.ic_action_bightness_low);
            item.setTitle(R.string.main_menu_toggle_sleepy);
            button.setAlpha(0.5f);
        }

    }

    private void countSeconds()
    {
        if (isSleeping) {
            Interruption from =
                interruption_datasource.getLatest(
                    current_session.getId()
                );
            Date start = from != null ? from.getTime()
                : current_session.getStart();
            seconds.setStartValue(start.getTime());
            seconds.start();
        }
        else
            seconds.stop();
    }

    private void startCounting(Interruption from)
    {
        Date start = from != null ? from.getTime()
            : current_session.getStart();

        seconds.stop();
        seconds.setStartValue(start.getTime());
        seconds.start();
    }

    public void registerInterruption(View view)
    {
        if (current_session != null) {
            Interruption interruption =
                interruption_datasource.createInterruption(
                    current_session.getId()
                );
            startCounting(interruption);
        }
    }
}
