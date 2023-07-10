package com.twd.twdsettings.network;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import com.twd.twdsettings.R;

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
        List<String> wifiNames = new ArrayList<>();
        for (ScanResult wifi : wifiList){
            wifiNames.add(wifi.SSID);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,wifiNames);
        wifiListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ScanResult selectedWifi = wifiList.get(position);
        showPasswordDialog(selectedWifi);
    }

    private void showPasswordDialog(final ScanResult wifi){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect to " + wifi.SSID);
        final EditText passwordEditText = new EditText(this);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordEditText);
        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordEditText.getText().toString();
                //TODO:使用密码连接wifi的逻辑
                connectToWifi(wifi,password);
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.show();
    }

    private void connectToWifi(ScanResult wifi,String password){
        //TODO:使用wifi.SSID和password参数进行连接
    }


}
