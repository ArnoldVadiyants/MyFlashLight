package com.arnold.myflashlight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenOffReciver extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equalsIgnoreCase(intent.getAction())) {
            PropertiesLight.sScreenOff = true;
            Log.d(TAG, "ScreenOff");
        }
    }

}
