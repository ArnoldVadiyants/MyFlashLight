package com.arnold.myflashlight;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class NotifyService extends Service {
    private PropertiesLight mPropertiesLight;
    private static final String TAG = "NotifyService";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mPropertiesLight = PropertiesLight.getInstance();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStart");

        String call = (String) intent.getExtras().get("do_action");
        int icon = 0;
        int action = 0;
        Log.d(TAG, "action");
        if (call.equals("activityCall")) {
            icon = android.R.drawable.checkbox_on_background;
            action = R.string.turn_off;
            Log.d(TAG, "action2");
        } else {
            Log.d(TAG, "action3");
            if (mPropertiesLight.getCheckLight()) {
                turnOff();
                icon = android.R.drawable.checkbox_off_background;
                action = R.string.turn_on;
                Log.d(TAG, "action4");
            } else {
                turnOn();
                icon = android.R.drawable.checkbox_on_background;
                action = R.string.turn_off;
                Log.d(TAG, "action5");
            }
        }

        createNotification(icon, action);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        destroyNotification();
    }

    private void turnOn() {
        mPropertiesLight.setCheckLight(true);
        startService(new Intent(NotifyService.this, LedService.class));
    }

    private void turnOff() {
        mPropertiesLight.setCheckLight(false);
        // stopService(new Intent(NotifyActivity.this, LedService.class));

    }

    private void createNotification(int icon, int action) {

        Resources r = getResources();
        Intent intent = new Intent(this, NotifyService.class);
        intent.putExtra("do_action", "notifyCall");
        PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(r.getString(R.string.app_name)).setSmallIcon(icon)
                .setContentTitle(r.getString(R.string.app_name))
                .setContentText(r.getString(action)).setContentIntent(pi)
                .setOngoing(true).build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notification);
    }

    private  void destroyNotification() {
        NotificationManager mNotificationManager = ( NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);

    }

}
