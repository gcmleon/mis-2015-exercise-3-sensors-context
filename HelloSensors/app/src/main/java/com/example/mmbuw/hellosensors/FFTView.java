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
 * TODO: document your custom view class.
 */
public class FFTView extends LinearLayout {

    private Drawable mExampleDrawable;
    private Paint paint;
    private int mColor = Color.WHITE;

    //FFT variables
    private float magnitude, prev_magnitude;




    public FFTView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int x = 20;
        int y = 20;
        int width = 150;
        int height = 25;

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


    public void updateMagnitude(float magnitude, int n) {

        this.magnitude = magnitude;

        setPrev_magnitude(magnitude);




    }

    protected void setPrev_magnitude(float magnitude) {
        prev_magnitude = magnitude;
    }


}
