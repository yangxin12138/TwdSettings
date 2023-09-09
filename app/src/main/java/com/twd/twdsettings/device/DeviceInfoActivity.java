package com.twd.twdsettings.device;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.twd.twdsettings.R;

public class DeviceInfoActivity extends AppCompatActivity {

    private TextView tv_MachineNO_value;
    private TextView tv_androidVersion_value;
    private TextView tv_softwareNO_value;
    private TextView tv_macAddressWifi_value;
    private TextView tv_macAddressBluetooth_value;
    private static final String TAG = DeviceInfoActivity.class.getName();
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        setMachineNO();
        setAndroidVersion();
        setSoftwareNO();
        setMACAddressWifi();
        setMACAddressBluetooth();
    }

    private void initView(){
            tv_MachineNO_value = findViewById(R.id.devices_tv_machineNO_value);
            tv_androidVersion_value = findViewById(R.id.devices_tv_androidVersion_value);
            tv_softwareNO_value = findViewById(R.id.devices_tv_softwareNO_value);
            tv_macAddressWifi_value = findViewById(R.id.devices_tv_macAddressWifi_value);
            tv_macAddressBluetooth_value = findViewById(R.id.devices_tv_macAddressBluetooth_value);
    }

    /*
    * 获取设备号*/
    private void setMachineNO(){
        String machineNo = Build.MODEL;
        tv_MachineNO_value.setText(machineNo);
        Log.i(TAG, "getMachineNO: --------machineNo = " + machineNo);
    }

    /*
    * 获取安卓版本号*/
    private void setAndroidVersion(){
        String version = Build.VERSION.RELEASE;
        tv_androidVersion_value.setText(version);
        Log.i(TAG, "setAndroidVersion: ----------version = " + version);
    }

    /*
    * 获取软件号*/
    private void setSoftwareNO(){
        String softwareNo = Build.VERSION.INCREMENTAL;
        Log.i(TAG, "setSoftwareNO: -----software = " + softwareNo);
        tv_softwareNO_value.setText(softwareNo);
    }

    /*
    * 获取wifi的mac地址*/
    private void setMACAddressWifi(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        if (macAddress != null){
            macAddress = macAddress.toUpperCase();
        }
        tv_macAddressWifi_value.setText(macAddress);
        Log.i(TAG, "setMACAddressWifi: ---------macAddress_wifi = " + macAddress);
    }

    /*
    * 获取蓝牙的mac地址*/
    private void setMACAddressBluetooth(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null){
            String macAddress = bluetoothAdapter.getAddress();
            if (macAddress != null){
                macAddress = macAddress.toUpperCase();
                Log.i(TAG, "setMACAddressBluetooth: --------bluetooth = " + macAddress);
            }
            tv_macAddressBluetooth_value.setText(macAddress);
        }
    }

}