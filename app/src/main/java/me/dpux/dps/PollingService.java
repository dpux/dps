package me.dpux.dps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;

public class PollingService extends Service {

    private int POLLING_PERIOD = 1000 * 60 * 30;  //30 mins
    public PollingService() {
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Irving.main(getApplicationContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        stopSelf();
        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (POLLING_PERIOD),
                PendingIntent.getService(getApplicationContext(), 0, new Intent(this, PollingService.class), 0)
        );
    }
}
