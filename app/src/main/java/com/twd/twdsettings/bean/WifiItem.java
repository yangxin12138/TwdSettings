package com.twd.twdsettings.bean;

public class WifiItem {
    private String name;
    private int signalStrength;

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
}
