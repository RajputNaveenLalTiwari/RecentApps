package com.example.recentapps.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver
{
    private static final int RECEIVER_REQUEST_CODE = 1;
    private static final String TAG = "StartupReceiver";

    public StartupReceiver()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            final int alaramType = AlarmManager.ELAPSED_REALTIME;
            final long triggerAt = SystemClock.elapsedRealtime();
            final long timeInterval = 1000;

            Intent boostReceiverIntent = new Intent(context,ReceiverToStartService.class);
            PendingIntent boostReceiverPendingIntent = PendingIntent.getBroadcast(context,RECEIVER_REQUEST_CODE,boostReceiverIntent,0);

            alarmManager.setRepeating(alaramType,triggerAt,timeInterval,boostReceiverPendingIntent);
        }
        catch (Exception e)
        {
            Log.e(TAG,""+e.getMessage());
        }

    }
}
