package com.example.mmbuw.sensorapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.widget.LinearLayout;




/**
 * TODO: document your custom view class.
 */
public class FFTView extends LinearLayout {


    private Drawable mExampleDrawable;
    private Paint paint;
    private int mColor = Color.WHITE;

    private static double[] magnitudeOverTime;
    private float magnitude, current_magnitude;
    private int counter = 0;



    public FFTView(Context context, AttributeSet attrs) {
        super(context, attrs);


        // holds max number n^2 current of magnitude values
        magnitudeOverTime = new double[1024];

        int x = 20;
        int y = 20;
        int width = 150;
        int height = 25;
        paint = new Paint();
        mExampleDrawable = new ShapeDrawable(new OvalShape());
        mExampleDrawable.setColorFilter(mColor, PorterDuff.Mode.DARKEN);
        mExampleDrawable.setBounds(x, y, x + width, y + height);

        this.setWillNotDraw(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;



        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }



    protected void refreshMagnitudeArr (float magnitude) {

      current_magnitude = magnitude;
        if (counter < 240) {
            counter ++;
            magnitudeOverTime[counter]= current_magnitude;
        }
        else{
            counter = 0;
        }
    }


    public static void updateFFT_n(int progress) {


       // int n =  (int) Math.pow(progress, 2);
        int n = progress;
        double[] FFTmagnitude;
        System.out.print("FFT --->" +n);

        FFT mFFT = new FFT(n);
        FFTmagnitude = new double[n];
        double[] x_input, y_input;

        x_input = new double [n];
        y_input = new double[n];
        for (int i = 0; i < n; i++){
            y_input[i] = 0;
            x_input[i] = magnitudeOverTime[i]; //x_input either copy or sunset of magnitudeOverTimes
        }

        mFFT.fft(x_input,y_input);
       // FFTmagnitude = calculateFFT_absolute(x_input, y_input,n);

    }


    public void updateMagnitude(float magnitude) {
        this.magnitude = magnitude;
        refreshMagnitudeArr(magnitude);

    }

    public double[] calculateFFT_absolute (double x[], double y[], int n){
        double [] absoluteFFT;

        absoluteFFT = new double[n];
        for (int i=0; i<n; i++){

            absoluteFFT[i] = Math.sqrt((Math.pow(x[i],2) + Math.pow(y[i],2)));
        }

        return absoluteFFT;
    }
}
