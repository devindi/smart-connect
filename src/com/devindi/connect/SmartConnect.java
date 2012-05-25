package com.devindi.connect;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

public class SmartConnect extends Service {
    private static BroadcastReceiver mReceiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        mReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
        Log.e("Connect", "onStart");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e("Connect", "onDestroy");
        unregisterReceiver(mReceiver);
    }
}
