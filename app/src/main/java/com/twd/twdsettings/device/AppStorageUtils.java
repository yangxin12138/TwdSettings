package com.twd.twdsettings.device;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;
public class AppStorageUtils{
    public static double getInstalledAppsStorageSize(Context context) {
        double totalSize = 0;

        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            String appSourceDir = applicationInfo.sourceDir;
            File appFile = new File(appSourceDir);
            totalSize += getFileSize(appFile);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        File externalDir = Environment.getExternalStorageDirectory();
        File externalAppsDir = new File(externalDir, "/system");
        totalSize += getDirectorySize(externalAppsDir);

        return convertBytesToGB(totalSize);
    }

    private static double getFileSize(File file) {
        double size = 0;
        if (file.exists()) {
            size = file.length();
        }
        return size;
    }

    private static double getDirectorySize(File directory) {
        double size = 0;
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else if (file.isDirectory()) {
                        size += getDirectorySize(file);
                    }
                }
            }
        }
        return size;
    }

    private static double convertBytesToGB(double bytes) {
        double kilobytes = bytes / 1024;
        double megabytes = kilobytes / 1024;
        double gigabytes = megabytes / 1024;
        return Math.round(gigabytes * 100.0) / 100.0;
    }
}
