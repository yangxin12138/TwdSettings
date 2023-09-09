package com.twd.twdsettings.device;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twd.twdsettings.R;

public class DeviceActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private final static String TAG = DeviceActivity.class.getName();
    private LinearLayout LL_info;
    private LinearLayout LL_storage;
    private TextView tv_info;
    private TextView tv_storage;
    private ImageView arrow_info;
    private ImageView arrow_storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView(){
        LL_info = findViewById(R.id.devices_LL_Info);
        LL_storage = findViewById(R.id.devices_LL_storage);
        tv_info = findViewById(R.id.devices_tv_Info);
        tv_storage = findViewById(R.id.devices_tv_storage);
        arrow_info = findViewById(R.id.arrow_info);
        arrow_storage = findViewById(R.id.arrow_storage);

        LL_info.setFocusable(true);
        LL_info.setFocusableInTouchMode(true);
        LL_storage.setFocusable(true);
        LL_storage.setFocusableInTouchMode(true);

        LL_info.setOnFocusChangeListener(this::onFocusChange);
        LL_storage.setOnFocusChangeListener(this::onFocusChange);

        LL_info.setOnClickListener(this::onClick);
        LL_storage.setOnClickListener(this::onClick);

        LL_info.requestFocus();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.devices_LL_Info){
            intent = new Intent(this,DeviceInfoActivity.class);
            startActivity(intent);
        }else {
            intent = new Intent(this,DeviceStorageActivity.class);
            startActivity(intent);
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
        if (view.getId() == R.id.devices_LL_Info){
            tv_info.setTextColor(getResources().getColor(R.color.sel_blue));
            arrow_info.setImageResource(R.drawable.arrow_sel);
        } else if (view.getId() == R.id.devices_LL_storage) {
            tv_storage.setTextColor(getResources().getColor(R.color.sel_blue));
            arrow_storage.setImageResource(R.drawable.arrow_sel);
        }
    }

    private void resetItem(View view){
        view.setBackgroundColor(getResources().getColor(R.color.background_color));
        if (view.getId() == R.id.devices_LL_Info){
            tv_info.setTextColor(getResources().getColor(R.color.white));
            arrow_info.setImageResource(R.drawable.arrow);
        } else if (view.getId() == R.id.devices_LL_storage) {
            tv_storage.setTextColor(getResources().getColor(R.color.white));
            arrow_storage.setImageResource(R.drawable.arrow);
        }
    }
}