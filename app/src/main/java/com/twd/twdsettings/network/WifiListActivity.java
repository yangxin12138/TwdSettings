package com.twd.twdsettings.network;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import com.twd.twdsettings.R;
import com.twd.twdsettings.bean.WifiItem;

import java.util.ArrayList;
import java.util.List;

public class WifiListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView wifiListView;
    List<ScanResult> wifiList;
    WifiManager wifiManager;

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
        wifiManager.startScan();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            wifiList = wifiManager.getScanResults();
        }
        List<WifiItem> wifiItems = new ArrayList<>();
        for (ScanResult wifi : wifiList){
            wifiItems.add(new WifiItem(wifi.SSID,wifi.level));
        }
        WifiAdapter adapter = new WifiAdapter(this,wifiItems);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ScanResult selectedWifi = wifiList.get(position);
        showPasswordDialog(selectedWifi);
    }

    private void showPasswordDialog(final ScanResult wifi){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect to " + wifi.SSID);

        //加载自定义布局文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.wifi_connect_layout,null);
        builder.setView(dialogView);

        final EditText passwordEditText = dialogView.findViewById(R.id.password_edittext);
        Button connectButton = dialogView.findViewById(R.id.password_btn);
        //设置文本输入框的输入类型
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        //设置连接按钮的点击事件
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                connectToWifi(wifi,password);
            }
        });
        builder.show();
    }

    private void connectToWifi(ScanResult wifi,String password){
        //TODO:使用wifi.SSID和password参数进行连接
    }

    public class WifiAdapter extends ArrayAdapter<WifiItem>{

        private LayoutInflater inflater;
        int signalStrength;
        ImageView wifiSignalImageView;

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
