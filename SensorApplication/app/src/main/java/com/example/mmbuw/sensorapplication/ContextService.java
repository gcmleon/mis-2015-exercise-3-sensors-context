package com.example.mmbuw.sensorapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Arrays;

import static android.util.FloatMath.sqrt;

public class ContextService extends Service implements SensorEventListener {

    private SensorManager mySensorManager;
    private Sensor accelerometer;

    FFT mFFT;
    private  double[] x, y;
    float [] FFT_magnitude;
    private int it_x;
    private final int n = 32;
    public static final int NOTIFICATION_ID = 1;
    private int sitting = 0;
    private int sitting_max = 15;
    private int walking = 1;
    private int walking_max = 40;
    private int running = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        mySensorManager = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);

        accelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Sensor.TYPE_LINEAR_ACCELERATION = excluding gravity

        // delay in microseconds
        mySensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        initializeFFT(n);
    }

    public ContextService() {
        System.out.println("Background service started.");

    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");

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

            // saving current magnitude
            x[it_x] =  magnitude;
            it_x += 1;

            if (it_x >= x.length)
            {
                mFFT.fft(x, y);
                FFT_magnitude = calculateFFT_absolute(x, y, x.length);
                it_x = 0;

                recognizeActivity();
            }

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void initializeFFT (int n){

        double [] x_initial =  new double[n];
        double [] y_initial = new double[n];
        mFFT = new FFT(n);
        FFT_magnitude = new float[n];

        //initial input arrays
        Arrays.fill(x_initial, 0);
        Arrays.fill(y_initial, 0);
        Arrays.fill(FFT_magnitude, 0);

        x = x_initial;
        y = y_initial;
        it_x = 0;
    }

    // calculating absolute magnitude
    public float[] calculateFFT_absolute(double x[], double y[], int n){

        float [] absoluteFFT;

        absoluteFFT = new float[n];

        for (int i=0; i<n; i++){

            absoluteFFT[i] = (float) Math.sqrt((Math.pow(x[i], 2) + Math.pow(y[i], 2)));
        }

        System.out.println("calculated the absolute magnitude");
        if (absoluteFFT == null) {
            System.out.println("absoluteFFT is null");
        }

        return absoluteFFT;
    }

    public void contextNotification(int activityRecognized) {

        String message = "doing a not recognized activity";

        Intent nIntent = new Intent(getApplicationContext(), this.getClass());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, nIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true); // notification disappears when it is clicked
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Activity Recognition");

        if (activityRecognized == sitting) {
            message = "sitting";
        } else if (activityRecognized == walking) {
            message = "walking";
        }else if (activityRecognized == running) {
            message = "running";
        }

        builder.setContentText("The user is currently " + message);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());

    }

    public void recognizeActivity() {

        int contextActivity = 0;

        float[] magnitude_copy = Arrays.copyOfRange(FFT_magnitude, 0, n);

        Arrays.sort(magnitude_copy);
        float min_value = magnitude_copy[0];
        float max_value = magnitude_copy[n-1];
        int range = (int) (max_value - min_value);
        float sum = 0;

        for (float d : magnitude_copy)
            sum += Math.abs(d);

        double averageRange = sum / magnitude_copy.length;

        /* Toasting
        String toasting = "Average is " + averageRange;
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toasty = Toast.makeText(context, toasting, duration);
        toasty.show();
        */

        if (averageRange <= sitting_max) {
            contextActivity = sitting;
        } else if (averageRange <= walking_max) {
            contextActivity = walking;
        } else {
            contextActivity = running;
        }

        contextNotification(contextActivity);
    }

}
