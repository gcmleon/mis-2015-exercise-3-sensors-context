package com.example.mmbuw.hellosensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.SeekBar;

import static android.util.FloatMath.sqrt;


public class SensorActivity extends Activity implements SensorEventListener {

    private SensorManager mySensorManager;
    private Sensor accelerometer;

    AccelerometerView accelerometerView;
    FFTView fftView;
    SeekBar seekBarRate;

    int defaultRate = 200000; // SENSOR_DELAY_NORMAL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sensor);

        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Sensor.TYPE_LINEAR_ACCELERATION, excluding gravity

        // delay in microseconds
        mySensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        accelerometerView = (AccelerometerView) findViewById(R.id.accelerometer);

        fftView = (FFTView) findViewById(R.id.fft);

        seekBarRate = (SeekBar) findViewById(R.id.seekBar);
        seekBarRate.setMax(defaultRate);
        seekBarRate.setProgress(defaultRate);

        seekBarRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println("SeekBar progress changed to: " + progress);
                changeSampleRate(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // not necessary?
        //accelerometerView.invalidate();

        //  For task 3: http://developer.android.com/samples/wearable.html
    }

    @Override
    protected void onResume() {
        super.onResume();
        mySensorManager.registerListener(this, accelerometer, seekBarRate.getProgress());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mySensorManager.unregisterListener(this, accelerometer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor, menu);
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
        float x_axis, y_axis, z_axis;

        // http://developer.android.com/reference/android/hardware/SensorEvent.html#values
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // m/s^2 units
            x_axis = event.values[0]; // (all including gravity)
            y_axis = event.values[1];
            z_axis = event.values[2];
            // http://examples.javacodegeeks.com/android/core/hardware/sensor/android-accelerometer-example/#code
            // add to view!
            accelerometerView.updateValues(x_axis, y_axis, z_axis);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /* It modifies the sample rate of the accelerometer sensor
       We are considering the max value as SENSOR_DELAY_NORMAL

       As a guide, the typical possible delays are:
            SENSOR_DELAY_NORMAL -> 200,000 microseconds
            SENSOR_DELAY_UI -> 60,000 microseconds
            SENSOR_DELAY_GAME -> 20,000 microseconds
            SENSOR_DELAY_FASTEST -> 0 microseconds
     */
    public void changeSampleRate(int microseconds){

        mySensorManager.unregisterListener(this, accelerometer);
        mySensorManager.registerListener(this, accelerometer, microseconds);
    }

}
