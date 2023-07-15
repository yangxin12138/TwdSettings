package com.twd.twdsettings.bean;

public class SpeedTestResult {
    private String ssid;
    private float averageSpeed;
    private String ipAddress;

    public SpeedTestResult(String ssid, float averageSpeed, String ipAddress) {
        this.ssid = ssid;
        this.averageSpeed = averageSpeed;
        this.ipAddress = ipAddress;
    }

    public String getSsid() {
        return ssid;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
