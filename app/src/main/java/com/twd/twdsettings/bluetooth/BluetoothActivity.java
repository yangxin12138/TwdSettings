package com.twd.twdsettings.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.twdsettings.R;
import com.twd.twdsettings.SystemPropertiesUtils;
import com.twd.twdsettings.bean.BluetoothItem;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class BluetoothActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> bluetoothDevices;
    private BluetoothDeviceAdapter deviceAdapter;
    private RecyclerView recyclerView;

    private LinearLayout bluetooth_LL_bt;
    private LinearLayout bluetooth_LL_search;
    private TextView bluetooth_tv_bt;
    private Switch bluetooth_switch;
    private TextView bluetooth_tv_search;
    private boolean isChecked;
    private List<BluetoothDevice> pairedDevicesList = new ArrayList<>();//保存已配对的设备

    private Context mContext = this;
    String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_bluetooth);

        //初始化BluetoothAdapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            //设备不支持蓝牙
            showToast(getString(R.string.bluetooth_index_NotSupport));
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
                if (bluetooth_tv_search.getText().equals(getString(R.string.bluetooth_index_search_again_tv))) {
                    bluetoothDevices.clear();
                    bluetooth_tv_search.setText(getString(R.string.bluetooth_index_search_tv));
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
                    recyclerView.setVisibility(View.VISIBLE);
                    bluetooth_tv_search.setText(getString(R.string.bluetooth_index_search_tv));
                } else {
                    //关闭蓝牙
                    disableBluetooth();
                    bluetooth_tv_search.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);

                }
            }
        });

        //初始化ListView和适配器
        recyclerView = findViewById(R.id.bluetooth_recyclerview);
        int spacing = 10;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(this,spacing));
        bluetoothDevices = new ArrayList<>();
        deviceAdapter = new BluetoothDeviceAdapter(this, bluetoothDevices, pairedDevicesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(deviceAdapter);
        getPairedDevices();

        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(bluetoothReceiver, filter);
        //开始扫描蓝牙设备
        startBluetoothScan();
        registerBroadcastReveiver();
    }

    /*获取已配对的设备*/
    private void getPairedDevices() {
        checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        pairedDevicesList.clear();
        pairedDevicesList.addAll(pairedDevices);
        deviceAdapter.notifyDataSetChanged();
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

    private void enableBluetooth() { /*打开蓝牙*/
        if (!bluetoothAdapter.isEnabled()) {
            checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
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

    private void disableBluetooth() { /*关闭蓝牙*/
        if (bluetoothAdapter.isEnabled()) {
            checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
            bluetoothAdapter.disable();
            stopBluetoothScan();
        }
    }

    private Handler handler = new Handler();

    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            checkPermission(Manifest.permission.BLUETOOTH_SCAN);
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            deviceAdapter.notifyDataSetChanged();
        }
    };

    private void startBluetoothScan() {
        //检查是否已经在扫描
        checkPermission(Manifest.permission.BLUETOOTH_SCAN);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        //开始扫描
        getPairedDevices();
        bluetoothAdapter.startDiscovery();
        Log.i("yang", "startBluetoothScan: --开始扫描");

        // 延迟一段时间后停止扫描
        handler.postDelayed(stopScanRunnable, 20000);
        Log.i("yang", "startBluetoothScan: --扫描结束");

    }

    private void stopBluetoothScan() {
        checkPermission(Manifest.permission.BLUETOOTH_SCAN);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothDevices.clear();
        getPairedDevices();
        deviceAdapter.notifyDataSetChanged();

    }

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //发现新设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!isPairedDevice(device)) {
                    bluetoothDevices.add(device);
                    Log.i("yangxin", "onReceive: 触发扫描回调");
                    deviceAdapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //蓝牙扫描完成
                showToast(getString(R.string.bluetooth_index_search_finish));
                bluetooth_tv_search.setText(getString(R.string.bluetooth_index_search_again_tv));
            }
        }
    };

    private boolean isPairedDevice(BluetoothDevice device) {
        for (BluetoothDevice pairedDevice : pairedDevicesList) {
            if (pairedDevice.getAddress().equals(device.getAddress())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBluetoothScan();
        unregisterReceiver(bluetoothReceiver);
        unRegisterBroadcastReceiver();
    }

    //进行配对
    private void pairToDevice(BluetoothItem item) {

        String deviceAddress = item.getDeviceAddress();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
        device.createBond();

        BroadcastReceiver pairingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    BluetoothDevice pairedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
                    int bondState = pairedDevice.getBondState();
                    if (bondState == BluetoothDevice.BOND_BONDED) {
                        item.setDeviceStatus(getString(R.string.bluetooth_index_deviceStatus_Bonded));//已配对
                        deviceAdapter.notifyDataSetChanged();
                    } else if (bondState == BluetoothDevice.BOND_BONDING) {
                        item.setDeviceStatus(getString(R.string.bluetooth_index_deviceStatus_Bonding));//配对中
                        deviceAdapter.notifyDataSetChanged();
                    } else if (bondState == BluetoothDevice.BOND_NONE) {
                        item.setDeviceStatus("");
                        showToast(item.getDeviceName() +
                                getString(R.string.bluetooth_index_deviceStatus_BondFiled));//绑定失败
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
        checkPermission(Manifest.permission.BLUETOOTH_SCAN);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        Dialog pairDialog = new Dialog(this,R.style.DialogStyle);

        //加载自定义布局文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.bluetooth_pairtodevice, null);
        pairDialog.setContentView(dialogView);
        dialogView.setPadding(100,0,100,50);

        final TextView PairTitle = dialogView.findViewById(R.id.pair_title);
        final LinearLayout PairLL = dialogView.findViewById(R.id.pair_btn);
        final LinearLayout PairCancelLL = dialogView.findViewById(R.id.pair_cancel_btn);
        PairTitle.setText(item.getDeviceName() + getString(R.string.bluetooth_index_piarTodevice_title));
        pairDialog.show();
        PairLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairToDevice(item);
                pairDialog.dismiss();
            }
        });

        PairCancelLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairDialog.dismiss();
            }
        });
    }

    public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {
        private List<BluetoothDevice> devices;
        private List<BluetoothDevice> pairedDevicesList;
        private Context context;
        private ConnectThread connectThread;

        public BluetoothDeviceAdapter(Context context, List<BluetoothDevice> devices, List<BluetoothDevice> pairedDevicesList) {
            this.context = context;
            this.devices = devices;
            this.pairedDevicesList = pairedDevicesList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_list_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BluetoothDevice device;
            if (position < pairedDevicesList.size()) {
                device = pairedDevicesList.get(position);
            } else {
                device = devices.get(position - pairedDevicesList.size());
            }
            //在主线程中创建Handler对象
            Handler mHandler = new Handler(Looper.getMainLooper());
            connectThread = new ConnectThread(device, mHandler);
            holder.bindData(device);
        }

        @Override
        public int getItemCount() {
            return devices.size() + pairedDevicesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView deviceNameTextView;
            public TextView deviceStatusTextView;
            public ImageView icon_bluetooth;
            BluetoothItem bluetoothItem;
            int iconResIdSel = 0; int iconResIdUnSel = 0;
            int textViewResIdSel = 0; int textViewResIdUnSel = 0;
            int backgroundResIdSel = 0; int backgroundResIdUnSel = 0;

            public ViewHolder(View itemView) {
                super(itemView);
                icon_bluetooth = itemView.findViewById(R.id.icon_bluetooth);
                deviceNameTextView = itemView.findViewById(R.id.bluetooth_name);
                deviceStatusTextView = itemView.findViewById(R.id.bluetooth_status);

                switch (String.valueOf(theme_code)){
                    case "0":
                        iconResIdSel = R.drawable.icon_bluetooth_iceblue_sel;
                        iconResIdUnSel = R.drawable.icon_bluetooth_iceblue_unsel;
                        textViewResIdSel = R.color.sel_blue; textViewResIdUnSel = R.color.white;
                        backgroundResIdSel = R.color.customWhite; backgroundResIdUnSel = Color.TRANSPARENT;
                        break;
                    case "1":
                        iconResIdSel = R.drawable.icon_bluetooth_kapokwhite_sel;
                        iconResIdUnSel = R.drawable.icon_bluetooth_starblue;
                        textViewResIdSel = R.color.text_red_new; textViewResIdUnSel =R.color.black;
                        backgroundResIdSel = R.drawable.red_border; backgroundResIdUnSel = R.drawable.black_border;
                        break;
                    case "2":
                        iconResIdSel = R.drawable.icon_bluetooth_starblue;
                        iconResIdUnSel = R.drawable.icon_bluetooth_starblue;
                        textViewResIdSel = R.color.customWhite; textViewResIdUnSel =R.color.customWhite;
                        backgroundResIdSel = R.color.text_red_new; backgroundResIdUnSel = Color.TRANSPARENT;
                        break;
                }

                itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            v.setBackgroundResource(backgroundResIdSel);
                            icon_bluetooth.setImageResource(iconResIdSel);
                            deviceNameTextView.setTextColor(ContextCompat.getColor(context, textViewResIdSel));
                            deviceStatusTextView.setTextColor(ContextCompat.getColor(context, textViewResIdSel));
                        } else {
                            v.setBackgroundResource(backgroundResIdUnSel);
                            icon_bluetooth.setImageResource(iconResIdUnSel);
                            deviceNameTextView.setTextColor(ContextCompat.getColor(context, textViewResIdUnSel));
                            deviceStatusTextView.setTextColor(ContextCompat.getColor(context, textViewResIdUnSel));
                        }
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String status = deviceStatusTextView.getText().toString();
                        String connected = getString(R.string.bluetooth_index_deviceStatus_Connected);
                        String none = "";
                        String paired = getString(R.string.bluetooth_index_deviceStatus_Bonded);
                        if (status.equals(none)){
                            showPairConectedDialog(bluetoothItem);
                        } else if (status.equals(paired)) {
                            showConnectDeviceDialog(0,bluetoothItem,connectThread);
                        } else if (status.equals(connected)) {
                            showConnectDeviceDialog(1,bluetoothItem,connectThread);
                        }
                    }
                });
            }

            public void bindData(BluetoothDevice device) {
                checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
                String name = device.getName();
                if (name == null){
                    name = device.getAddress();
                }
                String status = "";
                String address = device.getAddress();
                bluetoothItem = new BluetoothItem(name,status,address);
                deviceNameTextView.setText(bluetoothItem.getDeviceName());
                int pairState = device.getBondState();
                switch (pairState) {
                    case BluetoothDevice.BOND_BONDED:
                        bluetoothItem.setDeviceStatus(getString(R.string.bluetooth_index_deviceStatus_Bonded));
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        bluetoothItem.setDeviceStatus(getString(R.string.bluetooth_index_deviceStatus_Bonding));
                        break;
                    case BluetoothDevice.BOND_NONE:
                        bluetoothItem.setDeviceStatus("");
                        break;
                }
                ArrayList<BluetoothDevice> deviceList = checkLinkState();
                boolean isConnected = false;
                for (BluetoothDevice connectedDevice : deviceList) {
                    if (connectedDevice.getAddress().equals(device.getAddress())) {
                        isConnected = true;
                        break;
                    }
                }
                if (isConnected) {
                    bluetoothItem.setDeviceStatus(getString(R.string.bluetooth_index_deviceStatus_Connected));
                }
                deviceStatusTextView.setText(bluetoothItem.getDeviceStatus());

            }
        }
    }

    private ArrayList checkLinkState(){
        ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {
            //得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);
            if (state == BluetoothAdapter.STATE_CONNECTED) {
                checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
                Set<BluetoothDevice> devices = adapter.getBondedDevices();
                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        Log.i("yang", "checkLinkState: " + "connected:" + device.getName());
                        deviceList.add(device);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceList;
    }

    private void checkPermission(String Permission) {
        if (ActivityCompat.checkSelfPermission(this, Permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Permission}, 1);
        }
    }


    /*
     * 蓝牙连接Dialog*/
    private void showConnectDeviceDialog(int flag,BluetoothItem item,ConnectThread connectThread) {
        checkPermission(Manifest.permission.BLUETOOTH_SCAN);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        Dialog conDialog = new Dialog(this, R.style.DialogStyle);
        //TODO: 1.点击连接之后停止扫描 2.检测已连接的蓝牙设备

        //加载自定义布局文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.bluetooth_conectodevice, null);
        conDialog.setContentView(dialogView);
        dialogView.setPadding(100, 0, 100, 50);

        final TextView ConTitle = dialogView.findViewById(R.id.bt_connect_title);
        final LinearLayout ConConnectLL = dialogView.findViewById(R.id.bt_connect_btn);
        final LinearLayout ConCancelLL = dialogView.findViewById(R.id.bt_cancel_btn);
        final LinearLayout ConIgnoreLL = dialogView.findViewById(R.id.ignore_bt_btn);

        if (flag == 0){
            ConTitle.setText(getString(R.string.bluetooth_index_Connectdialog_flag_0));
            ConCancelLL.setVisibility(View.GONE);
            ConConnectLL.setVisibility(View.VISIBLE);
        } else if (flag == 1) {
            ConTitle.setText(getString(R.string.bluetooth_index_Connectdialog_flag_1));
            ConCancelLL.setVisibility(View.VISIBLE);
            ConConnectLL.setVisibility(View.GONE);
        }

        conDialog.show();
        String deviceAddress = item.getDeviceAddress();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        ConIgnoreLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showToast("点击了忽略");
                removeToDevice(device);
                conDialog.dismiss();
                deviceAdapter.notifyDataSetChanged();
            }
        });
        ConConnectLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showToast("点击了连接");
                conDialog.dismiss();
                connectThread.run();
                item.setConnectThread(connectThread);
                Log.i("yang", "onClick: 点击了连接connectThread = " + connectThread);
                deviceAdapter.notifyDataSetChanged();
            }
        });

        ConCancelLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conDialog.dismiss();
            }
        });
    }

    public class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final Handler mHandler;

        public ConnectThread(BluetoothDevice device, Handler handler) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            mHandler = handler;

            try {

                UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e("yang", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            checkPermission(Manifest.permission.BLUETOOTH_SCAN);
            bluetoothAdapter.cancelDiscovery();
            //创建一个新的线程来执行连接操作
            Thread connectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
                        mmSocket.connect();
                        Log.i("yang", "run: 连接成功");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast(getString(R.string.bluetooth_index_connect_succese));}
                        });
                    } catch (IOException connectException) {
                        try {
                            mmSocket.close();
                            Log.e("yang", "run: 连接失败" );
                            connectException.printStackTrace();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(getString(R.string.bluetooth_index_connect_failed));}
                            });
                        } catch (IOException closeException) {
                            Log.e("yang", "Could not close the client socket", closeException);
                        }
                        return;
                    }
                }
            });
            connectThread.start();//启动连接线程
            try {
                connectThread.join();//等待连接线程执行完毕
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast(getString(R.string.bluetooth_index_close));
                    }
                });
            } catch (IOException e) {
                Log.e("yang", "Could not close the client socket", e);
            }
        }
    }

    private void registerBroadcastReveiver() {
        IntentFilter connectStateChangeFilter = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        IntentFilter stateChangeFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter connectFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter disConnectFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(stateChangeReceiver, connectStateChangeFilter);
        registerReceiver(stateChangeReceiver, stateChangeFilter);
        registerReceiver(stateChangeReceiver, connectFilter);
        registerReceiver(stateChangeReceiver, disConnectFilter);
    }

    private void unRegisterBroadcastReceiver() {
        unregisterReceiver(stateChangeReceiver);
    }

    private BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                deviceAdapter.notifyDataSetChanged();
            }
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                deviceAdapter.notifyDataSetChanged();
            }

            if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                deviceAdapter.notifyDataSetChanged();
            }

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                deviceAdapter.notifyDataSetChanged();
            }
        }
    };

}


