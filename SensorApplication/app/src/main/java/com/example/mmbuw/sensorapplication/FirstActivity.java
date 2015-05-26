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
import android.widget.SeekBar;

import static android.util.FloatMath.sqrt;

public class FirstActivity extends Activity implements SensorEventListener {

    private SensorManager mySensorManager;
    private Sensor accelerometer;

    AccelerometerView accelerometerView;
    FFTView fftView;
    SeekBar seekBarRate;
    SeekBar seekBarFFT_n;



    int defaultRate = 200000; // SENSOR_DELAY_NORMAL


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Sensor.TYPE_LINEAR_ACCELERATION, excluding gravity

        // delay in microseconds
        mySensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        accelerometerView = (AccelerometerView) findViewById(R.id.accelerometer);
        fftView = (FFTView) findViewById(R.id.fft);
        seekBarRate = (SeekBar) findViewById(R.id.seekBar);
        seekBarFFT_n = (SeekBar) findViewById(R.id.seekBarFFT);
        seekBarRate.setMax(defaultRate);
        seekBarFFT_n.setMax(defaultRate);
        seekBarRate.setProgress(defaultRate);
        seekBarFFT_n.setProgress(32);

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

        seekBarFFT_n.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int n;
                if (progress<5) {

                    seekBar.setProgress(5);
                    n = ((int) Math.pow(2, progress));
                }
                else { n = ((int) Math.pow(2, progress));}
                System.out.println("FFT window size progress changed to: " + n);
                FFTView.updateFFT_n(n);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //accelerometerView.invalidate();        // not necessary?

        //  For task 3: http://developer.android.com/samples/wearable.html
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
            // http://examples.javacodegeeks.com/android/core/hardware/sensor/android-accelerometer-example/#code
            // add to view!
            accelerometerView.updateValues(x_axis, y_axis, z_axis);
            fftView.updateMagnitude(magnitude);
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
