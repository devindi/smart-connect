package com.devindi.connect;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {
    private Context mContext;
    private static final int HELLO_ID = 1;

    public UpdateChecker(Context context){
        mContext=context;
    }

    boolean check() throws IOException{
        Calendar calendar=Calendar.getInstance();
        int today=calendar.get(Calendar.DAY_OF_YEAR);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("day", today);
        editor.commit();
        // *************************************************************************
        Log.e("update", "check");
        String versionCode = "", updateLink="";
        URL url = new URL("http://devindi.com/updates.html");
        URLConnection conn = url.openConnection();
        InputStreamReader rd = new InputStreamReader(conn.getInputStream());
        StringBuilder allpage = new StringBuilder();
        int n = 0;
        char[] buffer = new char[40000];
        while (n >= 0)
        {
            n = rd.read(buffer, 0, buffer.length);
            if (n > 0)
            {
                allpage.append(buffer, 0, n);
            }
        }
        // работаем с регулярками
        Pattern pattern = Pattern.compile
                ("ConnectVersionCode(.)");
        Matcher matcher = pattern.matcher(allpage.toString());
        if (matcher.find())
        {
            versionCode = matcher.group(1);
        }
        pattern = Pattern.compile
                ("connectLink(.*apk)");
        matcher = pattern.matcher(allpage.toString());
        if (matcher.find())
        {
            updateLink = matcher.group(1);
        }
        PackageInfo pinfo;
        int versionNumber = 5, lastVersionNumber=versionNumber;
        try{
            pinfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionNumber = pinfo.versionCode;
            Log.e("update", "version "+versionNumber);
            lastVersionNumber= Integer.parseInt(versionCode);        }
        catch (Exception e){
        }
        if(lastVersionNumber>versionNumber){
            Log.e("update", "available");
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(ns);
            int icon = R.drawable.icon;
            CharSequence tickerText = "Smart Connect";
            long when = System.currentTimeMillis();
            Notification notification = new Notification(icon, tickerText, when);
            CharSequence contentTitle = "Доступно обновление";
            CharSequence contentText = "Нажмите для загрузки";
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devindi.com/smartConnect.1.5.apk"));
            PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
            notification.setLatestEventInfo(mContext, contentTitle, contentText, contentIntent);
            mNotificationManager.notify(HELLO_ID, notification);
        }
        return true;
    }
}
