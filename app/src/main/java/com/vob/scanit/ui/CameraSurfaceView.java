package com.vob.scanit.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;

/* The following class sets the surface view when the camera feature is requested by the application */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Context context;
    private Camera camera;
    private int cameraType;

    /*CameraSurfaceView() constructor to initialise class variables*/
    public CameraSurfaceView(Context context, Camera camera, int cameraType) {
        super(context);
        this.context = context;
        this.camera = camera;
        this.cameraType=cameraType;
        surfaceHolder = getHolder();
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);

    }

    /*Allows the user to have the device placed in both, portrait and landscape modes*/
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            setCameraDisplayOrientation((Activity) context,cameraType,camera);

            camera.startPreview();
        } catch (IOException e) {
            Log.d("tag", "Error setting camera preview: " + e.getMessage());
        }
    }

    /*Account for the changes in camera setting such as zooming in, modifying exposure levels, etc.*/
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (holder == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {
            Log.d("tag", "Error setting camera stop: " + e.getMessage());
        }

        try {
            camera.setPreviewDisplay(holder);
//                camera.setDisplayOrientation(90);
            setCameraDisplayOrientation((Activity) context,cameraType,camera);
            camera.startPreview();
        } catch (IOException e) {
            Log.d("tag", "Error setting camera preview: " + e.getMessage());
        }

    }

    /* The following function releases the camera when the existing preview surface gets destroyed */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.release();
    }

    /*setCameraDisplayOrientation() function allows the preview to be changed without having any effects
    * on how the image gets recorded*/
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}