package com.arnold.myflashlight;

import android.util.Log;

public class PropertiesLight {
    private static final String TAG = "PropertiesLight";
    private static PropertiesLight uniqueInstance;
    private int mLevelLight;
    private boolean mCheckLight;
    public static boolean sScreenOff = false;

    private PropertiesLight() {
        // TODO Auto-generated constructor stub
        this.mLevelLight = 0;
        this.mCheckLight = false;
    }

    public static PropertiesLight getInstance() {
        if (uniqueInstance == null) {
            //Toast.makeText(  , "Create PropertiesLight", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Create PropertiesLight");
            uniqueInstance = new PropertiesLight();
        }
        return uniqueInstance;
    }

    public void setCheckLight(boolean сheckLight) {
        this.mCheckLight = сheckLight;
    }

    public boolean getCheckLight() {
        return this.mCheckLight;
    }

    public void setLevelLight(int levelLight) {
        this.mLevelLight = levelLight;
    }

    public int getLevelLight() {
        return this.mLevelLight;
    }
}