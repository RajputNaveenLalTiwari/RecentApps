package com.example.recentapps.services;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class MyService extends Service
{
    private static final String TAG = "MyService";
    private Context context;

    public MyService()
    {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = MyService.this;
        handler.postDelayed(runnable,0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

            if(activityManager != null)
            {
                PackageManager packageManager;
                ApplicationInfo applicationInfo;
                String packageName = null;
                String appName = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                    List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis()- TimeUnit.DAYS.toMillis(1),System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(1));
                    if(usageStatsList != null)
                    {
                        SortedMap<Long, UsageStats> recentTasks = new TreeMap<>();

                        for (UsageStats usageStats : usageStatsList)
                        {
                            recentTasks.put(usageStats.getLastTimeUsed(), usageStats);
                        }

                        if (!recentTasks.isEmpty())
                        {
                            packageName = recentTasks.get(recentTasks.lastKey()).getPackageName();
//                            Log.i(TAG,"Package Name = "+packageName);
                        }
                    }
                }
                else
                {
                    List<ActivityManager.RecentTaskInfo> recentTaskInfoList = activityManager.getRecentTasks(1,ActivityManager.RECENT_WITH_EXCLUDED);

                    for (ActivityManager.RecentTaskInfo recentTaskInfo : recentTaskInfoList)
                    {
                        try
                        {
                            packageName = recentTaskInfo.baseIntent.getComponent().getPackageName();
                            packageManager = getPackageManager();
                            applicationInfo = packageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA);
                            appName = packageManager.getApplicationLabel(applicationInfo).toString();

//                            Log.i(TAG,"Package Name = "+packageName);
//                            Log.i(TAG,"Application Name = "+appName);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
        }

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);

            List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent,0);

            for (ResolveInfo resolveInfo : resolveInfoList)
            {
//                Log.i(TAG,"Package Name = "+resolveInfo.activityInfo.packageName);
            }
            handler.postDelayed(runnable,1000);
        }
    };
}
