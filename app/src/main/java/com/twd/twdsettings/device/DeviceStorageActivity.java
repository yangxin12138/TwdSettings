package com.twd.twdsettings.device;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.ProgressBar;

import com.twd.twdsettings.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class DeviceStorageActivity extends AppCompatActivity {

    private CustomProgressBar progressBar;
    private static final String TAG = DeviceStorageActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_storage);


        progressBar = findViewById(R.id.customProgressBar);
        progressBar.setRatios(0.18f, 0.28f, 0.39f, 0.13f);
        double system = getSystemUsage();
        float app = calculateInstalledAppsUsage(this);
        long total = getTotalStorageSize();
        Log.i(TAG, "onCreate: -------- system = " + system + " app = " + app);
        Log.i(TAG, "onCreate: --------total = " + total);

        double appStorageSize = AppStorageUtils.getInstalledAppsStorageSize(getApplicationContext());
        Log.i(TAG, "onCreate: ---------------install =" + appStorageSize + "GB");

    }

    public static long getTotalStorageSize() {
        String path = Environment.getExternalStorageDirectory().getPath();
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        long totalUsageGB =  blockSize * totalBlocks / (1024 * 1024 * 1024);
        return totalUsageGB;
    }

    public static double getSystemUsage(){
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        long systemUsageBytes = (totalBlocks - availableBlocks) * blockSize;
        double systemUsageGB = (double) systemUsageBytes / (1024*1024*1024); // 转化为GB
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formatSystemGB = decimalFormat.format(systemUsageGB);
        return Float.parseFloat(formatSystemGB);
    }

    public static float calculateInstalledAppsUsage(Context context) {
        long totalAppUsage = 0;
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(0);
        for (ApplicationInfo appInfo : installedApps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appSourceDir = appInfo.sourceDir;
                long appSize = new File(appSourceDir).length();
                totalAppUsage += appSize;
            }
        }
        float totalAppUsageGB = (float) totalAppUsage / (1024 * 1024 * 1024);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedTotalAppUsage = decimalFormat.format(totalAppUsageGB);
        return Float.parseFloat(formattedTotalAppUsage);
    }
}