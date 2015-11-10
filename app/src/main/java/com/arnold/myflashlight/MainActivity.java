package com.arnold.myflashlight;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private CheckBox mOnCheckBox;
    private SeekBar mLightSeekBar;
    private TextView mLvlTextView;
    private PropertiesLight mPropertiesLight;
    private long mBack_Pressed;
    private boolean mNotificationIsCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate_Activity");
        setContentView(R.layout.activity_main);
        mLightSeekBar = (SeekBar) findViewById(R.id.lightSeekBar);
        mLightSeekBar.setOnSeekBarChangeListener(lightSeekBarChangeListener);
        mOnCheckBox = (CheckBox) findViewById(R.id.onCheckBox);
        mOnCheckBox.setOnCheckedChangeListener(onCheckBoxListener);
        mLvlTextView = (TextView) findViewById(R.id.lvlLightTextView);
        mPropertiesLight = PropertiesLight.getInstance();
        mNotificationIsCreated = false;
        screenOffListener();

    } // onCreate

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOnCheckBox.setChecked(false);
        stopService(new Intent(MainActivity.this, NotifyService.class));
        stopService(new Intent(MainActivity.this, LedService.class));

    }

    @Override
    public void onPause() {
        super.onPause();

        if ((!mNotificationIsCreated) && mPropertiesLight.getCheckLight()) {
            startService(new Intent(MainActivity.this, NotifyService.class)
                    .putExtra("do_action", "activityCall"));
            mNotificationIsCreated = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mOnCheckBox.setChecked(mPropertiesLight.getCheckLight());
        if (mNotificationIsCreated) {
            stopService(new Intent(MainActivity.this, NotifyService.class));
            mNotificationIsCreated = false;
        }

    }

    private OnCheckedChangeListener onCheckBoxListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            mPropertiesLight.setCheckLight(isChecked);
            if (isChecked) {
                startService(new Intent(MainActivity.this, LedService.class));
                mOnCheckBox.setTextColor(getResources().getColor(
                        R.color.powerOn));

            } else {
                mOnCheckBox.setTextColor(getResources().getColor(
                        R.color.powerOff));
            }

        }// end onCheckedChanged
    };// end OnCheckedChangeListener

    public void finish() {
        mPropertiesLight.setLevelLight(0);
        mNotificationIsCreated = true;
        super.finish();
    }

    private OnSeekBarChangeListener lightSeekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            mLvlTextView.setText(seekBar.getProgress() + "");
            mPropertiesLight.setLevelLight((int) Math.pow(
                    seekBar.getProgress(), 1.7));
        }
    };

    @Override
    public void onBackPressed() {

        if (mBack_Pressed + 2000 > System.currentTimeMillis()) {
            finish();
        } else {
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
        }
        mBack_Pressed = System.currentTimeMillis();
    }

    private void screenOffListener() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new ScreenOffReciver(), filter);
    }

}// MainActivity
