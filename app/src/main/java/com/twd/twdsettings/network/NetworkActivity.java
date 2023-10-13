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
import com.twd.twdsettings.SystemPropertiesUtils;

public class NetworkActivity extends AppCompatActivity implements  View.OnClickListener {

    private LinearLayout net_LL_wlan;
    private LinearLayout net_LL_wlanAble;

    private TextView net_tv_wlan;
    private TextView net_tv_wlanAble;
    private TextView net_tv_ssidcurrent;
    private Switch mSwitch;
    private ImageView net_arrow_wlanAble;
    boolean isChecked = true;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
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
        net_tv_wlan = findViewById(R.id.net_tv_wlan);
        net_tv_wlanAble = findViewById(R.id.net_tv_wlanAble);
        net_arrow_wlanAble = findViewById(R.id.arrow_wlanAble);
        mSwitch = findViewById(R.id.net_switch);
        //读取系统wifi开关状态并设置
        SharedPreferences preferences = getSharedPreferences("Switch_Checked",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        boolean sysCheck = isWifiEnabled(this);
        editor.putBoolean("isChecked",sysCheck);
        editor.apply();
        mSwitch.setChecked(preferences.getBoolean("isChecked",false));

        if (!mSwitch.isChecked()){
            net_LL_wlanAble.setVisibility(View.GONE);
        }else {
            net_LL_wlanAble.setVisibility(View.VISIBLE);
        }

        net_LL_wlan.setOnClickListener(this);
        net_LL_wlanAble.setOnClickListener(this);

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


    private boolean isWifiEnabled(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }
    private String updateWifiInfo(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.i("yang","当前的ssid是："+wifiInfo.getSSID());
        return wifiInfo.getSSID().replace("\"","");
    }
}