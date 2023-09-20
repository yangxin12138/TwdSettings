package com.twd.twdsettings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twd.twdsettings.bluetooth.BluetoothActivity;
import com.twd.twdsettings.device.DeviceActivity;
import com.twd.twdsettings.network.NetworkActivity;
import com.twd.twdsettings.projection.ProjectionSettingsActivity;
import com.twd.twdsettings.universal.UniversalActivity;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {
    String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");
    private LinearLayout index_LLProjection;
    private LinearLayout index_LLWifi;
    private LinearLayout index_LLBluetooth;
    private LinearLayout index_LLUniversal;
    private LinearLayout index_LLAbout;

    private ImageView IV_projection;
    private ImageView IV_wifi;
    private ImageView IV_bluetooth;
    private ImageView IV_universal;
    private ImageView IV_about;

    private TextView TV_projection;
    private TextView TV_wifi;
    private TextView TV_bluetooth;
    private TextView TV_universal;
    private TextView TV_about;

    private ImageView arrow_projection;
    private ImageView arrow_wifi;
    private ImageView arrow_bluetooth;
    private ImageView arrow_universal;
    private ImageView arrow_about;

    private ImageView displayPic;
    private TextView displayText;
    TypedArray tyar;

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
        tyar= obtainStyledAttributes(new int[]{
                R.attr.main_projection_src, //0
                R.attr.main_internet_src,   //1
                R.attr.main_bluetooth_src,  //2
                R.attr.main_universal_src,  //3
                R.attr.main_device_src  //4
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        index_LLProjection = findViewById(R.id.LL_index_projection);
        index_LLWifi = findViewById(R.id.LL_index_wifi);
        index_LLBluetooth = findViewById(R.id.LL_index_bluetooth);
        index_LLUniversal = findViewById(R.id.LL_index_setup);
        index_LLAbout = findViewById(R.id.LL_index_about);

        IV_projection = findViewById(R.id.im_projection);
        IV_wifi = findViewById(R.id.im_wifi);
        IV_bluetooth = findViewById(R.id.im_bluetooth);
        IV_universal = findViewById(R.id.im_setup);
        IV_about = findViewById(R.id.im_about);

        TV_projection = findViewById(R.id.projection_text);
        TV_wifi = findViewById(R.id.wifi_text);
        TV_bluetooth = findViewById(R.id.bluetooth_text);
        TV_universal = findViewById(R.id.setup_text);
        TV_about = findViewById(R.id.about_text);

        arrow_projection = findViewById(R.id.indexArrow_projection);
        arrow_wifi = findViewById(R.id.indexArrow_wifi);
        arrow_bluetooth = findViewById(R.id.indexArrow_bluetooth);
        arrow_universal = findViewById(R.id.indexArrow_setup);
        arrow_about = findViewById(R.id.indexArrow_about);

        displayPic = findViewById(R.id.display_pic);
        displayText = findViewById(R.id.display_text);

        index_LLProjection.setFocusable(true);
        index_LLProjection.setFocusableInTouchMode(true);
        index_LLWifi.setFocusable(true);
        index_LLWifi.setFocusableInTouchMode(true);
        index_LLBluetooth.setFocusable(true);
        index_LLBluetooth.setFocusableInTouchMode(true);
        index_LLUniversal.setFocusable(true);
        index_LLUniversal.setFocusableInTouchMode(true);
        index_LLAbout.setFocusable(true);
        index_LLAbout.setFocusableInTouchMode(true);

        index_LLProjection.setOnFocusChangeListener(this);
        index_LLWifi.setOnFocusChangeListener(this);
        index_LLBluetooth.setOnFocusChangeListener(this);
        index_LLUniversal.setOnFocusChangeListener(this);
        index_LLAbout.setOnFocusChangeListener(this);

        index_LLProjection.setOnClickListener(this);
        index_LLWifi.setOnClickListener(this);
        index_LLBluetooth.setOnClickListener(this);
        index_LLUniversal.setOnClickListener(this);
        index_LLAbout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent ;
        if (v.getId() == R.id.LL_index_projection){
            intent = new Intent(this, ProjectionSettingsActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.LL_index_wifi) {
            intent = new Intent(this, NetworkActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.LL_index_bluetooth) {
            intent = new Intent(this, BluetoothActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.LL_index_setup) {
            intent = new Intent(this, UniversalActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.LL_index_about) {
            intent = new Intent(this, DeviceActivity.class);
            startActivity(intent);
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            if (v.getId() == R.id.LL_index_projection){
                displayPic.setImageDrawable(tyar.getDrawable(0));
                displayText.setText(getString(R.string.main_projection_text));
            } else if (v.getId() == R.id.LL_index_wifi) {
                displayPic.setImageDrawable(tyar.getDrawable(1));
                displayText.setText(getString(R.string.main_wifi_text));
            } else if (v.getId() == R.id.LL_index_bluetooth) {
                displayPic.setImageDrawable(tyar.getDrawable(2));
                displayText.setText(getString(R.string.main_bluetooth_text));
            } else if (v.getId() == R.id.LL_index_setup) {
                displayPic.setImageDrawable(tyar.getDrawable(3));
                displayText.setText(getString(R.string.main_Universal_text));
            } else if (v.getId() == R.id.LL_index_about) {
                displayPic.setImageDrawable(tyar.getDrawable(4));
                displayText.setText(getString(R.string.main_aboutDevice_text));
            }
        }
    }
}