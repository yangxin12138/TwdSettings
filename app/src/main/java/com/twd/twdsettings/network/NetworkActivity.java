package com.twd.twdsettings.network;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.InetAddresses;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.twd.twdsettings.R;

public class NetworkActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private LinearLayout net_LL_wlan;
    private LinearLayout net_LL_wlanAble;
    private LinearLayout net_LL_speed;

    private TextView net_tv_wlan;
    private TextView net_tv_wlanAble;
    private TextView net_tv_speed;
    private TextView net_tv_ssidcurrent;
    private Switch mSwitch;
    private ImageView net_arrow_wlanAble;
    private ImageView net_arrow_speed;
    boolean isChecked = true;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        initView();
        net_tv_ssidcurrent = findViewById(R.id.net_tv_ssidcurrent);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        net_tv_ssidcurrent.setText(updateWifiInfo());
                    }
                });

            }

            @Override
            public void onLost(@NonNull Network network) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        net_tv_ssidcurrent.setText("");
                    }
                });

            }
        };

        NetworkRequest networkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
        connectivityManager.registerNetworkCallback(networkRequest,networkCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(networkCallback);
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
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Intent intent;
        if (v.getId() == R.id.net_LL_wlan){
            if (isChecked){
                mSwitch.setChecked(false);
                net_LL_wlanAble.setVisibility(View.GONE);
                wifiManager.setWifiEnabled(false);
                editor.putBoolean("isChecked",false);
                editor.apply();
            }else {
                mSwitch.setChecked(true);
                net_LL_wlanAble.setVisibility(View.VISIBLE);
                wifiManager.setWifiEnabled(true);
                editor.putBoolean("isChecked",true);
                editor.apply();
            }
            Log.i("yang","mSwitch = "+mSwitch.isChecked());
        } else if (v.getId() == R.id.net_LL_wlanAble) {
            intent = new Intent(this,WifiListActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.net_LL_speed) {
            boolean isConnWifi = isConnectedToWifi();
            boolean isNetworkAvailable = isNetworkAvailable();
            Log.i("yang","isAvailable:"+isNetworkAvailable);
            if (!isConnWifi){
                Toast.makeText(this, "当前没有连接的网络！", Toast.LENGTH_SHORT).show();
                Log.i("yang","当前没有连接的网络！");
            } else if (!isNetworkAvailable) {
                Toast.makeText(this, "网络不可用，请检查网络状态！", Toast.LENGTH_SHORT).show();
                Log.i("yang","网络不可用，请检查网络状态");
            } else {
                Log.i("yang","网络正常，进入测试");
                intent = new Intent(this,NetSpeedActivity.class);
                startActivity(intent);
            }
        }
    }
    private boolean isConnectedToWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
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
            net_tv_ssidcurrent.setTextColor(getResources().getColor(R.color.sel_blue));
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
            net_tv_ssidcurrent.setTextColor(getResources().getColor(R.color.white));
            net_tv_wlanAble.setTextColor(getResources().getColor(R.color.white));
            net_arrow_wlanAble.setImageResource(R.drawable.arrow);
        } else if (view.getId() == R.id.net_LL_speed) {
            net_tv_speed.setTextColor(getResources().getColor(R.color.white));
            net_arrow_speed.setImageResource(R.drawable.arrow);
        }
    }

    private String updateWifiInfo(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.i("yang","当前的ssid是："+wifiInfo.getSSID());
        return wifiInfo.getSSID().replace("\"","");
    }
}