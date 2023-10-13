package com.twd.twdsettings.network;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.twd.twdsettings.R;
import com.twd.twdsettings.SystemPropertiesUtils;
import com.twd.twdsettings.bean.WifiItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WifiListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView wifiListView;
    List<ScanResult> wifiList;
    WifiManager wifiManager;
    Dialog passwordDialog;
    Dialog connectedDialog;

    List<WifiItem> wifiItems;

    private final Context context = this;
    private SharedPreferences wifi_infoPreferences;

    WifiAdapter adapter;
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
        setContentView(R.layout.activity_netlist);
        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanWifi();
    }

    private void showToast(String text){
        LayoutInflater inflater = getLayoutInflater();
        View customToastView = inflater.inflate(R.layout.custom_toast,null);

        TextView textView = customToastView.findViewById(R.id.toast_text);
        textView.setText(text);
        Toast customToast = new Toast(getApplicationContext());
        customToast.setView(customToastView);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.show();
    }

    private void initListView() {
        wifiListView = findViewById(R.id.wifiListView);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //检查并请求wifi权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            scanWifi();
        }

        wifiListView.setOnItemClickListener(this);
    }

    private void scanWifi() {

        //显示加载动画
        ProgressDialog progressDialog = ProgressDialog.show(context,"",getString(R.string.net_wifiList_scanToast_tv));
        new Thread(new Runnable() {
            @Override
            public void run() {
                wifiManager.startScan();
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }else {
                    wifiList = wifiManager.getScanResults();
                }
                wifiItems = new ArrayList<>();
                for (ScanResult wifi : wifiList){
                    wifiItems.add(new WifiItem(wifi.SSID,wifi.level));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //隐藏加载动画
                        progressDialog.dismiss();
                        adapter = new WifiAdapter(context,wifiItems);
                        wifiListView.setAdapter(adapter);
                        wifiListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                adapter.setFocusedItem(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
            }
        }).start();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        wifi_infoPreferences = getSharedPreferences("wifi_password",MODE_PRIVATE);
        ScanResult selectedWifi = wifiList.get(position);
        String selectedSSID = selectedWifi.SSID;
        if (!wifi_infoPreferences.getString(selectedSSID, "").equals("")){
            showConnectByPreferencesDialog(selectedWifi);
        }else {
            showPasswordDialog(selectedWifi);
        }

    }

    private void showConnectByPreferencesDialog(final ScanResult wifi){
        connectedDialog = new Dialog(this,R.style.DialogStyle);

        //加载自定义布局文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.wifi_connectbypreferences_layout,null);
        connectedDialog.setContentView(dialogView);
        Window window = connectedDialog.getWindow();
        if (window != null){
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
        dialogView.setPadding(50,0,50,50);


        final TextView connectTitle = dialogView.findViewById(R.id.connect_title);
        final LinearLayout connectLL = dialogView.findViewById(R.id.connect_btn);
        final LinearLayout ignoreNetworkLL = dialogView.findViewById(R.id.ignore_network_btn);

        connectTitle.setText(getString(R.string.net_wifiList_connectbypreference_title_tv));
        connectedDialog.show();
        SharedPreferences connectPreferences = getSharedPreferences("wifi_password",MODE_PRIVATE);
        String password  = connectPreferences.getString(wifi.SSID,"");
        connectLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToWifi(wifi,password);
                scanWifi();
                connectedDialog.dismiss();
            }
        });

        ignoreNetworkLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.net_wifiList_connectbypreference_ignoreTitle_tv))
                        .setPositiveButton(getString(R.string.net_wifiList_connectbypreference_ignoreConfirm_tv),
                                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO:

                        // 获取当前连接的WiFi网络的SSID
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        String connectedSsid = wifiInfo.getSSID();
                        Log.i("yang", "onClick: connectedSsid = " + connectedSsid);
                        Log.i("yang", "onClick: wifi.SSID = " + wifi.SSID);
                        String currentNetSSID = "\"" + wifi.SSID + "\"";
                        if (currentNetSSID.equals(connectedSsid)){
                            wifiManager.disconnect();
                        }

                        SharedPreferences.Editor ignoreEditor = getSharedPreferences("wifi_password",MODE_PRIVATE).edit();
                        ignoreEditor.putString(wifi.SSID,"");
                        ignoreEditor.apply();

                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                        connectedDialog.dismiss();
                    }
                })
                        .setNegativeButton(getString(R.string.net_wifiList_connectbypreference_ignoreCancel_tv)
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO:点击取消按钮关闭AlertDialog
                                dialog.dismiss();
                                connectedDialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    private void showPasswordDialog(final ScanResult wifi){
        passwordDialog = new Dialog(this,R.style.DialogStyle);
        Log.i("yang", "showPasswordDialog: ------wifi,ssid = " + wifi.SSID);

        //加载自定义布局文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.wifi_connect_layout,null);
        passwordDialog.setContentView(dialogView);

        final EditText passwordEditText = dialogView.findViewById(R.id.password_edittext);
        final TextView passwordTitle = dialogView.findViewById(R.id.password_title);
        passwordTitle.setText(wifi.SSID);
        //设置底部空间
        dialogView.setPadding(0,0,0,50);
        passwordDialog.show();
        LinearLayout connectLL = dialogView.findViewById(R.id.password_btn);
        connectLL.setFocusable(false);
        //设置文本输入框的输入类型
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //创建一个TextWatcher来监听EditText中的文本变化
        TextWatcher textWatcher  = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                Log.i("yang","字符长度："+password.length());
                connectLL.setFocusable(password.length() >= 8);
                System.out.println(password.length());
            }
        };
        //将TextWatcher添加到EditText中
        passwordEditText.addTextChangedListener(textWatcher);
        //设置连接按钮的点击事件
        connectLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                connectToWifi(wifi,password);
                passwordDialog.dismiss();
            }
        });


    }

    private void connectToWifi(ScanResult wifi,String password){

        //显示加载动画
        ProgressDialog progressDialog = ProgressDialog.show(context,"",getString(R.string.net_wifiList_connecttoWifi_title_tv));
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("yang","开始连接");
                //TODO:使用wifi.SSID和password参数进行连接
                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = "\"" + wifi.SSID + "\"";
                wifiConfig.preSharedKey = "\"" + password + "\"";
                //保存密码
                SharedPreferences.Editor editor = getSharedPreferences("wifi_password",MODE_PRIVATE).edit();
                editor.putString(wifi.SSID,password);
                editor.apply();
                int networkId = wifiManager.addNetwork(wifiConfig);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(networkId,true);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                                boolean isConnected = wifiInfo.getNetworkId() == networkId && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED;
                                if (isConnected){
//                                    Toast.makeText(context, "连接成功！", Toast.LENGTH_SHORT).show();
                                    scanWifi();
                                    Log.i("yang", "run: -------成功");
                                    showToast(getString(R.string.net_wifiList_connecttoWifi_isConnected_true));
                                }else {
//                                    Toast.makeText(context, "密码错误请重试！", Toast.LENGTH_SHORT).show();
                                    scanWifi();
                                    Log.i("yang", "run: ---------失败");
                                    showToast(getString(R.string.net_wifiList_connecttoWifi_isConnected_false));
                                }
                                progressDialog.dismiss();
                            }
                        },5000);
                    }
                });
            }
        }).start();
    }

    public class WifiAdapter extends ArrayAdapter<WifiItem>{

        private LayoutInflater inflater;
        int signalStrength;
        ImageView wifiSignalImageView;
        TextView isCheckedText;

        private int focusedItem = -1;

        public WifiAdapter(@NonNull Context context, List<WifiItem> wifiItems) {
            super(context, 0,wifiItems);
            inflater = LayoutInflater.from(context);
        }

        public void setFocusedItem(int position){
                focusedItem = position;
                notifyDataSetChanged();
        }

        @SuppressLint("ResourceType")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null){
                itemView = inflater.inflate(R.layout.wifi_list_item,parent,false);
            }


            TextView wifiNameTextView = itemView.findViewById(R.id.wifi_ssid);
            wifiSignalImageView = itemView.findViewById(R.id.wifiSignalImageView);
            isCheckedText = itemView.findViewById(R.id.text_isChecked);

            // 获取当前连接的WiFi网络的SSID
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String connectedSsid = wifiInfo.getSSID();

            int btnBgResId_sel = 0; int textColor_sel = 0;
            int btnBgResId = 0; int textColor = 0;
            int wifiSignal_full_sel = 0; int wifiSignal_medium_sel = 0; int wifiSignal_low_sel = 0;
            int wifiSignal_full = 0; int wifiSignal_medium = 0; int wifiSignal_low = 0;
            switch (String.valueOf(theme_code)){
                case "0": //IceBlue
                    btnBgResId_sel = R.color.customWhite; btnBgResId = Color.TRANSPARENT;
                    textColor_sel = R.color.sel_blue; textColor = R.color.customWhite;
                    wifiSignal_full_sel = R.drawable.wifisignal_sel_3; wifiSignal_medium_sel = R.drawable.wifisignal_sel_2; wifiSignal_low_sel = R.drawable.wifisignal_sel_1;
                    wifiSignal_full = R.drawable.wifisignal_unsel_3; wifiSignal_medium = R.drawable.wifisignal_unsel_2; wifiSignal_low = R.drawable.wifisignal_unsel_1;
                    break;
                case "1": //KapokWhite
                    btnBgResId_sel = R.drawable.red_border;
                    textColor_sel = R.color.text_red;
                    btnBgResId = Color.TRANSPARENT;
                    textColor = R.color.black;
                    wifiSignal_full_sel = R.drawable.wifi_red;
                    wifiSignal_medium_sel = R.drawable.wifisignal_red_medium;
                    wifiSignal_low_sel = R.drawable.wifisignal_red_low;
                    wifiSignal_full = R.drawable.wifi_black;
                    wifiSignal_medium = R.drawable.wifisignal_black_medium;
                    wifiSignal_low = R.drawable.wifisignal_black_low;
                    break;
                case "2": //StarBlue
                    btnBgResId_sel = R.color.text_red;
                    textColor_sel = R.color.customWhite;
                    btnBgResId = Color.TRANSPARENT;
                    textColor = R.color.customWhite;
                    wifiSignal_full_sel = R.drawable.wifisignal_unsel_3;
                    wifiSignal_medium_sel = R.drawable.wifisignal_unsel_2;
                    wifiSignal_low_sel = R.drawable.wifisignal_unsel_1;
                    wifiSignal_full = R.drawable.wifisignal_unsel_3;
                    wifiSignal_medium = R.drawable.wifisignal_unsel_2;
                    wifiSignal_low = R.drawable.wifisignal_unsel_1;
                    break;
            }

            WifiItem wifiItem = getItem(position);
            if (wifiItem != null){
                if(wifiItem.getName().isEmpty()){
                    wifiNameTextView.setText("unknown");
                }else {
                    wifiNameTextView.setText(wifiItem.getName());
                }
                 signalStrength= wifiItem.getSignalStrength();
                if (signalStrength >= -50){
                    wifiSignalImageView.setImageResource(wifiSignal_full); //full
                } else if (signalStrength >= -70) {
                    wifiSignalImageView.setImageResource(wifiSignal_medium); //medium
                } else  {
                    wifiSignalImageView.setImageResource(wifiSignal_low); //low
                }
            }

            wifi_infoPreferences = getSharedPreferences("wifi_password",MODE_PRIVATE);
            // 获取展示的网络的SSID
            String displayedSsid = wifiNameTextView.getText().toString();
            Log.i("yang1", "getView: 当前展示的网络SSID = " + displayedSsid + " , 当前连接的网络的SSID = " + connectedSsid);
            if (!wifi_infoPreferences.getString(displayedSsid, "").equals("")){
                isCheckedText.setText(getString(R.string.net_wifiList_isCheckedText_saved));
            } else {
                isCheckedText.setText("");
            }
            // 判断当前连接的网络和展示的网络是否相同
            String displayedSsidFormat = "\"" + displayedSsid + "\"";
            if (displayedSsidFormat.equals(connectedSsid)){
                isCheckedText.setText("已连接");
            }

            //设置聚焦效果
            if (position == focusedItem){
                itemView.setBackgroundResource(btnBgResId_sel);
                wifiNameTextView.setTextColor(getResources().getColor(textColor_sel));
                isCheckedText.setTextColor(getResources().getColor(textColor_sel));
                if (signalStrength >= -50){
                    wifiSignalImageView.setImageResource(wifiSignal_full_sel);
                } else if (signalStrength >= -70) {
                    wifiSignalImageView.setImageResource(wifiSignal_medium_sel);
                } else {
                    wifiSignalImageView.setImageResource(wifiSignal_low_sel);
                }
            }else {
                itemView.setBackgroundResource(btnBgResId);
                wifiNameTextView.setTextColor(getResources().getColor(textColor));
                isCheckedText.setTextColor(getResources().getColor(textColor));
                if (signalStrength >= -50){
                    wifiSignalImageView.setImageResource(wifiSignal_full);
                } else if (signalStrength >= -70) {
                    wifiSignalImageView.setImageResource(wifiSignal_medium);
                } else {
                    wifiSignalImageView.setImageResource(wifiSignal_low);
                }
            }
            Log.i("yang","Color = "+itemView.getBackground()+"icon = "+wifiSignalImageView.getDrawable());
            return itemView;
        }
    }
}
