package com.twd.twdsettings.network;

import android.net.InetAddresses;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.twdsettings.R;
import com.twd.twdsettings.bean.SpeedTestResult;
import com.twd.twdsettings.view.SpeedometerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class NetSpeedActivity extends AppCompatActivity {

    private Button startButton;
    private SpeedometerView speedometerView;
    private TextView speedTestResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_speed_activity);

        speedometerView = findViewById(R.id.speedometerView);
        speedTestResult = findViewById(R.id.speed_testResult);
        startButton = findViewById(R.id.buttonStart);

        startButton.setFocusable(true);
        startButton.setFocusableInTouchMode(true);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpeedTestTask().execute();
            }
        });
    }

    private class SpeedTestTask extends AsyncTask<Void,Float, SpeedTestResult> {

        @Override
        protected void onPreExecute() {
            startButton.setEnabled(false);
        }

        @Override
        protected SpeedTestResult doInBackground(Void... voids) {

            //获取当前网络SSID
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();

            //获取当前设备的IP地址
            String ipAddress =  getIPAddress();

            // 进行网速测试并获取速度
            float speed = performSpeedTest();

            publishProgress(speed);


            Log.i("yang","平均速度："+speed);
            return new SpeedTestResult(ssid, speed, ipAddress);
        }

        @Override
        protected void onPostExecute(SpeedTestResult result) {
            startButton.setEnabled(true);

            //显示测试结果
            String testResult = "当前网络SSID：" + result.getSsid()+
                    "\n测试网速：" + result.getAverageSpeed() + "kb/s"+
                    "\nIp地址：" + result.getIpAddress();


            speedTestResult.setText(testResult);
        }

        private String getIPAddress(){
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();en.hasMoreElements();){
                    NetworkInterface intf= en.nextElement();
                    for (Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses();enumIPAddr.hasMoreElements();){
                        InetAddress inetAddress  = enumIPAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address){
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }catch (SocketException e){
                e.printStackTrace();
            }
            return null;
        }

        private float performSpeedTest() {
            // 进行网速测试的代码
            float speed = 0.0f;
            try {
                URL url = new URL("https://w.wallhaven.cc/full/x6/wallhaven-x6pl9v.jpg");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000); //设置连接超时时间为5秒
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK){
                    long startTime = System.currentTimeMillis();
                    InputStream inputStream = connection.getInputStream();
                    File file = new File(getFilesDir(),"downloaded_file.jpg");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalBytesRead = 0;
                    long lastUpdateTime = startTime;
                    while ((bytesRead = inputStream.read(buffer))!= -1){
                        totalBytesRead += bytesRead;
                        long currentTime = System.currentTimeMillis();
                        long duration = currentTime - lastUpdateTime;

                        if (duration >= 1000){
                            speed = calculateSpeed(totalBytesRead,duration);
                            publishProgress(speed);
                            lastUpdateTime = currentTime;
                        }
                        outputStream.write(buffer,0,bytesRead);
                    }
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    speed = calculateSpeed(totalBytesRead,duration);
                    publishProgress(speed);
                    inputStream.close();
                    outputStream.close();

                    if (file.exists()){file.delete();} else {Log.i("yang","没有下载成功");}
                    return speed;
                }
            }catch (IOException e){e.printStackTrace();}
            return speed;
        }

//        https://img.zcool.cn/community/019p5y1ardipacs0fxjcg93331.jpg?x-oss-process=image/format,webp

        @Override
        protected void onProgressUpdate(Float... values) {

            // 更新速度表的值
            float speed = values[0];
            speedometerView.setSpeed(speed);

            }
        private float calculateSpeed(long totalBytesRead ,long duration){
            //计算下载速度 kb/s
            float speed = (float) totalBytesRead / duration;
            speed = speed * 1000 / 1024;
            speed = (float) Math.round(speed * 100) / 100;
            return speed;
        }
    }
}
