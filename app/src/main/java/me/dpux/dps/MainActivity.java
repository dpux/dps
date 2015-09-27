package me.dpux.dps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startPollingService(null);
    }

    public void startPollingService(View view) {
        startService(new Intent(getApplicationContext(), PollingService.class));
    }

    public void stopPollingService(View view) {
        stopService(new Intent(getApplicationContext(), PollingService.class));
    }

}
