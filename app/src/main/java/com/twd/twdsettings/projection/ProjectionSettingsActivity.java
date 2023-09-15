package com.twd.twdsettings.projection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.twdsettings.R;

/**
 * 投影设置主页
 */
public class ProjectionSettingsActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        RelativeLayout double_TrapezoidalRL = findViewById(R.id.trapezoidal_double_point);
        RelativeLayout single_TrapezoidalRL = findViewById(R.id.trapezoidal_single_point);
        RelativeLayout sizeRl = findViewById(R.id.size);
        RelativeLayout projectionRL = findViewById(R.id.projection);

        ImageView iv_trapezoidal_double_point = findViewById(R.id.iv_trapezoidal_double_point);
        ImageView iv_trapezoidal_single_point =  findViewById(R.id.iv_trapezoidal_single_point);
        ImageView iv_size =  findViewById(R.id.iv_size);
        ImageView iv_projection =  findViewById(R.id.iv_projection);

        TextView tv_trapezoidal_double_point =  findViewById(R.id.tv_trapezoidal_double_point);
        TextView tv_trapezoidal_single_point =  findViewById(R.id.tv_trapezoidal_single_point);
        TextView tv_size =  findViewById(R.id.tv_size);
        TextView tv_projection =  findViewById(R.id.tv_projection);

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
        double_TrapezoidalRL.setOnClickListener(this);
        single_TrapezoidalRL.setOnClickListener(this);
        sizeRl.setOnClickListener(this);
        projectionRL.setOnClickListener(this);

        double_TrapezoidalRL.setOnFocusChangeListener(this);
        single_TrapezoidalRL.setOnFocusChangeListener(this);
        sizeRl.setOnFocusChangeListener(this);
        projectionRL.setOnFocusChangeListener(this);
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


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            v.setBackgroundResource(R.drawable.test_red);
        } else {
            v.setBackgroundResource(R.drawable.test);
        }
    }
}
