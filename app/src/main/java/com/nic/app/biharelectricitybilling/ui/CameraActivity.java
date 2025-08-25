package com.nic.app.biharelectricitybilling.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.util.GlobalVariables;
import com.nic.app.biharelectricitybilling.util.MarshmallowPermission;
import com.nic.app.biharelectricitybilling.util.Utiilties;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


@SuppressLint("NewApi")
public class CameraActivity extends Activity {
    MarshmallowPermission permission;
    Button btnCamType;
    Button takePhoto;
    ProgressBar progress_finding_location;
    boolean init;
    int camType;
    FrameLayout preview;


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        init = false;
        permission = new MarshmallowPermission(this, Manifest.permission.CAMERA);
        if (permission.result == -1 || permission.result == 0) {
            try {
                if (!init) initializeCamera(camType);
            } catch (Exception e) {
            }
        } else if (permission.result == 1) {
            if (!init) initializeCamera(camType);
        }

        permission = new MarshmallowPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission.result == -1 || permission.result == 0) {
            try {
                if (!init) initializeCamera(camType);
            } catch (Exception e) {
            }
        } else if (permission.result == 1) {
            if (!init) initializeCamera(camType);
        }

        super.onResume();
        if (!Utiilties.isGPSEnabled(CameraActivity.this)){
            Utiilties.displayPromptForEnablingGPS(CameraActivity.this);
        }
        //initializeCamera(camType);
    }


    private Camera mCamera;
    private CameraPreview mPreview;

    LocationManager mlocManager = null;

    AlertDialog.Builder alert;


    private final int UPDATE_ADDRESS = 1;
    private final int UPDATE_LATLNG = 2;
    private static final String TAG = "MyActivity";
    private static byte[] CompressedImageByteArray;
    private static Bitmap CompressedImage;
    private boolean isTimerStarted = false;
    Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        takePhoto = (Button) findViewById(R.id.btnCapture);
        btnCamType = (Button) findViewById(R.id.btnCamType);
        progress_finding_location = (ProgressBar) findViewById(R.id.progress_finding_location);

        if (Utiilties.isfrontCameraAvalable() && getIntent().getStringExtra("KEY_PIC").equals("2")) {
            camType = CameraInfo.CAMERA_FACING_FRONT;
        } else {
            camType = CameraInfo.CAMERA_FACING_BACK;
        }
        preview = (FrameLayout) findViewById(R.id.camera_preview);


        takePhoto.setEnabled(false);


        btnCamType.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mCamera != null) {
                    mCamera.stopPreview();


                }


                if (camType == CameraInfo.CAMERA_FACING_BACK) {
                    camType = CameraInfo.CAMERA_FACING_FRONT;

                } else {
                    camType = CameraInfo.CAMERA_FACING_BACK;

                }

                preview.removeAllViews();

                initializeCamera(camType);


            }

        });

    }

    private void locationManager() {
        if (GlobalVariables.glocation != null && GlobalVariables.glocation.getAccuracy()< 250 && GlobalVariables.glocation.getAccuracy() > 0) {
            updateUILocation(GlobalVariables.glocation);
            takePhoto.setEnabled(true);
            progress_finding_location.setVisibility(View.GONE);
            takePhoto.setText(" Take Photo ");
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            takePhoto.setEnabled(false);
            mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, (float) 0.01, mlistener);
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, (float) 0.01, mlistener);
        }
    }

    private void initializeCamera(int camType) {

        init = true;
        chronometer = (Chronometer) findViewById(R.id.chronometer1);
        isTimerStarted = false;

        mCamera = getCameraInstance(camType);
        Camera.Parameters cp = mCamera.getParameters(); // get the current params
        if (mCamera != null) {
            mCamera.setParameters(cp);
            //param = mCamera.getParameters();
            List<Size> sizes = cp.getSupportedPictureSizes();
            int iTarget = 0;
            for (int i = 0; i < sizes.size(); i++) {
                Size size = sizes.get(i);
            /*if (size.width < 1000) {
				iTarget = i;
				break;
			}*/


                if (size.width >= 1024 && size.width <= 1280) {
                    iTarget = i;
                    break;
                } else {
                    if (size.width < 1024) {
                        iTarget = i;

                    }
                }

            }
            cp.setJpegQuality(100);
            cp.setPictureSize(sizes.get(iTarget).width,
                    sizes.get(iTarget).height);
            mCamera.setParameters(cp);
            alert = new AlertDialog.Builder(this);
            Display getOrient = getWindowManager().getDefaultDisplay();
            int rotation = getOrient.getRotation();
            switch (rotation) {
                case Surface.ROTATION_0:
                    mCamera.setDisplayOrientation(90);
                    break;
                case Surface.ROTATION_90:

                    break;
                case Surface.ROTATION_180:
                    mCamera.setDisplayOrientation(0);

                    break;
                case Surface.ROTATION_270:
                    mCamera.setDisplayOrientation(90);
                    break;
                default:
                    break;
            }
            try {
                mPreview = new CameraPreview(this, mCamera);
                preview.addView(mPreview);
            } catch (Exception e) {
                //Utiilties.writeIntoLog(Log.getStackTraceString(e));
                e.printStackTrace();
                finish();
            }
            locationManager();
        }

    }



    public void startTimer() {

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        isTimerStarted = true;

    }


    public static Camera getCameraInstance(int cameraType) {
        // Camera c = null;
        try {

            int numberOfCameras = Camera.getNumberOfCameras();
            int cameraId = 0;
            for (int i = 0; i < numberOfCameras; i++) {
                CameraInfo info = new CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == cameraType) {
                    // Log.d(DEBUG_TAG, "Camera found");
                    cameraId = i;
                    break;

                }
            }

            return Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
    }

    PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {

                Log.e("pic callback", "Yes");
                Log.d(TAG, "Start");
                Bitmap bmp = BitmapFactory
                        .decodeByteArray(data, 0, data.length);

                Matrix mat = new Matrix();
                if (camType == CameraInfo.CAMERA_FACING_FRONT) {
                    mat.postRotate(-90);

                } else mat.postRotate(90);

                Bitmap bMapRotate = Bitmap.createBitmap(bmp, 0, 0,
                        bmp.getWidth(), bmp.getHeight(), mat, true);
                //changing
                Bitmap bmapBitmap2 = bMapRotate;
                Date d = new Date(GlobalVariables.glocation.getTime());
                String dat = d.toLocaleString();
                /*Bitmap bitmapAfterAddText = Utiilties.DrawText(CameraActivity.this, bmapBitmap2, "Lat:" + Double.toString(GlobalVariables.glocation.getLatitude()), "Long:" + Double.toString(GlobalVariables.glocation.getLongitude())
                        , "Accurecy:" + Float.toString(GlobalVariables.glocation.getAccuracy()), "GpsTime:" + dat);*/

                Bitmap bitmapAfterAddText = Utiilties.DrawText(CameraActivity.this, bmapBitmap2,"","","","");
                Bitmap bitmapsp= BitmapFactory.decodeResource(CameraActivity.this.getResources(), R.drawable.ic_launcher);
                //Bitmap bitmapfinal=Utiilties.overlay(bitmapAfterAddText,bitmapsp);
                setCameraImage(Utiilties.GenerateThumbnail(bitmapAfterAddText, 500, 500));
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
               // Utiilties.writeIntoLog(Log.getStackTraceString(ex));
            }
        }
    };

    ShutterCallback shutterCallback = new ShutterCallback() {
        @Override
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };
    /**
     * Handles data for raw picture
     */
    PictureCallback rawCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    public void onCaptureClick(View view) {
        // System.gc();

        if (mCamera != null)
            mCamera.takePicture(shutterCallback, rawCallback, mPicture);

        Log.e("pic taken", "Yes");

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        // mCamera.takePicture(null, null, mPicture);

    }

    private void setCameraImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byte_arr = stream.toByteArray();
        CompressedImageByteArray = byte_arr;
        bitmap.recycle();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("CapturedImage", CompressedImageByteArray);
        returnIntent.putExtra("Lat", new DecimalFormat("#.0000000")
                .format(GlobalVariables.glocation.getLatitude()));
        returnIntent.putExtra("Lng", new DecimalFormat("#.0000000")
                .format(GlobalVariables.glocation.getLongitude()));
        try {
            returnIntent.putExtra("CapturedTime", Utiilties.getCurrentDateWithTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date d = new Date(GlobalVariables.glocation.getTime());
        String dat = d.toLocaleString();
        returnIntent.putExtra("GPSTime", dat);
        returnIntent.putExtra("KEY_PIC",Integer.parseInt(getIntent().getStringExtra("KEY_PIC")));
        // returnIntent.putExtra("ss", 0);
        setResult(RESULT_OK, returnIntent);
        Log.e("Set camera image", "Yes");
        finish();

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

    }


    private boolean checkLocationPermission() {
        if (!hasLocationPermission()) {
            Log.e("Tuts+", "Does not have location permission granted");
            requestLocationPermission();
            return false;
        }
        return true;
    }
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
    private final static int REQUEST_PERMISSION_RESULT_CODE = 42;
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                CameraActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSION_RESULT_CODE);
    }
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case UPDATE_ADDRESS:
                case UPDATE_LATLNG:
                    String[] LatLon = ((String) msg.obj).split("-");
                    TextView tv_Lat = (TextView) findViewById(R.id.tvLat);
                    TextView tv_Lon = (TextView) findViewById(R.id.tvLon);
                    TextView tvAcuracy = (TextView) findViewById(R.id.tvAcuracy);

                    tv_Lat.setText("" + LatLon[0]);
                    tv_Lon.setText("" + LatLon[1]);
                    tvAcuracy.setText("" +LatLon[2]);
                    Log.e("loglatlang", "Lat-Long" + LatLon[0] + "   " + LatLon[1]);

                    if (!isTimerStarted) {
                        startTimer();
                    }

                    break;
            }
        }
    };
    private void updateUILocation(Location location) {

        Message.obtain(
                mHandler,
                UPDATE_LATLNG,
                new DecimalFormat("#.0000000").format(location.getLatitude())
                        + "-"
                        + new DecimalFormat("#.0000000").format(location
                        .getLongitude()) + "-" + location.getAccuracy() + "-" + location.getTime())
                .sendToTarget();

    }

    private final LocationListener mlistener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

                GlobalVariables.glocation = location;
                updateUILocation(GlobalVariables.glocation);
                    if (location.getLatitude() > 0.0) {
                        if (GlobalVariables.glocation.getAccuracy() > 0 && GlobalVariables.glocation.getAccuracy() < 250) {
                            takePhoto.setText(" Take Photo ");
                            progress_finding_location.setVisibility(View.GONE);
                            takePhoto.setEnabled(true);
                        } else {

                            takePhoto.setText(" Wait for GPS to Stable ");
                            progress_finding_location.setVisibility(View.VISIBLE);
                            takePhoto.setEnabled(false);

                        }

                    }


        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    };


}
