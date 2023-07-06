package com.twd.twdsettings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twd.twdsettings.network.NetworkActivity;
import com.twd.twdsettings.projection.ProjectionSettingsActivity;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener , View.OnClickListener {

    private LinearLayout index_LLProjection;
    private LinearLayout index_LLWifi;
    private LinearLayout index_LLBluetooth;
    private LinearLayout index_LLSetup;
    private LinearLayout index_LLAbout;

    private ImageView IV_projection;
    private ImageView IV_wifi;
    private ImageView IV_bluetooth;
    private ImageView IV_setup;
    private ImageView IV_about;

    private TextView TV_projection;
    private TextView TV_wifi;
    private TextView TV_bluetooth;
    private TextView TV_setup;
    private TextView TV_about;

    private ImageView arrow_projection;
    private ImageView arrow_wifi;
    private ImageView arrow_bluetooth;
    private ImageView arrow_setup;
    private ImageView arrow_about;

    private ImageView displayPic;
    private TextView displayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        index_LLProjection = findViewById(R.id.LL_index_projection);
        index_LLWifi = findViewById(R.id.LL_index_wifi);
        index_LLBluetooth = findViewById(R.id.LL_index_bluetooth);
        index_LLSetup = findViewById(R.id.LL_index_setup);
        index_LLAbout = findViewById(R.id.LL_index_about);

        IV_projection = findViewById(R.id.im_projection);
        IV_wifi = findViewById(R.id.im_wifi);
        IV_bluetooth = findViewById(R.id.im_bluetooth);
        IV_setup = findViewById(R.id.im_setup);
        IV_about = findViewById(R.id.im_about);

        TV_projection = findViewById(R.id.projection_text);
        TV_wifi = findViewById(R.id.wifi_text);
        TV_bluetooth = findViewById(R.id.bluetooth_text);
        TV_setup = findViewById(R.id.setup_text);
        TV_about = findViewById(R.id.about_text);

        arrow_projection = findViewById(R.id.indexArrow_projection);
        arrow_wifi = findViewById(R.id.indexArrow_wifi);
        arrow_bluetooth = findViewById(R.id.indexArrow_bluetooth);
        arrow_setup = findViewById(R.id.indexArrow_setup);
        arrow_about = findViewById(R.id.indexArrow_about);

        displayPic = findViewById(R.id.display_pic);
        displayText = findViewById(R.id.display_text);

        index_LLProjection.setFocusable(true);
        index_LLProjection.setFocusableInTouchMode(true);
        index_LLWifi.setFocusable(true);
        index_LLWifi.setFocusableInTouchMode(true);
        index_LLBluetooth.setFocusable(true);
        index_LLBluetooth.setFocusableInTouchMode(true);
        index_LLSetup.setFocusable(true);
        index_LLSetup.setFocusableInTouchMode(true);
        index_LLAbout.setFocusable(true);
        index_LLAbout.setFocusableInTouchMode(true);

        index_LLProjection.setOnFocusChangeListener(this);
        index_LLWifi.setOnFocusChangeListener(this);
        index_LLBluetooth.setOnFocusChangeListener(this);
        index_LLBluetooth.setOnFocusChangeListener(this);
        index_LLSetup.setOnFocusChangeListener(this);
        index_LLAbout.setOnFocusChangeListener(this);

        index_LLProjection.setOnClickListener(this);
        index_LLWifi.setOnClickListener(this);
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
        changeItemColor(view.getId());

        if (view.getId() == R.id.LL_index_projection){
            displayPic.setImageResource(R.drawable.projection);
            displayText.setText(TV_projection.getText().toString());
        } else if (view.getId() == R.id.LL_index_wifi) {
            displayPic.setImageResource(R.drawable.wifi);
            displayText.setText(TV_wifi.getText().toString());
        } else if (view.getId() == R.id.LL_index_bluetooth) {
            displayPic.setImageResource(R.drawable.bluetooth);
            displayText.setText(TV_bluetooth.getText().toString());
        } else if (view.getId() == R.id.LL_index_setup) {
            displayPic.setImageResource(R.drawable.setup);
            displayText.setText(TV_setup.getText().toString());
        } else if (view.getId() == R.id.LL_index_about) {
            displayPic.setImageResource(R.drawable.about);
            displayText.setText(TV_about.getText().toString());
        }
    }

    private void resetItem(View view){
        view.setBackgroundColor(getResources().getColor(R.color.background_color));
        resetItemColor(view.getId());
    }

    private void changeItemColor(int ViewId){
        if (ViewId == R.id.LL_index_projection) {
            IV_projection.setImageResource(R.drawable.projection_sel);
            TV_projection.setTextColor(getResources().getColor(R.color.sel_blue));
            arrow_projection.setImageResource(R.drawable.arrow_sel);
        } else if (ViewId == R.id.LL_index_wifi) {
            IV_wifi.setImageResource(R.drawable.wifi_sel);
            TV_wifi.setTextColor(getResources().getColor(R.color.sel_blue));
            arrow_wifi.setImageResource(R.drawable.arrow_sel);
        } else if (ViewId == R.id.LL_index_bluetooth) {
            IV_bluetooth.setImageResource(R.drawable.bluetooth_sel);
            TV_bluetooth.setTextColor(getResources().getColor(R.color.sel_blue));
            arrow_bluetooth.setImageResource(R.drawable.arrow_sel);
        } else if (ViewId == R.id.LL_index_setup) {
            IV_setup.setImageResource(R.drawable.setup_sel);
            TV_setup.setTextColor(getResources().getColor(R.color.sel_blue));
            arrow_setup.setImageResource(R.drawable.arrow_sel);
        } else if (ViewId == R.id.LL_index_about) {
            IV_about.setImageResource(R.drawable.about_sel);
            TV_about.setTextColor(getResources().getColor(R.color.sel_blue));
            arrow_about.setImageResource(R.drawable.arrow_sel);
        }
    }
    
    private void resetItemColor(int ViewId){
        if (ViewId == R.id.LL_index_projection){
            IV_projection.setImageResource(R.drawable.projection);
            TV_projection.setTextColor(getResources().getColor(R.color.white));
            arrow_projection.setImageResource(R.drawable.arrow);
        } else if (ViewId == R.id.LL_index_wifi) {
            IV_wifi.setImageResource(R.drawable.wifi);
            TV_wifi.setTextColor(getResources().getColor(R.color.white));
            arrow_wifi.setImageResource(R.drawable.arrow);
        } else if (ViewId == R.id.LL_index_bluetooth) {
            IV_bluetooth.setImageResource(R.drawable.bluetooth);
            TV_bluetooth.setTextColor(getResources().getColor(R.color.white));
            arrow_bluetooth.setImageResource(R.drawable.arrow);
        } else if (ViewId == R.id.LL_index_setup) {
            IV_setup.setImageResource(R.drawable.setup);
            TV_setup.setTextColor(getResources().getColor(R.color.white));
            arrow_setup.setImageResource(R.drawable.arrow);
        } else if (ViewId == R.id.LL_index_about) {
            IV_about.setImageResource(R.drawable.about);
            TV_about.setTextColor(getResources().getColor(R.color.white));
            arrow_about.setImageResource(R.drawable.arrow);
        }
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
        }
    }
}