package com.twd.twdsettings.projection.keystone;

import android.content.Context;

public class keystoneOnePoint extends keystone{

    private static final String TAG = "keystoneOnePoint";
    protected static Context mContext;

    protected static final int _mStepX = 4;
    protected static final int _mStepY = 3;
    protected static final int maxXStep = 50;
    protected static final int maxYStep = 50;

    public keystoneOnePoint(Context context){
        super(context);
        mContext = context;
        init();
    }

    public void init(){
        vTopLeft.setMaxX(maxXStep);vTopLeft.setMaxY(maxYStep);
        vTopRight.setMaxX(maxXStep);vTopRight.setMaxY(maxYStep);

        vBottomLeft.setMaxX(maxXStep);vBottomLeft.setMaxY(maxYStep);
        vBottomRight.setMaxX(maxXStep);vBottomRight.setMaxY(maxYStep);
        mStepX = _mStepX;
        mStepY = _mStepY;

    }

    public String getOnePointInfo(int point){
        switch (point){
            case 0:
                return vTopLeft.toString();
            case 1:
                return vTopRight.toString();
            case 3:
                return vBottomLeft.toString();
            case 2:
                return vBottomRight.toString();
            default:
                break;
        }
        return null;
    }

    public void oneLeft(int point){
        switch (point){
            case 0:
                vTopLeft.doLeft();
                break;
            case 1:
                vTopRight.doLeft();
                break;
            case 3:
                vBottomLeft.doLeft();
                break;
            case 2:
                vBottomRight.doLeft();
                break;
            default:
                break;
        }
        updatePoint(point);
        savePoint(point);
        update();
    }

    public void oneRight(int point){
        switch (point){
            case 0:
                vTopLeft.doRight();
                break;
            case 1:
                vTopRight.doRight();
                break;
            case 3:
                vBottomLeft.doRight();
                break;
            case 2:
                vBottomRight.doRight();
                break;
            default:
                break;
        }
        updatePoint(point);
        savePoint(point);
        update();
    }

    public void oneTop(int point){
        switch (point){
            case 0:
                vTopLeft.doTop();
                break;
            case 1:
                vTopRight.doTop();
                break;
            case 3:
                vBottomLeft.doTop();
                break;
            case 2:
                vBottomRight.doTop();
                break;
            default:
                break;
        }
        updatePoint(point);
        savePoint(point);
        update();
    }

    public void oneBottom(int point){
        switch (point){
            case 0:
                vTopLeft.doBottom();
                break;
            case 1:
                vTopRight.doBottom();
                break;
            case 3:
                vBottomLeft.doBottom();
                break;
            case 2:
                vBottomRight.doBottom();
                break;
            default:
                break;
        }
        updatePoint(point);
        savePoint(point);
        update();
    }

    public void setZoom(int progress){
        //SharedPreferences prefs = mContext.getSharedPreferences("ty_zoom",Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = prefs.edit();
        saveZoom(progress);

        updatePoint(4);//update all
        update();
    }
}
