package org.ryuslash.imsleepy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity
{
    private boolean isSleeping = false;
    private SleepSessionDataSource session_datasource;
    private InterruptionDataSource interruption_datasource;
    private SleepSession current_session;

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
        super.onPause();
    }

    @Override
    public void onResume()
    {
        session_datasource.open();
        interruption_datasource.open();
        super.onResume();
    }

    private void createSleepSession()
    {
        if (isSleeping)
            current_session = session_datasource.startSleepSession();
        else {
            session_datasource.stopSleepSession(current_session);
            current_session = null;
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

    public void registerInterruption(View view)
    {
        interruption_datasource.createInterruption(current_session.getId());
    }
}
