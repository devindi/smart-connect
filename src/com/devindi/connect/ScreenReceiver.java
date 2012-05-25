package com.devindi.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;

public class ScreenReceiver extends BroadcastReceiver {
    private SharedPreferences.Editor editor;
    private SharedPreferences settings;
    private Context mContext;
    private final String LAST_SCREEN_STATE_CHANGE="1";
    @Override
    public void onReceive(Context context, Intent intent) {
        settings=PreferenceManager.getDefaultSharedPreferences(context);
        editor= settings.edit();
        mContext=context;
        Log.e("Connect free", "on receive");

        if( settings.getBoolean(main.POWER, true)){
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            change(context, false);
            //Log.e("123", "выключили");//выключили экран
            editor.putLong(LAST_SCREEN_STATE_CHANGE, Calendar.getInstance().getTimeInMillis());
            editor.commit();
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            change(context, true);
            check(context);
            //Log.e("123", "включили");//включили
            editor.putLong(LAST_SCREEN_STATE_CHANGE, Calendar.getInstance().getTimeInMillis());
            editor.commit();
        }
        else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            context.startService(new Intent(context, SmartConnect.class));
        }
        else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                NetworkInfo info= intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                String reason= info.getReason();
                if(Calendar.getInstance().getTimeInMillis()-settings.getLong(LAST_SCREEN_STATE_CHANGE, 0)>5000){
                    if(info.getType()==ConnectivityManager.TYPE_MOBILE){
                        Log.e("Connect", "ConnectivityManager.CONNECTIVITY_ACTION" + (settings.getLong(LAST_SCREEN_STATE_CHANGE, 0)-Calendar.getInstance().getTimeInMillis()));
                        try{
                            if(reason.equals("dataDisabled")){
                                Log.e("onReceive", "dataDisabled - service off");
                                editor.putBoolean(main.POWER, false);
                            }else if(reason.equals("apnSwitched")){
                                Log.e("onReceive", "apnSwitched - service on");
                                editor.putBoolean(main.POWER, true);
                            }
                            editor.commit();
                        }catch (NullPointerException ex){
                            ex.printStackTrace();
                        }
                    }

                }
            }
    }}

    void check(Context context){
        int lastUpdateDayOfYear = PreferenceManager.getDefaultSharedPreferences(context).getInt("day", 0);
        Calendar calendar=Calendar.getInstance();
        //if(lastUpdateDayOfYear<calendar.get(Calendar.DAY_OF_YEAR)){
        if(true){
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    //To change body of implemented methods use File | Settings | File Templates.
                    UpdateChecker checker=new UpdateChecker(mContext);
                    try {
                        checker.check();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }, 10000);

        }
    }

    void change(Context context, boolean newState){
        try{
            ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Method method = connectivityManager.getClass().getMethod("setMobileDataEnabled", boolean.class);
            method.invoke(connectivityManager, newState);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}