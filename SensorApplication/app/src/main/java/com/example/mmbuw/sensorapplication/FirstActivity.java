package com.example.mmbuw.sensorapplication;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import static android.util.FloatMath.sqrt;

public class FirstActivity extends Activity implements SensorEventListener {

    AccelerometerView accelerometerView;
    private SensorManager mySensorManager;
    private  Sensor Accelerometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        mySensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Accelerometer=mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, Accelerometer,SensorManager.SENSOR_DELAY_NORMAL);

        accelerometerView = (AccelerometerView) findViewById(R.id.accelerometer);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        float x_axis, y_axis, z_axis, magnitude;

        // http://developer.android.com/reference/android/hardware/SensorEvent.html#values
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // m/s^2 units
            x_axis = event.values[0]; // (all including gravity)
            y_axis = event.values[1];
            z_axis = event.values[2];
            magnitude = sqrt(x_axis * x_axis + y_axis * y_axis + z_axis * z_axis);
            System.out.println("Magnitude: " + magnitude);

            // http://examples.javacodegeeks.com/android/core/hardware/sensor/android-accelerometer-example/#code

            // add to view!
            accelerometerView.updateValues(x_axis, y_axis, z_axis);


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
