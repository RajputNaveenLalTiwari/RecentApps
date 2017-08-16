package com.example.recentapps.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.recentapps.services.MyService;

public class ReceiverToStartService extends BroadcastReceiver
{
    public ReceiverToStartService()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent boostServiceIntent = new Intent(context,MyService.class);
        context.startService(boostServiceIntent);
    }
}
