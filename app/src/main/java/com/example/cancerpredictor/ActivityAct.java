package com.example.cancerpredictor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityAct extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {

    //private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private SensorManager sensorManager;
    Sensor accelerometer;
    Sensor gyroscope;
    Sensor magnetometer;
    int pothole_identifier = 0;
    int pothole_type = 0;
    boolean isPothole = false;

    Button little_pothole_button, big_pothole_button, deep_pothole_button, speed_bump_button;
    LocationManager lm;
    Location location;

    double latitude, longitude;

    // for file writing
    String fileName = new SimpleDateFormat("yyyyMMddHHmm'.csv'").format(new Date());
    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);
    FileOutputStream fos = null;
    //Read text from file
    StringBuilder text = new StringBuilder();
    TextView accel_x_tw, accel_y_tw, accel_z_tw, gyro_x_tw, gyro_y_tw, gyro_z_tw, milli_tw, latitude_tw, longitude_tw;
    double acc_X, acc_Y, acc_Z, gyro_x, gyro_y, gyro_z, mag_x, mag_y, mag_z, milli_;

    private Handler mHandler;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act);
        requestPermission();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            fos = new FileOutputStream(file);
            fos.write(("acc_X,acc_Y,acc_Z,gyro_X,gyro_Y,gyro_Z,mag_x, mag_y, mag_z,milli,latitude,longitude,counter,pothole type,label" + "\n").getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        accel_x_tw = findViewById(R.id.acc_x);
        accel_y_tw = findViewById(R.id.acc_y);
        accel_z_tw = findViewById(R.id.acc_z);
        gyro_x_tw = findViewById(R.id.gyro_x);
        gyro_y_tw = findViewById(R.id.gyro_y);
        gyro_z_tw = findViewById(R.id.gyro_z);
        milli_tw = findViewById(R.id.milli_);
        latitude_tw = findViewById(R.id.latitude);
        longitude_tw = findViewById(R.id.longitude);

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Request accelerometer values
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(ActivityAct.this, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);

        // Request gyroscope values
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(ActivityAct.this, gyroscope, sensorManager.SENSOR_DELAY_NORMAL);

        // Request magnetometer values
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(ActivityAct.this, magnetometer, sensorManager.SENSOR_DELAY_NORMAL);

        little_pothole_button = findViewById(R.id.little_pothole_bt);
        big_pothole_button = findViewById(R.id.big_pothole_bt);
        deep_pothole_button = findViewById(R.id.deep_pothole_bt);
        speed_bump_button = findViewById(R.id.speed_bump_bt);

        mHandler = new Handler();
        mHandler.postDelayed(mAction, 100);

        little_pothole_button.setOnTouchListener(new View.OnTouchListener() {

            //private Handler mHandler;


            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pothole_identifier++;
                        pothole_type = 1;
                        isPothole = true;
                        //if (mHandler != null) return true;
                        //mHandler = new Handler();
                        //mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        //if (mHandler == null) return true;
                        //mHandler.removeCallbacks(mAction);
                        pothole_type = 0;
                        isPothole = false;
                        //mHandler = null;
                        break;
                }
                return false;
            }
            /*
            Runnable mAction = new Runnable() {
                @Override public void run() {
                    System.out.println("Performing action...");
                    pothole_type = 1;
                    mHandler.postDelayed(this, 100);
                }
            };*/
        });

        big_pothole_button.setOnTouchListener(new View.OnTouchListener() {

            //private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pothole_identifier++;
                        pothole_type = 2;
                        isPothole = true;
                        //if (mHandler != null) return true;
                        //mHandler = new Handler();
                        //mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        //if (mHandler == null) return true;
                        //mHandler.removeCallbacks(mAction);
                        pothole_type = 0;
                        isPothole = false;
                        //mHandler = null;
                        break;
                }
                return false;
            }
/*
            Runnable mAction = new Runnable() {
                @Override public void run() {
                    System.out.println("Performing action...");
                    pothole_type = 2;
                    mHandler.postDelayed(this, 100);
                }
            };
*/
        });

        deep_pothole_button.setOnTouchListener(new View.OnTouchListener() {

            //private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pothole_identifier++;
                        pothole_type = 3;
                        isPothole = true;
                        //if (mHandler != null) return true;
                        //mHandler = new Handler();
                        //mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        //if (mHandler == null) return true;
                        //mHandler.removeCallbacks(mAction);
                        pothole_type = 0;
                        isPothole = false;
                        //mHandler = null;
                        break;
                }
                return false;
            }
            /*
            Runnable mAction = new Runnable() {
                @Override public void run() {
                    System.out.println("Performing action...");
                    pothole_type = 3;
                    mHandler.postDelayed(this, 100);
                }
            };*/

        });

        speed_bump_button.setOnTouchListener(new View.OnTouchListener() {

            //private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pothole_identifier++;
                        pothole_type = 4;
                        isPothole = true;
                        //if (mHandler != null) return true;
                        //mHandler = new Handler();
                        //mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        //if (mHandler == null) return true;
                        //mHandler.removeCallbacks(mAction);
                        pothole_type = 0;
                        isPothole = false;
                        //mHandler = null;
                        break;
                }
                return false;
            }

            /*Runnable mAction = new Runnable() {
                @Override public void run() {
                    System.out.println("Performing action...");
                    pothole_type = 4;
                    mHandler.postDelayed(this, 100);
                }
            };*/

        });
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        //mMapView = (MapView) findViewById(R.id.map_view);
        //mMapView.onCreate(mapViewBundle);
        //mMapView.getMapAsync(this);
    } 

    private static final int REQUEST_WRITE_PERMISSION = 786;
    private static final int REQUEST_FINE_LOCATION = 785;
    private static final int REQUEST_COARSE_LOCATION = 784;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acc_X = sensorEvent.values[0];
            acc_Y = sensorEvent.values[1];
            acc_Z = sensorEvent.values[2];

        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyro_x = sensorEvent.values[0];
            gyro_y = sensorEvent.values[1];
            gyro_z = sensorEvent.values[2];

        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            mag_x = sensorEvent.values[0];
            mag_y = sensorEvent.values[1];
        }
        milli_ = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mMapView.onResume();
    }

    @Override
    protected void onPause() {
        //mMapView.onDestroy();
        super.onPause();
    }

    Runnable mAction = new Runnable() {
        @Override public void run() {
            System.out.println("Performing action...");
            writeToCSV();
            mHandler.postDelayed(this, 100);
        }
    };

    public void writeToCSV() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
                requestPermission();
            return;
        }
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String littleTest = acc_X + ","
                            + acc_Y + ","
                            + acc_Z + ","
                            + gyro_x + ","
                            + gyro_y + ","
                            + gyro_z + ","
                            + mag_x + ","
                            + mag_y + ","
                            + mag_z + ","
                            + milli_ + ","
                            + latitude + ","
                            + longitude + "," +
                            + (isPothole?pothole_identifier:0.0) + "," +
                            + pothole_type + ","
                            + (isPothole?"Yes":"No") + "\n";;

        accel_x_tw.setText(Double.toString(acc_X));
        accel_y_tw.setText(Double.toString(acc_Y));
        accel_z_tw.setText(Double.toString(acc_Z));

        gyro_x_tw.setText(Double.toString(gyro_x));
        gyro_y_tw.setText(Double.toString(gyro_y));
        gyro_z_tw.setText(Double.toString(gyro_z));

        milli_tw.setText(Double.toString(milli_));

        latitude_tw.setText(Double.toString(latitude));
        longitude_tw.setText(Double.toString(longitude));

        try {
            fos.write(littleTest.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        @Override
    public void  onAccuracyChanged(Sensor sensor, int i){

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        //mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        //mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        //mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        map.addMarker((new MarkerOptions().position((new LatLng(0,0))).title("Test")));
    }

    @Override
    public void onDestroy() {
        //mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //mMapView.onLowMemory();
    }
}
