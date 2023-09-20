package com.twd.twdsettings.projection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.twdsettings.R;
import com.twd.twdsettings.SystemPropertiesUtils;

/**
 * 投影设置主页
 */
public class ProjectionSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences mSharedPreferences;
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
        setContentView(R.layout.activity_projection_settings);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    /*页面初始化*/
    private void initView(){
        LinearLayout double_TrapezoidalLL = findViewById(R.id.trapezoidal_double_point);
        LinearLayout single_TrapezoidalLL = findViewById(R.id.trapezoidal_single_point);
        LinearLayout sizeLL = findViewById(R.id.size);
        LinearLayout projectionLL = findViewById(R.id.projection);

        mSharedPreferences = getSharedPreferences("MyPrefsFile",MODE_PRIVATE);
        boolean pos_pos_check = mSharedPreferences.getBoolean("pos_pos",false);
        boolean pos_neg_check = mSharedPreferences.getBoolean("pos_neg",false);
        boolean neg_pos_check = mSharedPreferences.getBoolean("neg_pos",false);
        boolean neg_neg_check = mSharedPreferences.getBoolean("neg_neg",false);

        /* 设置投影方式显示文字 根据mSharedPreferences中提取的值判断显示*/
        TextView tv_projection_small =  findViewById(R.id.tv_projection_small);
        Log.i("投影方式：","pos_pos_check:"+pos_pos_check+",pos_neg_check:"+pos_neg_check+",neg_pos_check:"+neg_pos_check+",neg_neg_check:"+neg_neg_check);
        if (pos_pos_check) tv_projection_small.setText(R.string.projection_method_pos_pos);
        if (pos_neg_check) tv_projection_small.setText(R.string.projection_method_pos_neg);
        if (neg_pos_check) tv_projection_small.setText(R.string.projection_method_neg_pos);
        if (neg_neg_check) tv_projection_small.setText(R.string.projection_method_neg_neg);
        /* 设置监听器 */
        double_TrapezoidalLL.setOnClickListener(this);
        single_TrapezoidalLL.setOnClickListener(this);
        sizeLL.setOnClickListener(this);
        projectionLL.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        /* 点击事件，跳转到对应菜单和活动*/
        Intent intent;
        if (v.getId() == R.id.trapezoidal_double_point) {//                Toast.makeText(this, "点击两点梯形校正", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, TrapezoidalDoubleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.trapezoidal_single_point) {
            intent = new Intent(this,TrapezoidalSingleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.size) {
            intent = new Intent(this,SizeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.projection) {
            intent = new Intent(this,ProjectionMethodActivity.class);
            startActivity(intent);
        }
    }
}
