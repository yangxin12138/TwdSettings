package com.twd.twdsettings.bean;

public class WifiItem {
    private String name;
    private int signalStrength;
    private String status;

    public WifiItem(String name, int signalStrength) {
        this.name = name;
        this.signalStrength = signalStrength;
    }

    public String getName() {
        return name;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
