package com.arnold.myflashlight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
 //   private CheckBox mOnCheckBox;\
    private ImageButton mFlashImageButton;
    private SeekBar mLightSeekBar;
    private TextView mLvlTextView;
    private PropertiesLight mPropertiesLight;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private long mBack_Pressed;
    private boolean mNotificationIsCreated;
    private boolean mIsFlashOn;
    private ScreenOffReciver mScreenOffReciver;
    public static Activity sActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sActivity = this;
        startService(new Intent(MainActivity.this, LedService.class));

        /*Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int width = 300;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
           width = ((int)(((size.x))/2));
        }
        else
        {
          width = ((int)(((display.getWidth()))/2));
        }
        Log.d(TAG, "onCreate_Activity" + "size = " + width);*/
        setContentView(R.layout.main);
       mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);

                    mSurfaceHolder = mSurfaceView.getHolder();
                    mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
                        @Override
                        public void surfaceCreated(SurfaceHolder holder) {
                            try {
                                LedService.mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        mLightSeekBar = (SeekBar) findViewById(R.id.lightSeekBar);
        mLightSeekBar.setOnSeekBarChangeListener(lightSeekBarChangeListener);
        mFlashImageButton = (ImageButton)findViewById(R.id.flashImageButton);
       mFlashImageButton.setOnClickListener(onFlashImageButtonListener);
        //   mOnCheckBox = (CheckBox) findViewById(R.id.onCheckBox);
        //mOnCheckBox.setWidth(width);
        //mOnCheckBox.setHeight(width);


      //  mOnCheckBox.setOnCheckedChangeListener(onCheckBoxListener);

        mLvlTextView = (TextView) findViewById(R.id.lvlLightTextView);
        mPropertiesLight = PropertiesLight.getInstance();
        mNotificationIsCreated = false;
        mScreenOffReciver = new ScreenOffReciver();





    } // onCreate

    @Override
    protected void onStart() {

        super.onStart();
        screenOffListener();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   mOnCheckBox.setChecked(false);
         mFlashImageButton.setPressed(false);
        stopService(new Intent(MainActivity.this, NotifyService.class));
        stopService(new Intent(MainActivity.this, LedService.class));
          unregisterReceiver(mScreenOffReciver);
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
if(mPropertiesLight.getCheckLight())
{
    //stopService(new Intent(MainActivity.this, LedService.class));
    //startService(new Intent(MainActivity.this, LedService.class));
}
        Log.v(TAG, "onResume");
//        mOnCheckBox.setChecked(mPropertiesLight.getCheckLight());
        mFlashImageButton.setSelected(mPropertiesLight.getCheckLight());
     /*   if(LedService.mCamera !=  null)
        {
            Log.v(TAG, "mCamera !=  null");
            mSurfaceHolder = mSurfaceView.getHolder();
      //      LedService.mCamera.setParameters(LedService.mParams);
            try {
                LedService.mCamera.setPreviewDisplay(mSurfaceHolder);
                LedService.mCamera.setParameters(LedService.mParams);
                LedService.mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    //    stopService(new Intent(MainActivity.this, LedService.class));
  //      LedService.releaseCameraResources();
      //  LedService.mCamera.release();
   //     startService(new Intent(MainActivity.this, LedService.class));
 /*     mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);

        mSurfaceHolder = mSurfaceView.getHolder();

        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    LedService.mCamera.lock();
                    LedService.mCamera.setPreviewDisplay(mSurfaceHolder);
                    LedService.mCamera.setParameters(LedService.mParams);
                    LedService.mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });*/

       /* try {

           // LedService.mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        if (mNotificationIsCreated) {
            stopService(new Intent(MainActivity.this, NotifyService.class));
            mNotificationIsCreated = false;
        }

    }
    private View.OnClickListener onFlashImageButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPropertiesLight.setCheckLight(!v.isSelected());

new Thread(new Runnable() {
    @Override
    public void run() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
    }
}).start();

        v.setSelected(!v.isSelected());

        if (v.isSelected()) {
            startService(new Intent(MainActivity.this, LedService.class));
        } else {
        }
        }
    };
    private OnCheckedChangeListener onCheckBoxListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            mPropertiesLight.setCheckLight(isChecked);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(50);
            if (isChecked) {
                startService(new Intent(MainActivity.this, LedService.class));
             //   mOnCheckBox.setTextColor(getResources().getColor(
               //         R.color.powerOn));

            } else {
            //    mOnCheckBox.setTextColor(getResources().getColor(
            //            R.color.powerOff));
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
        registerReceiver(mScreenOffReciver, filter);
    }

    @Override
    protected void onStop() {
      //  unregisterReceiver(mScreenOffReciver);
        super.onStop();
    }
}// MainActivity
