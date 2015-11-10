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
import android.widget.Toast;

public class LedService extends Service {
    private static final String TAG = "LedService";
    private Camera mCamera;
    private Parameters mParams;
    private PropertiesLight mPropertiesLight;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeCamera();
        mPropertiesLight = PropertiesLight.getInstance();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stu
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                int tmpLevel = 0;

                if (mPropertiesLight.getCheckLight()) {

                    try {

                        while (mPropertiesLight.getCheckLight()) {
                            tmpLevel = mPropertiesLight.getLevelLight();

                            if (tmpLevel == 0) {
                                if (!mParams.getFlashMode().equals(
                                        Camera.Parameters.FLASH_MODE_TORCH)) {
                                    mParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
                                    mCamera.setParameters(mParams);

                                }
                                if (PropertiesLight.sScreenOff) {
                                    mParams.setFlashMode(Parameters.FLASH_MODE_OFF);
                                    mCamera.setParameters(mParams);
                                    PropertiesLight.sScreenOff = false;
                                }
                            } else {

                                if (mParams.getFlashMode().equals(
                                        Camera.Parameters.FLASH_MODE_OFF)) {
                                    mParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
                                } else {
                                    mParams.setFlashMode(Parameters.FLASH_MODE_OFF);
                                }
                                mCamera.setParameters(mParams);
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
                    mParams.setFlashMode(Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(mParams);
                }// if
            } // end run
        });// end new Thread
        thread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPropertiesLight.setCheckLight(false);
        if (mCamera != null) {
            mCamera.release();
        }
    }

    private void initializeCamera() {
        if (checkCameraHardware()) {
            mCamera = getCameraInstance();
            setParams();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        return result;
    }

    private void setParams() {
        if (mCamera != null) {
            mParams = mCamera.getParameters();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        return camera;
    }

}