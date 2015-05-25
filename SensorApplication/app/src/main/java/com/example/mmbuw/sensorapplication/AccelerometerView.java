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
    private Drawable mExampleDrawable;
    private int xColor = Color.RED;
    private int yColor = Color.GREEN;
    private int zColor = Color.BLUE;
    private int mColor = Color.WHITE;
    private float[] acc_values = { 0.0f };
    private float x_value, y_value, z_value;
    private float prev_x_value, prev_y_value, prev_z_value;
    private float magnitude, prev_magnitude;
    private float min_value_axis, max_value_axis; // Y-axis in plot
    private int current_pixel, next_pixel, epsilon;
    private int paddingLeft, paddingTop, paddingRight, paddingBottom;
    private int contentWidth, contentHeight;

    // View is built from XML file
    public AccelerometerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        int x = 20;
        int y = 20;
        int width = 300;
        int height = 50;

        x_value = 0.0f;
        y_value = 0.0f;
        z_value = 0.0f;
        magnitude = 0.0f;

        min_value_axis = (float) -16.0;
        max_value_axis = (float) 16.0;

        paint = new Paint();

        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();

        current_pixel = paddingLeft;
        next_pixel = current_pixel + 1;

        contentWidth = getWidth() - paddingLeft - paddingRight;
        contentHeight = getHeight() - paddingTop - paddingBottom;

        mExampleDrawable = new ShapeDrawable(new OvalShape());
        mExampleDrawable.setColorFilter(xColor, PorterDuff.Mode.DARKEN);
        mExampleDrawable.setBounds(x, y, x + width, y + height);

        this.setWillNotDraw(false);
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

        int x_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, x_value);
        int y_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, y_value);
        int z_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, z_value);
        int m_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, magnitude);

        int prev_x_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, prev_x_value);
        int prev_y_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, prev_y_value);
        int prev_z_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, prev_z_value);
        int prev_m_pixels = toPixelInt(contentHeight, min_value_axis, max_value_axis, prev_magnitude);

        // Plot
        paint.setStrokeWidth(2.0f);
        canvas.drawARGB(255, 0, 0, 0);

        // Trying to draw a shape
        //mExampleDrawable.draw(canvas);
        //System.out.println("I drew the oval shape!"); // I see this message but no oval


        /*canvas.drawRect(paddingLeft, paddingTop,
                        paddingLeft + contentWidth,
                        paddingTop + contentHeight,
                        paint);*/
        //System.out.println("I drew the rectangle!");

        System.out.println("Width: " + getWidth());
        System.out.println("Height: " + getHeight());

        // Drawing plot axes
        paint.setColor(Color.YELLOW);
        // Magnitude in Y axis
        canvas.drawLine(paddingLeft + 1, paddingTop, paddingLeft + 1, contentHeight, paint);
        // Time in X axis
        canvas.drawLine(paddingLeft, paddingTop, contentWidth, paddingTop, paint);
        // canvas.drawLine(20, 0, 0, 20, paint); // test

        // Drawing x-axis acceleration
        paint.setColor(xColor); // 0.0
        System.out.println("Content height is " + contentHeight);

        // a difference of one pixel may be too small
        // it erases the previous lines, how to keep them?
        canvas.drawLine(current_pixel, contentHeight - prev_x_pixels, next_pixel, contentHeight - x_pixels, paint);

        // contentHeight/2 because i'm assuming that magnitude 0 is in the middle of the screen view
        //canvas.drawLine(paddingLeft, contentHeight/2 - prev_x_value, paddingLeft + contentWidth, contentHeight/2 - x_value, paint);
        //canvas.drawLine(paddingLeft, paddingTop + contentHeight/4, paddingLeft + contentWidth, paddingTop + contentHeight/4, paint);
        /*System.out.println("In red: (" + paddingLeft + ", " + (paddingTop + contentHeight/4)
                                    + ") to (" + (paddingLeft + contentWidth) + ", " + (paddingTop + contentHeight/4));*/

        // Y-axis acceleration
        paint.setColor(yColor); // 9.77622
        canvas.drawLine(paddingLeft, contentHeight - prev_y_pixels, paddingLeft + contentWidth, contentHeight - y_pixels, paint);
        //canvas.drawLine(paddingLeft, contentHeight/2 - prev_y_value, paddingLeft + contentWidth, contentHeight/2 - y_value, paint);
        //canvas.drawLine(paddingLeft, paddingTop + contentHeight/2, paddingLeft + contentWidth, paddingTop + contentHeight/2, paint);

        // Z-axis acceleration
        paint.setColor(zColor); // 0.81
        canvas.drawLine(paddingLeft, contentHeight - prev_z_pixels, paddingLeft + contentWidth, contentHeight - z_pixels, paint);
        //canvas.drawLine(paddingLeft, contentHeight/2 - prev_z_value, paddingLeft + contentWidth, contentHeight/2 - z_value, paint);
        //canvas.drawLine(paddingLeft, (float) (paddingTop + contentHeight*0.75), paddingLeft + contentWidth, (float) (paddingTop + contentHeight*0.75), paint);

        // Magnitude
        paint.setColor(mColor); // New Magnitude: 96.23613 - very high in comparison to the others!
        canvas.drawLine(paddingLeft, contentHeight - prev_m_pixels, paddingLeft + contentWidth, contentHeight - m_pixels, paint);
        //canvas.drawLine(paddingLeft, contentHeight/2 - prev_magnitude, paddingLeft + contentWidth, contentHeight/2 - magnitude, paint);

        //canvas.drawLines(new float[]{0.0f, 0.0f, getWidth(), getHeight()}, paint);
        canvas.drawLines(acc_values, paint);

        // Line color
        paint.setColor(xColor);

        //canvas.drawLines(acc_values, paint);

    }

    public void updateValues(float x_axis, float y_axis, float z_axis) {

        setPrev_values(x_value, y_value, z_value, magnitude);

        x_value = x_axis;
        y_value = y_axis;
        z_value = z_axis;
        magnitude = sqrt(x_value * x_value + y_value * y_value + z_value * z_value);

        /*System.out.println("New X: " + x_value);
        System.out.println("New Y: " + y_value);
        System.out.println("New Z: " + z_value);*/
        System.out.println("New Magnitude: " + magnitude);

        acc_values = new float[]{0.0f, contentHeight - prev_x_value,
                100.0f, contentHeight - x_value,
                0.0f, contentHeight - prev_y_value,
                100.0f, contentHeight - y_value,
                0.0f, contentHeight - prev_z_value,
                100.0f, contentHeight - z_value};

        System.out.println("First line, point_0 = (" + 0.0f + ", " + prev_x_value + ")");
        System.out.println("First line, point_1 = (" + 100.0f + ", " + x_value + ")");

        System.out.println("Second line, point_0 = (" + 0.0f + ", " + prev_y_value + ")");
        System.out.println("Second line, point_1 = (" + 100.0f + ", " + y_value + ")");

        System.out.println("Third line, point_0 = (" + 0.0f + ", " + prev_z_value + ")");
        System.out.println("Third line, point_1 = (" + 100.0f + ", " + z_value + ")");

        this.invalidate();

    }

    // Updating previous values to draw the lines to prev -> current
    protected void setPrev_values(float prev_x, float prev_y, float prev_z, float prev_m) {

        prev_x_value = prev_x;
        prev_y_value = prev_y;
        prev_z_value = prev_z;
        prev_magnitude = prev_m;
    }

    // Part of class implemented by Ankit Srivastava - <URL>?
    // pixels = contentHeight
    // min = minimum value possible (could be negative)
    // max = maximum value possible (could be negative)
    // value = to be converted
    private int toPixelInt(float pixels, float min, float max, float value) {

        double p;
        int pint;
        p = .1 * pixels + ((value-min)/(max-min)) * .8 * pixels;
        pint = (int) p;
        return (pint);
    }

}