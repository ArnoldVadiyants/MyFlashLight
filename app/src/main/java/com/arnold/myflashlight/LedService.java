package com.arnold.myflashlight;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;

public class LedService extends Service {
    private static final String TAG = "LedService";
    public static Camera mCamera;
    public static Parameters mParams;
    private PropertiesLight mPropertiesLight;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
       /* View view = (View) View.inflate(this, R.layout.main, null);
        mSurfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
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

        initializeCamera();
        mPropertiesLight = PropertiesLight.getInstance();

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stu

     //   this.initCamera();
      //  this.initCamera();
        Log.v(TAG, "onStartCommand");
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Log.v(TAG, "Thread");
                int tmpLevel = 0;

                if (mPropertiesLight.getCheckLight()) {
            //        Log.v(TAG, " if getCheckLight()");
                    try {

                        while (mPropertiesLight.getCheckLight()) {
                  //          Log.v(TAG, "while getCheckLight()");
                            tmpLevel = mPropertiesLight.getLevelLight();

                            if (tmpLevel == 0) {
                       //         Log.v(TAG, "Level = 0");
                                if (!mParams.getFlashMode().equals(
                                        FLASH_MODE_TORCH)) {
                                    mParams.setFlashMode(FLASH_MODE_TORCH);
                                    mCamera.setParameters(mParams);
                                    mCamera.startPreview();
                                    Log.v(TAG, "if (!mParams.getFlashMode().equals(\n" +
                                            "                                        Camera.Parameters.FLASH_MODE_TORCH))");

                                }
                                if (PropertiesLight.sScreenOff) {
                                    Log.v(TAG, "PropertiesLight.sScreenOff");
                                    mParams.setFlashMode(FLASH_MODE_OFF);
                                    mCamera.setParameters(mParams);
                                    mCamera.startPreview();
                                    PropertiesLight.sScreenOff = false;
                                }
                            } else {
                                Log.v(TAG, "tmpLevel != 0");
                                if (mParams.getFlashMode().equals(
                                        FLASH_MODE_OFF)) {
                                    Log.v(TAG, " if (mParams.getFlashMode().equals(\n" +
                                            "                                        Camera.Parameters.FLASH_MODE_OFF)");
                                    mParams.setFlashMode(FLASH_MODE_TORCH);
                                } else {
                                    Log.v(TAG, "!!!!(mParams.getFlashMode().equals(\n" +
                                            "                                        Camera.Parameters.FLASH_MODE_OFF))");
                                    mParams.setFlashMode(FLASH_MODE_OFF);
                                }
                                mCamera.setParameters(mParams);
                                mCamera.startPreview();
                                Thread.sleep(1000 / (tmpLevel));
                            }

                        }// while

                    }/* try */
                    catch (InterruptedException e) {
                        // e.printStackTrace();
                        Toast.makeText(LedService.this, "Interrupted",
                                Toast.LENGTH_SHORT).show();
                        Thread.currentThread().interrupt();
                    }// catch
                }
                if (!mPropertiesLight.getCheckLight()) {
                    Log.v(TAG, "!!!mPropertiesLight.getCheckLight()");
                    if(mCamera != null)
                    {
                        mParams.setFlashMode(FLASH_MODE_OFF);
                    mCamera.setParameters(mParams);
                }

             //       mCamera.stopPreview();
                }// if
            } // end run
        });// end new Thread
        thread.start();

        return START_STICKY;
}

    @Override
    public void onDestroy() {
        releaseCameraResources();
        super.onDestroy();
        mPropertiesLight.setCheckLight(false);

    }
   public static void releaseCameraResources() {
        if (null != mCamera) {
            mCamera.release();
            mCamera = null;
        }
    }
    private void initializeCamera() {
        Log.v(TAG, "1 - initializeCamera()");
        if (checkCameraHardware()) {
            mCamera = getCameraInstance();
            setParams();
       /*     if (Build.MANUFACTURER.equalsIgnoreCase("Samsung"))
            {
                Log.v(TAG, "5 - SAMSUNG");
                releaseCameraResources();
                mCamera = getCameraInstance();
                setParams();
            }*/
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.sActivity);
            builder.setTitle(R.string.flashTitle);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton(R.string.OK, null);
            builder.setCancelable(true);
            builder.setMessage(R.string.flashMessage);
            AlertDialog errorDialog = builder.create();
            errorDialog
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            onDestroy();
                        }
                    });
            errorDialog.show();
        }
    }

    private boolean checkCameraHardware() {
        boolean result = false;
        PackageManager pckMg = getPackageManager();
        if (pckMg.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            result = true;
        }
        Log.v(TAG, "2 - checkCameraHardware() = " + result);
        return result;
    }

    private void setParams() {

        if (mCamera != null) {
            mParams = mCamera.getParameters();
            Log.v(TAG, "4 - setParams()");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.sActivity);
            builder.setTitle(R.string.accessTitle);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton(R.string.OK, null);
            builder.setCancelable(true);
            builder.setMessage(R.string.accessMessage);
            AlertDialog errorDialog = builder.create();
            errorDialog
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            onDestroy();
                        }
                    });
            errorDialog.show();
        }
    }



    private Camera getCameraInstance() {

        Camera camera = null;
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                try {
                    camera = Camera.open(i);
                } catch (RuntimeException e) {

                }
            }
        }
        boolean isNull = false;

        if(camera == null)
        {
            isNull = true;
        }
        Log.v(TAG, " 3 - getCameraInstance(), isNull = " + isNull );
        return camera;
    }

}