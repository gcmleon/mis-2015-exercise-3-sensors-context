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

import static android.util.FloatMath.sqrt;

/**
 * Custom view to show live accelerometer data
 */
public class AccelerometerView extends LinearLayout {

    private Paint paint;
    private int xColor = Color.RED;
    private int yColor = Color.GREEN;
    private int zColor = Color.BLUE;
    private int mColor = Color.WHITE;
    private int[] acc_x_values, acc_y_values, acc_z_values, acc_m_values;
    private int it_values;
    private float x_value, y_value, z_value;
    private float prev_x_value, prev_y_value, prev_z_value;
    private float magnitude, prev_magnitude;
    private float min_value_axis, max_value_axis; // Y-axis in plot
    private int current_pixel, next_pixel;
    private int paddingLeft, paddingTop, paddingRight, paddingBottom;
    private int contentWidth, contentHeight;
    private boolean widthSet = false;

    // View is built from XML file
    public AccelerometerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        x_value = 0.0f;
        y_value = 0.0f;
        z_value = 0.0f;
        magnitude = 0.0f;

        min_value_axis = (float) -16.0;
        max_value_axis = (float) 16.0;

        paint = new Paint();

        current_pixel = paddingLeft;
        next_pixel = current_pixel + 1;

        this.setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (!widthSet) {
            acc_x_values = new int[w];
            acc_y_values = new int[w];
            acc_z_values = new int[w];
            acc_m_values = new int[w];
            it_values = 0;
            widthSet = true;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();

        contentWidth = getWidth() - paddingLeft - paddingRight;
        contentHeight = getHeight() - paddingTop - paddingBottom;

        // If it arrives to the right border, it continues from the left border
        if (next_pixel >= contentWidth) {
            current_pixel = contentWidth;
            next_pixel = paddingLeft;
        } else {
            current_pixel += 1;
            next_pixel = current_pixel + 1;
        }

        int prev_x_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, prev_x_value);
        int prev_y_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, prev_y_value);
        int prev_z_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, prev_z_value);
        int prev_m_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, prev_magnitude);

        acc_x_values[it_values] = prev_x_pixels;
        acc_y_values[it_values] = prev_y_pixels;
        acc_z_values[it_values] = prev_z_pixels;
        acc_m_values[it_values] = prev_m_pixels;

        int x_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, x_value);
        int y_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, y_value);
        int z_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, z_value);
        int m_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, magnitude);

        acc_x_values[it_values + 1] = x_pixels;
        acc_y_values[it_values + 1] = y_pixels;
        acc_z_values[it_values + 1] = z_pixels;
        acc_m_values[it_values + 1] = m_pixels;

        // Plot
        paint.setStrokeWidth(2.0f);
        canvas.drawARGB(255, 0, 0, 0);

        // Drawing plot axes
        paint.setColor(Color.YELLOW);
        // Magnitude in Y axis
        canvas.drawLine(paddingLeft + 1, paddingTop, paddingLeft + 1, contentHeight, paint);
        // Time in X axis
        canvas.drawLine(paddingLeft, paddingTop, contentWidth, paddingTop, paint);

        // Drawing x-axis acceleration
        paint.setColor(xColor); // 0.0

        // if there's "out of bounds", i < it_values + 1
        for (int i = 0; i < it_values + 1; i ++) {
            canvas.drawLine(i, contentHeight - acc_x_values[i],
                    i + 1, contentHeight - acc_x_values[i + 1], paint);
        }

        // Y-axis acceleration
        paint.setColor(yColor); // 9.77622

        for (int i = 0; i < it_values + 1; i ++) {
            canvas.drawLine(i, contentHeight - acc_y_values[i],
                    i + 1, contentHeight - acc_y_values[i + 1], paint);
        }

        // Z-axis acceleration
        paint.setColor(zColor); // 0.81

        for (int i = 0; i < it_values + 1; i ++) {
            canvas.drawLine(i, contentHeight - acc_z_values[i],
                    i + 1, contentHeight - acc_z_values[i + 1], paint);
        }

        // Magnitude
        paint.setColor(mColor);

        for (int i = 0; i < it_values + 1; i ++) {
            canvas.drawLine(i, contentHeight - acc_m_values[i],
                    i + 1, contentHeight - acc_m_values[i + 1], paint);
        }

    }

    // Called by the activity, it updates the accelerometer values used by the view to draw
    public void updateValues(float x_axis, float y_axis, float z_axis) {

        if (!widthSet) {
            return;
        }

        // Updating index according to view bounds
        if (it_values < acc_x_values.length - 2)
            it_values += 1;
        else
            it_values = 0;

        // the previous values are updated as such
        setPrev_values(x_value, y_value, z_value, magnitude);

        // saving the new values
        x_value = x_axis;
        y_value = y_axis;
        z_value = z_axis;
        magnitude = sqrt(x_value * x_value + y_value * y_value + z_value * z_value);

        this.invalidate();

    }

    // Updating previous values to draw the lines to prev -> current
    protected void setPrev_values(float prev_x, float prev_y, float prev_z, float prev_m) {

        prev_x_value = prev_x;
        prev_y_value = prev_y;
        prev_z_value = prev_z;
        prev_magnitude = prev_m;
    }

    /* Part of class implemented by Ankit Srivastava
     http://www.ankitsrivastava.net/2012/03/a-simple-2d-plot-class-for-android/
     pixels = contentHeight
     min = minimum value possible (could be negative)
     max = maximum value possible (could be negative)
     value = to be converted */
    private int toPixelInt(float pixels, float min, float max, float value) {
        double p;
        int pint;
        p = .1 * pixels + ((value-min)/(max-min)) * .8 * pixels;
        pint = (int) p;
        return (pint);
    }

}