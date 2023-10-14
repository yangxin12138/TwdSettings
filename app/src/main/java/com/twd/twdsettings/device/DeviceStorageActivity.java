package com.twd.twdsettings.device;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.twd.twdsettings.R;
import com.twd.twdsettings.SystemPropertiesUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class DeviceStorageActivity extends AppCompatActivity {
    private static final String TAG = DeviceStorageActivity.class.getName();
    private TextView storage_total ;
    private TextView storage_available;
    private TextView storage_system;
    private TextView storage_app;
    private TextView storage_other;
    private ImageView legend_total;
    private ImageView legend_available;
    private ImageView legend_system;
    private ImageView legend_app;
    private ImageView legend_other;

    String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");
    TypedArray typedArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        typedArray = obtainStyledAttributes(new int[]{
           R.attr.textColor
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_storage);
        initView();
    }

    private double FranceReplace(String rom){
        double Rom_Long;
        if (Locale.getDefault().getLanguage().equals("fr")){
            Rom_Long = Double.parseDouble(rom.replace(",","."));
        }else {
            Rom_Long = Double.parseDouble(rom);
        }
        return Rom_Long;
    }

    private void initView(){
         storage_total = findViewById(R.id.storage_total);
         storage_available = findViewById(R.id.storage_available);
         storage_system = findViewById(R.id.storage_system);
         storage_app = findViewById(R.id.storage_app);
         storage_other = findViewById(R.id.storage_other);
         legend_total = findViewById(R.id.storage_color_total);
         legend_available = findViewById(R.id.storage_color_available);
         legend_system = findViewById(R.id.storage_color_system);
         legend_app = findViewById(R.id.storage_color_app);
         legend_other = findViewById(R.id.storage_color_other);

         //总容量
        String totalRom = getTotalRom();
        String total = totalRom.substring(0, totalRom.indexOf("GB")).trim();
        double total_Long = FranceReplace(total);

        //可用容量
        String availableRom = getAvailableStorage();
        String available = availableRom.substring(0,availableRom.indexOf("GB")).trim();
        double available_Long = FranceReplace(available);

        //系统占用
        String systemRom = getSystemFileSize();
        String system = systemRom.substring(0,systemRom.indexOf("GB")).trim();
        double system_Long = FranceReplace(system);

        //应用数据
        String appRom = getTotalStorageUsedByApps();
        String app = appRom.substring(0,appRom.indexOf("GB")).trim();
        double app_Long = FranceReplace(app);

        //其他占用
        double otherRom = total_Long - (available_Long + system_Long + app_Long);
        String other = String.format("%.2f", otherRom);
        double other_Long = FranceReplace(other);

         storage_total.setText(getString(R.string.device_storage_total)+" : " + totalRom);
         storage_available.setText(getString(R.string.device_storage_available)+" : " + availableRom);
         storage_system.setText(getString(R.string.device_storage_system)+" : " + systemRom);
         storage_app.setText(getString(R.string.device_storage_app)+" : " + appRom);
         storage_other.setText(getString(R.string.device_storage_other)+" : " + other + "GB");

         Draw_Charts(total_Long,available_Long,system_Long,app_Long,other_Long);
    }

    //RAM内存大小, 返回1GB/2GB/3GB/4GB/8G/16G
    public static String getTotalRam(){
        String path = "/proc/meminfo";
        String ramMemorySize = null;
        int totalRam = 0 ;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 4096);
            ramMemorySize = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(ramMemorySize != null){
            totalRam = (int)Math.ceil((new Float(Float.valueOf(ramMemorySize) / (1024 * 1024)).doubleValue()));
        }

        return totalRam + "GB";
    }

    //ROM内存大小，返回 64G/128G/256G/512G
    /*
    * 总容量*/
    private static String getTotalRom() {
        File dataDir = Environment.getDataDirectory();
        StatFs stat = new StatFs(dataDir.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long size = totalBlocks * blockSize;
        long GB = 1024 * 1024 * 1024;
        final long[] deviceRomMemoryMap = {2*GB, 4*GB, 8*GB, 16*GB, 32*GB, 64*GB, 128*GB, 256*GB, 512*GB, 1024*GB, 2048*GB};
        String[] displayRomSize = {"2GB","4GB","8GB","16GB","32GB","64GB","128GB","256GB","512GB","1024GB","2048GB"};
        int i;
        for(i = 0 ; i < deviceRomMemoryMap.length; i++) {
            if(size <= deviceRomMemoryMap[i]) {
                break;
            }
            if(i == deviceRomMemoryMap.length) {
                i--;
            }
        }
        return displayRomSize[i];
    }

    /*
    * 可用容量*/
    private static String getAvailableStorage() {
        File dataDir = Environment.getDataDirectory();
        StatFs stat = new StatFs(dataDir.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long size = availableBlocks * blockSize;
        double GB = 1024.0 * 1024.0 * 1024.0;

        double availableSpaceGB = size / GB;
        String formattedAvailableSpace = String.format("%.2f", availableSpaceGB);

        return formattedAvailableSpace + "GB";
    }

    /*
    * 系统占用*/
    private static String getSystemFileSize() {
        File systemDir = Environment.getRootDirectory();
        StatFs stat = new StatFs(systemDir.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long size = totalBlocks * blockSize;
        double GB = 1024.0 * 1024.0 * 1024.0;

        double systemFileSizeGB = size / GB;
        String formattedSystemFileSize = String.format("%.2f", systemFileSizeGB);

        return formattedSystemFileSize + "GB";
    }

    /*
    * 应用数据*/
    private String getTotalStorageUsedByApps() {
        double totalSize = 0.0;
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(0);

        for (ApplicationInfo appInfo : installedApps) {
            try {
                String packageName = appInfo.packageName;
                long appSize = getAppSize(packageName);
                totalSize += appSize;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        double GB = 1024.0 * 1024.0 * 1024.0;
        double totalSizeGB = totalSize / GB;
        String formattedTotalSize = String.format("%.2f", totalSizeGB);

        return formattedTotalSize + "GB";
    }

    private long getAppSize(String packageName) {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, 0);
            String sourceDir = appInfo.sourceDir;
            return new File(sourceDir).length();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private void Draw_Charts(double total_Long,double available_Long,double system_Long,double app_Long,double other_Long){
        // 在你的Activity或Fragment中获取BarChart的引用
        BarChart barChart = findViewById(R.id.barChart);

        // 创建柱状图数据项
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) total_Long));
        entries.add(new BarEntry(1, (float) available_Long));
        entries.add(new BarEntry(2, (float) system_Long));
        entries.add(new BarEntry(3, (float) app_Long));
        entries.add(new BarEntry(4, (float) other_Long));

        //设置柱状图的数据
        BarDataSet dataSet = new BarDataSet(entries,"Storage Data");
        dataSet.setValueTextSize(20f);


        switch (theme_code){
            case "0": //iceblue
                dataSet.setColors(new int[] {getResources().getColor(R.color.storage_charts_iceblue_total),
                        getResources().getColor(R.color.storage_charts_iceblue_available),
                        getResources().getColor(R.color.storage_charts_iceblue_system),
                        getResources().getColor(R.color.storage_charts_iceblue_app),
                        getResources().getColor(R.color.storage_charts_iceblue_other)});
                dataSet.setValueTextColor(getResources().getColor(R.color.customWhite));

                break;
            case "1"://kapokwhite
                dataSet.setColors(new int[] {getResources().getColor(R.color.storage_charts_kapokwhite_total),
                        getResources().getColor(R.color.storage_charts_kapokwhite_available),
                        getResources().getColor(R.color.storage_charts_kapokwhite_system),
                        getResources().getColor(R.color.storage_charts_kapokwhite_app),
                        getResources().getColor(R.color.storage_charts_kapokwhite_other)});
                dataSet.setValueTextColor(getResources().getColor(R.color.black));

                break;
            case "2": //starblue
                dataSet.setColors(new int[] {getResources().getColor(R.color.storage_charts_starblue_total),
                        getResources().getColor(R.color.storage_charts_starblue_available),
                        getResources().getColor(R.color.storage_charts_starblue_system),
                        getResources().getColor(R.color.storage_charts_starblue_app),
                        getResources().getColor(R.color.storage_charts_starblue_other)});
                dataSet.setValueTextColor(getResources().getColor(R.color.customWhite));

                break;
        }

        BarData barData = new BarData(dataSet);


        //设置柱状图的其他属性
        barChart.setData(barData);//设置数据源
        barChart.setFitBars(true);//自适应宽度
        barChart.getDescription().setEnabled(false);//隐藏描述
        barChart.getXAxis().setDrawGridLines(false); // 去掉X轴网格线
        barChart.getAxisLeft().setDrawGridLines(false); // 去掉左侧Y轴网格线
        barChart.getAxisRight().setDrawGridLines(false); // 去掉右侧Y轴网格线
        barChart.getAxisLeft().setEnabled(false); // 去掉左侧Y轴
        barChart.getAxisRight().setEnabled(false); // 去掉右侧Y轴
        barChart.getXAxis().setDrawLabels(false);

        //刷新图表
        barChart.invalidate();
    }
}