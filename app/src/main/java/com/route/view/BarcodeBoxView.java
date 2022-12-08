package com.route.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BarcodeBoxView extends View {

    private Paint paint = new Paint();
    private RectF mRect = new RectF();

    public BarcodeBoxView(Context context) {
        super(context);
    }

    public BarcodeBoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BarcodeBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cornerRadius = 10f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5f);

        canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint);
    }

    public void setRect(RectF rect) {
        mRect = rect;
        invalidate();
        requestLayout();
    }
}
