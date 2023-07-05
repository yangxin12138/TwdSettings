package com.twd.twdsettings.projection.keystone;

import android.content.Context;
import android.content.SharedPreferences;

public class keystoneTwoPoint extends keystone{

    private static final String TAG = "keystoneTwoPoint";
    protected static Context mContext;

    //protected static final float vZoomRange = (1/2);
    protected static final int _mStepX = 4;
    protected static final int _mStepY = 2;
    protected static final int maxXStep = 40;
    protected static final int maxYStep = 40;

    private static int zoom_x=0;
    private static int zoom_y=0;

    protected static SharedPreferences prefs;
    protected static SharedPreferences.Editor editor;
    public keystoneTwoPoint(Context context){
        super(context);
        mContext = context;

        init(context);
    }

    public void init(Context context){
        prefs = context.getSharedPreferences("ty_zoom",Context.MODE_PRIVATE);
        editor = prefs.edit();
        zoom_x = prefs.getInt("zoom_x",0);
        zoom_y = prefs.getInt("zoom_y",0);

        vTopLeft.setMaxX(maxXStep);vTopLeft.setMaxY(maxYStep);
        vTopRight.setMaxX(maxXStep);vTopRight.setMaxY(maxYStep);

        vBottomLeft.setMaxX(maxXStep);vBottomLeft.setMaxY(maxYStep);
        vBottomRight.setMaxX(maxXStep);vBottomRight.setMaxY(maxYStep);
        mStepX = _mStepX;
        mStepY = _mStepY;
    }

    public void twoLeft(){
        /*int topLeft_y = vTopRight.getY();
        int bottomLeft_y = vBottomRight.getY();
        if(topLeft_y!= 0 && bottomLeft_y != 0) {
            vTopRight.doTop();
            vTopRight.doRight();
            vBottomRight.doBottom();
            vBottomRight.doRight();

            //savePoint(1);
            //savePoint(2);
            updateTopRight();
            updateBottomRight();
        }else{
            vTopLeft.doBottom();
            vTopLeft.doRight();
            vBottomLeft.doTop();
            vBottomLeft.doRight();

            //savePoint(0);
            //savePoint(3);
            updateTopLeft();
            updateBottomLeft();
        }*/
        if(zoom_x<=maxXStep) {
            leftZoomOut();
        }else{
            rightZoomIn();
        }
        zoom_x--;
        zoom_x= zoom_x<=0?0:zoom_x;

        savePoint(4);
        saveZoom();
        update();
    }

    public void twoRight(){
        /*int topLeft_y = vTopLeft.getY();
        int bottomLeft_y = vBottomLeft.getY();

        //if(topLeft_y !=0 && bottomLeft_y !=0){
            vTopLeft.doTop();
            vTopLeft.doLeft();
            vBottomLeft.doBottom();
            vBottomLeft.doLeft();

            //savePoint(0);
            //savePoint(3);
            updateTopLeft();
            updateBottomLeft();
        }else{
            vTopRight.doBottom();
            vTopRight.doLeft();
            vBottomRight.doTop();
            vBottomRight.doLeft();

            //savePoint(1);
            //savePoint(2);
            updateTopRight();
            updateBottomRight();
        }*/
        if(zoom_x>=maxXStep){
            rightZoomOut();
        }else{
            leftZoomIn();
        }
        zoom_x++;
        zoom_x= zoom_x>=2*maxXStep?2*maxXStep:zoom_x;
        savePoint(4);
        saveZoom();
        update();
    }

    public void twoTop(){
   /*     int bottomLeft_x = vBottomLeft.getX();
        int bottomRight_x = vBottomRight.getX();

        if(bottomLeft_x !=0 && bottomRight_x !=0){
            vBottomRight.doRight();
            vBottomRight.doBottom();
            vBottomLeft.doLeft();
            vBottomLeft.doBottom();
            //savePoint(2);
            //savePoint(3);
            updateBottomLeft();
            updateBottomRight();
        }else{
            vTopLeft.doRight();
            vTopLeft.doBottom();
            vTopRight.doLeft();
            vTopRight.doBottom();
            //savePoint(0);
            //savePoint(1);
            updateTopLeft();
            updateTopRight();
        }*/
        if(zoom_y>= maxYStep){
            topZoomOut();
        }else{
            bottomZoomIn();
        }
        zoom_y++;
        zoom_y= zoom_y>=2*maxYStep?2*maxYStep:zoom_y;
        savePoint(4);
        saveZoom();
        update();
    }

    public void twoBottom(){
   /*     int topLeft_x = vTopLeft.getX();
        int topRight_x = vTopRight.getX();

        if(topLeft_x !=0 && topRight_x !=0){
            vTopLeft.doLeft();
            vTopLeft.doTop();
            vTopRight.doRight();
            vTopRight.doTop();
            //savePoint(0);
            //savePoint(1);
            updateTopLeft();
            updateTopRight();
        }else{
            vBottomRight.doLeft();
            vBottomRight.doTop();
            vBottomLeft.doRight();
            vBottomLeft.doTop();
            //savePoint(2);
            //savePoint(3);
            updateBottomLeft();
            updateBottomRight();
        }*/
        if(zoom_y<=maxYStep){
            bottomZoomOut();
        }else{
            topZoomIn();
        }
        zoom_y--;
        zoom_y= zoom_y<=0?0:zoom_y;
        savePoint(4);
        saveZoom();
        update();
    }

    public void leftZoomOut(){
        vTopLeft.doBottom();
        vTopLeft.doRight();
        vBottomLeft.doTop();
        vBottomLeft.doRight();

        updateTopLeft();
        updateBottomLeft();
    }
    public void leftZoomIn(){
        vTopLeft.doTop();
        vTopLeft.doLeft();
        vBottomLeft.doBottom();
        vBottomLeft.doLeft();

        updateTopLeft();
        updateBottomLeft();
    }
    public void rightZoomOut(){
        vTopRight.doBottom();
        vTopRight.doLeft();
        vBottomRight.doTop();
        vBottomRight.doLeft();

        updateTopRight();
        updateBottomRight();
    }
    public void rightZoomIn(){
        vTopRight.doTop();
        vTopRight.doRight();
        vBottomRight.doBottom();
        vBottomRight.doRight();

        updateTopRight();
        updateBottomRight();
    }
    public void topZoomOut(){
        vTopLeft.doRight();
        vTopLeft.doBottom();
        vTopRight.doLeft();
        vTopRight.doBottom();

        updateTopLeft();
        updateTopRight();
    }
    public void topZoomIn(){
        vTopLeft.doLeft();
        vTopLeft.doTop();
        vTopRight.doRight();
        vTopRight.doTop();

        updateTopLeft();
        updateTopRight();
    }
    public void bottomZoomOut(){
        vBottomRight.doLeft();
        vBottomRight.doTop();
        vBottomLeft.doRight();
        vBottomLeft.doTop();

        updateBottomLeft();
        updateBottomRight();
    }
    public void bottomZoomIn(){
        vBottomRight.doRight();
        vBottomRight.doBottom();
        vBottomLeft.doLeft();
        vBottomLeft.doBottom();

        updateBottomLeft();
        updateBottomRight();
    }
    public void saveZoom(){
        editor.putInt("zoom_x",zoom_x);
        editor.putInt("zoom_y",zoom_y);
        editor.apply();
    }

    public int getTwoPointXInfo(){
        /*int topLeft = vTopLeft.getX();
        int topRight = vTopRight.getX();
        int bottomLeft = vBottomLeft.getX();
        int bottomRight = vBottomRight.getX();

        if(topLeft ==0 && bottomLeft==0) {
            return -(max(topRight,bottomRight));
        }else{
            return max(topLeft,bottomLeft);
        }*/
        return zoom_x-maxXStep;
    }
    public int getTwoPointYInfo(){
        /*int topLeft = vTopLeft.getY();
        int topRight = vTopRight.getY();
        int bottomLeft = vBottomLeft.getY();
        int bottomRight = vBottomRight.getY();

        if(topLeft ==0 && topRight==0) {
            return -(max(bottomLeft,bottomRight));
        }else{
            return max(topLeft,topRight);
        }*/
        return zoom_y-maxYStep;
    }
    public void resetKeystone(){
        zoom_x=maxXStep;
        zoom_y=maxYStep;
        restoreKeystone();
    }
    public void setZoom(int progress){
        saveZoom(progress);

        updateTopLeft();
        updateTopRight();
        updateBottomLeft();
        updateBottomRight();
        update();
    }
}
