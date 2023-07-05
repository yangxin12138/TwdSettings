package com.twd.twdsettings.projection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.twdsettings.R;
import com.twd.twdsettings.projection.keystone.keystoneTwoPoint;

/**
 * 梯形蕉子页面布局
 * 见他实现 监控器上下左右键的点击事件
 */
public class TrapezoidalDoubleActivity extends AppCompatActivity {
    private final static String TAG = "TrapezoidalActivity";
    private Context mContext;
    private keystoneTwoPoint mKeystone;
    private TextView mTextY;//上下调节
    private TextView mTextX;//

    protected static SharedPreferences prefsTextValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trapezoidal_double_point);
        initView();
        SharedPreferences prefs = this.getSharedPreferences("keystone_mode",Context.MODE_PRIVATE);
        int mode = prefs.getInt("mode",0);

        mKeystone = new keystoneTwoPoint(this);
        if (mode == 0){
            mKeystone.resetKeystone();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("mode",1);
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView(){
        //初始化并读取数据
        mTextY = (TextView) findViewById(R.id.tv_vertical);
        mTextX = (TextView) findViewById(R.id.tv_horizontal);
        prefsTextValue = this.getSharedPreferences("text_value",MODE_PRIVATE);
        String textX = prefsTextValue.getString("horizontalValue","0");
        String textY = prefsTextValue.getString("verticalValue","0");
        mTextX.setText(textX);
        mTextY.setText(textY);
    }


    /* 保存调整坐标数据 */
    private void saveTextValue(String VorH,String value){
        SharedPreferences.Editor editor = getSharedPreferences("text_value",MODE_PRIVATE).edit();
        editor.putString(VorH,value);
        editor.apply();
    }

    /* 处理按键方法 */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        if (action == KeyEvent.ACTION_DOWN){
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_UP:
                    //处理上键事件
                    Log.i(TAG,"触发处理上键事件");
                    mKeystone.twoTop();
                    int y =mKeystone.getTwoPointYInfo();
                    mTextY.setText("("+y+")");
                    saveTextValue("verticalValue","("+y+")");
                    goUp();
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    //处理下键事件
                    Log.i(TAG,"触发处理下键事件");
                    mKeystone.twoBottom();
                    int yy=mKeystone.getTwoPointYInfo();
                    mTextY.setText("("+yy+")");
                    saveTextValue("verticalValue","("+yy+")");
                    goDown();
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    //处理左键事件
                    Log.i(TAG,"触发处理左键事件");
                    mKeystone.twoLeft();
                    int x =mKeystone.getTwoPointXInfo();
                    mTextX.setText("("+x+")");
                    saveTextValue("horizontalValue","("+x+")");
                    goLeft();
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    //处理右键事件
                    Log.i(TAG,"触发处理右键事件");
                    mKeystone.twoRight();
                    int xx =mKeystone.getTwoPointXInfo();
                    mTextX.setText("("+xx+")");
                    saveTextValue("horizontalValue","("+xx+")");
                    goRight();
                    break;
                case KeyEvent.KEYCODE_MENU:
                    mKeystone.resetKeystone();
                    int xxx =mKeystone.getTwoPointXInfo();
                    mTextX.setText("("+xxx+")");
                    saveTextValue("horizontalValue","("+xxx+")");
                    int yyy=mKeystone.getTwoPointYInfo();
                    mTextY.setText("("+yyy+")");
                    saveTextValue("verticalValue","("+yyy+")");
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    private void goLeft(){
        Log.i("function:goLeft()","左键方法");
    }

    private void goRight(){
        Log.i("function:goRight()","右键方法");
    }

    private void goDown(){
        Log.i("function:goDown()","下键方法");
    }

    private void goUp(){
        Log.i("function:goUp()","上键方法");
    }
}
