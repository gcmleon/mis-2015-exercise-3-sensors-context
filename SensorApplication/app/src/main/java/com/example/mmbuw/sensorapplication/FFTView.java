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
 * Custom view to show  FFT magnitude plotted
 */
public class FFTView extends LinearLayout {


    private Drawable mExampleDrawable;
    private Paint paint;
    private int mColor = Color.WHITE;
    FFT mFFT;
    private  double[] magnitudeOverTime;
    private  double[] x, y;
    double [] FFT_magnitude;
    private int it_x;
    private int [] fft_m_values;
    private float magnitude, current_magnitude;
    private int counter = 0;
    private boolean widthSet = false;



    public FFTView(Context context, AttributeSet attrs) {
        super(context, attrs);


        // holds max number 2^n current of magnitude values
        magnitudeOverTime = new double[4096];

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
        if (counter <24 ) {
            counter ++;
            magnitudeOverTime[counter]= current_magnitude;
        }
        else{
            counter = 0;
        }
    }


   /* public  void updateFFT_n(int progress) {


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

    } */


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!widthSet){
            it_x =1;
            fft_m_values = new int[w];
            widthSet=true;
        }
    }

    public void updateMagnitude(float magnitude) {

        x[it_x]=  magnitude;
        mFFT.fft(x,y);
        FFT_magnitude = calculateFFT_absolute(x,y,x.length);
        this.invalidate();
        //increment it_x after draw

    }

    public double[] calculateFFT_absolute (double x[], double y[], int n){
        double [] absoluteFFT;

        absoluteFFT = new double[n];
        for (int i=0; i<n; i++){

            absoluteFFT[i] = Math.sqrt((Math.pow(x[i],2) + Math.pow(y[i],2)));
        }

        return absoluteFFT;
    }

    public void initialize (int n){
        double [] x_initial =  new double[n];
        double []  y_initial = new double[n];
        mFFT = new FFT(n);
        //initial input arrays
        for (int i = 0; i < n; i++){
            y_initial[i] = 0;
            x_initial[i] = 0;
        }

       x = x_initial;
       y = y_initial;

    }

    // Part of class implemented by Ankit Srivastava - <URL>?
    // pixels = contentHeight
    // min = minimum value possible (could be negative)
    // max = maximum value possible (could be negative)
    // value = to be converted

    private int toPixelIntFFT(float pixels, float min, float max, float value) {
        double p;
        int pint;
        p = .1 * pixels + ((value-min)/(max-min)) * .8 * pixels;
        pint = (int) p;
        return (pint);
    }
}
