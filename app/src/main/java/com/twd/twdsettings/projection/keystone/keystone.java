package com.twd.twdsettings.projection.keystone;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;

public abstract class keystone {
    private static final String TAG = "keystone";
    public static final String PROP_LT_ORIGIN = "ro.sys.keystone.lt";
    public static final String PROP_RT_ORIGIN = "ro.sys.keystone.rt";
    public static final String PROP_RB_ORIGIN = "ro.sys.keystone.rb";
    public static final String PROP_LB_ORIGIN = "ro.sys.keystone.lb";
    public static final String ORIGIN_NULL = "0,0";
    public static final String PROP_LT = "persist.sys.keystone.lt";
    public static final String PROP_RT = "persist.sys.keystone.rt";
    public static final String PROP_RB = "persist.sys.keystone.rb";
    public static final String PROP_LB = "persist.sys.keystone.lb";
    public static final String PROP_KEYSTONE_UPDATE = "persist.sys.keystone.update";

    protected static Context mContext;
    public static Vertex vTopLeftOrigin;
    public static Vertex vTopRightOrigin;
    public static Vertex vBottomLeftOrigin;
    public static Vertex vBottomRightOrigin;
    public static Vertex vTopLeft;
    public static Vertex vTopRight;
    public static Vertex vBottomLeft;
    public static Vertex vBottomRight;

    protected static float vZoom;
    //protected static final float vZoomRange = (1/2);
    protected static int mStepX = 5;
    protected static int mStepY = 3;

    protected static int lcdValidWidth_top ;
    protected static int lcdValidWidth_bottom ;
    protected static int lcdValidHeight_left;
    protected static int lcdValidHeight_right;
    protected static int lcdWidth;
    protected static int lcdHeight;
    protected static SharedPreferences prefs;
    protected static SharedPreferences.Editor editor;
    public  keystone(Context context){
        mContext = context;
        getKeystoneOrigin(context);
        getInitKeystone(context);
    }

    public void getKeystoneOrigin(Context context){
        String originLT = SystemPropertiesUtils.getProperty(PROP_LT_ORIGIN,ORIGIN_NULL);
        String originRT = SystemPropertiesUtils.getProperty(PROP_RT_ORIGIN,ORIGIN_NULL);
        String originLB = SystemPropertiesUtils.getProperty(PROP_LB_ORIGIN,ORIGIN_NULL);
        String originRB = SystemPropertiesUtils.getProperty(PROP_RB_ORIGIN,ORIGIN_NULL);

        vTopLeftOrigin = new Vertex(originLT);
        vTopRightOrigin = new Vertex(originRT);
        vBottomLeftOrigin = new Vertex(originLB);
        vBottomRightOrigin = new Vertex(originRB);
        Log.d(TAG, "getInitKeystone: origin_lt("+originLT+"),origin_rt("+originRT+"),origin_lb("+originLB+"),origin_rb("+originRB+")");

    }

    public void getInitKeystone(Context context){
        prefs = context.getSharedPreferences("ty_zoom",Context.MODE_PRIVATE);
        editor = prefs.edit();
        String strTopLeft = prefs.getString("top_left","0,0");
        String strTopRight = prefs.getString("top_right","0,0");
        String strBottomLeft = prefs.getString("bottom_left","0,0");
        String strBottomRight = prefs.getString("bottom_right","0,0");
        vZoom = prefs.getInt("zoom",0);
        vTopLeft = new Vertex(0,strTopLeft);
        vTopRight = new Vertex(1,strTopRight);
        vBottomLeft = new Vertex(3,strBottomLeft);
        vBottomRight = new Vertex(2,strBottomRight);
        Log.d(TAG, "getInitKeystone: lt("+strTopLeft+"),rt("+strTopRight+"),lb("+strBottomLeft+"),rb("+vBottomRight+")");
        Log.d(TAG, "getInitKeystone: zoom:"+vZoom);
        DisplayMetrics dm = new DisplayMetrics();
        dm =  context.getResources().getDisplayMetrics();
        lcdWidth = dm.widthPixels;
        lcdHeight = dm.heightPixels;
        lcdValidWidth_top = lcdWidth - vTopLeftOrigin.getX() +vTopRightOrigin.getX();
        lcdValidWidth_bottom = lcdWidth - vBottomLeftOrigin.getX() +vBottomRightOrigin.getX();
        lcdValidHeight_left = lcdHeight + vTopLeftOrigin.getY() -vBottomLeftOrigin.getY() ;
        lcdValidHeight_right = lcdHeight + vTopRightOrigin.getY() -vBottomRightOrigin.getY() ;
        Log.d(TAG, "getInitKeystone: lcdValidWidth_top="+lcdValidWidth_top+",lcdValidWidth_bottom=" +lcdValidWidth_bottom+
                ",lcdValidHeight_left="+lcdValidHeight_left+",lcdValidHeight_right="+lcdValidHeight_right);
    }

    public void savePoint(int point){
        switch (point){
            case 0:
                editor.putString("top_left",vTopLeft.toString());
                editor.apply();
                break;
            case 1:
                editor.putString("top_right",vTopRight.toString());
                editor.apply();
                break;
            case 3:
                editor.putString("bottom_left",vBottomLeft.toString());
                editor.apply();
                break;
            case 2:
                editor.putString("bottom_right",vBottomRight.toString());
                editor.apply();
                break;
            default:
                editor.putString("top_left",vTopLeft.toString());
                editor.putString("top_right",vTopRight.toString());
                editor.putString("bottom_left",vBottomLeft.toString());
                editor.putString("bottom_right",vBottomRight.toString());
                editor.apply();
                break;
        }
    }

    public void saveZoom(int progress){
        vZoom = progress;
        editor.putInt("zoom",progress);
        editor.apply();
    }
    public abstract void setZoom(int progress);

    public void updatePoint(int point){
        switch (point){
            case 0:
                updateTopLeft();
                break;
            case 1:
                updateTopRight();
                break;
            case 3:
                updateBottomLeft();
                break;
            case 2:
                updateBottomRight();
                break;
            default:
                updateTopLeft();
                updateTopRight();
                updateBottomLeft();
                updateBottomRight();
                break;
        }
    }

    public void restoreKeystone(){
        vTopLeft.setX(0);vTopLeft.setY(0);
        vTopRight.setX(0);vTopRight.setY(0);
        vBottomLeft.setX(0);vBottomLeft.setY(0);
        vBottomRight.setX(0);vBottomRight.setY(0);
        savePoint(4);//save all
        updatePoint(4);//update all
        update();
    }

    public void updateTopLeft(){
        /*float zoomx = (lcdWidth - vTopLeftOrigin.getX())/2*vZoom/10/2;
        float movex = vTopLeft.getX()*mStepX*(10-vZoom)/10;
        float x = vTopLeftOrigin.getX() + zoomx + movex;
        float zoomy = (lcdHeight - vTopLeftOrigin.getY())/2*vZoom/10/2;
        float movey = vTopLeft.getY()*mStepY*(10-vZoom)/10;
        float y = vTopLeftOrigin.getY() - zoomy - movey;
*/
        float zoomx = lcdValidWidth_top*vZoom/10/2/2;
        float movex = vTopLeft.getX()*mStepX*(20-vZoom)/20;
        float x = vTopLeftOrigin.getX() + (zoomx + movex);
        float zoomy = lcdValidHeight_left*vZoom/10/2/2;
        float movey = vTopLeft.getY()*mStepY*(20-vZoom)/20;
        float y = vTopLeftOrigin.getY() - (zoomy + movey);
        Log.d(TAG, "setTopLeft: "+x+","+y+",zoom:"+vZoom+"origin("+vTopLeftOrigin.getX()+","+vTopLeftOrigin.getY()
                +"),zoom("+zoomx+","+zoomy+"),move("+movex+","+movey+")");
        SystemPropertiesUtils.setProperty(PROP_LT,x+","+y);
    }

    public void updateTopRight(){

        /*float zoomx = (lcdWidth - vTopRightOrigin.getX())/2*vZoom/10/2;
        float movex = vTopRight.getX()*mStepX*(10-vZoom)/10;
        float x = vTopRightOrigin.getX() - zoomx - movex;
        float zoomy = (lcdHeight - vTopRightOrigin.getY())/2*vZoom/10/2;
        float movey = vTopRight.getY()*mStepY*(10-vZoom)/10;
        float y = vTopRightOrigin.getY() - zoomy - movey;*/
        float zoomx = lcdValidWidth_top*vZoom/10/2/2;
        float movex = vTopRight.getX()*mStepX*(20-vZoom)/20;
        float x = vTopRightOrigin.getX() - (zoomx + movex);
        float zoomy = lcdValidHeight_right*vZoom/10/2/2;
        float movey = vTopRight.getY()*mStepY*(20-vZoom)/20;
        float y = vTopRightOrigin.getY() - (zoomy + movey);
        Log.d(TAG, "setTopRight: "+x+","+y+",zoom:"+vZoom+"origin("+vTopLeftOrigin.getX()+","+vTopLeftOrigin.getY()
                +"),zoom("+zoomx+","+zoomy+"),move("+movex+","+movey+")");
        SystemPropertiesUtils.setProperty(PROP_RT,x+","+y);
    }

    public void updateBottomLeft(){
        //float zoomx = (lcdWidth - vBottomLeftOrigin.getX())/2*vZoom/10/2;
        /*float zoomx = (lcdWidth - vBottomLeftOrigin.getX())/2*vZoom/10/2;
        float movex = vBottomLeft.getX()*mStepX*(10-vZoom)/10;
        float x = vBottomLeftOrigin.getX() + zoomx + movex;
        float zoomy = (lcdHeight - vBottomLeftOrigin.getY())/2*vZoom/10/2;
        float movey = vBottomLeft.getY()*mStepY*(10-vZoom)/10;
        float y = vBottomLeftOrigin.getY() + zoomy + movey;*/
        float zoomx = lcdValidWidth_bottom*vZoom/10/2/2;
        float movex = vBottomLeft.getX()*mStepX*(20-vZoom)/20;
        float x = vBottomLeftOrigin.getX() + (zoomx + movex);
        float zoomy = lcdValidHeight_left*vZoom/10/2/2;
        float movey = vBottomLeft.getY()*mStepY*(20-vZoom)/20;
        float y = vBottomLeftOrigin.getY() + (zoomy + movey);
        Log.d(TAG, "setBottomLeft: "+x+","+y+",zoom:"+vZoom+"origin("+vTopLeftOrigin.getX()+","+vTopLeftOrigin.getY()
                +"),zoom("+zoomx+","+zoomy+"),move("+movex+","+movey+")");
        SystemPropertiesUtils.setProperty(PROP_LB,x+","+y);
    }

    public void updateBottomRight(){
        /*float zoomx = (lcdWidth - vBottomRightOrigin.getX())/2*vZoom/10/2;
        float movex = vBottomRight.getX()*mStepX*(10-vZoom)/10;
        float x = vBottomRightOrigin.getX() - zoomx - movex;
        float zoomy = (lcdHeight - vBottomRightOrigin.getY())/2*vZoom/10/2;
        float movey = vBottomRight.getY()*mStepY*(10-vZoom)/10;
        float y = vBottomRightOrigin.getY() + zoomy + movey;*/
        float zoomx = lcdValidWidth_bottom*vZoom/10/2/2;
        float movex = vBottomRight.getX()*mStepX*(20-vZoom)/20;
        float x = vBottomRightOrigin.getX() - (zoomx + movex);
        float zoomy = lcdValidHeight_right*vZoom/10/2/2;
        float movey = vBottomRight.getY()*mStepY*(20-vZoom)/20;
        float y = vBottomRightOrigin.getY() + (zoomy + movey);
        Log.d(TAG, "setBottomRight: "+x+","+y+",zoom:"+vZoom+"origin("+vTopLeftOrigin.getX()+","+vTopLeftOrigin.getY()
                +"),zoom("+zoomx+","+zoomy+"),move("+movex+","+movey+")");
        SystemPropertiesUtils.setProperty(PROP_RB,x+","+y);
    }

    public void updateTopLeft(String value){
        SystemPropertiesUtils.setProperty(PROP_LT,value);
    }
    public void updateTopRight(String value){
        SystemPropertiesUtils.setProperty(PROP_RT,value);
    }
    public void updateBottomLeft(String value){
        SystemPropertiesUtils.setProperty(PROP_LB,value);
    }
    public void updateBottomRight(String value){
        SystemPropertiesUtils.setProperty(PROP_RB,value);
    }
    public void update(){
        SystemPropertiesUtils.setProperty(PROP_KEYSTONE_UPDATE,"1");
    }
}
