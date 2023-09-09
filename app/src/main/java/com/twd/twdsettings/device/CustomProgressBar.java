package com.twd.twdsettings.device;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CustomProgressBar extends View {

    private static final  int LINE_HEIGHT_DP = 10;
    private static final  int LINE_WIDTH_DP = 500;

    private int systemColor = Color.BLUE;
    private int appColor = Color.YELLOW;
    private int otherColor = Color.BLACK;
    private int availableColor = Color.GRAY;

    private float systemRatio = 0.25f;
    private float appRatio = 0.25f;
    private float otherRatio = 0.25f;
    private float availableRatio = 0.25f;

    private Paint mPaint;
    public CustomProgressBar(Context context) {
        super(context);
        init();
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public void setRatios(float systemRatio, float appRatio, float otherRatio, float availableRatio) {
        this.systemRatio = systemRatio;
        this.appRatio = appRatio;
        this.otherRatio = otherRatio;
        this.availableRatio = availableRatio;
        invalidate(); // 重绘View
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // 计算线段的起始和结束位置
        float startX = 0;
        float startY = height / 2 - dpToPx(LINE_HEIGHT_DP) / 2;
        float endX = width;
        float endY = height / 2 + dpToPx(LINE_HEIGHT_DP) / 2;

        // 绘制系统占用部分
        mPaint.setColor(systemColor);
        RectF systemRect = new RectF(startX, startY, startX + width * systemRatio, endY);
        canvas.drawRect(systemRect, mPaint);
        startX += width * systemRatio;

        // 绘制app占用部分
        mPaint.setColor(appColor);
        RectF appRect = new RectF(startX, startY, startX + width * appRatio, endY);
        canvas.drawRect(appRect, mPaint);
        startX += width * appRatio;

        // 绘制其他占用部分
        mPaint.setColor(otherColor);
        RectF otherRect = new RectF(startX, startY, startX + width * otherRatio, endY);
        canvas.drawRect(otherRect, mPaint);
        startX += width * otherRatio;

        // 绘制可用容量部分
        mPaint.setColor(availableColor);
        RectF availableRect = new RectF(startX, startY, startX + width * availableRatio, endY);
        canvas.drawRect(availableRect, mPaint);
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
