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

import java.util.Arrays;


/**
 * Custom view to show  FFT magnitude plotted
 */
public class FFTView extends LinearLayout {

    private Paint paint;
    private int mColor = Color.WHITE;
    FFT mFFT;
    private  double[] x, y;
    float [] FFT_magnitude; // magnitude array of FFT, used for plot
    private int it_x;
    private int [] fft_m_values;
    private int counter = 0;
    private boolean widthSet = false;
    private float min_value_axis, max_value_axis;
    private int contentHeight;


    public FFTView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // holds max number 2^n current of magnitude values
        min_value_axis = 0.0f;
        max_value_axis = 0.0f;
        paint = new Paint();
        this.setWillNotDraw(false);
        widthSet = false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!widthSet) return;

        int n = x.length;
        contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int[] pixel_values = new int[n];

        float[] magnitude_copy = Arrays.copyOfRange(FFT_magnitude, 0, n);

        Arrays.sort(magnitude_copy);
        float min_value = magnitude_copy[0];
        float max_value = magnitude_copy[n-1];
        int range = (int) (max_value - min_value);
        min_value_axis = 0;
        max_value_axis = range;

        // transforming our plot values to pixels
        for (int i = 0; i < n; i++) {
            pixel_values[i] = toPixelIntFFT(contentHeight, min_value_axis, max_value_axis, FFT_magnitude[i]);
        }

       // Plot
        paint.setStrokeWidth(2.0f);
        canvas.drawARGB(255, 255, 255, 255);

        paint.setColor(Color.BLACK);

        // drawing pixel by pixel
        for (int i = 1; i < n; i++) {
            canvas.drawLine(i, contentHeight - FFT_magnitude[i - 1],
                    i + 1, contentHeight - FFT_magnitude[i], paint);
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!widthSet){
            fft_m_values = new int[w];
            it_x = 0;
            widthSet = true;
        }
    }

    public void updateMagnitude(double magnitude) {

        if (!widthSet) {
            System.out.println("width is zero");
            return;
        }

        // saving current magnitude
        x[it_x] =  magnitude;
        it_x += 1;

        //x = magnitude;

        if (it_x >= x.length)
        {
            mFFT.fft(x, y);
            FFT_magnitude = calculateFFT_absolute(x, y, x.length);
            //initialize(x.length);
            this.invalidate();
            it_x = 0;
        }

    }

    // calculating absolute magnitude
    public float[] calculateFFT_absolute(double x[], double y[], int n){

        float [] absoluteFFT;

        absoluteFFT = new float[n];

        for (int i=0; i<n; i++){

            absoluteFFT[i] = (float) Math.sqrt((Math.pow(x[i], 2) + Math.pow(y[i], 2)));
        }

        if (absoluteFFT == null) {
            System.out.println("absoluteFFT is null");
        }

        return absoluteFFT;
    }

    public void initialize (int n){

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
        System.out.println("initialized");

    }

    /* Part of class implemented by Ankit Srivastava - <URL>?
     pixels = contentHeight
     min = minimum value possible (could be negative)
     max = maximum value possible (could be negative)
     value = to be converted */

    private int toPixelIntFFT(float pixels, float min, float max, float value) {
        double p;
        int pint;
        p = .1 * pixels + ((value-min)/(max-min)) * .8 * pixels;
        pint = (int) p;
        return (pint);
    }
}
