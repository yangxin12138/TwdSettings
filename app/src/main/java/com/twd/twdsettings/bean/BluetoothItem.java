package com.twd.twdsettings.bean;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.twd.twdsettings.bluetooth.BluetoothActivity;

public class BluetoothItem {
    private String deviceName;
    private String deviceStatus;

    private String deviceAddress;
    private BluetoothActivity.ConnectThread connectThread;

    public BluetoothItem() {
    }

    public BluetoothItem(String deviceName, String deviceStatus, String deviceAddress) {
        this.deviceName = deviceName;
        this.deviceStatus = deviceStatus;
        this.deviceAddress = deviceAddress;
    }
/*
    public BluetoothItem(BluetoothDevice device, Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
        this.deviceName = device.getName();
        this.deviceStatus = device.getBondState();
    }*/

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public BluetoothActivity.ConnectThread getConnectThread() {
        return connectThread;
    }

    public void setConnectThread(BluetoothActivity.ConnectThread connectThread) {
        this.connectThread = connectThread;
    }
}
