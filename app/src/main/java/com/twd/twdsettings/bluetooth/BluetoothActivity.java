package com.twd.twdsettings.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.twd.twdsettings.bean.BluetoothItem;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


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

    private Context mContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        //初始化BluetoothAdapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            //设备不支持蓝牙
            showToast("设备不支持蓝牙");
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
                if (bluetooth_tv_search.getText().equals("重新搜索")) {
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

        if (!isChecked) {
            bluetooth_tv_search.setVisibility(View.INVISIBLE);
        } else {
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

    private void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        View customToastView = inflater.inflate(R.layout.custom_toast, null);

        TextView textView = customToastView.findViewById(R.id.toast_text);
        textView.setText(text);
        Toast customToast = new Toast(getApplicationContext());
        customToast.setView(customToastView);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.show();
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

    private Handler handler = new Handler();

    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
            }
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            deviceAdapter.notifyDataSetChanged();
        }
    };

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
        Log.i("yang", "startBluetoothScan: --开始扫描");

        // 延迟一段时间后停止扫描
        handler.postDelayed(stopScanRunnable, 20000);
        Log.i("yang", "startBluetoothScan: --扫描结束");

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
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //发现新设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(device);
                Log.i("yangxin", "onReceive: 触发扫描回调");
                deviceAdapter.focusedPosition = bluetoothDevices.size()-1;
                deviceAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //蓝牙扫描完成
                showToast("蓝牙扫描完成");
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

    //进行配对
    private void pairToDevice(BluetoothItem item) {

        String deviceAddress = item.getDeviceAddress();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
        device.createBond();

        BroadcastReceiver pairingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    BluetoothDevice pairedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                    }
                    int bondState = pairedDevice.getBondState();
                    if (bondState == BluetoothDevice.BOND_BONDED) {
                        item.setDeviceStatus("已配对");
                        deviceAdapter.notifyDataSetChanged();
                    } else if (bondState == BluetoothDevice.BOND_BONDING) {
                        item.setDeviceStatus("配对中");
                        deviceAdapter.notifyDataSetChanged();
                    } else if (bondState == BluetoothDevice.BOND_NONE) {
                        item.setDeviceStatus("");
                        showToast("与" + item.getDeviceName() + "配对失败");
                        deviceAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(pairingReceiver, filter);
    }

    private String removeToDevice(BluetoothDevice device) {
        try {
            Method removeBondMethod = device.getClass().getMethod("removeBond");
            boolean result = (boolean) removeBondMethod.invoke(device);
            if (result) {
                return "200";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "201";
    }


    /*
     * 蓝牙配对*/
    private void showPairConectedDialog(BluetoothItem item) {
        Dialog pairDialog = new Dialog(this);

        //加载自定义布局文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.bluetooth_pairtodevice, null);
        pairDialog.setContentView(dialogView);
        final TextView PairTitle = dialogView.findViewById(R.id.pair_title);
        final Button PairButton = dialogView.findViewById(R.id.pair_btn);
        final Button PairCancelButton = dialogView.findViewById(R.id.pair_cancel_btn);

        PairTitle.setText(item.getDeviceName() + " 的蓝牙配对请求");
        pairDialog.show();
        PairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairToDevice(item);
                pairDialog.dismiss();
            }
        });

        PairCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairDialog.dismiss();
            }
        });
    }

    public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
        private List<BluetoothDevice> devices;
        private Context context;

        BluetoothItem bluetoothItem;
        private int focusedPosition = -1;

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
            itemView.setFocusable(true);
            itemView.setFocusableInTouchMode(true);

            BluetoothDevice device = devices.get(position);

            TextView deviceNameTextView = itemView.findViewById(R.id.bluetooth_name);
            TextView deviceStatusTextView = itemView.findViewById(R.id.bluetooth_status);
            String name = deviceNameTextView.getText().toString();
            String status = deviceStatusTextView.getText().toString();
            String address = device.getAddress();
            bluetoothItem = new BluetoothItem(name, status, address);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            bluetoothItem.setDeviceName(device.getName());

            deviceNameTextView.setText(bluetoothItem.getDeviceName());
            int pairState = device.getBondState();
            switch (pairState) {
                case BluetoothDevice.BOND_BONDED:
                    bluetoothItem.setDeviceStatus("已配对");
                    break;
                case BluetoothDevice.BOND_BONDING:
                    bluetoothItem.setDeviceStatus("配对中");
                    break;
                case BluetoothDevice.BOND_NONE:
                    bluetoothItem.setDeviceStatus("");
                    break;
            }
            deviceStatusTextView.setText(bluetoothItem.getDeviceStatus());
            final BluetoothProfile.ServiceListener profileListener = new BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    if (profile == BluetoothProfile.A2DP) {
                        List<BluetoothDevice> devices = proxy.getConnectedDevices();
                        for (BluetoothDevice device : devices) {
                            String deviceAddress = bluetoothItem.getDeviceAddress();
                            if (device.getAddress().equals(deviceAddress)) {
                                // 当前选中的蓝牙设备已连接
                                Log.i("yang", "onServiceConnected: --已连接");
                                bluetoothItem.setDeviceStatus("已连接");
                                deviceStatusTextView.setText(bluetoothItem.getDeviceStatus());
                                return;
                            }
                        }
                    }
                }
                @Override
                public void onServiceDisconnected(int profile) {
                    if (profile == BluetoothProfile.A2DP) {
                        // 处理断开连接的逻辑
                    }
                }
            };

            //TODO:判断是否已连接
            // 检查当前选择的蓝牙设备是否已连接
            bluetoothAdapter.getProfileProxy(context,profileListener,BluetoothProfile.A2DP);

            //TODO:查下为什么连接之后重新扫描会断开连接

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // 子项获得焦点时的操作
                        v.setBackgroundResource(R.drawable.bg_sel);
                        deviceNameTextView.setTextColor(getResources().getColor(R.color.sel_blue));
                        deviceStatusTextView.setTextColor(getResources().getColor(R.color.sel_blue));
                    } else {
                        // 子项失去焦点时的操作
                        v.setBackgroundColor(getResources().getColor(R.color.background_color));
                        deviceNameTextView.setTextColor(getResources().getColor(R.color.white));
                        deviceStatusTextView.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            });

            if (position == focusedPosition) {
                // 设置聚焦效果
                itemView.requestFocus();
                // 设置背景和文本颜色
                itemView.setBackgroundResource(R.drawable.bg_sel);
                deviceNameTextView.setTextColor(getResources().getColor(R.color.sel_blue));
                deviceStatusTextView.setTextColor(getResources().getColor(R.color.sel_blue));
            } else {
                // 清除聚焦效果
                itemView.setBackgroundColor(getResources().getColor(R.color.background_color));
                deviceNameTextView.setTextColor(getResources().getColor(R.color.white));
                deviceStatusTextView.setTextColor(getResources().getColor(R.color.white));
            }

            if (position == 0){
                //设置第一个子项获取焦点
                itemView.requestFocus();
            }

            //设置点击监听
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:蓝牙配对的逻辑
                    String status = bluetoothItem.getDeviceStatus();
                    switch (status) {
                        case "":
                            showPairConectedDialog(bluetoothItem);
                            break;
                        case "已配对":
                            showConnectDeviceDialog(bluetoothItem);
                            break;
                        case "已连接":
//                showCancelPairDialog();
                            break;
                    }
                }
            });


            return itemView;
        }

    }

    /*
     * 蓝牙连接Dialog*/
    private void showConnectDeviceDialog(BluetoothItem item) {
        Dialog conDialog = new Dialog(this, R.style.DialogStyle);

        //加载自定义布局文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.bluetooth_conectodevice, null);
        conDialog.setContentView(dialogView);
        dialogView.setPadding(100, 0, 100, 100);

        final TextView ConTitle = dialogView.findViewById(R.id.bt_connect_title);
        final Button ConConnectButton = dialogView.findViewById(R.id.bt_connect_btn);
        final Button ConIgnoreButton = dialogView.findViewById(R.id.ignore_bt_btn);

        ConTitle.setText("此设备已经配对，是否直接连接？");
        conDialog.show();
        String deviceAddress = item.getDeviceAddress();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        ConnectThread connectThread = new ConnectThread(device);
        ConIgnoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("点击了忽略");
                removeToDevice(device);
                conDialog.dismiss();
            }
        });

        ConConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("点击了连接");
                conDialog.dismiss();
//                connectToDevice(item);
                connectThread.run();
            }
        });
    }

    private void connectToDevice(BluetoothItem item) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String deviceAddress = item.getDeviceAddress();
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

                try {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                    }
                    /*UUID uuid = null;
                    uuid = device.getUuids()[0].getUuid();*/

                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // 替换为您需要连接的服务UUID
                    BluetoothSocket socket  = device.createRfcommSocketToServiceRecord(uuid);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                            }
                            try {
                                socket.connect();
                                showToast("---连接成功---");
                            } catch (IOException e) {
                                e.printStackTrace();
                                showToast("与设备连接失败请重试");
                            } finally {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("---连接失败---");
                    // 连接失败的处理
                }
            }
        });

    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                }
                UUID MY_UUID = UUID.fromString("2CC174A9-095F-C7AF-AD72-F6E4619132B4");
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e("yang", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("yang", "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
//            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("yang", "Could not close the client socket", e);
            }
        }
    }

}
