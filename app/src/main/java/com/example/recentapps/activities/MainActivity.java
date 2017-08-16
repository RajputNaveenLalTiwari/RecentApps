package com.example.recentapps.activities;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Process;
import android.provider.Browser;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recentapps.R;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private static final int USAGE_ACCESS_REQUEST_CODE = 5;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        context.sendBroadcast(new Intent("StartupReceiver_Manual_Start"));
//        recentApps();
//        test();

        fillStats();
    }

    private void recentApps()
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
                List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();

                for (ActivityManager.AppTask appTask : appTaskList)
                {
                    try
                    {
                        packageName = appTask.getTaskInfo().baseIntent.getComponent().getPackageName();
                        packageManager = getPackageManager();
                        applicationInfo = packageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA);
                        appName = packageManager.getApplicationLabel(applicationInfo).toString();

                        Log.i(TAG,"Package Name = "+packageName);
                        Log.i(TAG,"Application Name = "+appName);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                List<ActivityManager.RecentTaskInfo> recentTaskInfoList = activityManager.getRecentTasks(5,ActivityManager.RECENT_WITH_EXCLUDED);

                for (ActivityManager.RecentTaskInfo recentTaskInfo : recentTaskInfoList)
                {
                    try
                    {
                        packageName = recentTaskInfo.baseIntent.getComponent().getPackageName();
                        packageManager = getPackageManager();
                        applicationInfo = packageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA);
                        appName = packageManager.getApplicationLabel(applicationInfo).toString();

                        Log.i(TAG,"Package Name = "+packageName);
                        Log.i(TAG,"Application Name = "+appName);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void test()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent,0);

        for (ResolveInfo resolveInfo : resolveInfoList)
        {
            Log.i(TAG,"Package Name = "+resolveInfo.activityInfo.packageName);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean hasPermission()
    {
        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), getPackageName());

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void requestPermission()
    {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent,USAGE_ACCESS_REQUEST_CODE);
    }

    private void fillStats()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (hasPermission())
            {
                getStats();
            }
            else
            {
                requestPermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == USAGE_ACCESS_REQUEST_CODE)
        {
            fillStats();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getStats() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis()- TimeUnit.DAYS.toMillis(1),System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(1));
        TextView textView = (TextView) findViewById(R.id.usage_stats);

        if(usageStatsList != null)
        {
            TreeMap<Long,UsageStats> recentTasks = new TreeMap<>(Collections.<Long>reverseOrder());

            for (UsageStats usageStats : usageStatsList)
            {
                if(recentTasks.entrySet().size() < 6)
                recentTasks.put(usageStats.getLastTimeUsed(),usageStats);
            }

            if(!recentTasks.isEmpty())
            {
//                textView.setText(recentTasks.get(recentTasks.lastKey()).getPackageName());
            }

            StringBuilder stringBuilder = new StringBuilder();


            for (Map.Entry<Long, UsageStats> map:recentTasks.entrySet())
            {
                stringBuilder.append(map.getValue().getPackageName());
                stringBuilder.append("\r\n");
            }

            textView.setText(stringBuilder.toString());
        }
    }
}
