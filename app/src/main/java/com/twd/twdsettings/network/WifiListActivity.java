package com.twd.twdsettings.network;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.twd.twdsettings.bean.WifiItem;

import java.util.ArrayList;
import java.util.List;

public class WifiListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView wifiListView;
    List<ScanResult> wifiList;
    WifiManager wifiManager;
    Dialog passwordDialog;

    List<WifiItem> wifiItems;

    private final Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netlist);
        initListView();
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
        ProgressDialog progressDialog = ProgressDialog.show(context,"","扫描中，请稍等...");
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
                        WifiAdapter adapter = new WifiAdapter(context,wifiItems);
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
        ScanResult selectedWifi = wifiList.get(position);
        showPasswordDialog(selectedWifi);
    }

    private void showPasswordDialog(final ScanResult wifi){
        passwordDialog = new Dialog(this);
        passwordDialog.setTitle("Connect to " + wifi.SSID);

        //加载自定义布局文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.wifi_connect_layout,null);
        passwordDialog.setContentView(dialogView);

        final EditText passwordEditText = dialogView.findViewById(R.id.password_edittext);
        final TextView passwordTitle = dialogView.findViewById(R.id.password_title);
        passwordTitle.setText(wifi.SSID);
        Button connectButton = dialogView.findViewById(R.id.password_btn);
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
                connectButton.setFocusable(password.length() >= 8);
            }
        };
        //将TextWatcher添加到EditText中
        passwordEditText.addTextChangedListener(textWatcher);
        //设置连接按钮的点击事件
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                connectToWifi(wifi,password);
            }
        });

        //设置底部空间
        dialogView.setPadding(0,0,0,50);
        passwordDialog.show();
    }

    private void connectToWifi(ScanResult wifi,String password){

        //显示加载动画
        ProgressDialog progressDialog = ProgressDialog.show(context,"","连接中，请稍等...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("yang","开始连接");
                //TODO:使用wifi.SSID和password参数进行连接
                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = "\"" + wifi.SSID + "\"";
                wifiConfig.preSharedKey = "\"" + password + "\"";

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
                                    Toast.makeText(context, "连接成功！", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, "密码错误请重试！", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                                passwordDialog.dismiss();
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

            // 获取展示的网络的SSID
            String displayedSsid = wifiNameTextView.getText().toString();
            // 判断当前连接的网络和展示的网络是否相同
            if (connectedSsid != null && connectedSsid.equals("\"" + displayedSsid + "\"")) {
                isCheckedText.setText("已连接");
            } else {
                isCheckedText.setText("");
            }

            WifiItem wifiItem = getItem(position);
            if (wifiItem != null){
                wifiNameTextView.setText(wifiItem.getName());
                 signalStrength= wifiItem.getSignalStrength();
                if (signalStrength >= -50){
                    wifiSignalImageView.setImageResource(R.drawable.wifisignal_unsel_3); //full
                } else if (signalStrength >= -70) {
                    wifiSignalImageView.setImageResource(R.drawable.wifisignal_unsel_2); //medium
                } else  {
                    wifiSignalImageView.setImageResource(R.drawable.wifisignal_unsel_1); //low
                }
            }

            //设置聚焦效果
            if (position == focusedItem){
                itemView.setBackgroundResource(R.drawable.bg_sel);
                wifiNameTextView.setTextColor(getResources().getColor(R.color.sel_blue));
                isCheckedText.setTextColor(getResources().getColor(R.color.sel_blue));
                if (signalStrength >= -50){
                    wifiSignalImageView.setImageResource(R.drawable.wifisignal_sel_3);
                } else if (signalStrength >= -70) {
                    wifiSignalImageView.setImageResource(R.drawable.wifisignal_sel_2);
                } else {
                    wifiSignalImageView.setImageResource(R.drawable.wifisignal_sel_1);
                }
            }else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
                wifiNameTextView.setTextColor(getResources().getColor(R.color.white));
                isCheckedText.setTextColor(getResources().getColor(R.color.white));
                if (signalStrength >= -50){
                    wifiSignalImageView.setImageResource(R.drawable.wifisignal_unsel_3);
                } else if (signalStrength >= -70) {
                    wifiSignalImageView.setImageResource(R.drawable.wifisignal_unsel_2);
                } else {
                    wifiSignalImageView.setImageResource(R.drawable.wifisignal_unsel_1);
                }
            }
            Log.i("yang","Color = "+itemView.getBackground()+"icon = "+wifiSignalImageView.getDrawable());
            return itemView;
        }
    }
}
