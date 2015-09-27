package me.dpux.dps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartPolling extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, PollingService.class));
    }
}