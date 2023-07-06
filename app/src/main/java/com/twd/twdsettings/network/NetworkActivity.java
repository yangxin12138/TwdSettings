package com.twd.twdsettings.network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.twd.twdsettings.R;

public class NetworkActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private LinearLayout net_LL_wlan;
    private LinearLayout net_LL_wlanAble;
    private LinearLayout net_LL_speed;

    private TextView net_tv_wlan;
    private TextView net_tv_wlanAble;
    private TextView net_tv_speed;
    private Switch mSwitch;
    private ImageView net_arrow_wlanAble;
    private ImageView net_arrow_speed;
    boolean isChecked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        initView();
    }

    private void initView(){
        net_LL_wlan = findViewById(R.id.net_LL_wlan);
        net_LL_wlanAble = findViewById(R.id.net_LL_wlanAble);
        net_LL_speed = findViewById(R.id.net_LL_speed);
        net_tv_wlan = findViewById(R.id.net_tv_wlan);
        net_tv_wlanAble = findViewById(R.id.net_tv_wlanAble);
        net_tv_speed = findViewById(R.id.net_tv_speed);
        net_arrow_wlanAble = findViewById(R.id.arrow_wlanAble);
        net_arrow_speed = findViewById(R.id.arrow_speed);
        mSwitch = findViewById(R.id.net_switch);
        SharedPreferences preferences = getSharedPreferences("Switch_Checked",MODE_PRIVATE);
        boolean check = preferences.getBoolean("isChecked",false);
        Log.i("yang","mSwitch = "+check);
        mSwitch.setChecked(preferences.getBoolean("isChecked",false));

        if (!mSwitch.isChecked()){
            net_LL_wlanAble.setVisibility(View.GONE);
        }else {
            net_LL_wlanAble.setVisibility(View.VISIBLE);
        }
        net_LL_wlan.setFocusable(true);
        net_LL_wlan.setFocusableInTouchMode(true);
        net_LL_wlanAble.setFocusable(true);
        net_LL_wlanAble.setFocusableInTouchMode(true);
        net_LL_speed.setFocusable(true);
        net_LL_speed.setFocusableInTouchMode(true);

        net_LL_wlan.setOnFocusChangeListener(this);
        net_LL_wlanAble.setOnFocusChangeListener(this);
        net_LL_speed.setOnFocusChangeListener(this);

        net_LL_wlan.setOnClickListener(this);
        net_LL_wlanAble.setOnClickListener(this);
        net_LL_speed.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = getSharedPreferences("Switch_Checked",MODE_PRIVATE).edit();
        isChecked = mSwitch.isChecked();
        if (v.getId() == R.id.net_LL_wlan){
            if (isChecked){
                mSwitch.setChecked(false);
                net_LL_wlanAble.setVisibility(View.GONE);
                editor.putBoolean("isChecked",false);
                editor.apply();
            }else {
                mSwitch.setChecked(true);
                net_LL_wlanAble.setVisibility(View.VISIBLE);
                editor.putBoolean("isChecked",true);
                editor.apply();
            }

            Log.i("yang","mSwitch = "+mSwitch.isChecked());
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            changeItem(v);
        }else {
            resetItem(v);
        }
    }

    private void changeItem(View view){
        view.setBackgroundResource(R.drawable.bg_sel);
        if (view.getId() == R.id.net_LL_wlan){
            net_tv_wlan.setTextColor(getResources().getColor(R.color.sel_blue));
        } else if (view.getId() == R.id.net_LL_wlanAble) {
            net_tv_wlanAble.setTextColor(getResources().getColor(R.color.sel_blue));
            net_arrow_wlanAble.setImageResource(R.drawable.arrow_sel);
        } else if (view.getId() == R.id.net_LL_speed) {
            net_tv_speed.setTextColor(getResources().getColor(R.color.sel_blue));
            net_arrow_speed.setImageResource(R.drawable.arrow_sel);
        }
    }

    private void resetItem(View view){
        view.setBackgroundColor(getResources().getColor(R.color.background_color));
        if (view.getId() == R.id.net_LL_wlan){
            net_tv_wlan.setTextColor(getResources().getColor(R.color.white));
        } else if (view.getId() == R.id.net_LL_wlanAble) {
            net_tv_wlanAble.setTextColor(getResources().getColor(R.color.white));
            net_arrow_wlanAble.setImageResource(R.drawable.arrow);
        } else if (view.getId() == R.id.net_LL_speed) {
            net_tv_speed.setTextColor(getResources().getColor(R.color.white));
            net_arrow_speed.setImageResource(R.drawable.arrow);
        }
    }
}