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
    private SensorManager mySensorManager;
    private  Sensor Accelerometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        mySensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Accelerometer=mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, Accelerometer,SensorManager.SENSOR_DELAY_NORMAL);

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

        Sensor mySensor =event.sensor;
        if (mySensor.getType()==Sensor.TYPE_ACCELEROMETER){
        
        float X_Direction = event.values[0];
        float Y_Direction = event.values[1];
        float Z_Direction = event.values[2];
        float Magnitude = sqrt(X_Direction * X_Direction + Y_Direction * Y_Direction + Z_Direction * Z_Direction);
        System.out.print("Magnitude:");
        System.out.println(Magnitude);}

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
