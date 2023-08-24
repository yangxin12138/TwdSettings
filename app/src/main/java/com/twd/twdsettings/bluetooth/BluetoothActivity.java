package com.twd.twdsettings.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.twd.twdsettings.R;

import java.util.ArrayList;
import java.util.List;


public class BluetoothActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> bluetoothDevices;
    private BluetoothDeviceAdapter deviceAdapter;
    private ListView listView;

    private LinearLayout bluetooth_LL_bt;
    private LinearLayout bluetooth_LL_search;
    private TextView bluetooth_tv_bt;
    private Switch bluetooth_switch;
    private TextView bluetooth_tv_search;
    private boolean isChecked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        //初始化BluetoothAdapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            //设备不支持蓝牙
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bluetooth_LL_bt = findViewById(R.id.bluetooth_LL_bt);//蓝牙开关
        bluetooth_tv_bt = findViewById(R.id.bluetooth_tv_bt);
        bluetooth_switch = findViewById(R.id.bluetooth_switch);
        bluetooth_LL_search = findViewById(R.id.bluetooth_LL_search);
        bluetooth_tv_search = findViewById(R.id.bluetooth_tv_search);//搜索中

        bluetooth_LL_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth_tv_search.getText().equals("重新搜索")){
                    bluetoothDevices.clear();
                    bluetooth_tv_search.setText("搜索中...");
                    deviceAdapter.notifyDataSetChanged();
                    startBluetoothScan();
                }
            }
        });

        //设置switch的初始状态
        bluetooth_switch.setChecked(bluetoothAdapter.isEnabled());
        isChecked = bluetooth_switch.isChecked();

        if (!isChecked){
            bluetooth_tv_search.setVisibility(View.INVISIBLE);
        }else {
            bluetooth_tv_search.setVisibility(View.VISIBLE);
        }

        bluetooth_LL_bt.setFocusable(true);
        bluetooth_LL_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked) {
                    bluetooth_switch.setChecked(false);
                    isChecked = bluetooth_switch.isChecked();
                    Log.i("yangxin", "onClick: isChecked = " + isChecked);
                } else {
                    bluetooth_switch.setChecked(true);
                    isChecked = bluetooth_switch.isChecked();
                    Log.i("yangxin", "onClick: isChecked = " + isChecked);
                }
            }
        });

        // 设置Switch按钮的监听器
        bluetooth_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //打开蓝牙
                    enableBluetooth();
                    bluetooth_tv_search.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    bluetooth_tv_search.setText("搜索中....");
                } else {
                    //关闭蓝牙
                    disableBluetooth();
                    bluetooth_tv_search.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.INVISIBLE);

                }
            }
        });
        bluetooth_LL_search.setFocusable(true);

        bluetooth_LL_bt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    changeItem(v);
                } else {
                    resetItem(v);
                }
            }
        });

        bluetooth_LL_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    changeItem(v);
                } else {
                    resetItem(v);
                }
            }
        });

        //初始化ListView和适配器
        listView = findViewById(R.id.bluetooth_listview);
        bluetoothDevices = new ArrayList<>();
        deviceAdapter = new BluetoothDeviceAdapter(this, bluetoothDevices);
        listView.setAdapter(deviceAdapter);

        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(bluetoothReceiver, filter);
        //开始扫描蓝牙设备
        startBluetoothScan();
    }

    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            bluetoothAdapter.enable();
            bluetoothDevices.clear();
            deviceAdapter.notifyDataSetChanged();
            // 延迟一段时间等待UI更新完成
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 重新打开蓝牙搜索
                    startBluetoothScan();
                }
            }, 2000);
        }
    }

    private void disableBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            bluetoothAdapter.disable();
            stopBluetoothScan();
        }
    }

    private void changeItem(View view) {
        view.setBackgroundResource(R.drawable.bg_sel);
        if (view.getId() == R.id.bluetooth_LL_bt) {
            bluetooth_tv_bt.setTextColor(getResources().getColor(R.color.sel_blue));
        } else if (view.getId() == R.id.bluetooth_LL_search) {
            bluetooth_tv_search.setTextColor(getResources().getColor(R.color.sel_blue));
        }
    }

    private void resetItem(View view) {
        view.setBackgroundColor(getResources().getColor(R.color.background_color));
        if (view.getId() == R.id.bluetooth_LL_bt) {
            bluetooth_tv_bt.setTextColor(getResources().getColor(R.color.white));
        } else if (view.getId() == R.id.bluetooth_LL_search) {
            bluetooth_tv_search.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void startBluetoothScan() {
        //检查是否已经在扫描
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        //开始扫描
        bluetoothAdapter.startDiscovery();
        deviceAdapter.notifyDataSetChanged();
    }

    private void stopBluetoothScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothDevices.clear();
        deviceAdapter.notifyDataSetChanged();

    }

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                //发现新设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(device);
                Log.i("yangxin", "onReceive: 触发扫描回调");
                deviceAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //蓝牙扫描完成
                Toast.makeText(context, "蓝牙扫描完成", Toast.LENGTH_SHORT).show();
                bluetooth_tv_search.setText("重新搜索");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBluetoothScan();
        unregisterReceiver(bluetoothReceiver);
    }

    public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
        private List<BluetoothDevice> devices;
        private Context context;

        public BluetoothDeviceAdapter(Context context, List<BluetoothDevice> devices) {
            super(context, 0, devices);
            this.context = context;
            this.devices = devices;
        }

        @NonNull
        @Override
        public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.bluetooth_list_item, parent, false);
            }
            BluetoothDevice device = devices.get(position);

            TextView deviceNameTextView = itemView.findViewById(R.id.bluetooth_name);
            TextView deviceStatusTextView = itemView.findViewById(R.id.bluetooth_status);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            deviceNameTextView.setText(device.getName());
            deviceStatusTextView.setText(device.getBondState() == BluetoothDevice.BOND_BONDED ? "已配对":"");

            return itemView;
        }
    }

}
