package com.example.mmbuw.hellosensors;

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
 * Custom view to show live accelerometer data
 */
public class AccelerometerView extends LinearLayout {

    private Drawable mExampleDrawable;
    private int mExampleColor = Color.RED;

    // View is built from XML file
    public AccelerometerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        int x = 20;
        int y = 20;
        int width = 300;
        int height = 50;

        mExampleDrawable = new ShapeDrawable(new OvalShape());
        mExampleDrawable.setColorFilter(mExampleColor, PorterDuff.Mode.DARKEN);
        mExampleDrawable.setBounds(x,y, x + width, y + height);

        this.setWillNotDraw(false);

        System.out.println("This constructor is used");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Trying to draw a shape
        //mExampleDrawable.draw(canvas);
        System.out.println("I drew the oval shape!"); // I see this message but no oval

        // consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        Paint p = new Paint();
        p.setColor(mExampleColor);

        canvas.drawRect(paddingLeft, paddingTop,
                        paddingLeft + contentWidth,
                        paddingTop + contentHeight,
                        p);

        System.out.println("I drew the rectangle!");

        // Draw the example drawable on top of the text.
        /*if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }*/
    }

}
