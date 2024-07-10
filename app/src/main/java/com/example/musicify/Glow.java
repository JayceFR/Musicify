package com.example.musicify;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class Glow extends View {

    public Paint paint;

    public Glow(Context context) {
        super(context);
        init();
    }

    public Glow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Glow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);
        paint.setMaskFilter(new BlurMaskFilter(40, BlurMaskFilter.Blur.NORMAL));
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 100, paint);
    }
}
