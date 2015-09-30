package com.example.wohlig.cameratest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "CameraTest";
    Preview preview;
    Button buttonClick;
    Camera camera;
    Activity act;
    Context ctx;
    Camera.Parameters parameters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        act = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);




        setContentView(R.layout.activity_main);

        preview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
        preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);
        buttonClick = (Button) findViewById(R.id.btnCapture);

        buttonClick.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                camera.setParameters(parameters);
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                /*parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);*/
            }
        });

        /*preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    focusOnTouch(event);
                }
                return true;
            }
        });*/

        /*preview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                camera.autoFocus(new Camera.AutoFocusCallback(){
                    @Override
                    public void onAutoFocus(boolean arg0, Camera arg1) {
                        //camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                    }
                });
            }
        });*/



        //Toast.makeText(ctx, getString(), Toast.LENGTH_LONG).show();

        		/*buttonClick = (Button) findViewById(R.id.btnCapture);

        		buttonClick.setOnClickListener(new OnClickListener() {
        			public void onClick(View v) {

        				preview.setCamera(camera).takePicture(shutterCallback, rawCallback, jpegCallback);
        				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
        			}
        		});*/

        		/*buttonClick.setOnLongClickListener(new OnLongClickListener(){
        			@Override
        			public boolean onLongClick(View arg0) {
        				camera.autoFocus(new Camera.AutoFocusCallback(){
        					@Override
        					public void onAutoFocus(boolean arg0, Camera arg1) {
        						//camera.takePicture(shutterCallback, rawCallback, jpegCallback);
        					}
        				});
        				return true;
        			}
        		});*/
    }

    private static  final int FOCUS_AREA_SIZE= 300;




    private void focusOnTouch(MotionEvent event) {
        /*if (camera != null ) {

            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0){
                Log.i(TAG,"fancy !");
                Rect rect = calculateFocusArea(event.getX(), event.getY());

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);

                camera.setParameters(parameters);
                camera.autoFocus(mAutoFocusTakePictureCallback);
            }else {
                camera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }*/

        /*if (camera != null) {

            camera.cancelAutoFocus();
            Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
            Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f);

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            parameters.setFocusAreas(Lists.newArrayList(new Camera.Area(focusRect, 1000)));

            if (meteringAreaSupported) {
                parameters.setMeteringAreas(Lists.newArrayList(new Camera.Area(meteringRect, 1000)));
            }

            camera.setParameters(parameters);
            camera.autoFocus(this);
        }*/
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / preview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / preview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
            if (touchCoordinateInCameraReper>0){
                result = 1000 - focusAreaSize/2;
            } else {
                result = -1000 + focusAreaSize/2;
            }
        } else{
            result = touchCoordinateInCameraReper - focusAreaSize/2;
        }
        return result;
    }


    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus","success!");
            } else {
                // do something...
                Log.i("tap_to_focus","fail!");
            }
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = Camera.open(0);
                camera.startPreview();
                preview.setCamera(camera);
            } catch (RuntimeException ex){
                //Toast.makeText(ctx, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        if(camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            new SaveImageTask().execute(data);
            //camera.stopPreview();
            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/CameraTest");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);
                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }
}