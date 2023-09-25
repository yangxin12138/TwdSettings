package com.twd.twdsettings.device;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twd.twdsettings.R;
import com.twd.twdsettings.SystemPropertiesUtils;

public class DeviceActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = DeviceActivity.class.getName();
    private LinearLayout LL_info;
    private LinearLayout LL_storage;
    private TextView tv_info;
    private TextView tv_storage;
    private ImageView arrow_info;
    private ImageView arrow_storage;
    String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}