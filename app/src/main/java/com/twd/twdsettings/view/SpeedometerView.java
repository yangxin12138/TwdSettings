package com.twd.twdsettings.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class SpeedometerView extends View {

    private float speed; //实时速度
    private Paint speedometerPaint;
    private Paint speedTextPaint;
    public SpeedometerView(Context context) {
        super(context);
        init();
    }

    public SpeedometerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeedometerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        speedometerPaint = new Paint();
        speedometerPaint.setColor(Color.RED);
        speedometerPaint.setStrokeWidth(10f);
        speedometerPaint.setStyle(Paint.Style.STROKE);
        speedometerPaint.setAntiAlias(true);

        speedTextPaint = new Paint();
        speedTextPaint.setColor(Color.BLACK);
        speedTextPaint.setTextSize(40f);
        speedTextPaint.setTextAlign(Paint.Align.CENTER);
        speedTextPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        float centerY = width / 2f;
        float centerX = height / 2f;
        float radius = Math.min(width,height) /2f;

        // 绘制仪表盘圆弧
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius,
                centerY + radius, 150f, 240f, false, speedometerPaint);

        // 绘制速度文本
        canvas.drawText(String.valueOf(speed), centerX, centerY, speedTextPaint);
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        invalidate(); // 通知View进行重绘
    }
}
