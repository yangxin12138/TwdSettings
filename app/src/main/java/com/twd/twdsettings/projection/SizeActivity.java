package com.twd.twdsettings.projection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.twd.twdsettings.R;
import com.twd.twdsettings.SystemPropertiesUtils;
import com.twd.twdsettings.projection.keystone.keystone;
import com.twd.twdsettings.projection.keystone.keystoneOnePoint;
import com.twd.twdsettings.projection.keystone.keystoneTwoPoint;

/**
 * 尺寸调节页面
 * SeekBar做调节滑块，TextView文字显示Progress进度
 */
public class SizeActivity extends AppCompatActivity implements View.OnFocusChangeListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "SizeActivity";
    private SeekBar seekBar_level;
    private TextView textView_level;
    private keystone mKeystone;
    protected static SharedPreferences prefs;
    String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        switch (theme_code){
            case "0": //冰激蓝
                this.setTheme(R.style.Theme_IceBlue);
                break;
            case "1": //木棉白
                this.setTheme(R.style.Theme_KapokWhite);
                break;
            case "2": //星空蓝
                this.setTheme(R.style.Theme_StarBlue);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size);
        initView();
        prefs = this.getSharedPreferences("keystone_mode", Context.MODE_PRIVATE);
        int mode = prefs.getInt("mode",0);
        if (mode == 0){
            mKeystone = new keystoneTwoPoint(this);
        } else if (mode == 1) {
            mKeystone = new keystoneOnePoint(this);
        }
    }

    private void initView(){
        seekBar_level = (SeekBar) findViewById(R.id.seekbar_level);
        textView_level = (TextView) findViewById(R.id.text_level);

        //初始化，读取seekbar状态
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        int seekBarValue = prefs.getInt("seekBarValue",0);
        seekBar_level.setProgress(seekBarValue);
        textView_level.setText("Level:"+seekBar_level.getProgress());

        seekBar_level.setOnFocusChangeListener(this);
        seekBar_level.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            // 此处为得到焦点时的处理内容
            ViewCompat.animate(v)
                    .scaleX(1.10f)
                    .scaleY(1.10f)
                    .translationZ(1)
                    .start();
        } else {
            // 此处为失去焦点时的处理内容
            ViewCompat.animate(v)
                    .scaleX(1)
                    .scaleY(1)
                    .translationZ(1)
                    .start();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //保存进度条
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt("seekBarValue",progress);
        editor.apply();
        Log.d(TAG, "ZOOM: "+progress);
        mKeystone.setZoom(progress);

        textView_level.setText("Level:"+progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
